package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

// TODO :: provide boolean to only target exposed blocks / visible entities (isVisible)
public record AreaTarget(Either<BlockTargeting, EntityTargeting> target, LevelBasedValue radius) implements AbilityTargeting {
    public static final MapCodec<AreaTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.either(BlockTargeting.CODEC, EntityTargeting.CODEC).fieldOf("target").forGetter(AreaTarget::target),
            LevelBasedValue.CODEC.fieldOf("radius").forGetter(AreaTarget::radius)
    ).apply(instance, AreaTarget::new));

    public void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability) {
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

    @Override
    public MapCodec<? extends AbilityTargeting> codec() {
        return CODEC;
    }
}
