package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import javax.annotation.Nullable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public abstract class Hunter extends PathfinderMob implements DragonHunter{
	public Hunter(EntityType<? extends PathfinderMob> entityType, Level world){
		super(entityType, world);
	}

	@Override
	public void tick(){
		updateSwingTime();
		super.tick();
	}

	@Nullable @Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pSpawnType, @Nullable SpawnGroupData pSpawnGroupData){
		populateDefaultEquipmentSlots(random, pDifficulty);
		return super.finalizeSpawn(pLevel, pDifficulty, pSpawnType, pSpawnGroupData);
	}

	public AbstractIllager.IllagerArmPose getArmPose(){
		return isAggressive() ? AbstractIllager.IllagerArmPose.ATTACKING : AbstractIllager.IllagerArmPose.NEUTRAL;
	}
}