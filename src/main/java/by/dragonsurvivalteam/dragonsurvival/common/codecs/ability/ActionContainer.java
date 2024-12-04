package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability;

import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncStopCast;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MagicData;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting.AbilityTargeting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

public record ActionContainer(AbilityTargeting effect, LevelBasedValue triggerRate, Optional<ManaCost> manaCost) {
    public static Codec<ActionContainer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AbilityTargeting.CODEC.fieldOf("target_selection").forGetter(ActionContainer::effect),
            LevelBasedValue.CODEC.fieldOf("trigger_rate").forGetter(ActionContainer::triggerRate),
            ManaCost.CODEC.optionalFieldOf("mana_cost").forGetter(ActionContainer::manaCost)
    ).apply(instance, ActionContainer::new));

    // Passive: Triggered per tick
    // Active (simple): Triggered once on key press (trigger_rate is not relevant in this case)
    // Active (channeled): Triggered per tick while key is being held
    public void tick(final ServerPlayer dragon, final DragonAbilityInstance instance, int currentTick) {
        float rate = triggerRate.calculate(instance.level());

        if (rate > 0 && currentTick % rate != 0) {
            return;
        }

        /* TODO :: how to handle mana cost properly
            - ticking cannot be used for simple activation - validate?
            - reserved can only realistically be used for passive (since this action container holds no duration)
                - having some mana switch between reserved and un-reserved while playing doesn't sound enticing anyway
                - when is the reservation applied / when is it disabled?
                - (add a check at the top for 'instance.isEnabled()' and if it returns false, remove the reservation?)
                - (do reservations need an id to keep track of them? i.e. add resource location to codec? would need Codec.either?)
                - (should we simplify this whole ability -> list of action containers -> list of target selections -> list of applied effects construct?)
            - move mana cost to activation? can still be balanced for the ability (list of effects)
        */

        DragonAbility.Type abilityType = instance.ability().value().type();
        ManaCost.Type manaCostType = manaCost.map(ManaCost::type).orElse(null);

        if (manaCostType == ManaCost.Type.TICKING && (abilityType == DragonAbility.Type.PASSIVE || abilityType == DragonAbility.Type.ACTIVE_CHANNELED)) {
            float cost = manaCost.get().manaCost().calculate(instance.level());

            if (ManaHandler.hasEnoughMana(dragon, cost)) {
                ManaHandler.consumeMana(dragon, cost); // TODO :: make this return a boolean and remove 'hasEnoughMana'?
            } else {
                stopCasting(dragon, instance);
                return;
            }
        }

        effect.apply(dragon, instance);

        if (abilityType == DragonAbility.Type.ACTIVE_SIMPLE) {
            stopCasting(dragon, instance);
        }
    }

    private void stopCasting(final ServerPlayer dragon, final DragonAbilityInstance instance) {
        instance.release(dragon);
        MagicData magicData = MagicData.getData(dragon);
        magicData.stopCasting(dragon);
        // TODO: We can send back the reason we failed here to the client
        PacketDistributor.sendToPlayer(dragon, new SyncStopCast(dragon.getId(), false));
    }
}
