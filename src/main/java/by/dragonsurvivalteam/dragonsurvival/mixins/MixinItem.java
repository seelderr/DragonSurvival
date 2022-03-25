package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( Item.class )
public class MixinItem{
	@Inject( at = @At( "HEAD" ), method = "inventoryTick", cancellable = true )
	public void onItemUpdate(ItemStack stack, Level world, Entity entity, int slot, boolean isSelected, CallbackInfo ci){
		if(!(entity instanceof Player) || entity.tickCount % 10 != 0){
			return;
		}

		Player player = (Player)entity;

		if(player.isCreative() || player.isSpectator()){
			return;
		}

		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon() && ConfigHandler.SERVER.blacklistedSlots.get().contains(slot) && ConfigUtils.parseConfigItemList(ConfigHandler.SERVER.blacklistedItems.get()).contains(stack.getItem())){
				if(slot >= 0 && slot < 9 && !isSelected && !ItemStack.matches(player.getOffhandItem(), stack)){
					return;
				}
				if(stack.isEmpty() || !stack.onDroppedByPlayer(player)){
					return;
				}

				player.drop(stack, false);
				player.getInventory().removeItem(stack);
			}
		});
	}

	@Inject( at = @At( "HEAD" ), method = "use", cancellable = true )
	public void dragonUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> ci){
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				if(DragonFoodHandler.isDragonEdible((Item)(Object)this, dragonStateHandler.getType())){
					ItemStack itemstack = player.getItemInHand(hand);
					if(player.canEat(DragonFoodHandler.getDragonFoodProperties(((Item)(Object)this), dragonStateHandler.getType()).canAlwaysEat())){
						player.startUsingItem(hand);
						ci.setReturnValue(InteractionResultHolder.consume(itemstack));
					}else{
						ci.setReturnValue(InteractionResultHolder.fail(itemstack));
					}
				}else{
					ci.setReturnValue(InteractionResultHolder.pass(player.getItemInHand(hand)));
				}
			}
		});
	}

	@Inject( at = @At( "HEAD" ), method = "finishUsingItem", cancellable = true )
	public void dragonFinishUsingItem(ItemStack item, Level level, LivingEntity player, CallbackInfoReturnable<ItemStack> ci){
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				ci.setReturnValue(DragonFoodHandler.isDragonEdible((Item)(Object)this, dragonStateHandler.getType()) ? player.eat(level, item) : item);
			}
		});
	}
}