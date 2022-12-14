package by.dragonsurvivalteam.dragonsurvival.util;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.function.Function;

public class Functions{
	public static int minutesToTicks(int minutes){
		return secondsToTicks(minutes) * 60;
	}

	public static int secondsToTicks(int seconds){
		return seconds * 20;
	}

	public static int ticksToMinutes(int ticks){
		return ticksToSeconds(ticks) / 60;
	}

	public static int ticksToSeconds(int ticks){
		return ticks / 20;
	}

	public static float angleDifference(float angle1, float angle2){
		float phi = Math.abs(angle1 - angle2) % 360;
		float dif = phi > 180 ? 360 - phi : phi;
		int sign = angle1 - angle2 >= 0 && angle1 - angle2 <= 180 || angle1 - angle2 <= -180 && angle1 - angle2 >= -360 ? 1 : -1;
		dif *= sign;
		return dif;
	}

	@Nullable
	public static BlockPos findRandomSpawnPosition(Player player, int p_221298_1_, int timesToCheck, float distance){
		int i = p_221298_1_ == 0 ? 2 : 2 - p_221298_1_;
		MutableBlockPos blockpos$mutable = new MutableBlockPos();

		for(int i1 = 0; i1 < timesToCheck; i1++){
			float f = player.level.random.nextFloat() * 6.2831855F;
			double xRandom = player.getX() + Mth.floor(Mth.cos(f) * distance * i) + player.level.random.nextInt(5);
			double zRandom = player.getZ() + Mth.floor(Mth.sin(f) * distance * i) + player.level.random.nextInt(5);
			int y = player.level.getHeight(Types.WORLD_SURFACE, (int)xRandom, (int)zRandom);
			blockpos$mutable.set(xRandom, y, zRandom);
			if(player.level.hasChunksAt(blockpos$mutable.getX() - 10, blockpos$mutable.getY() - 10, blockpos$mutable.getZ() - 10, blockpos$mutable.getX() + 10, blockpos$mutable.getY() + 10, blockpos$mutable.getZ() + 10) && (NaturalSpawner.canSpawnAtBody(Type.ON_GROUND, player.level, blockpos$mutable, DSEntities.HUNTER_HOUND) || player.level.getBlockState(blockpos$mutable).is(Blocks.SNOW) && player.level.getBlockState(blockpos$mutable).isAir()))
				return blockpos$mutable;
		}
		return null;
	}

	public static void spawn(Mob mob, BlockPos blockPos, ServerLevel serverWorld){
		mob.setPos(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
		mob.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(blockPos), MobSpawnType.NATURAL, null, null);
		serverWorld.addFreshEntity(mob);
	}

	public static boolean isAirOrFluid(BlockPos blockPos, Level world, Player player, BlockHitResult blockHitResult){
		return isAirOrFluid(blockPos, world, new BlockPlaceContext(player, InteractionHand.MAIN_HAND, player.getMainHandItem(), blockHitResult));
	}

	public static boolean isAirOrFluid(BlockPos blockPos, Level world, BlockPlaceContext context){
		return !world.getFluidState(blockPos).isEmpty() || world.isEmptyBlock(blockPos) || world.getBlockState(blockPos).canBeReplaced(context);
	}


	public static boolean attackTargets(Entity attacker, Function<Entity, Boolean> action, Entity... entities){
		boolean valid = false;
		for(Entity entity : entities){
			if(isValidTarget(attacker, entity)){
				if(action.apply(entity)){
					valid = true;
				}
			}
		}

		return valid;
	}

	public static boolean isValidTarget(Entity attacker, Entity target){
		if(target == null || attacker == null){
			return false;
		}
		if(target == attacker){
			return false;
		}

		if(target instanceof FakePlayer){
			return false;
		}

		if(attacker instanceof Player attackerPlayer && target instanceof Player targetPlayer){
			if(!attackerPlayer.canHarmPlayer(targetPlayer)){
				return false;
			}
		}

		if(attacker.getTeam() != null){
			if(target.getTeam() != null && attacker.getTeam().getPlayers().contains(target.getScoreboardName())){
				if(!target.getTeam().isAllowFriendlyFire()){
					return false;
				}
			}
		}

		if(target instanceof TamableAnimal && ((TamableAnimal)target).getOwner() == attacker){
			return false;
		}

		return !(attacker instanceof TamableAnimal) || isValidTarget(((TamableAnimal)attacker).getOwner(), target);
	}

	public static ListTag newDoubleList(double... pNumbers) {
		ListTag listtag = new ListTag();

		for(double d0 : pNumbers) {
			listtag.add(DoubleTag.valueOf(d0));
		}

		return listtag;
	}

	/**
	 * Returns a new NBTTagList filled with the specified floats
	 */
	public static ListTag newFloatList(float... pNumbers) {
		ListTag listtag = new ListTag();

		for(float f : pNumbers) {
			listtag.add(FloatTag.valueOf(f));
		}

		return listtag;
	}
}