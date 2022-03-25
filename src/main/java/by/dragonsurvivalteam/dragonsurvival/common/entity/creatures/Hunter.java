package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.FollowMobGoal;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Mob;
import net.minecraft.entity.MobSpawnType;
import net.minecraft.entity.SpawnGroupData;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.Monster;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.MobEffects;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public abstract class Hunter extends Mob implements DragonHunter{
	public Hunter(EntityType<? extends Mob> entityType, Level world){
		super(entityType, world);
	}

	@Override
	protected void registerGoals(){
		goalSelector.addGoal(0, new SwimGoal(this));
		goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1));
		goalSelector.addGoal(8, new FollowMobGoal<>(Knight.class, this, 15));

		targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, 0, true, true, living -> (living.hasEffect(MobEffects.BAD_OMEN) || living.hasEffect(DragonEffects.EVIL_DRAGON))));
		targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Monster.class, 0, true, true, living -> (living instanceof net.minecraft.entity.monster.IMob && !(living instanceof DragonHunter))));
		targetSelector.addGoal(7, new HurtByTargetGoal(this, Shooter.class).setAlertOthers());
	}

	protected int getExperienceReward(Player p_70693_1_){
		return 5 + this.level.random.nextInt(5);
	}

	@Override
	public void tick(){
		updateSwingTime();
		super.tick();
	}

	@Override
	public boolean removeWhenFarAway(double distance){
		return !this.hasCustomName() && tickCount >= Functions.minutesToTicks(ConfigHandler.COMMON.hunterDespawnDelay.get());
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverWorld, DifficultyInstance difficultyInstance, MobSpawnType spawnReason,
		@Nullable
			SpawnGroupData entityData,
		@Nullable
			CompoundTag nbt){
		populateDefaultEquipmentSlots(difficultyInstance);
		return super.finalizeSpawn(serverWorld, difficultyInstance, spawnReason, entityData, nbt);
	}

	public AbstractIllager.ArmPose getArmPose(){
		return isAggressive() ? AbstractIllager.ArmPose.ATTACKING : AbstractIllager.ArmPose.NEUTRAL;
	}
}