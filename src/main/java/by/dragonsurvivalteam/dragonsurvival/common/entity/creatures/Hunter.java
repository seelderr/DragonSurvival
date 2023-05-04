package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public abstract class Hunter extends PathfinderMob implements DragonHunter{
	public Hunter(EntityType<? extends PathfinderMob> entityType, Level world){
		super(entityType, world);
	}

	@Override
	public int getExperienceReward(){
		return 5 + level.random.nextInt(5);
	}

	@Override
	public void tick(){
		updateSwingTime();
		super.tick();
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverWorld, DifficultyInstance difficultyInstance, MobSpawnType spawnReason,
		@Nullable
			SpawnGroupData entityData,
		@Nullable
			CompoundTag nbt){
		populateDefaultEquipmentSlots(random, difficultyInstance);
		return super.finalizeSpawn(serverWorld, difficultyInstance, spawnReason, entityData, nbt);
	}

	public AbstractIllager.IllagerArmPose getArmPose(){
		return isAggressive() ? AbstractIllager.IllagerArmPose.ATTACKING : AbstractIllager.IllagerArmPose.NEUTRAL;
	}
}