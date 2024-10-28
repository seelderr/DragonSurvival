package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.ClawInventory;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonPenaltyHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonSizeHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.MagicHandler;
import by.dragonsurvivalteam.dragonsurvival.util.ToolUtils;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow
	public abstract ItemStack getMainHandItem();

	@Shadow
	public abstract ItemStack getItemBySlot(EquipmentSlot pSlot);

	@Shadow
	protected ItemStack useItem;

	public LivingEntityMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

	@SuppressWarnings("ConstantValue")
	@Redirect(method = "collectEquipmentChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
	private ItemStack dragonSurvival$grantDragonSwordAttributes(LivingEntity entity, EquipmentSlot slot) {
		if (slot == EquipmentSlot.MAINHAND && (Object) this instanceof Player player) {
			DragonStateHandler handler = DragonStateProvider.getData(player);

			if (handler.isDragon() && ToolUtils.shouldUseDragonTools(getMainHandItem())) {
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

    @Unique
    private int dragonSurvival$getHumanOrDragonUseDuration(int original) {
		if ((Object) this instanceof Player player) {
			DragonStateHandler handler = DragonStateProvider.getData(player);

			if (handler != null && handler.isDragon()) {
				return DragonFoodHandler.getUseDuration(useItem, player);
			}
		}

		return original;
	}

	@ModifyExpressionValue(method = "shouldTriggerItemUseEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseDuration(Lnet/minecraft/world/entity/LivingEntity;)I"))
	private int replaceUseDurationInShouldTriggerItemUseEffects(int original) {
		return dragonSurvival$getHumanOrDragonUseDuration(original);
	}

	@ModifyExpressionValue(method = "onSyncedDataUpdated", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseDuration(Lnet/minecraft/world/entity/LivingEntity;)I"))
	private int replaceUseDurationInSyncedDataUpdated(int original) {
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

	/**
	 * There is no event to actually modify the effect when it's being applied
	 */
	@ModifyVariable(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), argsOnly = true)
	private MobEffectInstance dragonSurvival$modifyEffect(final MobEffectInstance instance, final @Local(argsOnly = true) Entity applier) {
		if ((Object) this instanceof Player affected) {
			return MagicHandler.modifyEffect(affected, instance, applier);
		}

		return instance;
	}
}