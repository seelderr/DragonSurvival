package by.dragonsurvivalteam.dragonsurvival.registry.projectile.targeting;

import by.dragonsurvivalteam.dragonsurvival.registry.projectile.ProjectileInstance;
import by.dragonsurvivalteam.dragonsurvival.network.particle.SyncParticleTrail;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

public record ProjectileAreaTarget(Either<BlockTargeting, EntityTargeting> target, LevelBasedValue radius, Optional<ParticleOptions> particleTrail) implements ProjectileTargeting {
    public static final MapCodec<ProjectileAreaTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.either(BlockTargeting.CODEC, EntityTargeting.CODEC).fieldOf("target").forGetter(ProjectileAreaTarget::target),
            LevelBasedValue.CODEC.fieldOf("radius").forGetter(ProjectileAreaTarget::radius),
            ParticleTypes.CODEC.optionalFieldOf("particle_trail").forGetter(ProjectileAreaTarget::particleTrail)
    ).apply(instance, ProjectileAreaTarget::new));

    public void apply(final ServerLevel level, final ServerPlayer player, final ProjectileInstance projectile, final Vec3 position) {
        double radius = radius().calculate(projectile.getLevel());

        target().ifLeft(blockTarget -> {
            BlockPos.betweenClosedStream(AABB.ofSize(position, radius, radius, radius)).forEach(blockPos -> {
                if (blockTarget.targetConditions().isEmpty() || blockTarget.targetConditions().get().matches(level, blockPos)) {
                    blockTarget.effect().apply(level, player, projectile, blockPos);
                    if(particleTrail().isPresent()) {
                        Vec3 trailMidpoint = blockPos.getCenter().subtract(position).scale(0.5).add(position);
                        PacketDistributor.sendToPlayersNear(
                                level,
                                null,
                                trailMidpoint.x,
                                trailMidpoint.y,
                                trailMidpoint.z,
                                64,
                                new SyncParticleTrail(position.toVector3f(), blockPos.getCenter().toVector3f(), particleTrail().get()));
                    }
                }
            });
        }).ifRight(entityTarget -> {
            // TODO :: use Entity.class (would affect items etc.)?
            level.getEntities(EntityTypeTest.forClass(LivingEntity.class), AABB.ofSize(position, radius, radius, radius),
                    entity -> entityTarget.targetConditions().map(conditions -> conditions.matches(level, position, entity)).orElse(true)
            ).forEach(entity -> {
                entityTarget.effect().apply(level, player, projectile, entity);
                if(particleTrail().isPresent()) {
                    Vec3 trailMidpoint = entity.position().subtract(position).scale(0.5).add(position);
                    PacketDistributor.sendToPlayersNear(
                            level,
                            null,
                            trailMidpoint.x,
                            trailMidpoint.y,
                            trailMidpoint.z,
                            64,
                            new SyncParticleTrail(position.toVector3f(), entity.position().toVector3f(), particleTrail().get()));
                }
            });
        });
    }

    @Override
    public MapCodec<? extends ProjectileTargeting> codec() {
        return CODEC;
    }
}
