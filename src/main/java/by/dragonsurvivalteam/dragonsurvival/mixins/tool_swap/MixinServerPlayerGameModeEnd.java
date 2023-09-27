package by.dragonsurvivalteam.dragonsurvival.mixins.tool_swap;

import by.dragonsurvivalteam.dragonsurvival.util.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ServerPlayerGameMode.class, priority = 10_000)
public class MixinServerPlayerGameModeEnd {
    @Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;onBlockBreakEvent(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/GameType;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/core/BlockPos;)I", shift = At.Shift.AFTER))
    public void finishSwap_onBlockBreakEvent(final BlockPos blockPosition, final CallbackInfoReturnable<Boolean> callback) {
        ToolUtils.swapFinish(player);
    }

    @Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayerGameMode;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z", shift = At.Shift.AFTER, ordinal = 1))
    public void finishSwap_canHarvestBlock(final BlockPos blockPosition, final CallbackInfoReturnable<Boolean> callback) {
        ToolUtils.swapFinish(player);
    }

    @Shadow @Final protected ServerPlayer player;
}
