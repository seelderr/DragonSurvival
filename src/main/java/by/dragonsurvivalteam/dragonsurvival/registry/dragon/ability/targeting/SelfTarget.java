package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;

public record SelfTarget(Either<BlockTargeting, EntityTargeting> target, boolean removeAutomatically) implements AbilityTargeting {
    public static final MapCodec<SelfTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> AbilityTargeting.codecStart(instance)
            .and(Codec.BOOL.optionalFieldOf("remove_automatically", false).forGetter(SelfTarget::removeAutomatically)).apply(instance, SelfTarget::new)
    );

    @Override
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability) {
        target().ifLeft(blockTarget -> {
            if (blockTarget.targetConditions().map(conditions -> conditions.matches(dragon.serverLevel(), dragon.blockPosition())).orElse(true)) {
                blockTarget.effect().forEach(target -> target.apply(dragon, ability, dragon.blockPosition(), null));
            }
        }).ifRight(entityTarget -> {
            if (entityTarget.targetConditions().map(conditions -> conditions.matches(dragon.serverLevel(), dragon.position(), dragon)).orElse(true)) {
                entityTarget.effect().forEach(target -> target.apply(dragon, ability, dragon));
            } else if (removeAutomatically) {
                // TODO :: would also need to remove when disabling the ability
                entityTarget.effect().forEach(target -> target.remove(dragon, ability, dragon));
            }
        });
    }

    @Override
    public MapCodec<? extends AbilityTargeting> codec() {
        return CODEC;
    }
}
