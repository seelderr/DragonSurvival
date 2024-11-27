package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.block_effects.ProjectileBlockEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.entity_effects.ProjectileEntityEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.targeting.ProjectileTargeting;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;


public class GenericBallEntity extends AbstractHurtingProjectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final EntityDataAccessor<Vector3f> DELTA_MOVEMENT = SynchedEntityData.defineId(GenericBallEntity.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<String> RES_LOCATION = SynchedEntityData.defineId(GenericBallEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Boolean> LINGERING = SynchedEntityData.defineId(GenericBallEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> DIMENSION_WIDTH = SynchedEntityData.defineId(GenericBallEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> DIMENSION_HEIGHT = SynchedEntityData.defineId(GenericBallEntity.class, EntityDataSerializers.FLOAT);

    private final Optional<EntityPredicate> canHitPredicate;
    private final int projectileLevel;
    private final List<ProjectileTargeting> tickingEffects;
    private final List<ProjectileTargeting> commonHitEffects;
    private final List<ProjectileEntityEffect> entityHitEffects;
    private final List<ProjectileBlockEffect> blockHitEffects;
    private final List<ProjectileTargeting> onDestroyEffects;
    private final int maxLingeringTicks;
    private final int maxMoveDistance;
    private final int maxLifespan;
    private final Optional<ParticleOptions> trailParticle;

    public boolean hasHit = false;
    protected int lingerTicks;
    private float moveDistance;
    private int lifespan;

    public GenericBallEntity(
            ResourceLocation location,
            Optional<ParticleOptions> trailParticle,
            Level level,
            EntityDimensions dimensions,
            Optional<EntityPredicate> canHitPredicate,
            List<ProjectileTargeting> tickingEffects,
            List<ProjectileTargeting> commonHitEffects,
            List<ProjectileEntityEffect> entityHitEffects,
            List<ProjectileBlockEffect> blockHitEffects,
            List<ProjectileTargeting> onDestroyEffects,
            int projectileLevel,
            int maxLingeringTicks,
            int maxMoveDistance,
            int maxLifespan) {
        super(DSEntities.GENERIC_BALL_ENTITY.get(), level);
        setResourceLocation(location);
        setDimensionWidth(dimensions.width());
        setDimensionHeight(dimensions.height());
        this.canHitPredicate = canHitPredicate;
        this.trailParticle = trailParticle;
        this.tickingEffects = tickingEffects;
        this.commonHitEffects = commonHitEffects;
        this.entityHitEffects = entityHitEffects;
        this.blockHitEffects = blockHitEffects;
        this.onDestroyEffects = onDestroyEffects;
        this.projectileLevel = projectileLevel;
        this.maxLingeringTicks = maxLingeringTicks;
        this.lingerTicks = maxLingeringTicks;
        this.maxMoveDistance = maxMoveDistance;
        this.maxLifespan = maxLifespan;
        this.lifespan = 0;
        this.moveDistance = 0;
        // TODO: Currently unused, but we could use acceleration in the future. Would just need to sync it.
        this.accelerationPower = 0;
    }

    public GenericBallEntity(EntityType<GenericBallEntity> genericBallEntityEntityType, Level level) {
        super(DSEntities.GENERIC_BALL_ENTITY.get(), level);
        this.canHitPredicate = Optional.empty();
        this.trailParticle = Optional.empty();
        this.tickingEffects = List.of();
        this.commonHitEffects = List.of();
        this.entityHitEffects = List.of();
        this.blockHitEffects = List.of();
        this.onDestroyEffects = List.of();
        this.projectileLevel = 0;
        this.maxLingeringTicks = 0;
        this.lingerTicks = 0;
        this.maxMoveDistance = 0;
        this.maxLifespan = 0;
        this.moveDistance = 0;
        this.lifespan = 0;
        // TODO: Currently unused, but we could use acceleration in the future. Would just need to sync it.
        this.accelerationPower = 0;
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key.equals(DIMENSION_WIDTH) || key.equals(DIMENSION_HEIGHT)) {
            this.refreshDimensions();
        }
    }

    private void setDimensionWidth(float width) {
        this.entityData.set(DIMENSION_WIDTH, width);
    }

    private void setDimensionHeight(float height) {
        this.entityData.set(DIMENSION_HEIGHT, height);
    }

    private EntityDimensions getDimensions() {
        return EntityDimensions.scalable(this.entityData.get(DIMENSION_WIDTH), this.entityData.get(DIMENSION_HEIGHT));
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return getDimensions();
    }

    private void setResourceLocation(ResourceLocation location) {
        this.entityData.set(RES_LOCATION, location.toString());
    }

    public ResourceLocation getResourceLocation() {
        return ResourceLocation.read(this.entityData.get(RES_LOCATION)).getOrThrow();
    }

    @Override
    public void setDeltaMovement(@NotNull Vec3 deltaMovement) {
        if(!level().isClientSide) {
            setSyncedDeltaMovement(deltaMovement);
        }
    }

    private Vec3 getSyncedDeltaMovement() {
        return new Vec3(this.entityData.get(DELTA_MOVEMENT));
    }

    private void setSyncedDeltaMovement(Vec3 deltaMovement) {
        this.entityData.set(DELTA_MOVEMENT, new Vector3f((float) deltaMovement.x, (float) deltaMovement.y, (float) deltaMovement.z));
    }

    private boolean isLingering() {
        return this.entityData.get(LINGERING);
    }

    private void setLingering(boolean lingering) {
        this.entityData.set(LINGERING, lingering);
    }

    @Override
    protected @NotNull Component getTypeName() {
        return Component.translatable(getResourceLocation().getNamespace() + Translation.Type.PROJECTILE.suffix + "." + getResourceLocation().getPath());
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return trailParticle.orElse(null);
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
        if (!level().isClientSide) {
            for (ProjectileTargeting effect : onDestroyEffects) {
                effect.apply(this, projectileLevel);
            }

            this.discard();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(DELTA_MOVEMENT, new Vector3f(0, 0, 0));
        pBuilder.define(RES_LOCATION, ResourceLocation.fromNamespaceAndPath(DragonSurvival.MODID, "generic_ball").toString());
        pBuilder.define(LINGERING, false);
        pBuilder.define(DIMENSION_WIDTH, 0.5F);
        pBuilder.define(DIMENSION_HEIGHT, 0.5F);
    }

    @Override
    public @NotNull Vec3 getDeltaMovement() {
        if (isLingering()) {
            return Vec3.ZERO;
        }

        return getSyncedDeltaMovement();
    }


    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            moveDistance += (float) getDeltaMovement().length();
            lifespan++;

            if (moveDistance > maxMoveDistance || lifespan > maxLifespan) {
                // Call onHitBlock rather than onHit, since calling onHit using the helper function from
                // vanilla will result in HitResult.Miss from 1.20.6 onwards, causing nothing to happen
                this.onHitBlock(new BlockHitResult(this.position(), this.getDirection(), this.blockPosition(), false));
            }

            for (ProjectileTargeting effect : tickingEffects) {
                effect.apply(this, projectileLevel);
            }

            if (isLingering()) {
                lingerTicks--;
                if (lingerTicks <= 0) {
                    this.onDestroy();
                }
            }
        }
    }


    @Override
    protected void onHitEntity(@NotNull EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (!level().isClientSide) {
            for (ProjectileEntityEffect effect : entityHitEffects) {
                effect.apply(this, hitResult.getEntity(), projectileLevel);
            }
        }

        onHitCommon();
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        if (!level().isClientSide) {
            for (ProjectileBlockEffect effect : blockHitEffects) {
                effect.apply(this, hitResult.getBlockPos(), projectileLevel);
            }
        }

        onHitCommon();
    }

    public void onHitCommon() {
        if (!level().isClientSide) {
            for (ProjectileTargeting effect : commonHitEffects) {
                effect.apply(this, projectileLevel);
            }

            if (!isLingering()) {
                setLingering(true);
                // These power variables drive the movement of the entity in the parent tick() function, so we need to zero them out as well.
                setDeltaMovement(Vec3.ZERO);
            }

            hasHit = true;

            if(this.maxLingeringTicks <= 0)
                this.discard();
        }
    }

    public PlayState predicate(final AnimationState<GenericBallEntity> state) {

        if (!isLingering() && maxLingeringTicks > 0) {
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
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private static final RawAnimation EXPLOSION = RawAnimation.begin().thenLoop("explosion");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
}
