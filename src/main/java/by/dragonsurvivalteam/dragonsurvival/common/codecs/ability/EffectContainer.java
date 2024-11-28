package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability;

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
    // TODO :: current_tick parameter? for active it's the time the ability was being held and for passive the entity tick count?
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance instance, int currentTick) {
        // TODO :: check trigger rate -> return if it's not time yet
        //  what format should trigger_rate be?
        //  int for 'currentTick % tick_rate == 0?

        AbilityInfo.Type abilityType = instance.getAbility().type();

        if ((abilityType == AbilityInfo.Type.PASSIVE || abilityType == AbilityInfo.Type.ACTIVE_CHANNELED) && manaCost().isPresent() && manaCost().get().type() == ManaCost.Type.TICKING) {
            // TODO:
            //  Subtract and check mana
            //  Force ability into cooldown if no mana is present
        }

        effect.apply(dragon, instance);
    }
}
