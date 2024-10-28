package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerGameMode.class)
public class MixinServerPlayerGameMode {
	@Shadow
	@Final
	protected ServerPlayer player;

	/**
	 * Additional check for mods which do not use the forge harvest check event
	 */
	@ModifyExpressionValue(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;canHarvestBlock(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)Z"))
	private boolean canHarvestBlock(boolean originalResult, @Local(ordinal = 1) BlockState state) {
		if (!originalResult) {
			DragonStateHandler handler = DragonStateProvider.getData(player);
			if (handler.isDragon()) {
				return handler.canHarvestWithPaw(state);
			}
		}

		return originalResult;
	}
}
