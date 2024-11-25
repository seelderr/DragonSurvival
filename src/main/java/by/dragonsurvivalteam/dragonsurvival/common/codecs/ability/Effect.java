package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.Modifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;

import java.util.List;
import java.util.Optional;

// TODO: How to handle block destruction/interactions for breath abilities? (e.g. cave dragon setting things on fire and igniting TNT)
// TODO: How do we properly display the scaling of the abilities with player level as we do currently in the abilities UI?
public record Effect(
        Optional<EntityPredicate> targetConditions,
        // Like mob effect itself -> -1 is INFINITE / 0 is one time trigger / otherwise acts as ticks of duration
        LevelBasedValue duration,
        // For each time this effect triggers or is active
        double manaCost,
        // If this is present the mana cost is applied per tick specified here
        int manaDepletionRate,
        // In ticks
        int triggerRate,
        List<Modifier> modifiers,
        // TODO: For breath abilities, should we spawn particles through this entity effect or handle it ourselves?
        Optional<EnchantmentEntityEffect> effect
) {
    public static final Codec<Effect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.CODEC.optionalFieldOf("target_conditions").forGetter(Effect::targetConditions),
            LevelBasedValue.CODEC.fieldOf("duration").forGetter(Effect::duration),
            Codec.DOUBLE.fieldOf("mana_cost").forGetter(Effect::manaCost),
            Codec.INT.fieldOf("mana_depletion_rate").forGetter(Effect::manaDepletionRate),
            Codec.INT.fieldOf("cooldown").forGetter(Effect::triggerRate),
            Modifier.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(Effect::modifiers),
            EnchantmentEntityEffect.CODEC.optionalFieldOf("effect").forGetter(Effect::effect)
    ).apply(instance, instance.stable(Effect::new)));
}