package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class MixinServerPlayerGameMode {
    @Unique private BlockPos dragonSurvival$blockPosition;

    @Inject(method = "destroyBlock", at = @At("HEAD"))
    public void extendedHarvestCheck(final BlockPos blockPosition, final CallbackInfoReturnable<Boolean> callback) {
        dragonSurvival$blockPosition = blockPosition;
    }

    /** Additional check for mods which do not use the forge harvest check event */
    @ModifyVariable(method = "destroyBlock", at = @At(value = "STORE", ordinal = 0))
    public boolean extendedHarvestCheck(boolean originalResult) {
        boolean result = originalResult;

        if (!originalResult && dragonSurvival$blockPosition != null) {
            DragonStateHandler handler = DragonUtils.getHandler(player);

            if (handler.isDragon()) {
                result = handler.canHarvestWithPaw(player.level.getBlockState(dragonSurvival$blockPosition));
                dragonSurvival$blockPosition = null;
            }
        }

        return result;
    }

    @Shadow @Final protected ServerPlayer player;
}
