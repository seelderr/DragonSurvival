package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.block_effects.BlockEffect;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.AABB;

import java.util.Optional;

public record AreaBlockTarget(Optional<BlockPredicate> targetConditions, BlockEffect effect, LevelBasedValue radius) implements Targeting {
    public static final MapCodec<AreaBlockTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockPredicate.CODEC.optionalFieldOf("target_conditions").forGetter(AreaBlockTarget::targetConditions),
            BlockEffect.CODEC.fieldOf("effect").forGetter(AreaBlockTarget::effect),
            LevelBasedValue.CODEC.fieldOf("radius").forGetter(AreaBlockTarget::radius)
    ).apply(instance, AreaBlockTarget::new));

    public void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability) {
        double radius = radius().calculate(ability.getLevel());
        BlockPos.betweenClosedStream(AABB.ofSize(dragon.position(), radius, radius, radius)).forEach(position -> effect().apply(level, dragon, ability, position));
    }

    @Override
    public MapCodec<? extends Targeting> codec() {
        return CODEC;
    }
}
