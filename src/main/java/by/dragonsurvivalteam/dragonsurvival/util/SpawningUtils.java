package by.dragonsurvivalteam.dragonsurvival.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;

public class SpawningUtils
{
	// TODO: This used to have more conditions, and now it doesn't. Might lead to too many spawns?
	@Nullable
	public static BlockPos findRandomSpawnPosition(Player player, int p_221298_1_, int timesToCheck, float distance){
		int i = p_221298_1_ == 0 ? 2 : 2 - p_221298_1_;
		BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

		for(int i1 = 0; i1 < timesToCheck; i1++){
			float f = player.getRandom().nextFloat() * 6.2831855F;
			double xRandom = player.getX() + Mth.floor(Mth.cos(f) * distance * i) + player.getRandom().nextInt(5);
			double zRandom = player.getZ() + Mth.floor(Mth.sin(f) * distance * i) + player.getRandom().nextInt(5);
			int y = player.level().getHeight(Heightmap.Types.WORLD_SURFACE, (int)xRandom, (int)zRandom);
			blockpos$mutable.set(xRandom, y, zRandom);
			if(player.level().hasChunksAt(blockpos$mutable.getX() - 10, blockpos$mutable.getY() - 10, blockpos$mutable.getZ() - 10, blockpos$mutable.getX() + 10, blockpos$mutable.getY() + 10, blockpos$mutable.getZ() + 10))
				return blockpos$mutable;
		}
		return null;
	}
	
	public static void spawn(Mob mob, BlockPos blockPos, ServerLevel serverWorld){
		mob.setPos(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
		EventHooks.finalizeMobSpawn(mob, serverWorld, serverWorld.getCurrentDifficultyAt(blockPos), MobSpawnType.NATURAL, null);
		serverWorld.addFreshEntity(mob);
	}
	
	public static boolean isAirOrFluid(BlockPos blockPos, Level world, Player player, BlockHitResult blockHitResult){
		return isAirOrFluid(blockPos, world, new BlockPlaceContext(player, InteractionHand.MAIN_HAND, player.getMainHandItem(), blockHitResult));
	}
	
	public static boolean isAirOrFluid(BlockPos blockPos, Level world, BlockPlaceContext context){
		return !world.getFluidState(blockPos).isEmpty() || world.isEmptyBlock(blockPos) || world.getBlockState(blockPos).canBeReplaced(context);
	}
}