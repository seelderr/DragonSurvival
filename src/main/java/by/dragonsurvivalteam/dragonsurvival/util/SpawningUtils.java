package by.dragonsurvivalteam.dragonsurvival.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public class SpawningUtils
{
	private static BlockPos findRandomSpawnPosition(Level level, Vec3 worldPos, int spawnAttempts, float radius) {
		BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
		for(int i = 0; i < spawnAttempts; i++) {
			float f = level.random.nextFloat() * Mth.TWO_PI;
			double x = worldPos.x + Mth.floor(Mth.cos(f) * radius);
			double z = worldPos.z + Mth.floor(Mth.sin(f) * radius);
			int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, (int)x, (int)z);
			blockPos.set(x, y, z);
			if(level.hasChunksAt(blockPos.getX() - 10, blockPos.getY() - 10, blockPos.getZ() - 10, blockPos.getX() + 10, blockPos.getY() + 10, blockPos.getZ() + 10))
				return blockPos;
		}

		return null;
	}

	public static boolean spawn(Mob mob, Vec3 worldPos, Level level, MobSpawnType type, int spawnAttempts, float radius, boolean useSpawnParticles){
		assert level instanceof ServerLevel;

		BlockPos blockPos = findRandomSpawnPosition(level, worldPos, spawnAttempts, radius);
		if(blockPos != null) {
			mob.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
			EventHooks.finalizeMobSpawn(mob, (ServerLevel)level, level.getCurrentDifficultyAt(blockPos), type, null);
			level.addFreshEntity(mob);
			if(useSpawnParticles) {
				mob.spawnAnim();
			}

			return true;
		}

		return false;
	}
	
	public static boolean isAirOrFluid(BlockPos blockPos, Level world, BlockPlaceContext context){
		return !world.getFluidState(blockPos).isEmpty() || world.isEmptyBlock(blockPos) || world.getBlockState(blockPos).canBeReplaced(context);
	}
}