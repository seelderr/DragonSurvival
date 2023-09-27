package by.dragonsurvivalteam.dragonsurvival.mixins.tool_swap;

import by.dragonsurvivalteam.dragonsurvival.util.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class, priority = 10_000)
public class MixinPlayerEnd {
    @Inject(method = "getDigSpeed", at = @At("RETURN"), /* Forge method */ remap = false)
    public void finishSwap_getDigSpeed(final BlockState blockState, final BlockPos blockPosition, final CallbackInfoReturnable<Float> callback) {
        ToolUtils.swapFinish((Player) (Object) this);
    }

    @Inject(method = "hasCorrectToolForDrops", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;doPlayerHarvestCheck(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/state/BlockState;Z)Z",shift = At.Shift.AFTER))
    public void finishSwap_hasCorrectToolForDrops(final BlockState blockState, final CallbackInfoReturnable<Boolean> callback) {
        ToolUtils.swapFinish((Player) (Object) this);
    }
}
