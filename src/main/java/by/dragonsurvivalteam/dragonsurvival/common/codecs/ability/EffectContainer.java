package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability;

import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbility;
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
    public void tick(final ServerPlayer dragon, final DragonAbilityInstance instance, int currentTick) {
        float rate = triggerRate().calculate(instance.getLevel());

        if (rate > 0 && currentTick % rate != 0) {
            return;
        }

        effect.apply(dragon, instance);
        DragonAbility.Type abilityType = instance.getAbility().type();

        if ((abilityType == DragonAbility.Type.PASSIVE || abilityType == DragonAbility.Type.ACTIVE_CHANNELED) && manaCost().isPresent() && manaCost().get().type() == ManaCost.Type.TICKING) {
            int cost = (int) manaCost().get().manaCost().calculate(instance.getLevel());
            ManaHandler.consumeMana(dragon, cost);

            if (!ManaHandler.hasEnoughMana(dragon, cost)) {
                instance.release();
            }
        }
    }
}
