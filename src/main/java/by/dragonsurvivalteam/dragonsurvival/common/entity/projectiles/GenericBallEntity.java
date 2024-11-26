package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.active.StormBreathAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import by.dragonsurvivalteam.dragonsurvival.util.TargetingFunctions;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
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

    private final Optional<EntityPredicate> canHitPredicate;
    public final ResourceLocation modelResourceLocation;
    public final ResourceLocation textureResourceLocation;
    public final ResourceLocation animationResourceLocation;
    private final ResourceKey<DamageType> damageType;
    private final float damage;
    private final float explosionPower;
    private final int maxLingeringTicks;
    private final float chainedDamageRadius;
    private final float chainedDamageAmount;
    private final int maxMoveDistance;
    private final int maxLifespan;
    private final boolean canSelfDamage;
    private final boolean canCauseFire;
    private final ParticleOptions trailParticle;

    public boolean hasHit = false;
    protected boolean isLingering = false;
    protected int lingerTicks;

    public GenericBallEntity(
            ResourceKey<DamageType> damageType,
            Optional<EntityPredicate> canHitPredicate,
            ResourceLocation modelResourceLocation,
            ResourceLocation textureResourceLocation,
            ResourceLocation animationResourceLocation,
            ParticleOptions trailParticle,
            Level level,
            float damage,
            float explosionPower,
            int maxLingeringTicks,
            float chainedDamageRadius,
            float chainedDamageAmount,
            int maxMoveDistance,
            int maxLifespan,
            boolean canSelfDamage,
            boolean canCauseFire) {
        super(DSEntities.GENERIC_BALL_ENTITY.get(), level);
        this.canHitPredicate = canHitPredicate;
        this.modelResourceLocation = modelResourceLocation;
        this.textureResourceLocation = textureResourceLocation;
        this.animationResourceLocation = animationResourceLocation;
        this.trailParticle = trailParticle;
        this.damageType = damageType;
        this.damage = damage;
        this.explosionPower = explosionPower;
        this.maxLingeringTicks = maxLingeringTicks;
        this.lingerTicks = maxLingeringTicks;
        this.chainedDamageRadius = chainedDamageRadius;
        this.chainedDamageAmount = chainedDamageAmount;
        this.maxMoveDistance = maxMoveDistance;
        this.maxLifespan = maxLifespan;
        this.canSelfDamage = canSelfDamage;
        this.canCauseFire = canCauseFire;
    }

    public GenericBallEntity(EntityType<GenericBallEntity> genericBallEntityEntityType, Level level) {
        super(DSEntities.GENERIC_BALL_ENTITY.get(), level);
        this.canHitPredicate = Optional.empty();
        this.modelResourceLocation = null;
        this.textureResourceLocation = null;
        this.animationResourceLocation = null;
        this.trailParticle = ParticleTypes.FLAME;
        this.damageType = DamageTypes.ARROW;
        this.damage = 0;
        this.explosionPower = 0;
        this.maxLingeringTicks = 0;
        this.lingerTicks = 0;
        this.chainedDamageRadius = 0;
        this.chainedDamageAmount = 0;
        this.maxMoveDistance = 0;
        this.maxLifespan = 0;
        this.canSelfDamage = false;
        this.canCauseFire = false;
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

        if (level().getGameTime() % 5 == 0 && chainedDamageRadius != 0) // Once per 5 ticks (0.25 seconds)
            attackMobs();

        if (isLingering) {
            lingerTicks--;
            if (lingerTicks <= 0) {
                if (!this.level().isClientSide) {
                    causeExplosion();
                }
                this.discard();
            }
        }
    }

    public void attackMobs() {
        Entity owner = getOwner();
        DamageSource source;

        if (owner instanceof Player) {
            source = owner.damageSources().playerAttack((Player) owner);
        } else {
            source = damageSources().lightningBolt();
        }

        List<Entity> targets = level().getEntities(null, new AABB(position().x - chainedDamageRadius, position().y - chainedDamageRadius, position().z - chainedDamageRadius, position().x + chainedDamageRadius, position().y + chainedDamageRadius, position().z + chainedDamageRadius));
        targets.removeIf(e -> e instanceof BallLightningEntity);
        targets.removeIf(e -> !(e instanceof LivingEntity));

        for (Entity target : targets) {
            if (!level().isClientSide()) {
                EntityPredicate predicate = canHitPredicate.orElse(null);
                if(predicate == null) {
                    TargetingFunctions.attackTargets(owner, ent1 -> ent1.hurt(source, chainedDamageAmount), target);
                } else {
                    TargetingFunctions.attackTargets((ServerLevel)level(), predicate, owner, ent1 -> ent1.hurt(source, chainedDamageAmount), target);
                }

                if (target instanceof LivingEntity livingEntity) {
                    if (livingEntity.getRandom().nextInt(100) < 40) {
                        // TODO: This needs to become data driven
                        if (!livingEntity.level().isClientSide() && !StormBreathAbility.chargedBlacklist.contains(ResourceHelper.getKey(livingEntity).toString())) {
                            livingEntity.addEffect(new MobEffectInstance(DSEffects.CHARGED, Functions.secondsToTicks(10), 0, false, true));
                        }
                    }

                    level().playLocalSound(blockPosition(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 2.0F, 0.5f, true);
                }

                EnchantmentHelper.doPostAttackEffects(((ServerLevel) level()), target, source);
            }

            if (level().isClientSide()) {
                // Creates a trail of particles between the entity and target(s)
                int steps = 10;
                float stepSize = 1.f / steps;
                Vec3 distV = new Vec3(getX() - target.getX(), getY() - target.getY(), getZ() - target.getZ());
                for (int i = 0; i < steps; i++) {
                    // the current entity coordinate + ((the distance between it and the target) * (the fraction of the total))
                    Vec3 step = target.position().add(distV.scale(stepSize * i));
                    level().addParticle(trailParticle, step.x(), step.y(), step.z(), 0.0, 0.0, 0.0);
                }
            }
        }

        // TODO: This should be data driven
        if (!level().isClientSide) {
            if (level().isThundering()) {
                if (level().random.nextInt(100) < 30) {
                    if (level().canSeeSky(blockPosition())) {
                        LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(level());
                        lightningboltentity.moveTo(new Vec3(position().x, position().y, position().z));

                        level().addFreshEntity(lightningboltentity);
                    }
                }
            }
        }
    }

    private Holder<DamageType> getDamageTypeHolder() {
        return ResourceHelper.get(level().registryAccess(), damageType, Registries.DAMAGE_TYPE).get();
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult hitResult) {
        Entity attacker = getOwner();
        hitResult.getEntity().hurt(new DamageSource(getDamageTypeHolder(), this, attacker), damage);
        if(canCauseFire)
            hitResult.getEntity().setRemainingFireTicks(40);

        onHitCommon();
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        onHitCommon();
    }

    private void causeExplosion() {
        DamageSource damagesource;
        if (getOwner() == null) {
            damagesource = new DamageSource(getDamageTypeHolder(), this, this);
        } else {
            damagesource = new DamageSource(getDamageTypeHolder(), this, getOwner());
        }
        Entity attacker = canSelfDamage ? this : getOwner();
        level().explode(attacker, damagesource, null, getX(), getY(), getZ(), explosionPower, canCauseFire, Level.ExplosionInteraction.BLOCK);
    }

    public void onHitCommon() {
        if (!this.level().isClientSide) {
            if ((getOwner() == null || !getOwner().isRemoved())
                    && this.level().hasChunkAt(this.blockPosition())
                    && !hasHit
                    && explosionPower > 0
                    && maxLingeringTicks <= 0) {
                causeExplosion();
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
