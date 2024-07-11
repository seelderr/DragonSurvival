package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.client.render.util.AnimationTimer;
import by.dragonsurvivalteam.dragonsurvival.client.render.util.RandomAnimationPicker;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.DragonSpikeEntity;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.util.AnimationUtils;
import by.dragonsurvivalteam.dragonsurvival.util.SpawningUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import java.util.List;

public class AmbusherEntity extends Hunter implements RangedAttackMob {

    private boolean isRandomIdleAnimSet = false;
    private boolean hasPlayedReleaseAnimation = false;
    private boolean hasPlayedReinforcementsAnimation = false;
    private float nextArrowVelocity = 0.0f;
    private RawAnimation currentIdleAnim;
    private final AnimationTimer ambusherTimer = new AnimationTimer();

    private static final EntityDataAccessor<Boolean> HAS_RELEASED_GRIFFIN = SynchedEntityData.defineId(AmbusherEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> NEARBY_DRAGON_PLAYER = SynchedEntityData.defineId(AmbusherEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_CALLED_REINFORCEMENTS = SynchedEntityData.defineId(AmbusherEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_SUMMONED_REINFORCEMENTS = SynchedEntityData.defineId(AmbusherEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> RANGED_ATTACK_TIMER = SynchedEntityData.defineId(AmbusherEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> AMBUSH_HORN_AND_RELOAD_TIMER = SynchedEntityData.defineId(AmbusherEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> GRIFFIN_RELEASE_RELOAD_TIMER = SynchedEntityData.defineId(AmbusherEntity.class, EntityDataSerializers.INT);

    public AmbusherEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1, ServerConfig.ambusherAttackInterval, 5.f));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8, 1.0f));
    }

    @Override
    public void tick() {
        super.tick();
        if(isAggro() && !hasReleasedGriffin()) {
            if(!this.level().isClientSide()) {
                setHasReleasedGriffin(true);
                summonGriffin();
                if(this.getTarget().hasEffect(DSEffects.ROYAL_CHASE)) {
                    beginSummonReinforcements();
                } else {
                    beginGriffinReleaseReloadTimer();
                }
            }
        }

        if(!this.level().isClientSide()) {
            if(getRangedAttackTimer() == CROSSBOW_ATTACK_START_TIME) {
                fireArrow();
            }
            if(getRangedAttackTimer() == CROSSBOW_RELOAD_CHARGE_SOUND_TIME) {
                this.playSound(SoundEvents.CROSSBOW_LOADING_MIDDLE.value(), 1.0F, 1.0F);
            }

            if(getRangedAttackTimer() == CROSSBOW_RELOAD_ARROW_PLACE_SOUND_TIME) {
                this.playSound(SoundEvents.CROSSBOW_LOADING_END.value(), 1.0F, 1.0F);
            }

            if(getRangedAttackTimer() < ServerConfig.ambusherAttackInterval && getRangedAttackTimer() >= 0){
                setRangedAttackTimer(getRangedAttackTimer() + 1);
            } else {
                setRangedAttackTimer(-1);
            }

            if(getAmbushHornTimer() == AMBUSH_HORN_SOUND_START_TIME) {
                this.playSound(SoundEvents.GOAT_HORN_SOUND_VARIANTS.getFirst().value(), 1.0F, 1.0F);
            }

            if(getAmbushHornTimer() == AMBUSH_ARROW_PLACE_SOUND_TIME) {
                this.playSound(SoundEvents.CROSSBOW_LOADING_END.value(), 1.0F, 1.0F);
            }

            if(getAmbushHornTimer() < AMBUSH_ANIM_DURATION && getAmbushHornTimer() >= 0) {
                setAmbushHornTimer(getAmbushHornTimer() + 1);
            } else {
                if(hasCalledReinforcements() && !hasSummonedReinforcements()) {
                    summonReinforcements();
                }
                setAmbushHornTimer(-1);
            }

            if(getGriffinReleaseReloadTimer() < GRIFFIN_RELEASE_ANIM_DURATION && getGriffinReleaseReloadTimer() >= 0) {
                setGriffinReleaseReloadTimer(getGriffinReleaseReloadTimer() + 1);
            } else {
                setGriffinReleaseReloadTimer(-1);
            }

            setNearbyDragonPlayer(isNearbyDragonPlayer());
        }
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity pTarget, float pVelocity) {
        if(getGriffinReleaseReloadTimer() == -1 && getAmbushHornTimer() == -1) {
            setRangedAttackTimer(0);
            nextArrowVelocity = pVelocity;
        }
    }

    private void fireArrow() {
        CrossbowItem tempCrossbowitem = (CrossbowItem)Items.CROSSBOW;
        ItemStack tempCrossbowItemStack = new ItemStack(tempCrossbowitem, 1);
        CrossbowItem.tryLoadProjectiles(this, tempCrossbowItemStack);
        tempCrossbowitem.setDamage(tempCrossbowItemStack, ServerConfig.ambusherDamage);
        tempCrossbowitem.performShooting(this.level(), this, InteractionHand.MAIN_HAND, tempCrossbowItemStack, nextArrowVelocity, 1.0f, this.getTarget());
    }

    @Override
    public @NotNull ItemStack getProjectile(@NotNull ItemStack pWeaponStack) {
        return net.neoforged.neoforge.common.CommonHooks.getProjectile(this, pWeaponStack, new ItemStack(Items.ARROW, 1));
    }

    private void beginSummonReinforcements() {
        setHasCalledReinforcements(true);
        setAmbushHornTimer(0);
    }

    private void beginGriffinReleaseReloadTimer() {
        setGriffinReleaseReloadTimer(0);
    }

    private void summonReinforcements() {
        for(int i = 0; i < ServerConfig.ambusherReinforcementCount; i++) {
            Mob mob = DSEntities.HUNTER_SPEARMAN.get().create(this.level());
            SpawningUtils.spawn(mob, this.position(), this.level(), MobSpawnType.MOB_SUMMONED, 20, 3.0f, true);
            mob.setTarget(this.getTarget());
        }

        Mob mob = DSEntities.HUNTER_KNIGHT.get().create(this.level());
        SpawningUtils.spawn(mob, this.position(), this.level(), MobSpawnType.MOB_SUMMONED, 20, 3.0f, true);
        mob.setTarget(this.getTarget());

        setHasSummonedReinforcements(true);
    }

    private void summonGriffin() {
        // Summon a griffin
    }

    private boolean isNearbyDragonPlayer() {
        double detectionRadius = 8.0;
        List<Player> players = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(detectionRadius));

        for (Player player : players) {
            if (DragonStateProvider.isDragon(player)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(HAS_RELEASED_GRIFFIN, false);
        pBuilder.define(NEARBY_DRAGON_PLAYER, false);
        pBuilder.define(HAS_CALLED_REINFORCEMENTS, false);
        pBuilder.define(HAS_SUMMONED_REINFORCEMENTS, false);
        pBuilder.define(RANGED_ATTACK_TIMER, -1);
        pBuilder.define(AMBUSH_HORN_AND_RELOAD_TIMER, -1);
        pBuilder.define(GRIFFIN_RELEASE_RELOAD_TIMER, -1);
    }

    public boolean hasReleasedGriffin() {
        return this.entityData.get(HAS_RELEASED_GRIFFIN);
    }

    public void setHasReleasedGriffin(boolean hasReleasedGriffin) {
        this.entityData.set(HAS_RELEASED_GRIFFIN, hasReleasedGriffin);
    }

    public int getRangedAttackTimer() {
        return this.entityData.get(RANGED_ATTACK_TIMER);
    }

    public void setRangedAttackTimer(int rangedAttackTimer) {
        this.entityData.set(RANGED_ATTACK_TIMER, rangedAttackTimer);
    }

    public int getAmbushHornTimer() {
        return this.entityData.get(AMBUSH_HORN_AND_RELOAD_TIMER);
    }

    public void setAmbushHornTimer(int ambushHornTimer) {
        this.entityData.set(AMBUSH_HORN_AND_RELOAD_TIMER, ambushHornTimer);
    }

    public int getGriffinReleaseReloadTimer() {
        return this.entityData.get(GRIFFIN_RELEASE_RELOAD_TIMER);
    }

    public void setGriffinReleaseReloadTimer(int griffinReleaseReloadTimer) {
        this.entityData.set(GRIFFIN_RELEASE_RELOAD_TIMER, griffinReleaseReloadTimer);
    }

    public boolean hasCalledReinforcements() {
        return this.entityData.get(HAS_CALLED_REINFORCEMENTS);
    }

    public void setHasCalledReinforcements(boolean hasCalledReinforcements) {
        this.entityData.set(HAS_CALLED_REINFORCEMENTS, hasCalledReinforcements);
    }

    public boolean hasNearbyDragonPlayer() {
        return this.entityData.get(NEARBY_DRAGON_PLAYER);
    }

    public void setNearbyDragonPlayer(boolean nearbyDragonPlayer) {
        this.entityData.set(NEARBY_DRAGON_PLAYER, nearbyDragonPlayer);
    }

    public boolean hasSummonedReinforcements() {
        return this.entityData.get(HAS_SUMMONED_REINFORCEMENTS);
    }

    public void setHasSummonedReinforcements(boolean hasSummonedReinforcements) {
        this.entityData.set(HAS_SUMMONED_REINFORCEMENTS, hasSummonedReinforcements);
    }

    @Override
    public double getRunThreshold() {
        return 0.15;
    }

    @Override
    public double getWalkThreshold() {
        return 0.01;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "everything", 3, this::fullPredicate));
        controllers.add(new AnimationController<>(this, "arms", 3, this::armsPredicate));
    }

