package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.effects;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import java.util.Optional;

// TODO :: have one 'AreaEffect' class which uses Codec.either() to decide between entity and block effect
public record AreaEntityEffect(Optional<EntityPredicate> targetConditions, AbilityEffect effect, LevelBasedValue radius) {
    public static final Codec<AreaEntityEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            // TODO :: add sub entity predicate for easy 'enemy' / 'hostile' check (without having to add them all to a tag
            EntityPredicate.CODEC.optionalFieldOf("target_conditions").forGetter(AreaEntityEffect::targetConditions),
            AbilityEffect.CODEC.fieldOf("effect").forGetter(AreaEntityEffect::effect),
            LevelBasedValue.CODEC.fieldOf("radius").forGetter(AreaEntityEffect::radius)
    ).apply(instance, AreaEntityEffect::new));

    public void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability) {
        double radius = radius().calculate(ability.getLevel());

        level.getEntities(EntityTypeTest.forClass(LivingEntity.class), AABB.ofSize(dragon.position(), radius, radius, radius),
                // This will include the dragon itself
                entity -> targetConditions().map(conditions -> conditions.matches(level, dragon.position(), entity)).orElse(true)
        ).forEach(entity -> effect().apply(level, dragon, ability, entity));
    }
}
