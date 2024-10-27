package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.ClawInventory;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonPenaltyHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonSizeHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.MagicHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.BlockPosHelper;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.EnchantmentUtils;
import by.dragonsurvivalteam.dragonsurvival.util.ToolUtils;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow protected boolean jumping;
	@Shadow protected ItemStack useItem;

	public LivingEntityMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

    @SuppressWarnings("ConstantValue") // both checks in the if statement are valid
    @Redirect(method = "collectEquipmentChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack dragonSurvival$grantDragonSwordAttributes(LivingEntity entity, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND && (Object) this instanceof Player player) {
            DragonStateHandler handler = DragonStateProvider.getData(player);

            if (handler.isDragon() && ToolUtils.shouldUseDragonTools(player.getMainHandItem())) {
                // Without this the item in the dragon slot for the sword would not grant any of its attributes
                ItemStack sword = handler.getClawToolData().getClawsInventory().getItem(ClawInventory.Slot.SWORD.ordinal());

                if (!sword.isEmpty()) {
                    return sword;
                }
            }
        }

        return getItemBySlot(slot);
    }

	@ModifyReturnValue(at = @At(value = "RETURN"), method = "getPassengerRidingPosition")
	public Vec3 dragonSurvival$getDragonPassengersRidingOffset(Vec3 original) {
		if ((Object) this instanceof Player player) {
			DragonStateHandler handler = DragonStateProvider.getData(player);

			if (handler.isDragon()) {
				double height = DragonSizeHandler.calculateDragonHeight(handler, player);

				if (!DragonStateProvider.isDragon(getPassengers().getFirst())) {
					// Human passenger
					switch (getPose()) {
						case FALL_FLYING, SWIMMING, SPIN_ATTACK -> {
							return original.add(new Vec3(0, (height * 0.65) - 1D, 0));
						}
						case CROUCHING -> {
							return original.add(new Vec3(0, (height * 0.73D) - 2D, 0));
						}
						default -> {
							return original.add(new Vec3(0, (height * 0.66D) - 1.9D, 0));
						}
					}
				} else {
					// Dragon passenger
					switch (getPose()) {
						case FALL_FLYING, SWIMMING, SPIN_ATTACK -> {
							return original.add(new Vec3(0, (height * 0.66) - 0.4D, 0));
						}
						case CROUCHING -> {
							return original.add(new Vec3(0, (height * 0.79D) - 1.7D, 0));
						}
						default -> {
							return original.add(new Vec3(0, (height * 0.72D) - 1.9D, 0));
						}
					}
				}
			}
		}

		return original;
  	}

	@Inject(method = "getEquipmentSlotForItem", at = @At(value = "HEAD"), cancellable = true)
	private void dragonSurvival$disallowBlackListedItemsFromBeingEquipped(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> callback) {
		if (DragonStateProvider.isDragon((LivingEntity) (Object) this)) {
			if (DragonPenaltyHandler.itemIsBlacklisted(stack.getItem())) {
				callback.setReturnValue(EquipmentSlot.MAINHAND);
			}
		}
	}

	@Unique private int dragonSurvival$getHumanOrDragonUseDuration(int original){
		if ((Object) this instanceof Player player) {
			DragonStateHandler handler = DragonStateProvider.getData(player);

			if (handler != null && handler.isDragon()) {
				return DragonFoodHandler.getUseDuration(useItem, player);
			}
		}

		return original;
	}

	@ModifyExpressionValue(method = "shouldTriggerItemUseEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseDuration(Lnet/minecraft/world/entity/LivingEntity;)I"))
	private int replaceUseDurationInShouldTriggerItemUseEffects(int original){
		return dragonSurvival$getHumanOrDragonUseDuration(original);
	}

	@ModifyExpressionValue(method = "onSyncedDataUpdated", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseDuration(Lnet/minecraft/world/entity/LivingEntity;)I"))
	private int replaceUseDurationInSyncedDataUpdated(int original){
		return dragonSurvival$getHumanOrDragonUseDuration(original);
	}

	@ModifyExpressionValue(method = "triggerItemUseEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/UseAnim;"))
	private UseAnim dragonSurvival$replaceEatAndDrinkAnimation(UseAnim original, ItemStack stack, int amount) {
		if ((Object) this instanceof Player player) {
			DragonStateHandler handler = DragonStateProvider.getData(player);

			if (handler.isDragon()) {
				return DragonFoodHandler.isEdible(stack, handler.getType()) ? UseAnim.EAT : original;
			}
		}

		return original;
	}

	/** There is no event to actually modify the effect when it's being applied */
	@ModifyVariable(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), argsOnly = true)
	private MobEffectInstance dragonSurvival$modifyEffect(final MobEffectInstance instance, final @Local(argsOnly = true) Entity applier) {
		if ((Object) this instanceof Player affected) {
			return MagicHandler.modifyEffect(affected, instance, applier);
		}

		return instance;
	}

	/** Enable cave dragons to properly swim in lava */
	@Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;", shift = At.Shift.AFTER), cancellable = true)
	private void dragonSurvival$handleLavaSwimming(final Vec3 travelVector, final CallbackInfo callback, @Local double gravity) {
        //noinspection ConstantValue -> it's not always true
        if (!((Object) this instanceof Player player)) {
			return;
		}

		DragonStateHandler data = DragonStateProvider.getData(player);

		if (!data.isDragon()) {
			return;
		}

		if (ServerConfig.bonusesEnabled && ServerConfig.caveLavaSwimming && DragonUtils.isDragonType(data, DragonTypes.CAVE) && isInLava() && player.isAffectedByFluids() && !player.canStandOnFluid(level().getFluidState(blockPosition()))) {
			// This y-related movement logic is copied from 'Player#travel' (it doesn't get called when swimming in lava)
			Vec3 deltaMovement = getDeltaMovement();
			float lookY = (float) getLookAngle().y;

			// Speed increase depending on how much the player looks up or down (0.06 is the min. speed and 0.15 is the max. speed bonus)
			float yModifier = 0.06f + (0.15f - 0.06f) * Mth.abs(Math.clamp(lookY, -1, 1));

			if (isSprinting()) {
				yModifier *= 1.2f;
			}

			// Move the player up or down, depending on where they look (but only if they're moving)
			if (deltaMovement.horizontalDistance() > 0.05 && (lookY <= 0 || jumping || !level().getBlockState(BlockPosHelper.get(getX(), getY() + 1 - 0.1, getZ())).getFluidState().isEmpty())) {
				setDeltaMovement(deltaMovement.add(0, (lookY - deltaMovement.y) * Mth.abs(yModifier), 0));
			}

			double oldY = getY();
			float speedModifier = isSprinting() ? 0.9f : getWaterSlowDown();
			float swimSpeed = 0.05f;
			// FIXME :: Use Attributes#WATER_MOVEMENT_EFFICIENCY instead
			float swimSpeedModifier = Math.min(3, EnchantmentUtils.getLevel(player, Enchantments.DEPTH_STRIDER));

			if (!onGround()) {
				swimSpeedModifier *= 0.5f;
			}

			if (swimSpeedModifier > 0) {
				speedModifier += (0.54600006f - speedModifier) * swimSpeedModifier / 2.5f;
				swimSpeed += (player.getSpeed() - swimSpeed) * swimSpeedModifier / 2.5f;
			}

			if (player.hasEffect(MobEffects.DOLPHINS_GRACE)) {
				speedModifier = 0.96f;
			}

			swimSpeed *= (float) player.getAttributeValue(NeoForgeMod.SWIM_SPEED);
			moveRelative(swimSpeed, travelVector);
			move(MoverType.SELF, getDeltaMovement());
			Vec3 newMovement = getDeltaMovement();

			if (horizontalCollision && player.onClimbable()) {
				newMovement = new Vec3(newMovement.x, 0.2, newMovement.z);
			}

			setDeltaMovement(newMovement.multiply(speedModifier, 0.8, speedModifier));
			Vec3 adjustedMovement = player.getFluidFallingAdjustedMovement(gravity, player.isFallFlying(), getDeltaMovement());
			setDeltaMovement(adjustedMovement);

			if (horizontalCollision && isFree(adjustedMovement.x, adjustedMovement.y + 0.6 - getY() + oldY, adjustedMovement.z)) {
				setDeltaMovement(adjustedMovement.x, 0.3, adjustedMovement.z);
			}

			player.calculateEntityAnimation(false);
			callback.cancel();
		}
	}

	@Shadow public abstract ItemStack getItemBySlot(EquipmentSlot pSlot);
	@Shadow protected abstract float getWaterSlowDown();
}