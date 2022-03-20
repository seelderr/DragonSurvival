package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ClawToolHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( PlayerInventory.class )
public class MixinPlayerInventory{
	@Shadow
	@Final
	public PlayerEntity player;

	@Shadow
	@Final
	public NonNullList<ItemStack> items;

	@Inject( at = @At( "HEAD" ), method = "getDestroySpeed", cancellable = true )
	public void getDestroySpeed(BlockState state, CallbackInfoReturnable<Float> ci){
		ItemStack mainStack = player.inventory.getSelected();
		ItemStack breakStack = ClawToolHandler.getDragonTools(player);

		if(!ItemStack.isSame(mainStack, breakStack)){
			float tempSpeed = breakStack.getDestroySpeed(state);
			ci.setReturnValue(tempSpeed);
		}
	}
}