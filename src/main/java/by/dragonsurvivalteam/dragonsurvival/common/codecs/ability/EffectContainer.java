package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability;

import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.AbilityInfo;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting.AbilityTargeting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.Optional;

public record EffectContainer(AbilityTargeting effect, LevelBasedValue triggerRate, Optional<ManaCost> manaCost) {
    public static Codec<EffectContainer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AbilityTargeting.CODEC.fieldOf("effect").forGetter(EffectContainer::effect),
            LevelBasedValue.CODEC.fieldOf("trigger_rate").forGetter(EffectContainer::triggerRate),
            ManaCost.CODEC.optionalFieldOf("mana_cost").forGetter(EffectContainer::manaCost)
    ).apply(instance, EffectContainer::new));

    // Passive: Triggered per tick
    // Active (simple): Triggered once on key press (trigger_rate is not relevant in this case)
    // Active (channeled): Triggered per tick while key is being held
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance instance, int currentTick) {
        if(triggerRate.calculate(instance.getLevel()) > 0 && currentTick % triggerRate.calculate(instance.getLevel()) != 0) {
            return;
        }

        effect.apply(dragon, instance);

        AbilityInfo.Type abilityType = instance.getAbility().type();
        if ((abilityType == AbilityInfo.Type.PASSIVE || abilityType == AbilityInfo.Type.ACTIVE_CHANNELED) && manaCost().isPresent() && manaCost().get().type() == ManaCost.Type.TICKING) {
            int cost = (int)manaCost.get().manaCost().calculate(instance.getLevel());
            ManaHandler.consumeMana(dragon, cost);
            if (!ManaHandler.hasEnoughMana(dragon, cost)) {
                instance.release();
            }
        }
    }
}
