package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class MixinBlock {
	@Unique private static Player dragonSurvival$player;

	@Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"))
	private static void storeData(final BlockState blockState, final Level level, final BlockPos blockPosition, final BlockEntity blockEntity, final Entity entity, final ItemStack tool, final CallbackInfo callback) {
		if (entity instanceof Player player && DragonUtils.isDragonType(player, DragonTypes.CAVE)) {
			dragonSurvival$player = player;
		}
	}

	@Inject(method = "lambda$popResource$5", at = @At("HEAD"), cancellable = true)
	private static void test(final Level level, double x, double y, double z, final ItemStack itemStack, final CallbackInfoReturnable<ItemEntity> callback) {
		ItemEntity result;

		if (dragonSurvival$player != null) {
			result = new ItemEntity(level, x, y, z, itemStack) {
				@Override
				public boolean fireImmune() {
					return true;
				}
			};
		} else {
			result = new ItemEntity(level, x, y, z, itemStack);
		}

		dragonSurvival$player = null;
		callback.setReturnValue(result);
	}
}