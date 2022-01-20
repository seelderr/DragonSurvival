package by.jackraidenph.dragonsurvival.common.entity.creatures;

import by.jackraidenph.dragonsurvival.common.DragonEffects;
import by.jackraidenph.dragonsurvival.common.EffectInstance2;
import by.jackraidenph.dragonsurvival.common.entity.goals.AlertExceptHunters;
import by.jackraidenph.dragonsurvival.common.entity.goals.FollowMobGoal;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.util.Functions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.BegGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public class HunterHoundEntity extends Wolf implements DragonHunter
{
    public static final EntityDataAccessor<Integer> variety = SynchedEntityData.defineId(HunterHoundEntity.class, EntityDataSerializers.INT);

    public HunterHoundEntity(EntityType<? extends Wolf> type, Level world) {
        super(type, world);
    }
    
    protected int getExperienceReward(Player p_70693_1_) {
        return 1 + this.level.random.nextInt(2);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.getAvailableGoals().removeIf(prioritizedGoal -> {
            Goal goal = prioritizedGoal.getGoal();
            return (goal instanceof FollowOwnerGoal || goal instanceof BreedGoal || goal instanceof BegGoal);
        });
        this.targetSelector.getAvailableGoals().removeIf(prioritizedGoal -> {
            Goal goal = prioritizedGoal.getGoal();
            return (goal instanceof NearestAttackableTargetGoal || goal instanceof OwnerHurtByTargetGoal || goal instanceof HurtByTargetGoal);
        });
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, 0, true, false, livingEntity ->
                (livingEntity.hasEffect(MobEffects.BAD_OMEN) || livingEntity.hasEffect(DragonEffects.EVIL_DRAGON))));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Monster.class, 0, true, false, livingEntity ->
                (livingEntity instanceof Mob && !(livingEntity instanceof DragonHunter))));
        targetSelector.addGoal(4, new HurtByTargetGoal(this, ShooterEntity.class).setAlertOthers());
        this.goalSelector.addGoal(7, new FollowMobGoal<>(KnightEntity.class, this, 15));
        this.goalSelector.addGoal(8, new AlertExceptHunters(this, KnightEntity.class, ShooterEntity.class, SquireEntity.class));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(variety, 0);
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverWorld, DifficultyInstance difficultyInstance, MobSpawnType reason, @Nullable SpawnGroupData livingEntityData, @Nullable CompoundTag compoundNBT) {
        this.entityData.set(variety, this.random.nextInt(8));
        return super.finalizeSpawn(serverWorld, difficultyInstance, reason, livingEntityData, compoundNBT);
    }

    public void addAdditionalSaveData(CompoundTag compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putInt("Variety", this.entityData.get(variety));
    }

    public void readAdditionalSaveData(CompoundTag compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        this.entityData.set(variety, compoundNBT.getInt("Variety"));
    }

    public boolean doHurtTarget(Entity entity) {
        if (ConfigHandler.COMMON.houndDoesSlowdown.get() && entity instanceof LivingEntity) {
            if (((LivingEntity) entity).hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                ((LivingEntity) entity).addEffect(new EffectInstance2(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
            } else {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200));
            }
        }
        return super.doHurtTarget(entity);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return !this.hasCustomName() && tickCount >= Functions.minutesToTicks(ConfigHandler.COMMON.hunterDespawnDelay.get());
    }
}