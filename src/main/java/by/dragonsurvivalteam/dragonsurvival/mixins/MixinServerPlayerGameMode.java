package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerPlayerGameMode.class)
public class MixinServerPlayerGameMode {
    /** Additional check for mods which do not use the forge harvest check event */
    @ModifyVariable(method = "destroyBlock", at = @At(value = "STORE", ordinal = 0))
    public boolean extendedHarvestCheck(boolean originalResult, /* Method arguments: */ final BlockPos blockPosition) {
        if (!originalResult) {
            DragonStateHandler handler = DragonUtils.getHandler(player);

            if (handler.isDragon()) {
                return handler.canHarvestWithPaw(player.level().getBlockState(blockPosition));
            }
        }

        return originalResult;
    }

    @Shadow @Final protected ServerPlayer player;
}
