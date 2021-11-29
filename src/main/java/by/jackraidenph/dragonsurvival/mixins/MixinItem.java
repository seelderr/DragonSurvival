package by.jackraidenph.dragonsurvival.mixins;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.config.ConfigUtils;
import by.jackraidenph.dragonsurvival.handlers.DragonFoodHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class MixinItem {
	@Inject(at = @At("HEAD"), method = "inventoryTick", cancellable = true)
	public void onItemUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected, CallbackInfo ci) {
		if (!(entity instanceof PlayerEntity) || entity.tickCount % 20 != 0)
			return;

		PlayerEntity player = (PlayerEntity) entity;

		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if (dragonStateHandler.isDragon() && ConfigHandler.SERVER.blacklistedSlots.get().contains(slot)
					&& ConfigUtils.parseConfigItemList(ConfigHandler.SERVER.blacklistedItems.get()).contains(stack.getItem())) {
				player.drop(stack, false, true);
				stack.shrink(stack.getCount());;
			}
		});
	}

	@Inject(at = @At("HEAD"), method = "use", cancellable = true)
	public void dragonUse(World level, PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult<ItemStack>> ci) {
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if (dragonStateHandler.isDragon()) {
				if (DragonFoodHandler.isDragonEdible((Item)(Object)this, dragonStateHandler.getType())) {
					ItemStack itemstack = player.getItemInHand(hand);
					if (player.canEat(DragonFoodHandler.getDragonFoodProperties(((Item)(Object)this), dragonStateHandler.getType()).canAlwaysEat())) {
						player.startUsingItem(hand);
						ci.setReturnValue(ActionResult.consume(itemstack));
					} else {
						ci.setReturnValue(ActionResult.fail(itemstack));
					}
				} else {
					ci.setReturnValue(ActionResult.pass(player.getItemInHand(hand)));
				}
			}
		});
	}

	@Inject(at = @At("HEAD"), method = "finishUsingItem", cancellable = true)
	public void dragonFinishUsingItem(ItemStack item, World level, LivingEntity player, CallbackInfoReturnable<ItemStack> ci) {
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if (dragonStateHandler.isDragon())
				ci.setReturnValue(DragonFoodHandler.isDragonEdible((Item)(Object)this, dragonStateHandler.getType()) ? player.eat(level, item) : item);
		});
	}

}
