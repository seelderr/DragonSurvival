package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

// TODO :: provide boolean to only target exposed blocks / visible entities (isVisible)
public record AreaTarget(Either<BlockTargeting, EntityTargeting> target, LevelBasedValue radius) implements AbilityTargeting {
    public static final MapCodec<AreaTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> AbilityTargeting.codecStart(instance)
            .and(LevelBasedValue.CODEC.fieldOf("radius").forGetter(AreaTarget::radius)).apply(instance, AreaTarget::new)
    );

    @Override
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability) {
        double radius = radius().calculate(ability.getLevel());

        target().ifLeft(blockTarget -> {
            BlockPos.betweenClosedStream(AABB.ofSize(dragon.position(), radius * 2, radius * 2, radius * 2)).forEach(position -> {
                if (blockTarget.targetConditions().isEmpty() || blockTarget.targetConditions().get().matches(dragon.serverLevel(), position)) {
                    blockTarget.effect().forEach(target -> target.apply(dragon, ability, position));
                }
            });
        }).ifRight(entityTarget -> {
            // TODO :: add field 'visible' and check using ProjectileUtil.getHitResultOnViewVector()
            //  maybe need to differentiate between behind blocks and below player (under blocks)?
            dragon.serverLevel().getEntities(EntityTypeTest.forClass(Entity.class), AABB.ofSize(dragon.position(), radius * 2, radius * 2, radius * 2),
                    entity -> isEntityRelevant(dragon, entityTarget, entity) && entityTarget.targetConditions().map(conditions -> conditions.matches(dragon.serverLevel(), dragon.position(), entity)).orElse(true)
            ).forEach(entity -> entityTarget.effect().forEach(target -> target.apply(dragon, ability, entity)));
        });
    }

    @Override
    public MapCodec<? extends AbilityTargeting> codec() {
        return CODEC;
    }
}
