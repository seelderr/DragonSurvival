package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin( Block.class )
public class MixinBlock{
	@Inject( at = @At( "HEAD" ), method = "dropResources(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V", cancellable = true )
	private static void dropResources(BlockState p_220054_0_, World p_220054_1_, BlockPos p_220054_2_,
		@Nullable
			TileEntity p_220054_3_, Entity entity, ItemStack p_220054_5_, CallbackInfo ci){
		if(!DragonUtils.isDragon(entity)){
			return;
		}
		DragonStateHandler handler = DragonStateProvider.getCap(entity).orElse(null);
		if(handler == null || handler.getType() != DragonType.CAVE){
			return;
		}

		if(p_220054_1_ instanceof ServerWorld){
			getDrops(p_220054_0_, (ServerWorld)p_220054_1_, p_220054_2_, p_220054_3_, entity, p_220054_5_).forEach((p_220057_2_) -> {
				if(!p_220054_1_.isClientSide && !p_220057_2_.isEmpty() && p_220054_1_.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !p_220054_1_.restoringBlockSnapshots){
					float f = 0.5F;
					double d0 = (double)(p_220054_1_.random.nextFloat() * 0.5F) + 0.25D;
					double d1 = (double)(p_220054_1_.random.nextFloat() * 0.5F) + 0.25D;
					double d2 = (double)(p_220054_1_.random.nextFloat() * 0.5F) + 0.25D;
					ItemEntity itementity = new ItemEntity(p_220054_1_, (double)p_220054_2_.getX() + d0, (double)p_220054_2_.getY() + d1, (double)p_220054_2_.getZ() + d2, p_220057_2_){
						@Override
						public boolean fireImmune(){
							return true;
						}
					};
					itementity.setDefaultPickUpDelay();
					p_220054_1_.addFreshEntity(itementity);
				}
			});
			p_220054_0_.spawnAfterBreak((ServerWorld)p_220054_1_, p_220054_2_, p_220054_5_);
		}

		ci.cancel();
	}

	@Shadow
	private static List<ItemStack> getDrops(BlockState p_220077_0_, ServerWorld p_220077_1_, BlockPos p_220077_2_,
		@Nullable
			TileEntity p_220077_3_,
		@Nullable
			Entity p_220077_4_, ItemStack p_220077_5_){
		throw new IllegalStateException("Mixin failed to shadow getDrops()");
	}
}