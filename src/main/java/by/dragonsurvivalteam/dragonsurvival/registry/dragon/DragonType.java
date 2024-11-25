package by.dragonsurvivalteam.dragonsurvival.registry.dragon;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record DragonType(
        List<DragonAbility> abilities)
{
    public static final ResourceKey<Registry<DragonType>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvival.res("dragon_types"));

    public static final Codec<DragonType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DragonAbility.DIRECT_CODEC.listOf().optionalFieldOf("abilities", List.of()).forGetter(DragonType::abilities)
    ).apply(instance, instance.stable(DragonType::new)));

    @SubscribeEvent
    public static void register(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY, DIRECT_CODEC, DIRECT_CODEC);
    }

    public static void update(@Nullable final HolderLookup.Provider provider) {
        validate(provider);
    }

    private static final HashSet<DragonAbility.AbilitySlot> REQUIRED_SLOTS = new HashSet<>();
    static {
        REQUIRED_SLOTS.add(new DragonAbility.AbilitySlot(0, DragonAbility.AbilityType.ACTIVE));
        REQUIRED_SLOTS.add(new DragonAbility.AbilitySlot(1, DragonAbility.AbilityType.ACTIVE));
        REQUIRED_SLOTS.add(new DragonAbility.AbilitySlot(2, DragonAbility.AbilityType.ACTIVE));
        REQUIRED_SLOTS.add(new DragonAbility.AbilitySlot(3, DragonAbility.AbilityType.ACTIVE));
        REQUIRED_SLOTS.add(new DragonAbility.AbilitySlot(0, DragonAbility.AbilityType.PASSIVE));
        REQUIRED_SLOTS.add(new DragonAbility.AbilitySlot(1, DragonAbility.AbilityType.PASSIVE));
        REQUIRED_SLOTS.add(new DragonAbility.AbilitySlot(2, DragonAbility.AbilityType.PASSIVE));
        REQUIRED_SLOTS.add(new DragonAbility.AbilitySlot(3, DragonAbility.AbilityType.PASSIVE));
        REQUIRED_SLOTS.add(new DragonAbility.AbilitySlot(0, DragonAbility.AbilityType.INNATE));
        REQUIRED_SLOTS.add(new DragonAbility.AbilitySlot(1, DragonAbility.AbilityType.INNATE));
        REQUIRED_SLOTS.add(new DragonAbility.AbilitySlot(2, DragonAbility.AbilityType.INNATE));
        REQUIRED_SLOTS.add(new DragonAbility.AbilitySlot(3, DragonAbility.AbilityType.INNATE));
    }

    private static void validate(@Nullable final HolderLookup.Provider provider) {
        StringBuilder nextAbilityCheck = new StringBuilder("The following types are incorrectly defined:");
        AtomicBoolean areAbilitiesValid = new AtomicBoolean(true);

        ResourceHelper.keys(provider, REGISTRY).forEach(key -> {
            //noinspection OptionalGetWithoutIsPresent -> ignore
            Holder.Reference<DragonType> type = ResourceHelper.get(provider, key, REGISTRY).get();

            HashSet<DragonAbility.AbilitySlot> slotsUsed = new HashSet<>();
            type.value().abilities().forEach(ability -> slotsUsed.add(ability.slot()));
            if (!slotsUsed.containsAll(REQUIRED_SLOTS)) {
                nextAbilityCheck.append("\n- Type [").append(key.location()).append("] is missing required abilities");
                areAbilitiesValid.set(false);
            }

            if (!REQUIRED_SLOTS.containsAll(slotsUsed)) {
                nextAbilityCheck.append("\n- Type [").append(key.location()).append("] has extra abilities that are not required");
                areAbilitiesValid.set(false);
            }
        });

        if (!areAbilitiesValid.get()) {
            throw new IllegalStateException(nextAbilityCheck.toString());
        }
    }
}
