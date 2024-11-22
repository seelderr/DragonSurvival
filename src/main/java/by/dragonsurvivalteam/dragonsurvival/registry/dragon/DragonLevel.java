package by.dragonsurvivalteam.dragonsurvival.registry.dragon;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.MiscCodecs;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.Modifier;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record DragonLevel(
        MiscCodecs.Bounds sizeRange,
        int ticksUntilGrown,
        List<Modifier> modifiers,
        int harvestLevelBonus,
        double breakSpeedMultiplier,
        List<MiscCodecs.GrowthItem> growthItems
) implements AttributeModifierSupplier {
    public static final ResourceKey<Registry<DragonLevel>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvival.res("dragon_levels"));

    public static final Codec<DragonLevel> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MiscCodecs.bounds().fieldOf("size_range").forGetter(DragonLevel::sizeRange),
            ExtraCodecs.intRange(20, Functions.daysToTicks(30)).fieldOf("ticks_until_grown").forGetter(DragonLevel::ticksUntilGrown),
            Modifier.CODEC.listOf().fieldOf("modifiers").forGetter(DragonLevel::modifiers),
            ExtraCodecs.intRange(0, 4).fieldOf("harvest_level_bonus").forGetter(DragonLevel::harvestLevelBonus),
            MiscCodecs.doubleRange(1, 10).fieldOf("break_speed_multiplier").forGetter(DragonLevel::breakSpeedMultiplier),
            MiscCodecs.GrowthItem.CODEC.listOf().optionalFieldOf("growth_items", List.of()).forGetter(DragonLevel::growthItems)
    ).apply(instance, instance.stable(DragonLevel::new)));

    public static final Codec<Holder<DragonLevel>> CODEC = RegistryFixedCodec.create(REGISTRY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<DragonLevel>> STREAM_CODEC = ByteBufCodecs.holderRegistry(REGISTRY);

    /** Currently used for certain mechanics / animations */
    public static final double MAX_HANDLED_SIZE = 60; // TODO level :: remove

    private static DragonLevel smallest;
    private static DragonLevel largest;

    public static void update(@Nullable final HolderLookup.Provider provider) {
        Pair<DragonLevel, DragonLevel> sizes = getSizes(provider);
        smallest = sizes.first();
        largest = sizes.second();

        StringBuilder builder = new StringBuilder();
        checkLevel(builder, provider, DragonLevels.newborn);
        checkLevel(builder, provider, DragonLevels.young);
        checkLevel(builder, provider, DragonLevels.adult);

        if (!builder.isEmpty()) {
            throw new IllegalStateException("The following built-in dragon levels are missing, resulting in an invalid game state:" + builder);
        }
    }

    private static void checkLevel(final StringBuilder builder, @Nullable final HolderLookup.Provider provider, final ResourceKey<DragonLevel> levelKey) {
        get(provider, levelKey).ifPresentOrElse(level -> { /* Nothing to do */ }, () -> builder.append("\n- ").append(levelKey.location()));
    }

    public double ticksToSize(int ticks) {
        return (sizeRange().max() - sizeRange().min()) / ticksUntilGrown() * ticks;
    }

    public double getProgress(double size) {
        return ((size - sizeRange().min())) / (sizeRange().max() - sizeRange().min());
    }

    @SubscribeEvent
    public static void register(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY, DIRECT_CODEC, DIRECT_CODEC);
    }

    public static boolean isBuiltinLevel(final ResourceKey<DragonLevel> dragonLevel) {
        return dragonLevel == DragonLevels.newborn || dragonLevel == DragonLevels.young || dragonLevel == DragonLevels.adult;
    }

    public static Component translatableName(final ResourceKey<DragonLevel> dragonLevel) {
        return Component.translatable(Translation.Type.LEVEL.wrap(dragonLevel.location().getNamespace(), dragonLevel.location().getPath()));
    }

    public static Component translatableDescription(final ResourceKey<DragonLevel> dragonLevel) {
        return Component.translatable(Translation.Type.LEVEL_DESCRIPTION.wrap(dragonLevel.location().getNamespace(), dragonLevel.location().getPath()));
    }

    /** Used for the skin texture name */
    public static String name(final Holder<DragonLevel> level) {
        return level.getRegisteredName().replace(":", ".");
    }

    public static List<ResourceKey<DragonLevel>> keys(final HolderLookup.Provider provider) {
        HolderLookup.RegistryLookup<DragonLevel> registry;

        if (provider == null) {
            registry = CommonHooks.resolveLookup(REGISTRY);
        } else {
            registry = provider.lookupOrThrow(REGISTRY);
        }

        //noinspection DataFlowIssue -> registry is expected to be present
        return registry.listElementIds().toList();
    }

    public static double getBoundedSize(double size) {
        return Math.clamp(size, smallest.sizeRange().min(), largest.sizeRange().max());
    }

    public static MiscCodecs.Bounds getBounds() {
        return new MiscCodecs.Bounds(smallest.sizeRange().min(), largest.sizeRange().max());
    }

    public static Optional<Holder.Reference<DragonLevel>> get(@Nullable final HolderLookup.Provider provider, final ResourceKey<DragonLevel> key) {
        HolderLookup.RegistryLookup<DragonLevel> registry;

        if (provider == null) {
            registry = CommonHooks.resolveLookup(REGISTRY);
        } else {
            registry = provider.lookupOrThrow(REGISTRY);
        }

        return Objects.requireNonNull(registry).get(key);
    }

    public static Holder<DragonLevel> get(@Nullable final HolderLookup.Provider provider, double size) {
        HolderLookup.RegistryLookup<DragonLevel> registry;

        if (provider == null) {
            registry = CommonHooks.resolveLookup(REGISTRY);
        } else {
            registry = provider.lookupOrThrow(REGISTRY);
        }

        double fallbackDifference = Double.MAX_VALUE;
        Holder<DragonLevel> fallback = null;

        //noinspection DataFlowIssue -> registry is expected to be present
        for (Holder.Reference<DragonLevel> level : registry.listElements().toList()) {
            if (level.value().sizeRange().matches(size)) {
                return level;
            }

            double difference = Math.abs(level.value().sizeRange().min() - size);

            if (fallback == null || difference < fallbackDifference) {
                fallbackDifference = difference;
                fallback = level;
            }
        }

        if (fallback != null) {
            DragonSurvival.LOGGER.warn("No matching dragon level found for size [{}] - using [{}] as fallback", size, fallback.getRegisteredName());
            return fallback;
        }

        throw new IllegalStateException("There is no valid dragon level for the supplied size [" + size + "]");
    }

    private static Pair<DragonLevel, DragonLevel> getSizes(@Nullable final HolderLookup.Provider provider) {
        HolderLookup.RegistryLookup<DragonLevel> registry;

        if (provider == null) {
            registry = CommonHooks.resolveLookup(REGISTRY);
        } else {
            registry = provider.lookupOrThrow(REGISTRY);
        }

        DragonLevel smallest = null;
        DragonLevel largest = null;

        for (Holder.Reference<DragonLevel> level : Objects.requireNonNull(registry).listElements().toList()) {
            if (smallest == null || level.value().sizeRange().min() < smallest.sizeRange().min()) {
                smallest = level.value();
            }

            if (largest == null || level.value().sizeRange().max() > largest.sizeRange().max()) {
                largest = level.value();
            }
        }

        return Pair.of(smallest, largest);
    }
}
