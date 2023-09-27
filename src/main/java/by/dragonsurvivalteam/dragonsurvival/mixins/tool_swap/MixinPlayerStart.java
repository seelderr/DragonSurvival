package by.dragonsurvivalteam.dragonsurvival.mixins.tool_swap;

import by.dragonsurvivalteam.dragonsurvival.util.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class, priority = 1)
public class MixinPlayerStart {
    @Inject(method = "getDigSpeed", at = @At("HEAD"), /* Forge method */ remap = false)
    public void swapStart_getDigSpeed(final BlockState blockState, final BlockPos blockPosition, final CallbackInfoReturnable<Float> callback) {
        ToolUtils.swapStart((Player) (Object) this, blockState);
    }

    @Inject(method = "hasCorrectToolForDrops", at = @At(value = "HEAD"))
    public void swapStart_hasCorrectToolForDrops(final BlockState blockState, final CallbackInfoReturnable<Boolean> callback) {
        ToolUtils.swapStart((Player) (Object) this, blockState);
    }
}
