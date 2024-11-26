package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

// TODO :: provide boolean to only target exposed blocks / visible entities (isVisible)
public record AreaTarget(Either<BlockTargeting, EntityTargeting> target, LevelBasedValue radius, Optional<ParticleOptions> particleTrail, int triggerRate) implements PositionalTargeting {
    public static final MapCodec<AreaTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.either(BlockTargeting.CODEC, EntityTargeting.CODEC).fieldOf("target").forGetter(AreaTarget::target),
            LevelBasedValue.CODEC.fieldOf("radius").forGetter(AreaTarget::radius),
            ParticleTypes.CODEC.optionalFieldOf("particle_trail").forGetter(AreaTarget::particleTrail),
            // Intended for targeting effects that are being called every tick
            Codec.INT.optionalFieldOf("trigger_rate", 1).forGetter(AreaTarget::triggerRate)
    ).apply(instance, AreaTarget::new));

    public void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability) {
        if(level.getGameTime() % triggerRate == 0) return;

        double radius = radius().calculate(ability.getLevel());

        target().ifLeft(blockTarget -> {
            BlockPos.betweenClosedStream(AABB.ofSize(dragon.position(), radius, radius, radius)).forEach(position -> {
                if (blockTarget.targetConditions().isEmpty() || blockTarget.targetConditions().get().matches(level, position)) {
                    blockTarget.effect().apply(level, dragon, ability, position);
                }
            });
        }).ifRight(entityTarget -> {
            // TODO :: use Entity.class (would affect items etc.)?
            // ProjectileUtil.getHitResultOnViewVector()
            level.getEntities(EntityTypeTest.forClass(LivingEntity.class), AABB.ofSize(dragon.position(), radius, radius, radius),
                    entity -> entityTarget.targetConditions().map(conditions -> conditions.matches(level, dragon.position(), entity)).orElse(true)
            ).forEach(entity -> entityTarget.effect().apply(level, dragon, ability, entity));
        });
    }

    public void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability, final Vec3 position) {
        if(level.getGameTime() % triggerRate == 0) return;

        double radius = radius().calculate(ability.getLevel());

        target().ifLeft(blockTarget -> {
            BlockPos.betweenClosedStream(AABB.ofSize(position, radius, radius, radius)).forEach(blockPos -> {
                if (blockTarget.targetConditions().isEmpty() || blockTarget.targetConditions().get().matches(level, blockPos)) {
                    blockTarget.effect().apply(level, dragon, ability, blockPos);
                }
            });
        }).ifRight(entityTarget -> {
            level.getEntities(EntityTypeTest.forClass(LivingEntity.class), AABB.ofSize(position, radius, radius, radius),
                    entity -> entityTarget.targetConditions().map(conditions -> conditions.matches(level, position, entity)).orElse(true)
            ).forEach(entity -> entityTarget.effect().apply(level, dragon, ability, entity));
        });
    }

    @Override
    public MapCodec<? extends PositionalTargeting> codec() {
        return CODEC;
    }
}
