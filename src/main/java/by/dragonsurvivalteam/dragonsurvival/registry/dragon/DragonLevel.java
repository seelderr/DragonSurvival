package by.dragonsurvivalteam.dragonsurvival.registry.dragon;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.DSAttributeModifier;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.MiscCodecs;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ModifierType;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ScalingAttributeModifier;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAttributes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record DragonLevel(
        MiscCodecs.Bounds sizeRange,
        int ticksUntilGrown,
        List<DSAttributeModifier> modifiers,
        List<ScalingAttributeModifier> scalingModifiers,
        int harvestLevelBonus,
        double breakSpeedMultiplier,
        List<MiscCodecs.GrowthItem> growthItems
) implements AttributeModifierSupplier {
    public static final ResourceKey<Registry<DragonLevel>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvival.res("dragon_levels"));

    public static final Codec<DragonLevel> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MiscCodecs.bounds().fieldOf("size_range").forGetter(DragonLevel::sizeRange),
            ExtraCodecs.intRange(20, Functions.daysToTicks(30)).fieldOf("ticks_until_grown").forGetter(DragonLevel::ticksUntilGrown),
            DSAttributeModifier.CODEC.listOf().fieldOf("modifiers").forGetter(DragonLevel::modifiers),
            ScalingAttributeModifier.CODEC.listOf().fieldOf("scaling_modifiers").forGetter(DragonLevel::scalingModifiers),
            ExtraCodecs.intRange(0, 4).fieldOf("harvest_level_bonus").forGetter(DragonLevel::harvestLevelBonus),
            MiscCodecs.doubleRange(1, 10).fieldOf("break_speed_multiplier").forGetter(DragonLevel::breakSpeedMultiplier),
            MiscCodecs.GrowthItem.CODEC.listOf().optionalFieldOf("growth_items", List.of()).forGetter(DragonLevel::growthItems)
    ).apply(instance, instance.stable(DragonLevel::new)));

    public static final Codec<Holder<DragonLevel>> CODEC = RegistryFixedCodec.create(REGISTRY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<DragonLevel>> STREAM_CODEC = ByteBufCodecs.holderRegistry(REGISTRY);

    @Translation(type = Translation.Type.LEVEL, comments = "Newborn")
    public static ResourceKey<DragonLevel> newborn = key("newborn");

    @Translation(type = Translation.Type.LEVEL, comments = "Young")
    public static ResourceKey<DragonLevel> young = key("young");

    @Translation(type = Translation.Type.LEVEL, comments = "Adult")
    public static ResourceKey<DragonLevel> adult = key("adult");

    @Translation(type = Translation.Type.LEVEL, comments = "Ancient")
    public static ResourceKey<DragonLevel> ancient = key("ancient");

    // --- Actual fields --- //

    /** Currently used for certain mechanics / animations */
    public static final double MAX_HANDLED_SIZE = 60; // TODO level :: remove

    private static DragonLevel smallest;
    private static DragonLevel largest;

    public static void update(@Nullable final HolderLookup.Provider provider) {
        Pair<DragonLevel, DragonLevel> sizes = getSizes(provider);
        smallest = sizes.first();
        largest = sizes.second();
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

    public static void registerLevels(final BootstrapContext<DragonLevel> context) {
        context.register(newborn, new DragonLevel(
                new MiscCodecs.Bounds(14, 20),
                Functions.hoursToTicks(3),
                List.of(
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.SUBMERGED_MINING_SPEED, 1, AttributeModifier.Operation.ADD_VALUE, DragonTypes.SEA.getTypeNameLowerCase()),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.MAX_HEALTH, -7, AttributeModifier.Operation.ADD_VALUE),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.ATTACK_DAMAGE, 1, AttributeModifier.Operation.ADD_VALUE),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.JUMP_STRENGTH, 0.025, AttributeModifier.Operation.ADD_VALUE),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.SAFE_FALL_DISTANCE, 0.25, AttributeModifier.Operation.ADD_VALUE),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, DSAttributes.DRAGON_BREATH_RANGE, 1.5, AttributeModifier.Operation.ADD_VALUE)
                ),
                List.of(
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.MOVEMENT_SPEED, 0.0015f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.MAX_HEALTH, 0.5f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, DSAttributes.DRAGON_BREATH_RANGE, 0.05f, AttributeModifier.Operation.ADD_VALUE)
                ),
                0,
                1.5,
                List.of(
                        MiscCodecs.GrowthItem.create(Functions.minutesToTicks(30), DSItems.ELDER_DRAGON_HEART.value()),
                        MiscCodecs.GrowthItem.create(Functions.minutesToTicks(20), DSItems.WEAK_DRAGON_HEART.value()),
                        MiscCodecs.GrowthItem.create(Functions.minutesToTicks(10), DSItems.DRAGON_HEART_SHARD.value()),
                        MiscCodecs.GrowthItem.create(Functions.hoursToTicks(-1), DSItems.STAR_BONE.value())
                )
        ));

        context.register(young, new DragonLevel(
                new MiscCodecs.Bounds(20, 30),
                Functions.hoursToTicks(15),
                List.of(
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.SUBMERGED_MINING_SPEED, 2, AttributeModifier.Operation.ADD_VALUE, DragonTypes.SEA.getTypeNameLowerCase()),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.STEP_HEIGHT, 0.25, AttributeModifier.Operation.ADD_VALUE),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.ATTACK_DAMAGE, 2, AttributeModifier.Operation.ADD_VALUE),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.JUMP_STRENGTH, 0.05, AttributeModifier.Operation.ADD_VALUE),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.SAFE_FALL_DISTANCE, 0.5, AttributeModifier.Operation.ADD_VALUE),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, DSAttributes.DRAGON_BREATH_RANGE, 2.5, AttributeModifier.Operation.ADD_VALUE)
                ),
                List.of(
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.MOVEMENT_SPEED, 0.0015f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.ENTITY_INTERACTION_RANGE, 0.01f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.BLOCK_INTERACTION_RANGE, 0.01f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.MAX_HEALTH, 0.5f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, DSAttributes.DRAGON_BREATH_RANGE, 0.05f, AttributeModifier.Operation.ADD_VALUE)
                ),
                1,
                2,
                List.of(
                        MiscCodecs.GrowthItem.create(Functions.minutesToTicks(45), DSItems.ELDER_DRAGON_HEART.value()),
                        MiscCodecs.GrowthItem.create(Functions.minutesToTicks(25), DSItems.WEAK_DRAGON_HEART.value()),
                        MiscCodecs.GrowthItem.create(Functions.hoursToTicks(-1), DSItems.STAR_BONE.value())
                )
        ));

        context.register(adult, new DragonLevel(
                new MiscCodecs.Bounds(30, 40),
                Functions.hoursToTicks(24),
                List.of(
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.SUBMERGED_MINING_SPEED, 3, AttributeModifier.Operation.ADD_VALUE, DragonTypes.SEA.getTypeNameLowerCase()),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.STEP_HEIGHT, 0.5, AttributeModifier.Operation.ADD_VALUE),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.ATTACK_DAMAGE, 3, AttributeModifier.Operation.ADD_VALUE),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.JUMP_STRENGTH, 0.1, AttributeModifier.Operation.ADD_VALUE),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.SAFE_FALL_DISTANCE, 1, AttributeModifier.Operation.ADD_VALUE),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, DSAttributes.DRAGON_BREATH_RANGE, 4, AttributeModifier.Operation.ADD_VALUE)
                ),
                List.of(
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.MOVEMENT_SPEED, 0.0015f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.ENTITY_INTERACTION_RANGE, 0.01f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.BLOCK_INTERACTION_RANGE, 0.01f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.MAX_HEALTH, 0.5f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, DSAttributes.DRAGON_BREATH_RANGE, 0.05f, AttributeModifier.Operation.ADD_VALUE)
                ),
                1,
                3,
                List.of(
                        MiscCodecs.GrowthItem.create(Functions.minutesToTicks(30), DSItems.ELDER_DRAGON_HEART.value()),
                        MiscCodecs.GrowthItem.create(Functions.hoursToTicks(-1), DSItems.STAR_BONE.value())
                )
        ));

        context.register(ancient, new DragonLevel(
                new MiscCodecs.Bounds(40, 60),
                Functions.daysToTicks(30),
                List.of(
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.SUBMERGED_MINING_SPEED, 3, AttributeModifier.Operation.ADD_VALUE, DragonTypes.SEA.getTypeNameLowerCase()),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.STEP_HEIGHT, 0.5, AttributeModifier.Operation.ADD_VALUE),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.ATTACK_DAMAGE, 3, AttributeModifier.Operation.ADD_VALUE),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.JUMP_STRENGTH, 0.1, AttributeModifier.Operation.ADD_VALUE),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.SAFE_FALL_DISTANCE, 1, AttributeModifier.Operation.ADD_VALUE),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, DSAttributes.DRAGON_BREATH_RANGE, 4, AttributeModifier.Operation.ADD_VALUE)
                ),
                List.of(
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.MOVEMENT_SPEED, 0.0015f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.ENTITY_INTERACTION_RANGE, 0.01f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.BLOCK_INTERACTION_RANGE, 0.01f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.MAX_HEALTH, 0.5f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, DSAttributes.DRAGON_BREATH_RANGE, 0.05f, AttributeModifier.Operation.ADD_VALUE)
                ),
                1,
                3,
                List.of(
                        MiscCodecs.GrowthItem.create(Functions.hoursToTicks(1), DSItems.ELDER_DRAGON_HEART.value()),
                        MiscCodecs.GrowthItem.create(Functions.hoursToTicks(-1), DSItems.STAR_BONE.value())
                )
        ));
    }

    public static ResourceKey<DragonLevel> key(final ResourceLocation location) {
        return ResourceKey.create(REGISTRY, location);
    }

    private static ResourceKey<DragonLevel> key(final String path) {
        return key(DragonSurvival.res(path));
    }

    public static boolean isBuiltinLevel(final ResourceKey<DragonLevel> dragonLevel) {
        return dragonLevel == newborn || dragonLevel == young || dragonLevel == adult;
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
