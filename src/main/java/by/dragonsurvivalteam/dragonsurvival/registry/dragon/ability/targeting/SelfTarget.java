package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;

public record SelfTarget(Either<BlockTargeting, EntityTargeting> target) implements AbilityTargeting {
    public static final MapCodec<SelfTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> AbilityTargeting.codecStart(instance).apply(instance, SelfTarget::new));

    @Override
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability) {
        target().ifLeft(blockTarget -> blockTarget.effect().forEach(target -> target.apply(dragon, ability, dragon.blockPosition(), null)))
                .ifRight(entityTarget -> entityTarget.effect().forEach(target -> target.apply(dragon, ability, dragon)));
    }

    @Override
    public MapCodec<? extends AbilityTargeting> codec() {
        return CODEC;
    }
}
