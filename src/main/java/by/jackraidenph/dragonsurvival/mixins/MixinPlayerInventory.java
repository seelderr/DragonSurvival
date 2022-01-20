package by.jackraidenph.dragonsurvival.mixins;

import by.jackraidenph.dragonsurvival.common.handlers.magic.ClawToolHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( Inventory.class)
public class MixinPlayerInventory
{
	@Shadow
	@Final
	public Player player;
	
	@Shadow
	@Final
	public NonNullList<ItemStack> items;
	
	@Inject( at = @At("HEAD"), method = "getDestroySpeed", cancellable = true)
	public void getDestroySpeed(BlockState state, CallbackInfoReturnable<Float> ci){
		ItemStack mainStack = player.getInventory().getSelected();
		ItemStack breakStack = ClawToolHandler.getDragonTools(player);
		
		if(!ItemStack.isSame(mainStack, breakStack)){
			float tempSpeed = breakStack.getDestroySpeed(state);
			ci.setReturnValue(tempSpeed);
		}
	}
}
