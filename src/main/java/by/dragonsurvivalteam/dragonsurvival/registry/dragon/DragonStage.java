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
        AtomicBoolean areStagesValid = new AtomicBoolean(true);

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

    private static boolean isValid(final StringBuilder builder, @Nullable final HolderLookup.Provider provider, final ResourceKey<DragonStage> stageKey) {
        Optional<Holder.Reference<DragonStage>> optional = get(provider, stageKey);

        if (optional.isPresent()) {
            return true;
        } else {
            builder.append("\n- ").append(stageKey.location());
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

    public static boolean isBuiltinLevel(final ResourceKey<DragonStage> dragonStage) {
        return dragonStage == DragonStages.newborn || dragonStage == DragonStages.young || dragonStage == DragonStages.adult;
    }

    public static Component translatableName(final ResourceKey<DragonStage> dragonStage) {
        return Component.translatable(Translation.Type.STAGE.wrap(dragonStage.location().getNamespace(), dragonStage.location().getPath()));
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

    public double getBoundedSize(double size) {
        return Math.clamp(size, sizeRange().min(), sizeRange().max());
    }

    /**
     * Returns a valid size (see {@link DragonStage#getValidSize(double)}) <br>
     * <br> If the size is larger than the max. size of the current dragon stage: <br>
     *   - The {@link DragonStage#getBoundedSize(double)} of the matching dragon stage of the next dragon stage chain <br>
     * <br> If the size is smaller than the min. size of the current dragon stage: <br>
     *   - The current (valid) size if a previous stage is present, and its size bounds matches the current size <br>
     *   - The {@link DragonStage#getBoundedSize(double)} of a matching dragon stage <br>
     * <br> Otherwise (if the size is within the bounds of the current stage) the current (valid) size will be returned
     */
    public double getNextSize(@Nullable final HolderLookup.Provider provider, double size, @Nullable final DragonStage previousStage) {
        double newSize = getValidSize(size);

        if (newSize > sizeRange().max()) {
            return getNextStage(provider, this).map(nextStage -> nextStage.value().getNextSize(provider, newSize, this)).orElseGet(() -> getBoundedSize(newSize));
        } else if (newSize < sizeRange().min()) {
            if (previousStage != null && previousStage.sizeRange().matches(newSize)) {
                return newSize;
            }

            return DragonStage.get(provider, newSize).value().getBoundedSize(newSize);
        }

        return newSize;
    }

    /** Returns a valid size (meaning a size within the bounds of the smallest and largest dragon) */
    public static double getValidSize(double size) {
        return Math.clamp(size, smallest.sizeRange.min(), largest.sizeRange().max());
    }

    /** Returns the bounds between the smallest and largest dragon sizes */
    public static MiscCodecs.Bounds getBounds() {
        return new MiscCodecs.Bounds(smallest.sizeRange().min(), largest.sizeRange().max());
    }

    public static Optional<Holder.Reference<DragonStage>> getNextStage(@Nullable final HolderLookup.Provider provider, final DragonStage stage) {
        return stage.nextStage().flatMap(nextStage -> get(provider, nextStage));
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
