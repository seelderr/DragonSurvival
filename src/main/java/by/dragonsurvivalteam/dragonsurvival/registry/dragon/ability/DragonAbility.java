package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.LevelBasedResource;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Activation;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.ActionContainer;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Upgrade;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
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
        List<ActionContainer> effects,
        LevelBasedResource icon,
        String description
) {
    public enum Type {
        PASSIVE,
        ACTIVE_SIMPLE,
        ACTIVE_CHANNELED
    }

    /* TODO ::
        when / how do we reset applied modifications?
        - e.g. dragon changes -> could remove the data attachment (entity.removeData(...))
        - but what if the data is changed and an effect is removed or the id was changed -> there is not really a way to track that, is there?
            (do we need to consider that? or consider it an unsafe / unstable modification?)

        let's say there is a passive ability effect with an area target and infinite duration on application
        - should said effect only be present when the other entity is within the target area?
        - would passive abilities always have an infinite duration (while mana is present) for the dragon itself? unsure how this all should work atm
    */

    public static final ResourceKey<Registry<DragonAbility>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvival.res("dragon_abilities"));

    public static final Codec<DragonAbility> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Activation.CODEC.optionalFieldOf("activation").forGetter(DragonAbility::activation),
            Upgrade.CODEC.optionalFieldOf("upgrade").forGetter(DragonAbility::upgrade),
            EntityPredicate.CODEC.optionalFieldOf("usage_blocked").forGetter(DragonAbility::usageBlocked), // TODO :: e.g. when the ability is not supposed to be used underwater
            ActionContainer.CODEC.listOf().optionalFieldOf("actions", List.of()).forGetter(DragonAbility::effects),
            LevelBasedResource.CODEC.fieldOf("icon").forGetter(DragonAbility::icon),
            // TODO: How do we handle descriptions that are fed various values from the ability itself?
            Codec.STRING.fieldOf("description").forGetter(DragonAbility::description)
    ).apply(instance, instance.stable(DragonAbility::new)));

    public static final Codec<Holder<DragonAbility>> CODEC = RegistryFixedCodec.create(REGISTRY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<DragonAbility>> STREAM_CODEC = ByteBufCodecs.holderRegistry(REGISTRY);

    public static final int MAX_ACTIVE_ON_HOTBAR = 4;
    public static final int MAX_PASSIVE = 8;

    public Type type() {
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

    private Type getType(final Activation.Type activationType) {
        if (activationType == null) {
            return Type.PASSIVE;
        } else if (activationType == Activation.Type.SIMPLE) {
            return Type.ACTIVE_SIMPLE;
        }

        return Type.ACTIVE_CHANNELED;
    }
}
