package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.GenericArrowEntity;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.GenericBallEntity;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.block_effects.BlockEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting.PositionalTargeting;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

// TODO: Should we add in a way to apply effects to entities that are hit by the projectile?
// TODO: We need some way to properly name the projectile entity itself through this codec (it currently has the generic name)
public record ProjectileEffect(
        Either<GenericArrowData, GenericBallData> projectileData,
        Optional<EntityPredicate> canHitPredicate,
        List<PositionalTargeting> tickingEffects,
        List<PositionalTargeting> commonHitEffects,
        List<EntityEffect> entityHitEffects,
        List<BlockEffect> blockHitEffects,
        LevelBasedValue numberOfProjectiles,
        LevelBasedValue projectileSpread,
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
            // Needed since there is a difference between being hit and destroyed (e.g if we linger)
            List<PositionalTargeting> onDestroyEffects,
            LevelBasedValue maxLingeringTicks,
            LevelBasedValue maxMoveDistance,
            LevelBasedValue maxLifespan) {
        public static final Codec<GenericBallData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("model").forGetter(GenericBallData::modelResourceLocation),
                ResourceLocation.CODEC.fieldOf("texture").forGetter(GenericBallData::textureResourceLocation),
                ResourceLocation.CODEC.fieldOf("animation").forGetter(GenericBallData::animationResourceLocation),
                ParticleTypes.CODEC.fieldOf("trail_particle").forGetter(GenericBallData::trailParticle),
                PositionalTargeting.CODEC.listOf().optionalFieldOf("on_destroy_effects", List.of()).forGetter(GenericBallData::onDestroyEffects),
                LevelBasedValue.CODEC.optionalFieldOf("max_lingering_ticks", LevelBasedValue.constant(0)).forGetter(GenericBallData::maxLingeringTicks),
                LevelBasedValue.CODEC.fieldOf("max_move_distance").forGetter(GenericBallData::maxMoveDistance),
                LevelBasedValue.CODEC.fieldOf("max_lifespan").forGetter(GenericBallData::maxLifespan)
        ).apply(instance, GenericBallData::new));
    }

    public static final MapCodec<ProjectileEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.either(GenericArrowData.CODEC, GenericBallData.CODEC).fieldOf("projectile_data").forGetter(ProjectileEffect::projectileData),
            EntityPredicate.CODEC.optionalFieldOf("can_hit_predicate").forGetter(ProjectileEffect::canHitPredicate),
            PositionalTargeting.CODEC.listOf().fieldOf("ticking_effects").forGetter(ProjectileEffect::tickingEffects),
            PositionalTargeting.CODEC.listOf().fieldOf("common_hit_effects").forGetter(ProjectileEffect::commonHitEffects),
            EntityEffect.CODEC.listOf().fieldOf("entity_hit_effects").forGetter(ProjectileEffect::entityHitEffects),
            BlockEffect.CODEC.listOf().fieldOf("block_hit_effects").forGetter(ProjectileEffect::blockHitEffects),
            LevelBasedValue.CODEC.fieldOf("number_of_projectiles").forGetter(ProjectileEffect::numberOfProjectiles),
            LevelBasedValue.CODEC.optionalFieldOf("projectile_spread", LevelBasedValue.constant(0)).forGetter(ProjectileEffect::projectileSpread),
            LevelBasedValue.CODEC.fieldOf("speed").forGetter(ProjectileEffect::speed)
    ).apply(instance, ProjectileEffect::new));


    @Override
    public void apply(ServerLevel level, Player player, DragonAbilityInstance ability, Entity entity) {
        float speed = 1;

        if(projectileData.left().isPresent()) {
            GenericArrowData arrowData = projectileData.left().get();
            EntityType<? extends AbstractArrow> entityType = (EntityType<? extends AbstractArrow>) level.registryAccess().registry(Registries.ENTITY_TYPE).get().getOrThrow(arrowData.entityType());

            for (int i = 0; i < numberOfProjectiles.calculate(ability.getLevel()); i++) {
                // Copied from AbstractArrow.java constructor
                Vec3 launchPos = new Vec3(player.getX(), player.getEyeY() - 0.1F, player.getZ());
                GenericArrowEntity arrow = new GenericArrowEntity(
                        entityType,
                        canHitPredicate,
                        tickingEffects,
                        commonHitEffects,
                        entityHitEffects,
                        blockHitEffects,
                        ability,
                        level,
                        (int)arrowData.piercingLevel().calculate(ability.getLevel())
                );
                arrow.setPos(launchPos);
                arrow.setOwner(player);
                arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
                arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, speed, i * projectileSpread.calculate(ability.getLevel()));
                player.level().addFreshEntity(entity);
            }
        } else if(projectileData.right().isPresent()) {
            GenericBallData ballData = projectileData.right().get();

            for (int i = 0; i < numberOfProjectiles.calculate(ability.getLevel()); i++) {
                Vec3 eyePos = player.getEyePosition();
                Vec3 lookAngle = player.getLookAngle();

                Vec3 projPos;
                if (player.getAbilities().flying) {
                    projPos = lookAngle.scale(2.0F).add(eyePos);
                } else {
                    projPos = lookAngle.scale(1.0F).add(eyePos);
                }

                GenericBallEntity projectile = new GenericBallEntity(
                        canHitPredicate,
                        // TODO: Probably don't want to have explicit locations here, instead base them off of the name parameter that we would add
                        ballData.modelResourceLocation,
                        ballData.textureResourceLocation,
                        ballData.animationResourceLocation,
                        ballData.trailParticle,
                        level,
                        tickingEffects,
                        commonHitEffects,
                        entityHitEffects,
                        blockHitEffects,
                        ballData.onDestroyEffects,
                        ability,
                        (int)ballData.maxLingeringTicks.calculate(ability.getLevel()),
                        (int)ballData.maxMoveDistance.calculate(ability.getLevel()),
                        (int)ballData.maxLifespan.calculate(ability.getLevel())
                );

                projectile.setPos(projPos);
                projectile.accelerationPower = 0;
                projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 1.0F, speed, i * projectileSpread.calculate(ability.getLevel()));
                player.level().addFreshEntity(entity);
            }
        }
    }

    @Override
    public MapCodec<? extends EntityEffect> entityCodec() {
        return null;
    }
}