    public boolean isIdle() {
        double movement = AnimationUtils.getMovementSpeed(this);
        return !(swingTime > 0 || movement > getWalkThreshold());
    }

    public PlayState fullPredicate(final AnimationState<Hunter> state) {
        double movement = AnimationUtils.getMovementSpeed(this);
        boolean isCurrentlyIdlingRandomly = false;
        if(hasReleasedGriffin() && !hasPlayedReleaseAnimation && !hasPlayedReinforcementsAnimation) {
            if(hasCalledReinforcements()) {
                hasPlayedReinforcementsAnimation = true;
                ambusherTimer.putAnimation(AMBUSH_AND_GRIFFIN_RELEASE, (double) AMBUSH_ANIM_DURATION);
                state.setAndContinue(AMBUSH_AND_GRIFFIN_RELEASE);
            } else {
                hasPlayedReleaseAnimation = true;
                ambusherTimer.putAnimation(ONLY_GRIFFIN_RELEASE, (double) GRIFFIN_RELEASE_ANIM_DURATION);
                state.setAndContinue(ONLY_GRIFFIN_RELEASE);
            }
        } else if (ambusherTimer.getDuration(ONLY_GRIFFIN_RELEASE) > 0 || ambusherTimer.getDuration(AMBUSH_AND_GRIFFIN_RELEASE) > 0) {
            // Let release animation conclude
            return PlayState.CONTINUE;
        } else {
            if (movement > getRunThreshold()) {
                if (hasReleasedGriffin()) {
                    state.setAndContinue(RUN_NO_GRIFFIN);
                } else {
                    state.setAndContinue(RUN);
                }
            }
            else if (movement > getWalkThreshold()) {
                if(hasReleasedGriffin()) {
                    state.setAndContinue(WALK_NO_GRIFFIN);
                } else {
                    state.setAndContinue(WALK);
                }
            } else {
                if(hasReleasedGriffin()) {
                    state.setAndContinue(IDLE_NO_GRIFFIN);
                } else {
                    if(hasNearbyDragonPlayer()) {
                        state.setAndContinue(IDLE_AGGRESSIVE);
                    } else {
                        isCurrentlyIdlingRandomly = true;
                        state.setAndContinue(getIdleAnim());
                    }
                }
            }
        }

        if(!isCurrentlyIdlingRandomly && isRandomIdleAnimSet) {
            isRandomIdleAnimSet = false;
        }

        return PlayState.CONTINUE;
    }

