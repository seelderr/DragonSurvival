package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;


import by.dragonsurvivalteam.dragonsurvival.common.particles.LargeLightningParticleOption;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.active.BallLightningAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.active.StormBreathAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DSDamageTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import by.dragonsurvivalteam.dragonsurvival.util.TargetingFunctions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.List;
import javax.annotation.Nullable;

public class BallLightningEntity extends DragonBallEntity {
    protected boolean isLingering = false;
    protected int lingerTicks = 100;
    protected LargeLightningParticleOption trail = new LargeLightningParticleOption(37, false);

    public BallLightningEntity(double x, double y, double z, Vec3 velocity, Level level) {
        super(DSEntities.BALL_LIGHTNING.get(), x, y, z, velocity, level);
    }

    public BallLightningEntity(EntityType<? extends Fireball> p_i50166_1_, Level p_i50166_2_) {
        super(p_i50166_1_, p_i50166_2_);
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return trail;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public float getExplosivePower() {
        return getSkillLevel() / 1.25f;
    }

    @Override
    protected DamageSource getDamageSource(Fireball pFireball, @Nullable Entity pIndirectEntity) {
        // This damage source is used since it is specifically not fire damage, so that cave dragons don't ignore the ball lightning damage
        return new DamageSource(DSDamageTypes.get(pIndirectEntity.level(), DSDamageTypes.DRAGON_BALL_LIGHTNING));
    }

    @Override
    public void onHitCommon() {
        if ((getOwner() == null || !getOwner().isRemoved()) && this.level().hasChunkAt(this.blockPosition()) && !hasHit) {
            if (!isLingering) {
                isLingering = true;
                // These power variables drive the movement of the entity in the parent tick() function, so we need to zero them out as well.
                accelerationPower = 0;
                setDeltaMovement(Vec3.ZERO);
            }
        }
        super.onHitCommon();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().getGameTime() % 5 == 0) // Once per 5 ticks (0.25 seconds)
            attackMobs();
        if (isLingering) {
            lingerTicks--;
            if (lingerTicks <= 0) {
                if (!this.level().isClientSide) {
                    float explosivePower = getExplosivePower();
                    DamageSource damagesource;
                    if (getOwner() == null) {
                        damagesource = getDamageSource(this, this);
                    } else {
                        damagesource = getDamageSource(this, getOwner());
                    }
                    Entity attacker = canSelfDamage() ? this : getOwner();
                    level().explode(attacker, damagesource, null, getX(), getY(), getZ(), explosivePower, false, Level.ExplosionInteraction.BLOCK);
                }
                this.discard();
            }
        }
    }

    public void attackMobs() {
        int range = 4;
        Entity owner = getOwner();
        DamageSource source;

        if (owner instanceof Player player) {
            range = DragonAbilities.getAbility(player, BallLightningAbility.class).map(BallLightningAbility::getRange).orElse(range);
            source = owner.damageSources().playerAttack((Player) owner);
        } else {
            source = damageSources().lightningBolt();
        }

        List<Entity> entities = level().getEntities(null, new AABB(position().x - range, position().y - range, position().z - range, position().x + range, position().y + range, position().z + range));
        entities.removeIf(e -> e instanceof BallLightningEntity);
        entities.removeIf(e -> !(e instanceof LivingEntity));

        for (Entity ent : entities) {
            if (!level().isClientSide()) {
                TargetingFunctions.attackTargets(owner, ent1 -> ent1.hurt(source, BallLightningAbility.getDamage(getSkillLevel())), ent);

                if (ent instanceof LivingEntity livingEntity) {
                    if (livingEntity.getRandom().nextInt(100) < 40) {
                        if (!livingEntity.level().isClientSide() && !StormBreathAbility.chargedBlacklist.contains(ResourceHelper.getKey(livingEntity).toString())) {
                            livingEntity.addEffect(new MobEffectInstance(DSEffects.CHARGED, Functions.secondsToTicks(10), 0, false, true));
                        }
                    }

                    level().playLocalSound(blockPosition(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 2.0F, 0.5f, true);
                }

                EnchantmentHelper.doPostAttackEffects(((ServerLevel) level()), ent, source);
            }

            if (level().isClientSide()) {
                // Creates a trail of particles between the entity and target(s)
                int steps = 10;
                float stepSize = 1.f / steps;
                Vec3 distV = new Vec3(getX() - ent.getX(), getY() - ent.getY(), getZ() - ent.getZ());
                for (int i = 0; i < steps; i++) {
                    // the current entity coordinate + ((the distance between it and the target) * (the fraction of the total))
                    Vec3 step = ent.position().add(distV.scale(stepSize * i));
                    level().addParticle(trail, step.x(), step.y(), step.z(), 0.0, 0.0, 0.0);
                }
            }
        }

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

    @Override
    public PlayState predicate(final AnimationState<DragonBallEntity> state) {
        if (!isLingering) {
            state.getController().setAnimation(FLY);
            return PlayState.CONTINUE;
        } else if (lingerTicks < 16) {
            state.getController().setAnimation(EXPLOSION);
        } else {
            state.getController().setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

    private static final RawAnimation EXPLOSION = RawAnimation.begin().thenLoop("explosion");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
}