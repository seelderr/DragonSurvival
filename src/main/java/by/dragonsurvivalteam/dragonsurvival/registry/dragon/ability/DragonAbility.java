package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Activation;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.EffectContainer;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Upgrade;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
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
        // TODO :: depending on the logic, mark an entity / block with an outline (as target)?
        // TODO :: for passive add a button in ability ui to deactivate them?
        Optional<Activation> activation,
        Optional<Upgrade> upgrade,
        Optional<EntityPredicate> usageBlocked,
        List<EffectContainer> effects,
        ResourceLocation icon,
        Component description
) {
    public static final ResourceKey<Registry<DragonAbility>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvival.res("dragon_abilities"));

    public static final Codec<DragonAbility> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Activation.CODEC.optionalFieldOf("activation").forGetter(DragonAbility::activation),
            Upgrade.CODEC.optionalFieldOf("upgrade").forGetter(DragonAbility::upgrade),
            EntityPredicate.CODEC.optionalFieldOf("usage_blocked").forGetter(DragonAbility::usageBlocked), // TODO :: e.g. when the ability is not supposed to be used underwater
            EffectContainer.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(DragonAbility::effects),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(DragonAbility::icon),
            // TODO: How do we handle descriptions that are fed various values from the ability itself?
            ComponentSerialization.CODEC.fieldOf("description").forGetter(DragonAbility::description)
    ).apply(instance, instance.stable(DragonAbility::new)));

    public static final Codec<Holder<DragonAbility>> CODEC = RegistryFixedCodec.create(REGISTRY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<DragonAbility>> STREAM_CODEC = ByteBufCodecs.holderRegistry(REGISTRY);

    public static final int MAX_ACTIVE = 4;
    public static final int MAX_PASSIVE = 8;

    public DragonAbility {
        // Usage as direct field access since that relates to the (invisible) constructor parameters
        // (Method call would be null at this point in time)
        Activation.Type type = activation.map(Activation::type).orElse(null);

        effects.forEach(target -> target.effect().target()
                .ifLeft(block -> block.effect().forEach(effect -> validate(type, effect)))
                .ifRight(entity -> entity.effect().forEach(effect -> validate(type, effect))));
    }

    public AbilityInfo.Type type() {
        return getType(activation().map(Activation::type).orElse(null));
    }

    public int getCooldown(int abilityLevel) {
        // TODO :: We should probably use Minecraft's way of handling time (meaning measure things in ticks (int))
        return activation().map(activation -> activation.cooldown().map(cooldown -> cooldown.calculate(abilityLevel)).orElse(0f)).orElse(0f).intValue();
    }

    @SubscribeEvent
    public static void register(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY, DIRECT_CODEC, DIRECT_CODEC);
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

    private void validate(final Activation.Type activationType, final Object effect) {
        AbilityInfo abilityInfo = effect.getClass().getAnnotation(AbilityInfo.class);
        AbilityInfo.Type abilityType = getType(activationType);

        for (AbilityInfo.Type type : abilityInfo.compatibleWith()) {
            if (type == abilityType) {
                return;
            }
        }

        throw new IllegalStateException("Invalid effect [" + effect + "] for the activation type of ability [" + this + "]");
    }

    private AbilityInfo.Type getType(final Activation.Type activationType) {
        if (activationType == null) {
            return AbilityInfo.Type.PASSIVE;
        } else if (activationType == Activation.Type.SIMPLE) {
            return AbilityInfo.Type.ACTIVE_SIMPLE;
        }

        return AbilityInfo.Type.ACTIVE_CHANNELED;
    }
}