    public PlayState armsPredicate(final AnimationState<Hunter> state) {
        if (hasReleasedGriffin() && getGriffinReleaseReloadTimer() == -1 && getAmbushHornTimer() == -1) {
            // We check at 1 because the first client tick already sees the value incremented by 1 (we start at 0)
            if(getRangedAttackTimer() == 1) {
                ambusherTimer.putAnimation(CROSSBOW_SHOOT_AND_RELOAD_BLEND, (double) CROSSBOW_SHOOT_AND_RELOAD_TIME);
                return state.setAndContinue(CROSSBOW_SHOOT_AND_RELOAD_BLEND);
            } else if(ambusherTimer.getDuration(CROSSBOW_SHOOT_AND_RELOAD_BLEND) > 0) {
                // Always let the reload animation conclude
                return PlayState.CONTINUE;
            } else {
                return state.setAndContinue(CROSSBOW_READY_BLEND);
            }
        }

        return PlayState.STOP;
    }

    public RawAnimation getIdleAnim() {
        if (!isRandomIdleAnimSet) {
            currentIdleAnim = IDLE_ANIMS.pickRandomAnimation();
            isRandomIdleAnimSet = true;
        }
        return currentIdleAnim;
    }

    private static final RandomAnimationPicker IDLE_ANIMS = new RandomAnimationPicker(
            new RandomAnimationPicker.WeightedAnimation(RawAnimation.begin().thenLoop("idle1"), 90),
            new RandomAnimationPicker.WeightedAnimation(RawAnimation.begin().thenLoop("idle2"), 10)
    );

