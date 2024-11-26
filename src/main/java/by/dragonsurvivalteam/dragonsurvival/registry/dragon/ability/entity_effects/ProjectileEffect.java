package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.GenericArrowEntity;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.GenericBallEntity;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

// TODO: We need some way to properly name the projectile entity itself through this codec (it currently has the generic name)
public record ProjectileEffect(
        Either<GenericArrowData, GenericBallData> projectileData,
        Optional<EntityPredicate> canHitPredicate,
        ResourceKey<DamageType> damageTypeResourceKey,
        ResourceKey<SoundEvent> soundEvent,
        LevelBasedValue damage,
        LevelBasedValue numberOfProjectiles,
        LevelBasedValue projectileSpread,
        LevelBasedValue explosionPower,
        LevelBasedValue speed) implements EntityEffect {

    public record GenericArrowData(ResourceKey<EntityType<?>> entityType, LevelBasedValue piercingLevel) {
        public static final Codec<GenericArrowData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceKey.codec(Registries.ENTITY_TYPE).fieldOf("entity_type").forGetter(GenericArrowData::entityType),
                LevelBasedValue.CODEC.optionalFieldOf("piercing_level", LevelBasedValue.constant(0)).forGetter(GenericArrowData::piercingLevel)
        ).apply(instance, GenericArrowData::new));
    }

    public record GenericBallData(
            ResourceLocation modelResourceLocation,
            ResourceLocation textureResourceLocation,
            ResourceLocation animationResourceLocation,
            ParticleOptions trailParticle,
            LevelBasedValue maxLingeringTicks,
            LevelBasedValue chainedDamageRadius,
            LevelBasedValue chainedDamageAmount,
            LevelBasedValue maxMoveDistance,
            LevelBasedValue maxLifespan,
            boolean canSelfDamage,
            boolean canCauseFire) {
        public static final Codec<GenericBallData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("model").forGetter(GenericBallData::modelResourceLocation),
                ResourceLocation.CODEC.fieldOf("texture").forGetter(GenericBallData::textureResourceLocation),
                ResourceLocation.CODEC.fieldOf("animation").forGetter(GenericBallData::animationResourceLocation),
                ParticleTypes.CODEC.fieldOf("trail_particle").forGetter(GenericBallData::trailParticle),
                LevelBasedValue.CODEC.optionalFieldOf("max_lingering_ticks", LevelBasedValue.constant(0)).forGetter(GenericBallData::maxLingeringTicks),
                LevelBasedValue.CODEC.optionalFieldOf("chained_damage_radius", LevelBasedValue.constant(0)).forGetter(GenericBallData::chainedDamageRadius),
                LevelBasedValue.CODEC.optionalFieldOf("chained_damage_amount", LevelBasedValue.constant(0)).forGetter(GenericBallData::chainedDamageAmount),
                LevelBasedValue.CODEC.fieldOf("max_move_distance").forGetter(GenericBallData::maxMoveDistance),
                LevelBasedValue.CODEC.fieldOf("max_lifespan").forGetter(GenericBallData::maxLifespan),
                Codec.BOOL.fieldOf("can_self_damage").forGetter(GenericBallData::canSelfDamage),
                Codec.BOOL.fieldOf("can_cause_fire").forGetter(GenericBallData::canCauseFire)
        ).apply(instance, GenericBallData::new));
    }

    public static final MapCodec<ProjectileEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.either(GenericArrowData.CODEC, GenericBallData.CODEC).fieldOf("projectile_data").forGetter(ProjectileEffect::projectileData),
            EntityPredicate.CODEC.optionalFieldOf("can_hit_predicate").forGetter(ProjectileEffect::canHitPredicate),
            ResourceKey.codec(Registries.DAMAGE_TYPE).fieldOf("damage_type").forGetter(ProjectileEffect::damageTypeResourceKey),
            ResourceKey.codec(Registries.SOUND_EVENT).fieldOf("sound_event").forGetter(ProjectileEffect::soundEvent),
            LevelBasedValue.CODEC.fieldOf("damage").forGetter(ProjectileEffect::damage),
            LevelBasedValue.CODEC.fieldOf("number_of_projectiles").forGetter(ProjectileEffect::numberOfProjectiles),
            LevelBasedValue.CODEC.optionalFieldOf("projectile_spread", LevelBasedValue.constant(0)).forGetter(ProjectileEffect::projectileSpread),
            LevelBasedValue.CODEC.optionalFieldOf("explosion_power", LevelBasedValue.constant(0)).forGetter(ProjectileEffect::explosionPower),
            LevelBasedValue.CODEC.fieldOf("speed").forGetter(ProjectileEffect::speed)
    ).apply(instance, ProjectileEffect::new));


    @Override
    public void apply(ServerLevel level, Player player, DragonAbilityInstance ability, Entity entity) {
        float speed = 1;

        if(projectileData.left().isPresent()) {
            GenericArrowData arrowData = projectileData.left().get();
            EntityType<? extends AbstractArrow> entityType = (EntityType<? extends AbstractArrow>) level.registryAccess().registry(Registries.ENTITY_TYPE).get().getOrThrow(arrowData.entityType());
            SoundEvent soundEvent = level.registryAccess().registry(Registries.SOUND_EVENT).get().getOrThrow(soundEvent());

            for (int i = 0; i < numberOfProjectiles.calculate(ability.getLevel()); i++) {
                // Copied from AbstractArrow.java constructor
                Vec3 launchPos = new Vec3(player.getX(), player.getEyeY() - 0.1F, player.getZ());
                GenericArrowEntity arrow = new GenericArrowEntity(
                        entityType,
                        canHitPredicate,
                        damageTypeResourceKey,
                        level,
                        damage.calculate(ability.getLevel()),
                        (int)arrowData.piercingLevel().calculate(ability.getLevel()),
                        explosionPower.calculate(ability.getLevel())
                );
                arrow.setPos(launchPos);
                arrow.setOwner(player);
                arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
                arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, speed, i * projectileSpread.calculate(ability.getLevel()));
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), soundEvent, entity.getSoundSource(), 1.0F, 2.0F);
                player.level().addFreshEntity(entity);
            }
        } else if(projectileData.right().isPresent()) {
            GenericBallData ballData = projectileData.right().get();
            SoundEvent soundEvent = level.registryAccess().registry(Registries.SOUND_EVENT).get().getOrThrow(soundEvent());

            for (int i = 0; i < numberOfProjectiles.calculate(ability.getLevel()); i++) {
                // Copied from AbstractArrow.java constructor
                Vec3 eyePos = player.getEyePosition();
                Vec3 lookAngle = player.getLookAngle();

                Vec3 projPos;
                if (player.getAbilities().flying) {
                    projPos = lookAngle.scale(2.0F).add(eyePos);
                } else {
                    projPos = lookAngle.scale(1.0F).add(eyePos);
                }

                GenericBallEntity projectile = new GenericBallEntity(
                        damageTypeResourceKey,
                        canHitPredicate,
                        ballData.modelResourceLocation,
                        ballData.textureResourceLocation,
                        ballData.animationResourceLocation,
                        ballData.trailParticle,
                        level,
                        damage.calculate(ability.getLevel()),
                        explosionPower.calculate(ability.getLevel()),
                        (int)ballData.maxLingeringTicks.calculate(ability.getLevel()),
                        ballData.chainedDamageRadius.calculate(ability.getLevel()),
                        ballData.chainedDamageAmount.calculate(ability.getLevel()),
                        (int)ballData.maxMoveDistance.calculate(ability.getLevel()),
                        (int)ballData.maxLifespan.calculate(ability.getLevel()),
                        ballData.canSelfDamage,
                        ballData.canCauseFire);

                projectile.setPos(projPos);
                projectile.accelerationPower = 0;
                projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 1.0F, speed, 0);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), soundEvent, entity.getSoundSource(), 1.0F, 2.0F);
                player.level().addFreshEntity(entity);
            }
        }
    }

    @Override
    public MapCodec<? extends EntityEffect> codec() {
        return null;
    }
}
