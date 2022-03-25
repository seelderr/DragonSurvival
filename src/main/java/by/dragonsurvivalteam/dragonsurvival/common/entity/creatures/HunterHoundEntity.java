package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.EffectInstance2;
import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.AlertExceptHunters;
import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.FollowMobGoal;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.passive.Wolf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.datasync.EntityDataAccessor;
import net.minecraft.network.datasync.EntityDataSerializers;
import net.minecraft.network.datasync.SynchedEntityData;
import net.minecraft.potion.MobEffectInstance;
import net.minecraft.potion.MobEffects;
import net.minecraft.world.DifficultyInstance;

import javax.annotation.Nullable;

public class HunterHound extends Wolf implements DragonHunter{
	public static final EntityDataAccessor<Integer> variety = SynchedEntityData.defineId(HunterHound.class, EntityDataSerializers.INT);

	public HunterHound(EntityType<? extends Wolf> type, Level world){
		super(type, world);
	}

	protected void registerGoals(){
		super.registerGoals();
		this.goalSelector.availableGoals.removeIf(prioritizedGoal -> {
			Goal goal = prioritizedGoal.getGoal();
			return (goal instanceof net.minecraft.entity.ai.goal.SitGoal || goal instanceof net.minecraft.entity.ai.goal.FollowOwnerGoal || goal instanceof net.minecraft.entity.ai.goal.BreedGoal || goal instanceof net.minecraft.entity.ai.goal.BegGoal);
		});
		this.targetSelector.availableGoals.removeIf(prioritizedGoal -> {
			Goal goal = prioritizedGoal.getGoal();
			return (goal instanceof NearestAttackableTargetGoal || goal instanceof net.minecraft.entity.ai.goal.OwnerHurtByTargetGoal || goal instanceof HurtByTargetGoal);
		});
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, 0, true, false, living -> (living.hasEffect(MobEffects.BAD_OMEN) || living.hasEffect(DragonEffects.EVIL_DRAGON))));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Monster.class, 0, true, false, living -> (living instanceof net.minecraft.entity.monster.IMob && !(living instanceof DragonHunter))));
		targetSelector.addGoal(4, new HurtByTargetGoal(this, Shooter.class).setAlertOthers());
		this.goalSelector.addGoal(7, new FollowMobGoal<>(Knight.class, this, 15));
		this.goalSelector.addGoal(8, new AlertExceptHunters(this, Knight.class, Shooter.class, Squire.class));
	}

	protected void defineSynchedData(){
		super.defineSynchedData();
		this.entityData.define(variety, 0);
	}

	public void addAdditionalSaveData(CompoundTag compoundNBT){
		super.addAdditionalSaveData(compoundNBT);
		compoundNBT.putInt("Variety", this.entityData.get(variety));
	}

	public void readAdditionalSaveData(CompoundTag compoundNBT){
		super.readAdditionalSaveData(compoundNBT);
		this.entityData.set(variety, compoundNBT.getInt("Variety"));
	}

	public boolean doHurtTarget(Entity entity){
		if(ConfigHandler.COMMON.houndDoesSlowdown.get() && entity instanceof LivingEntity){
			if(((LivingEntity)entity).hasEffect(MobEffects.MOVEMENT_SLOWDOWN)){
				((LivingEntity)entity).addEffect(new EffectInstance2(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
			}else{
				((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200));
			}
		}
		return super.doHurtTarget(entity);
	}

	public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverWorld, DifficultyInstance difficultyInstance, MobSpawnType reason,
		@Nullable
			SpawnGroupData livingEntityData,
		@Nullable
			CompoundTag compoundNBT){
		this.entityData.set(variety, this.random.nextInt(8));
		return super.finalizeSpawn(serverWorld, difficultyInstance, reason, livingEntityData, compoundNBT);
	}

	@Override
	public boolean removeWhenFarAway(double distance){
		return !this.hasCustomName() && tickCount >= Functions.minutesToTicks(ConfigHandler.COMMON.hunterDespawnDelay.get());
	}

	protected int getExperienceReward(Player p_70693_1_){
		return 1 + this.level.random.nextInt(2);
	}
}