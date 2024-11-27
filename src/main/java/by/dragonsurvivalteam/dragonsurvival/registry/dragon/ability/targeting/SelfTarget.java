package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.AbilityEntityEffect;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public record SelfTarget(Optional<EntityPredicate> targetConditions, AbilityEntityEffect effect) implements AbilityTargeting {
    public static final MapCodec<SelfTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EntityPredicate.CODEC.optionalFieldOf("target_conditions").forGetter(SelfTarget::targetConditions),
            AbilityEntityEffect.CODEC.fieldOf("effect").forGetter(SelfTarget::effect)
    ).apply(instance, SelfTarget::new));

    @Override
    public void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability) {
        if (targetConditions().isPresent() && !targetConditions().get().matches(level, dragon.position(), dragon)) {
            return;
        }

        effect().apply(level, dragon, ability, dragon);
    }

    @Override
    public MapCodec<? extends AbilityTargeting> codec() {
        return CODEC;
    }
}
