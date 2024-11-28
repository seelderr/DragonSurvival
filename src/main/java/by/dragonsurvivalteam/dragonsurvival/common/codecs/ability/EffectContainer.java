package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting.AbilityTargeting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record EffectContainer(AbilityTargeting effect, LevelBasedValue triggerRate, LevelBasedValue manaCost) {
    public static Codec<EffectContainer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AbilityTargeting.CODEC.fieldOf("effect").forGetter(EffectContainer::effect),
            // When an ability is pressed it should be triggered once per tick and the trigger rate then decides whether the effect triggers or not?
            LevelBasedValue.CODEC.fieldOf("trigger_rate").forGetter(EffectContainer::triggerRate),
            // Use ManaCost codec? Codec.either(a, Codec.either(b, c))?
            LevelBasedValue.CODEC.fieldOf("mana_cost").forGetter(EffectContainer::manaCost)
    ).apply(instance, EffectContainer::new));

    // Passive: Triggered per tick
    // Active (simple): Triggered once on key press (trigger_rate is not relevant in this case)
    // Active (channeled): Triggered per tick while key is being held
    // TODO :: current_tick parameter? for active it's the time the ability was being held and for passive the entity tick count?
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance instance) {
        Activation.Type type = instance.getAbility().activation().map(Activation::type).orElse(null);

        if ((type == null || type == Activation.Type.CHANNELED)) {
            // Passive or channeled
            // Apply mana cost (should maybe be in activation itself, even the triggered part)

            // TODO :: check trigger_rate
            return;
        }

        effect.apply(dragon, instance);
    }
}
