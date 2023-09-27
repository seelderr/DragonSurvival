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

@Mixin(value = ServerPlayerGameMode.class, priority = 1)
public class MixinServerPlayerGameModeStart {
    @Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;onBlockBreakEvent(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/GameType;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/core/BlockPos;)I", shift = At.Shift.BEFORE))
    public void startSwap_onBlockBreakEvent(final BlockPos blockPosition, final CallbackInfoReturnable<Boolean> callback) {
        ToolUtils.swapStart(player, player.level.getBlockState(blockPosition));
    }

    /**
     Certain blocks override the `canHarvestBlock` method without calling `super()` therefor avoiding the Forge event<br>
     Also needed to make sure the item gets properly damaged for mining the block
    */
    @Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.BEFORE, ordinal = 1))
    public void startSwap_canHarvestBlock(final BlockPos blockPosition, final CallbackInfoReturnable<Boolean> callback) {
        ToolUtils.swapStart(player, player.level.getBlockState(blockPosition));
    }

    @Shadow @Final protected ServerPlayer player;
}
