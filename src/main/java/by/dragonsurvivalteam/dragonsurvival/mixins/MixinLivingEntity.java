package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.ClawInventory;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonSizeHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonPenaltyHandler;
import by.dragonsurvivalteam.dragonsurvival.util.ToolUtils;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( LivingEntity.class )
public abstract class MixinLivingEntity extends Entity{
	@Shadow public abstract ItemStack getMainHandItem();
	@Shadow public abstract ItemStack getItemBySlot(EquipmentSlot pSlot);
	@Shadow protected ItemStack useItem;

	@Shadow public abstract void knockback(double pStrength, double pX, double pZ);

	public MixinLivingEntity(EntityType<?> p_i48580_1_, Level p_i48580_2_){
		super(p_i48580_1_, p_i48580_2_);
	}

	@Redirect( method = "collectEquipmentChanges", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;" ) )
	private ItemStack grantDragonSwordAttributes(LivingEntity entity, EquipmentSlot slotType){
		if (slotType == EquipmentSlot.MAINHAND) {
			if ((LivingEntity)(Object)this instanceof Player player) {
				if(DragonStateProvider.isDragon(player)) {
					DragonStateHandler cap = DragonStateProvider.getOrGenerateHandler(entity);
					if (ToolUtils.shouldUseDragonTools(getMainHandItem())) {
						// Without this the item in the dragon slot for the sword would not grant any of its attributes
						ItemStack sword = cap.getClawToolData().getClawsInventory().getItem(ClawInventory.Slot.SWORD.ordinal());

						if (!sword.isEmpty()) {
							return sword;
						}
					}
				}
			}
		}

		return getItemBySlot(slotType);
	}

	@ModifyReturnValue( at = @At( value = "RETURN" ), method = "getPassengerRidingPosition")
	public Vec3 getDragonPassengersRidingOffset(Vec3 original) {
		if ((Object) (this) instanceof Player player) {
			DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);

			if (handler.isDragon()) {
				double height = DragonSizeHandler.getDragonHeight(player);

				if (!DragonStateProvider.getOrGenerateHandler(getPassengers().getFirst()).isDragon()) {
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

	@Inject( method = "getEquipmentSlotForItem", at = @At( value = "HEAD"), cancellable = true)
	private void disallowBlackListedItemsFromBeingEquipped(ItemStack pStack, CallbackInfoReturnable<EquipmentSlot> info){
		if(DragonStateProvider.isDragon((LivingEntity)(Object)this)) {
			if (DragonPenaltyHandler.itemIsBlacklisted(pStack.getItem())) {
				info.setReturnValue(EquipmentSlot.MAINHAND);
			}
		}
	}

	@ModifyExpressionValue(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getFoodProperties(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/food/FoodProperties;"))
	private FoodProperties replaceFoodPropertiesInEat(FoodProperties original, @Local(argsOnly = true) ItemStack itemStack){
		if(DragonStateProvider.isDragon((LivingEntity)(Object)this))
		{
			DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler((LivingEntity)(Object)this);
			return DragonFoodHandler.getDragonFoodProperties(itemStack.getItem(), handler.getType());
		}

		return original;
	}

	@Unique private int dragon_Survival$getHumanOrDragonUseDuration(int original){
		if ((LivingEntity)(Object)this instanceof Player player) {
			DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);
			if (handler != null && handler.isDragon()) {
				return DragonFoodHandler.getUseDuration(useItem, player);
			}
		}

		return original;
	}

	@ModifyExpressionValue(method = "shouldTriggerItemUseEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseDuration(Lnet/minecraft/world/entity/LivingEntity;)I"))
	private int replaceUseDurationInShouldTriggerItemUseEffects(int original){
		return dragon_Survival$getHumanOrDragonUseDuration(original);
	}

	@ModifyExpressionValue(method = "onSyncedDataUpdated", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseDuration(Lnet/minecraft/world/entity/LivingEntity;)I"))
	private int replaceUseDurationInSyncedDataUpdated(int original){
		return dragon_Survival$getHumanOrDragonUseDuration(original);
	}

	@Unique private UseAnim dragon_Survival$getUseAnimation(UseAnim original, ItemStack pStack){

		if(DragonStateProvider.isDragon((LivingEntity)(Object)this)) {
			DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler((LivingEntity)(Object)this);
			return DragonFoodHandler.isEdible(pStack, handler.getType()) ? UseAnim.EAT : original;
		}

		return original;
	}

	@ModifyExpressionValue(method = "triggerItemUseEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/UseAnim;", ordinal = 0))
	private UseAnim replaceGetUseAnimationInTriggerItemUseEffects0(UseAnim original, ItemStack pStack, int pAmount){
		return dragon_Survival$getUseAnimation(original, pStack);
	}

	@ModifyExpressionValue(method = "triggerItemUseEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/UseAnim;", ordinal = 1))
	private UseAnim replaceGetUseAnimationInTriggerItemUseEffects1(UseAnim original, ItemStack pStack, int pAmount){
		return dragon_Survival$getUseAnimation(original, pStack);
	}
}