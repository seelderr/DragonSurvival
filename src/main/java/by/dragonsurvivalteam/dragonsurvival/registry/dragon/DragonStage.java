package by.dragonsurvivalteam.dragonsurvival.registry.dragon;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.MiscCodecs;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.Modifier;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.advancements.critereon.EntityPredicate;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record DragonStage(
        MiscCodecs.Bounds sizeRange,
        int ticksUntilGrown,
        Optional<ResourceKey<DragonStage>> nextStage,
        List<Modifier> modifiers,
        int harvestLevelBonus,
        double breakSpeedMultiplier,
        List<MiscCodecs.GrowthItem> growthItems,
        Optional<EntityPredicate> isNaturalGrowthStopped,
        Optional<EntityPredicate> growIntoRequirements
) implements AttributeModifierSupplier {
    public static final ResourceKey<Registry<DragonStage>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvival.res("dragon_stages"));

    public static final Codec<DragonStage> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MiscCodecs.bounds().fieldOf("size_range").forGetter(DragonStage::sizeRange),
            ExtraCodecs.intRange(20, Functions.daysToTicks(365)).fieldOf("ticks_until_grown").forGetter(DragonStage::ticksUntilGrown),
            ResourceKey.codec(DragonStage.REGISTRY).optionalFieldOf("next_stage").forGetter(DragonStage::nextStage),
            Modifier.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(DragonStage::modifiers),
            ExtraCodecs.intRange(0, 4).optionalFieldOf("harvest_level_bonus", 0).forGetter(DragonStage::harvestLevelBonus),
            MiscCodecs.doubleRange(1, 10).optionalFieldOf("break_speed_multiplier", 1d).forGetter(DragonStage::breakSpeedMultiplier),
            MiscCodecs.GrowthItem.CODEC.listOf().optionalFieldOf("growth_items", List.of()).forGetter(DragonStage::growthItems),
            EntityPredicate.CODEC.optionalFieldOf("is_natural_growth_stopped").forGetter(DragonStage::isNaturalGrowthStopped),
            EntityPredicate.CODEC.optionalFieldOf("grow_into_requirements").forGetter(DragonStage::growIntoRequirements)
    ).apply(instance, instance.stable(DragonStage::new)));

    public static final Codec<Holder<DragonStage>> CODEC = RegistryFixedCodec.create(REGISTRY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<DragonStage>> STREAM_CODEC = ByteBufCodecs.holderRegistry(REGISTRY);

    /** Currently used for certain mechanics / animations */
    public static final double MAX_HANDLED_SIZE = 60; // TODO :: remove

    private static DragonStage smallest;
    private static DragonStage largest;

    public static void update(@Nullable final HolderLookup.Provider provider) {
        Pair<DragonStage, DragonStage> sizes = getSizes(provider);
        smallest = sizes.first();
        largest = sizes.second();

        validate(provider);
    }

    @SuppressWarnings("DataFlowIssue") // ignore
    private static void validate(@Nullable final HolderLookup.Provider provider) {
        boolean areBuiltInLevelsValid = true;
        StringBuilder builtInCheck = new StringBuilder("The following required built-in dragon levels are missing:");

        //noinspection ConstantValue -> ignore for clarity
        areBuiltInLevelsValid = areBuiltInLevelsValid && isValid(builtInCheck, provider, DragonStages.newborn);
        areBuiltInLevelsValid = areBuiltInLevelsValid && isValid(builtInCheck, provider, DragonStages.young);
        areBuiltInLevelsValid = areBuiltInLevelsValid && isValid(builtInCheck, provider, DragonStages.adult);

        if (!areBuiltInLevelsValid) {
            throw new IllegalStateException(builtInCheck.toString());
        }

        StringBuilder nextStageCheck = new StringBuilder("The following stages are incorrectly defined:");
        AtomicBoolean areStagesValid = new AtomicBoolean();

        keys(provider).forEach(key -> {
            //noinspection OptionalGetWithoutIsPresent -> ignore
            Holder.Reference<DragonStage> stage = get(provider, key).get();
            ResourceKey<DragonStage> nextStage = stage.value().nextStage().orElse(null);

            if (nextStage == null) {
                return;
            }

            Optional<Holder.Reference<DragonStage>> optional = get(provider, nextStage);

            if (optional.isEmpty()) {
                nextStageCheck.append("\n- The next stage [").append(nextStage.location()).append("] of [").append(stage.getKey().location()).append("] is not present");
                areStagesValid.set(false);
            } else if (stage.value().sizeRange().max() != optional.get().value().sizeRange().min()) {
                nextStageCheck.append("\n- Max. size of [").append(stage.getKey().location()).append("] does not match min. size of [").append(nextStage.location()).append("]");
                areStagesValid.set(false);
            }
        });

        if (!areStagesValid.get()) {
            throw new IllegalStateException(nextStageCheck.toString());
        }
    }

    private static boolean isValid(final StringBuilder builder, @Nullable final HolderLookup.Provider provider, final ResourceKey<DragonStage> levelKey) {
        Optional<Holder.Reference<DragonStage>> optional = get(provider, levelKey);

        if (optional.isPresent()) {
            return true;
        } else {
            builder.append("\n- ").append(levelKey.location());
            return false;
        }
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

    public static boolean isBuiltinLevel(final ResourceKey<DragonStage> dragonLevel) {
        return dragonLevel == DragonStages.newborn || dragonLevel == DragonStages.young || dragonLevel == DragonStages.adult;
    }

    public static Component translatableName(final ResourceKey<DragonStage> dragonLevel) {
        return Component.translatable(Translation.Type.STAGE.wrap(dragonLevel.location().getNamespace(), dragonLevel.location().getPath()));
    }

    public static Component translatableDescription(final ResourceKey<DragonStage> dragonLevel) {
        return Component.translatable(Translation.Type.STAGE_DESCRIPTION.wrap(dragonLevel.location().getNamespace(), dragonLevel.location().getPath()));
    }

    /** Used for the skin texture name */
    public static String name(final Holder<DragonStage> level) {
        return level.getRegisteredName().replace(":", ".");
    }

    public static List<ResourceKey<DragonStage>> keys(final HolderLookup.Provider provider) {
        HolderLookup.RegistryLookup<DragonStage> registry;

        if (provider == null) {
            registry = CommonHooks.resolveLookup(REGISTRY);
        } else {
            registry = provider.lookupOrThrow(REGISTRY);
        }

        //noinspection DataFlowIssue -> registry is expected to be present
        return registry.listElementIds().toList();
    }

    public double getSizeWithinRange(double size) {
        return Math.clamp(size, sizeRange().min(), sizeRange().max());
    }

    /** Returns the size that is clamped to the smallest and largest dragon sizes */
    public static double getBoundedSize(double size) {
        return Math.clamp(size, smallest.sizeRange().min(), largest.sizeRange().max());
    }

    /** Returns the bounds between the smallest and largest dragon sizes */
    public static MiscCodecs.Bounds getBounds() {
        return new MiscCodecs.Bounds(smallest.sizeRange().min(), largest.sizeRange().max());
    }

    public static Optional<Holder.Reference<DragonStage>> get(@Nullable final HolderLookup.Provider provider, final ResourceKey<DragonStage> key) {
        HolderLookup.RegistryLookup<DragonStage> registry;

        if (provider == null) {
            registry = CommonHooks.resolveLookup(REGISTRY);
        } else {
            registry = provider.lookupOrThrow(REGISTRY);
        }

        return Objects.requireNonNull(registry).get(key);
    }

    public static Holder<DragonStage> get(@Nullable final HolderLookup.Provider provider, double size) {
        HolderLookup.RegistryLookup<DragonStage> registry;

        if (provider == null) {
            registry = CommonHooks.resolveLookup(REGISTRY);
        } else {
            registry = provider.lookupOrThrow(REGISTRY);
        }

        double fallbackDifference = Double.MAX_VALUE;
        Holder<DragonStage> fallback = null;

        //noinspection DataFlowIssue -> registry is expected to be present
        for (Holder.Reference<DragonStage> level : registry.listElements().toList()) {
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

    private static Pair<DragonStage, DragonStage> getSizes(@Nullable final HolderLookup.Provider provider) {
        HolderLookup.RegistryLookup<DragonStage> registry;

        if (provider == null) {
            registry = CommonHooks.resolveLookup(REGISTRY);
        } else {
            registry = provider.lookupOrThrow(REGISTRY);
        }

        DragonStage smallest = null;
        DragonStage largest = null;

        for (Holder.Reference<DragonStage> level : Objects.requireNonNull(registry).listElements().toList()) {
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