    private static final RawAnimation IDLE_AGGRESSIVE = RawAnimation.begin().thenLoop("idle_agressive_griffin");

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");

    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("swim");

    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("run");

    private static final int AMBUSH_ANIM_DURATION = 83;
    private static final int AMBUSH_HORN_SOUND_START_TIME = 38;
    private static final int AMBUSH_ARROW_PLACE_SOUND_TIME = 74;
    private static final RawAnimation AMBUSH_AND_GRIFFIN_RELEASE = RawAnimation.begin().thenPlay("ambush_and_griffin_release");

    private static final int GRIFFIN_RELEASE_ANIM_DURATION = 42;
    private static final int GRIFFIN_RELEASE_ARROW_PLACE_SOUND_TIME = 33;
    private static final RawAnimation ONLY_GRIFFIN_RELEASE = RawAnimation.begin().thenPlay("griffin_release");

    private static final RawAnimation IDLE_NO_GRIFFIN = RawAnimation.begin().thenLoop("idle_no_griffin");

    private static final RawAnimation WALK_NO_GRIFFIN = RawAnimation.begin().thenLoop("walk_no_griffin");

    private static final RawAnimation RUN_NO_GRIFFIN = RawAnimation.begin().thenLoop("run_no_griffin");

    private static final RawAnimation SWIM_NO_GRIFFIN = RawAnimation.begin().thenLoop("swim_no_griffin");

    private static final RawAnimation CROSSBOW_READY_BLEND = RawAnimation.begin().thenLoop("blend_crossbow_ready");

    public static final int CROSSBOW_SHOOT_AND_RELOAD_TIME = 60;
    // The ambusher shoots his crossbow 4 ticks into the animation
    private static final int CROSSBOW_ATTACK_START_TIME = 4;
    private static final int CROSSBOW_RELOAD_CHARGE_SOUND_TIME = 25;
    private static final int CROSSBOW_RELOAD_ARROW_PLACE_SOUND_TIME = 49;
    private static final RawAnimation CROSSBOW_SHOOT_AND_RELOAD_BLEND = RawAnimation.begin().thenPlay("blend_crossbow_shoot_and_reloading");
}
