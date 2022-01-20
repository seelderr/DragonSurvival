package by.jackraidenph.dragonsurvival.common.entity.creatures;

import by.jackraidenph.dragonsurvival.common.DragonEffects;
import by.jackraidenph.dragonsurvival.common.entity.goals.FollowMobGoal;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.util.Functions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public abstract class Hunter extends PathfinderMob implements DragonHunter {
    public Hunter(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }
    
    protected int getExperienceReward(Player p_70693_1_) {
        return 5 + this.level.random.nextInt(5);
    }
    
    @Override
    protected void registerGoals() {
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1));
        goalSelector.addGoal(8, new FollowMobGoal<>(KnightEntity.class, this, 15));

        targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, 0, true, true, livingEntity ->
                (livingEntity.hasEffect(MobEffects.BAD_OMEN) || livingEntity.hasEffect(DragonEffects.EVIL_DRAGON))));
        targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Monster.class, 0, true, true, livingEntity ->
                (livingEntity instanceof Mob && !(livingEntity instanceof DragonHunter))));
        targetSelector.addGoal(7, new HurtByTargetGoal(this, ShooterEntity.class).setAlertOthers());
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return !this.hasCustomName() && tickCount >= Functions.minutesToTicks(ConfigHandler.COMMON.hunterDespawnDelay.get());
    }

    public AbstractIllager.IllagerArmPose getArmPose() {
        return isAggressive() ? AbstractIllager.IllagerArmPose.ATTACKING : AbstractIllager.IllagerArmPose.NEUTRAL;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverWorld, DifficultyInstance difficultyInstance, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable
            CompoundTag nbt) {
        populateDefaultEquipmentSlots(difficultyInstance);
        return super.finalizeSpawn(serverWorld, difficultyInstance, spawnReason, entityData, nbt);
    }

    @Override
    public void tick() {
        updateSwingTime();
        super.tick();
    }
}
