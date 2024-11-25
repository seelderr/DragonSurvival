package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.EntityEffect;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import java.util.Optional;

public record AreaEntityTarget(Optional<EntityPredicate> targetConditions, LevelBasedValue radius, EntityEffect effect) implements Targeting {
    public static final MapCodec<AreaEntityTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            // TODO :: add sub entity predicate for easy 'enemy' / 'hostile' check (without having to add them all to a tag
            EntityPredicate.CODEC.optionalFieldOf("target_conditions").forGetter(AreaEntityTarget::targetConditions),
            LevelBasedValue.CODEC.fieldOf("radius").forGetter(AreaEntityTarget::radius),
            EntityEffect.CODEC.fieldOf("effect").forGetter(AreaEntityTarget::effect)
    ).apply(instance, AreaEntityTarget::new));

    public void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability) {
        double radius = radius().calculate(ability.getLevel());

        level.getEntities(EntityTypeTest.forClass(LivingEntity.class), AABB.ofSize(dragon.position(), radius, radius, radius),
                // This will include the dragon itself
                entity -> targetConditions().map(conditions -> conditions.matches(level, dragon.position(), entity)).orElse(true)
        ).forEach(entity -> effect().apply(level, dragon, ability, entity));
    }

    @Override
    public MapCodec<? extends Targeting> codec() {
        return CODEC;
    }
}
