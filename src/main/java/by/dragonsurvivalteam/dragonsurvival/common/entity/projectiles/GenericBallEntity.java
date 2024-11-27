package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.registry.projectile.ProjectileInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.block_effects.ProjectileBlockEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.entity_effects.ProjectileEntityEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.targeting.ProjectileTargeting;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;


public class GenericBallEntity extends AbstractHurtingProjectile implements GeoEntity {
    public static final EntityDataAccessor<Integer> LIFESPAN = SynchedEntityData.defineId(GenericBallEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> MOVE_DISTANCE = SynchedEntityData.defineId(GenericBallEntity.class, EntityDataSerializers.FLOAT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final Component name;
    private final Optional<EntityPredicate> canHitPredicate;
    private final ProjectileInstance projectileInstance;
    private final List<ProjectileTargeting> tickingEffects;
    private final List<ProjectileTargeting> commonHitEffects;
    private final List<ProjectileEntityEffect> entityHitEffects;
    private final List<ProjectileBlockEffect> blockHitEffects;
    private final List<ProjectileTargeting> onDestroyEffects;
    private final int maxLingeringTicks;
    private final int maxMoveDistance;
    private final int maxLifespan;
    private final ParticleOptions trailParticle;

    public boolean hasHit = false;
    protected boolean isLingering = false;
    protected int lingerTicks;

    public GenericBallEntity(
            Component name,
            ParticleOptions trailParticle,
            Level level,
            Optional<EntityPredicate> canHitPredicate,
            List<ProjectileTargeting> tickingEffects,
            List<ProjectileTargeting> commonHitEffects,
            List<ProjectileEntityEffect> entityHitEffects,
            List<ProjectileBlockEffect> blockHitEffects,
            List<ProjectileTargeting> onDestroyEffects,
            ProjectileInstance projectileInstance,
            int maxLingeringTicks,
            int maxMoveDistance,
            int maxLifespan) {
        super(DSEntities.GENERIC_BALL_ENTITY.get(), level);
        this.name = name;
        this.canHitPredicate = canHitPredicate;
        this.trailParticle = trailParticle;
        this.tickingEffects = tickingEffects;
        this.commonHitEffects = commonHitEffects;
        this.entityHitEffects = entityHitEffects;
        this.blockHitEffects = blockHitEffects;
        this.onDestroyEffects = onDestroyEffects;
        this.projectileInstance = projectileInstance;
        this.maxLingeringTicks = maxLingeringTicks;
        this.lingerTicks = maxLingeringTicks;
        this.maxMoveDistance = maxMoveDistance;
        this.maxLifespan = maxLifespan;
    }

    public GenericBallEntity(EntityType<GenericBallEntity> genericBallEntityEntityType, Level level) {
        super(DSEntities.GENERIC_BALL_ENTITY.get(), level);
        this.name = Component.literal("Generic Ball");
        this.canHitPredicate = Optional.empty();
        this.trailParticle = ParticleTypes.FLAME;
        this.tickingEffects = List.of();
        this.commonHitEffects = List.of();
        this.entityHitEffects = List.of();
        this.blockHitEffects = List.of();
        this.onDestroyEffects = List.of();
        this.projectileInstance = null;
        this.maxLingeringTicks = 0;
        this.lingerTicks = 0;
        this.maxMoveDistance = 0;
        this.maxLifespan = 0;
    }

    protected @NotNull Component getTypeName() {
        return name;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return trailParticle;
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity target) {
        boolean canHit = super.canHitEntity(target);
        if(canHitPredicate.isPresent() && level() instanceof ServerLevel serverLevel){
            canHit = canHit && canHitPredicate.get().matches(serverLevel, position(), target);
        }
        return canHit;
    }

    protected void onDestroy() {
        if (level() instanceof ServerLevel serverLevel && getOwner() instanceof ServerPlayer player) {
            for (ProjectileTargeting effect : onDestroyEffects) {
                effect.apply(serverLevel, player, projectileInstance, position());
            }
        }
        this.discard();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(MOVE_DISTANCE, 0f);
        pBuilder.define(LIFESPAN, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide || (getOwner() == null || !getOwner().isRemoved()) && this.level().hasChunkAt(this.blockPosition())) {
            entityData.set(MOVE_DISTANCE, entityData.get(MOVE_DISTANCE) + (float) getDeltaMovement().length());
            entityData.set(LIFESPAN, entityData.get(LIFESPAN) + 1);
        }
        if (entityData.get(MOVE_DISTANCE) > maxMoveDistance || entityData.get(LIFESPAN) > maxLifespan) {
            // Call onHitBlock rather than onHit, since calling onHit using the helper function from
            // vanilla will result in HitResult.Miss from 1.20.6 onwards, causing nothing to happen
            this.onHitBlock(new BlockHitResult(this.position(), this.getDirection(), this.blockPosition(), false));
        }

        if (isLingering) {
            lingerTicks--;
            if (lingerTicks <= 0) {
                this.onDestroy();
            }
        }
    }


    @Override
    protected void onHitEntity(@NotNull EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (level() instanceof ServerLevel serverLevel && getOwner() instanceof ServerPlayer player) {
            for (ProjectileEntityEffect effect : entityHitEffects) {
                effect.apply(serverLevel, player, projectileInstance, hitResult.getEntity());
            }
        }

        onHitCommon();
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        if (level() instanceof ServerLevel serverLevel && getOwner() instanceof ServerPlayer player) {
            for (ProjectileBlockEffect effect : blockHitEffects) {
                effect.apply(serverLevel, player, projectileInstance, hitResult.getBlockPos());
            }
        }

        onHitCommon();
    }

    public void onHitCommon() {
        if (level() instanceof ServerLevel serverLevel && getOwner() instanceof ServerPlayer player) {
            for (ProjectileTargeting effect : commonHitEffects) {
                effect.apply(serverLevel, player, projectileInstance, position());
            }
        }

        if ((getOwner() == null || !getOwner().isRemoved()) && this.level().hasChunkAt(this.blockPosition()) && !hasHit) {
            if (!isLingering) {
                isLingering = true;
                // These power variables drive the movement of the entity in the parent tick() function, so we need to zero them out as well.
                accelerationPower = 0;
                setDeltaMovement(Vec3.ZERO);
            }
        }

        hasHit = true;

        if(this.maxLingeringTicks <= 0)
            this.discard();
    }

    public PlayState predicate(final AnimationState<GenericBallEntity> state) {

        if (!isLingering && maxLingeringTicks > 0) {
            state.getController().setAnimation(FLY);
            return PlayState.CONTINUE;
        } else if (lingerTicks < 16 && maxLingeringTicks > 0) {
            state.getController().setAnimation(EXPLOSION);
        } else {
            state.getController().setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "everything", this::predicate));
    }

    // We don't want these entities to slow down
    @Override
    protected float getInertia() {
        return 1.0F;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private static final RawAnimation EXPLOSION = RawAnimation.begin().thenLoop("explosion");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
}
