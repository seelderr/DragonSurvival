package by.jackraidenph.dragonsurvival.mixins;

import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( PlayerInventory.class)
public class MixinPlayerInventory
{
	@Shadow
	@Final
	public PlayerEntity player;
	
	@Shadow
	@Final
	public NonNullList<ItemStack> items;
	
	@Inject( at = @At("HEAD"), method = "getDestroySpeed", cancellable = true)
	public void getDestroySpeed(BlockState state, CallbackInfoReturnable<Float> ci){
		ItemStack mainStack = player.getMainHandItem();
		DragonStateHandler cap = DragonStateProvider.getCap(player).orElse(null);
		
		if(!(mainStack.getItem() instanceof TieredItem) && cap != null && state != null && player != null) {
			float newSpeed = 0F;
			ItemStack harvestTool = null;
			
			for (int i = 1; i < 4; i++) {
				if (state.getHarvestTool() == DragonStateHandler.CLAW_TOOL_TYPES[i]) {
					ItemStack breakingItem = cap.getClawInventory().getClawsInventory().getItem(i);
					if (breakingItem != null && !breakingItem.isEmpty()) {
						float tempSpeed = breakingItem.getDestroySpeed(state);
						
						if (tempSpeed > newSpeed) {
							newSpeed = tempSpeed;
							harvestTool = breakingItem;
						}
					}
				}
			}
			
			if(harvestTool != null && !harvestTool.isEmpty()){
				ci.setReturnValue(newSpeed);
			}
		}
	}
}
