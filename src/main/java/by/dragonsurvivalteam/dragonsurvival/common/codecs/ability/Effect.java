package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting.Targeting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.List;

public record Effect(
        LevelBasedValue duration, // -1 = INFINITE / 0 = one time trigger
        double initialManaCost,
        Application application,
        List<Targeting> targets
) {
    public static final Codec<Effect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("duration").forGetter(Effect::duration),
            Codec.DOUBLE.fieldOf("initial_mana_cost").forGetter(Effect::initialManaCost),
            Application.CODEC.fieldOf("application").forGetter(Effect::application),
            Targeting.CODEC.listOf().fieldOf("targets").forGetter(Effect::targets)
    ).apply(instance, Effect::new));

    public record Application(int manaCost, int triggerRate) {
        public static final Codec<Application> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("mana_cost").forGetter(Application::manaCost),
                Codec.INT.fieldOf("trigger_rate").forGetter(Application::triggerRate)
        ).apply(instance, Application::new));
    }

    public int getDuration(int abilityLevel) {
        return (int) duration().calculate(abilityLevel);
    }
}