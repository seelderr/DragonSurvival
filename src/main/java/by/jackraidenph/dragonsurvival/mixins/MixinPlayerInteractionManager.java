package by.jackraidenph.dragonsurvival.mixins;

import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin( PlayerInteractionManager.class)
public class MixinPlayerInteractionManager
{

	@Redirect( method = "destroyBlock",
	           at = @At( value="INVOKE", target="Lnet/minecraft/entity/player/ServerPlayerEntity;getMainHandItem()Lnet/minecraft/item/ItemStack;"))
	public ItemStack getTools(ServerPlayerEntity player)
	{
		ItemStack mainStack = player.getMainHandItem();
		DragonStateHandler cap = DragonStateProvider.getCap(player).orElse(null);
		
		if(!(mainStack.getItem() instanceof TieredItem) && cap != null) {
			float newSpeed = 0F;
			ItemStack harvestTool = null;
			
			Vector3d vector3d = player.getDeltaMovement();
			World world = player.level;
			Vector3d vector3d1 = player.position();
			Vector3d vector3d2 = vector3d1.add(vector3d);
			BlockRayTraceResult raytraceresult = world.clip(new RayTraceContext(vector3d1, vector3d2, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player));
			
			if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS) {
				BlockState state = world.getBlockState(raytraceresult.getBlockPos());
				
				if(state != null) {
					for (int i = 1; i < 4; i++) {
						if (state.getHarvestTool() == null || state.getHarvestTool() == DragonStateHandler.CLAW_TOOL_TYPES[i]) {
							ItemStack breakingItem = cap.getClawInventory().getClawsInventory().getItem(i);
							if (!breakingItem.isEmpty()) {
								float tempSpeed = breakingItem.getDestroySpeed(state);
								
								if (tempSpeed > newSpeed) {
									newSpeed = tempSpeed;
									harvestTool = breakingItem;
								}
							}
						}
					}
				}
			}
			
			if(harvestTool != null && !harvestTool.isEmpty()){
				return harvestTool;
			}
		}
		
		return mainStack;
	}
}
