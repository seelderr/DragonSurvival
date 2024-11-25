package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Activation;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Effect;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Penalty;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Upgrade;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record DragonAbility(
        // On activation go through the list of effects and check the initial mana cost
        Optional<Activation> activation,
        Optional<Upgrade> upgrade,
        // TODO :: should this be usage_blocked? Might be easier to define a blacklist than a whitelist
        Optional<EntityPredicate> usageConditions,
        List<Effect> effects,
        List<Penalty> penalties, // TODO :: what is the purpose of this? sounds more like a thing for the dragon type?
        ResourceLocation icon,
        Component description
) {
    public static final ResourceKey<Registry<DragonAbility>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvival.res("dragon_abilities"));

    public static final Codec<DragonAbility> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Activation.CODEC.optionalFieldOf("activation").forGetter(DragonAbility::activation),
            // TODO: We can remove the innate claw abilities from the DragonStages class and just add them here instead
            Upgrade.CODEC.optionalFieldOf("upgrade").forGetter(DragonAbility::upgrade),
            EntityPredicate.CODEC.optionalFieldOf("usage_conditions").forGetter(DragonAbility::usageConditions),
            Effect.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(DragonAbility::effects),
            Penalty.CODEC.listOf().optionalFieldOf("penalties", List.of()).forGetter(DragonAbility::penalties),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(DragonAbility::icon),
            // TODO: How do we handle descriptions that are fed various values from the ability itself?
            ComponentSerialization.CODEC.fieldOf("description").forGetter(DragonAbility::description)
    ).apply(instance, instance.stable(DragonAbility::new)));

    /*
        The slot will not be specified in the ability
        Players will be able to assign / customize the slot while playing
        For the GUI we can have them move them around as well
        (this was a thing in the past, currently it is kind of broken in the ability screen)
    */

    // TODO :: Can we define the dragon ability in way that this is no longer needed?
    //  At the least we might want to move away from differentiating between 'PASSIVE' and 'INNATE'
    //  Active can be it's own thing due to the active ability slots
    //  The dragon can then have 8 'PASSIVE' abilities which are a separate class & codec (no Activation trigger)
    //    Or we make Activation optional in here and if that is the case it is considered a PASSIVE ability, otherwise an ACTIVE one
    public enum Type {
        PASSIVE,
        ACTIVE
    }

    @SubscribeEvent
    public static void register(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY, DIRECT_CODEC, DIRECT_CODEC);
    }

    public Type type() {
        return activation().isPresent() ? Type.ACTIVE : Type.PASSIVE;
    }

    public static void update(@Nullable final HolderLookup.Provider provider) {
        validate(provider);
    }

    private static void validate(@Nullable final HolderLookup.Provider provider) {
        StringBuilder nextAbilityCheck = new StringBuilder("The following abilities are incorrectly defined:");
        AtomicBoolean areAbilitiesValid = new AtomicBoolean(true);

        ResourceHelper.keys(provider, REGISTRY).forEach(key -> {
            //noinspection OptionalGetWithoutIsPresent -> ignore
            Holder.Reference<DragonAbility> ability = ResourceHelper.get(provider, key, REGISTRY).get();

//            if(ability.value().slot.type != Type.ACTIVE && ability.value().effect.isPresent()) {
//                nextAbilityCheck.append("\n- Ability [").append(key.location()).append("] has an active effect but is not defined as active");
//                areAbilitiesValid.set(false);
//            }
//
//            if(ability.value().slot.type == Type.PASSIVE && ability.value().passiveModifiers.isEmpty()) {
//                nextAbilityCheck.append("\n- Ability [").append(key.location()).append("] has no passive modifiers but is defined as passive");
//                areAbilitiesValid.set(false);
//            }
//
//            if(ability.value().slot.type == Type.PASSIVE && ability.value().upgradeCost.isPresent()) {
//                nextAbilityCheck.append("\n- Ability [").append(key.location()).append("] has an upgrade cost but is defined as passive");
//                areAbilitiesValid.set(false);
//            }
//
//            if(ability.value().slot.type != Type.INNATE && ability.value().penalties.isEmpty()) {
//                nextAbilityCheck.append("\n- Ability [").append(key.location()).append("] has a penalty effect but is not defined as innate");
//                areAbilitiesValid.set(false);
//            }
        });

        if (!areAbilitiesValid.get()) {
            throw new IllegalStateException(nextAbilityCheck.toString());
        }
    }
}
