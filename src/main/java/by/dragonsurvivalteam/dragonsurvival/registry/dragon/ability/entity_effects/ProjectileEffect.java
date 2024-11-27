package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.ProjectileInstance;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.block_effects.ProjectileBlockEffect;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.entity_effects.ProjectileEntityEffect;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.targeting.ProjectileTargeting;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceKey;
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
public record ProjectileEffect(
        Component name,
        Either<GenericArrowData, GenericBallData> projectileData,
        Optional<EntityPredicate> canHitPredicate,
        List<ProjectileTargeting> tickingEffects,
        List<ProjectileTargeting> commonHitEffects,
        List<ProjectileEntityEffect> entityHitEffects,
        List<ProjectileBlockEffect> blockHitEffects,
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
            ParticleOptions trailParticle,
            // Needed since there is a difference between being hit and destroyed (e.g if we linger)
            List<ProjectileTargeting> onDestroyEffects,
            LevelBasedValue maxLingeringTicks,
            LevelBasedValue maxMoveDistance,
            LevelBasedValue maxLifespan) {
        public static final Codec<GenericBallData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ParticleTypes.CODEC.fieldOf("trail_particle").forGetter(GenericBallData::trailParticle),
                ProjectileTargeting.CODEC.listOf().optionalFieldOf("on_destroy_effects", List.of()).forGetter(GenericBallData::onDestroyEffects),
                LevelBasedValue.CODEC.optionalFieldOf("max_lingering_ticks", LevelBasedValue.constant(0)).forGetter(GenericBallData::maxLingeringTicks),
                LevelBasedValue.CODEC.fieldOf("max_move_distance").forGetter(GenericBallData::maxMoveDistance),
                LevelBasedValue.CODEC.fieldOf("max_lifespan").forGetter(GenericBallData::maxLifespan)
        ).apply(instance, GenericBallData::new));
    }

    public static final MapCodec<ProjectileEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("name").forGetter(ProjectileEffect::name),
            Codec.either(GenericArrowData.CODEC, GenericBallData.CODEC).fieldOf("projectile_data").forGetter(ProjectileEffect::projectileData),
            EntityPredicate.CODEC.optionalFieldOf("can_hit_predicate").forGetter(ProjectileEffect::canHitPredicate),
            ProjectileTargeting.CODEC.listOf().fieldOf("ticking_effects").forGetter(ProjectileEffect::tickingEffects),
            ProjectileTargeting.CODEC.listOf().fieldOf("common_hit_effects").forGetter(ProjectileEffect::commonHitEffects),
            ProjectileEntityEffect.CODEC.listOf().fieldOf("entity_hit_effects").forGetter(ProjectileEffect::entityHitEffects),
            ProjectileBlockEffect.CODEC.listOf().fieldOf("block_hit_effects").forGetter(ProjectileEffect::blockHitEffects),
            LevelBasedValue.CODEC.fieldOf("number_of_projectiles").forGetter(ProjectileEffect::numberOfProjectiles),
            LevelBasedValue.CODEC.optionalFieldOf("projectile_spread", LevelBasedValue.constant(0)).forGetter(ProjectileEffect::projectileSpread),
            LevelBasedValue.CODEC.fieldOf("speed").forGetter(ProjectileEffect::speed)
    ).apply(instance, ProjectileEffect::new));


    @Override
    public void apply(ServerLevel level, Player player, DragonAbilityInstance ability, Entity entity) {
        if(projectileData.left().isPresent()) {
            GenericArrowData arrowData = projectileData.left().get();
            EntityType<? extends AbstractArrow> entityType = (EntityType<? extends AbstractArrow>) level.registryAccess().registry(Registries.ENTITY_TYPE).get().getOrThrow(arrowData.entityType());

            for (int i = 0; i < numberOfProjectiles.calculate(ability.getLevel()); i++) {
                // Copied from AbstractArrow.java constructor
                Vec3 launchPos = new Vec3(player.getX(), player.getEyeY() - 0.1F, player.getZ());
                GenericArrowEntity arrow = new GenericArrowEntity(
                        name,
                        entityType,
                        canHitPredicate,
                        tickingEffects,
                        commonHitEffects,
                        entityHitEffects,
                        blockHitEffects,
                        ability,
                        level,
                        new ProjectileInstance(ability.getLevel()),
                        (int)arrowData.piercingLevel().calculate(ability.getLevel())
                );
                arrow.setPos(launchPos);
                arrow.setOwner(player);
                arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
                arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, speed.calculate(ability.getLevel()), i * projectileSpread.calculate(ability.getLevel()));
                player.level().addFreshEntity(arrow);
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
                        name,
                        ballData.trailParticle,
                        level,
                        canHitPredicate,
                        tickingEffects,
                        commonHitEffects,
                        entityHitEffects,
                        blockHitEffects,
                        ballData.onDestroyEffects,
                        new ProjectileInstance(ability.getLevel()),
                        (int)ballData.maxLingeringTicks.calculate(ability.getLevel()),
                        (int)ballData.maxMoveDistance.calculate(ability.getLevel()),
                        (int)ballData.maxLifespan.calculate(ability.getLevel())
                );

                projectile.setPos(projPos);
                projectile.accelerationPower = 0;
                projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 1.0F, speed.calculate(ability.getLevel()), i * projectileSpread.calculate(ability.getLevel()));
                player.level().addFreshEntity(entity);
            }
        }
    }

    @Override
    public MapCodec<? extends EntityEffect> entityCodec() {
        return null;
    }
}
