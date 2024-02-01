package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerPlayerGameMode.class)
public class MixinServerPlayerGameMode {
    @Unique
    private static final ThreadLocal<BlockState> STORED_STATE = new ThreadLocal<>();

    @ModifyVariable(method = "destroyBlock", at = @At(value = "STORE", ordinal = 0))
    public BlockState extendedHarvestCheck(final BlockState state) {
        STORED_STATE.set(state);
        return state;
    }

    /** Additional check for mods which do not use the forge harvest check event */
    @ModifyVariable(method = "destroyBlock", at = @At(value = "STORE", ordinal = 0))
    public boolean extendedHarvestCheck(boolean originalResult) {
        BlockState state = STORED_STATE.get();
        STORED_STATE.remove();

        if (!originalResult && state != null) {
            DragonStateHandler handler = DragonUtils.getHandler(player);

            if (handler.isDragon()) {
                return handler.canHarvestWithPaw(state);
            }
        }

        return originalResult;
    }

    @Shadow @Final protected ServerPlayer player;
}
