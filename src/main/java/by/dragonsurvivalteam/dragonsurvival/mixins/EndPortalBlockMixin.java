package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.EndPortalBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin {

    @Unique private static final BlockPos END_CAVE_DRAGON_SPAWN_POINT = new BlockPos(-100, 50, 0);
    @Unique private static final BlockPos END_SEA_DRAGON_SPAWN_POINT = new BlockPos(0, 50, 100);
    @Unique private static final BlockPos END_FOREST_DRAGON_SPAWN_POINT = new BlockPos(0, 50, -100);

    @ModifyVariable(method = "getPortalDestination", at = @At(value = "STORE"), ordinal = 1)
    private BlockPos modifyBlockPosForEndSpawnPoint(BlockPos original, @Local(argsOnly = true) Entity entity, @Local(argsOnly = true) ServerLevel level) {
        boolean travellingToTheEnd = level.dimension().equals(ServerLevel.OVERWORLD);
        if(travellingToTheEnd) {
            if(DragonUtils.isDragonType(entity, DragonTypes.CAVE)) {
                return END_CAVE_DRAGON_SPAWN_POINT;
            } else if(DragonUtils.isDragonType(entity, DragonTypes.SEA)) {
                return END_SEA_DRAGON_SPAWN_POINT;
            } else if(DragonUtils.isDragonType(entity, DragonTypes.FOREST)) {
                return END_FOREST_DRAGON_SPAWN_POINT;
            }
        }

        return original;
    }

    @WrapOperation(method = "getPortalDestination", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/EndPlatformFeature;createEndPlatform(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Z)V"))
    private void spawnDragonPlatform(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, boolean dropBlocks, Operation<Void> original, @Local(argsOnly = true) Entity entity) {
        if(entity instanceof Player player) {
            DragonStateHandler handler = DragonStateProvider.getData(player);
            if(handler.isDragon()) {
                // Construct a different platform for the dragon to spawn on. For now, just spawn the regular platform.
                original.call(serverLevelAccessor, blockPos, dropBlocks);
                return;
            }
        }

        original.call(serverLevelAccessor, blockPos, dropBlocks);
    }
}
