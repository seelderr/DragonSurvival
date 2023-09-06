package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin( Block.class )
public class MixinBlock{
	@Inject( at = @At( "HEAD" ), method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V", cancellable = true )
	private static void dropResources(BlockState state, Level level, BlockPos pos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfo ci) {
        if (!(level instanceof ServerLevel)) {
            return;
        }

		if(!DragonUtils.isDragonType(DragonUtils.getHandler(entity), DragonTypes.CAVE)){
			return;
		}

		Block.getDrops(state, (ServerLevel)level, pos, blockEntity, entity, stack).forEach(droppedStack -> {
			if(!level.isClientSide && !droppedStack.isEmpty() && level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !level.restoringBlockSnapshots) {
				double d0 = (double)(level.random.nextFloat() * 0.5F) + 0.25D;
				double d1 = (double)(level.random.nextFloat() * 0.5F) + 0.25D;
				double d2 = (double)(level.random.nextFloat() * 0.5F) + 0.25D;
				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, droppedStack){
					@Override
					public boolean fireImmune(){
						return true;
					}
				};
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		});

		state.spawnAfterBreak((ServerLevel)level, pos, stack);

		ci.cancel();
	}
}