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
public record Effect(
        Optional<EntityPredicate> targetConditions,
        LevelBasedValue duration, // -1 = INFINITE / 0 = one time trigger
        double initialManaCost,
        Application application,
        List<Modifier> modifiers,
        List<EnchantmentEntityEffect> effects // TODO :: we need our own system to properly apply the effect
) {
    public static final Codec<Effect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.CODEC.optionalFieldOf("target_conditions").forGetter(Effect::targetConditions),
            LevelBasedValue.CODEC.fieldOf("duration").forGetter(Effect::duration),
            Codec.DOUBLE.fieldOf("initial_mana_cost").forGetter(Effect::initialManaCost),
            Application.CODEC.fieldOf("application").forGetter(Effect::application),
            Modifier.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(Effect::modifiers),
            EnchantmentEntityEffect.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(Effect::effects)
    ).apply(instance, Effect::new));

    public record Application(int manaCost, int triggerRate) {
        public static final Codec<Application> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("mana_cost").forGetter(Application::manaCost),
                Codec.INT.fieldOf("trigger_rate").forGetter(Application::triggerRate)
        ).apply(instance, Application::new));
    }
}