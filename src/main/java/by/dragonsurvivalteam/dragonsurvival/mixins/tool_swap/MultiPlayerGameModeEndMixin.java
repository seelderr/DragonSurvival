package by.dragonsurvivalteam.dragonsurvival.mixins.tool_swap;

import by.dragonsurvivalteam.dragonsurvival.registry.attachments.ClawInventoryData;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MultiPlayerGameMode.class, priority = 10_000)
public class MultiPlayerGameModeEndMixin {
    @Inject(method = {"startDestroyBlock", "continueDestroyBlock"}, at = @At("RETURN"))
    private void finishSwap(final BlockPos blockPosition, final Direction directionFacing, final CallbackInfoReturnable<Boolean> callback) {
        Player player = Minecraft.getInstance().player;
        ClawInventoryData.getData(player).swapFinish(player);
    }
}
