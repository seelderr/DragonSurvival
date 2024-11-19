package by.dragonsurvivalteam.dragonsurvival.registry.dragon;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.DSAttributeModifier;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ModifierType;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ScalingAttributeModifier;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
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

import javax.annotation.Nullable;
import java.util.List;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record DragonLevel(MinMaxBounds.Doubles sizeRange, List<DSAttributeModifier> modifiers, List<ScalingAttributeModifier> scalingModifiers, int harvestLevelBonus) {
    public static final ResourceKey<Registry<DragonLevel>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvival.res("dragon_levels"));

    public static final Codec<DragonLevel> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MinMaxBounds.Doubles.CODEC.fieldOf("size_range").forGetter(DragonLevel::sizeRange),
            DSAttributeModifier.CODEC.listOf().fieldOf("modifiers").forGetter(DragonLevel::modifiers),
            ScalingAttributeModifier.CODEC.listOf().fieldOf("scaling_modifiers").forGetter(DragonLevel::scalingModifiers),
            ExtraCodecs.intRange(1, 4).fieldOf("harvest_level_bonus").forGetter(DragonLevel::harvestLevelBonus)
    ).apply(instance, instance.stable(DragonLevel::new)));

    public static final Codec<Holder<DragonLevel>> CODEC = RegistryFixedCodec.create(REGISTRY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<DragonLevel>> STREAM_CODEC = ByteBufCodecs.holderRegistry(REGISTRY);

    @Translation(type = Translation.Type.DESCRIPTION, comments = "Newborn")
    public static ResourceKey<DragonLevel> newborn = key("newborn");

    @Translation(type = Translation.Type.DESCRIPTION, comments = "Young")
    public static ResourceKey<DragonLevel> young = key("young");

    @Translation(type = Translation.Type.DESCRIPTION, comments = "Adult")
    public static ResourceKey<DragonLevel> adult = key("adult");

    @Translation(type = Translation.Type.DESCRIPTION, comments = "Ancient")
    public static ResourceKey<DragonLevel> ancient = key("ancient");

    @SubscribeEvent
    public static void register(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY, DIRECT_CODEC, DIRECT_CODEC);
    }

    // TODO :: add block_break_speed, jump height, step height and damage bonus
    public static void registerLevels(final BootstrapContext<DragonLevel> context) {
        context.register(newborn, new DragonLevel(
                MinMaxBounds.Doubles.between(14, 20),
                List.of(
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.SUBMERGED_MINING_SPEED, 1, AttributeModifier.Operation.ADD_VALUE, DragonTypes.SEA.getTypeNameLowerCase()),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.MAX_HEALTH, -6, AttributeModifier.Operation.ADD_VALUE),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.ATTACK_DAMAGE, 1, AttributeModifier.Operation.ADD_VALUE)
                ),
                List.of(
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.MOVEMENT_SPEED, 0.01f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.MAX_HEALTH, 1, AttributeModifier.Operation.ADD_VALUE)
                ),
                0
        ));

        context.register(young, new DragonLevel(
                MinMaxBounds.Doubles.between(20, 30),
                List.of(
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.SUBMERGED_MINING_SPEED, 2, AttributeModifier.Operation.ADD_VALUE, DragonTypes.SEA.getTypeNameLowerCase()),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.STEP_HEIGHT, 0.25, AttributeModifier.Operation.ADD_VALUE),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.ATTACK_DAMAGE, 2, AttributeModifier.Operation.ADD_VALUE)
                ),
                List.of(
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.MOVEMENT_SPEED, 0.01f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.ENTITY_INTERACTION_RANGE, 0.01f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.BLOCK_INTERACTION_RANGE, 0.01f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.MAX_HEALTH, 1, AttributeModifier.Operation.ADD_VALUE)
                ),
                1
        ));

        context.register(adult, new DragonLevel(
                MinMaxBounds.Doubles.between(30, 40),
                List.of(
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.SUBMERGED_MINING_SPEED, 3, AttributeModifier.Operation.ADD_VALUE, DragonTypes.SEA.getTypeNameLowerCase()),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.STEP_HEIGHT, 0.5, AttributeModifier.Operation.ADD_VALUE),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.ATTACK_DAMAGE, 3, AttributeModifier.Operation.ADD_VALUE)
                ),
                List.of(
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.MOVEMENT_SPEED, 0.01f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.ENTITY_INTERACTION_RANGE, 0.01f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.BLOCK_INTERACTION_RANGE, 0.01f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.MAX_HEALTH, 1, AttributeModifier.Operation.ADD_VALUE)
                ),
                1
        ));

        context.register(ancient, new DragonLevel(
                MinMaxBounds.Doubles.between(40, 60),
                List.of(
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.SUBMERGED_MINING_SPEED, 3, AttributeModifier.Operation.ADD_VALUE, DragonTypes.SEA.getTypeNameLowerCase()),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.STEP_HEIGHT, 0.5, AttributeModifier.Operation.ADD_VALUE),
                        DSAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.ATTACK_DAMAGE, 3, AttributeModifier.Operation.ADD_VALUE)
                ),
                List.of(
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.MOVEMENT_SPEED, 0.01f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.ENTITY_INTERACTION_RANGE, 0.01f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.BLOCK_INTERACTION_RANGE, 0.01f, AttributeModifier.Operation.ADD_VALUE),
                        ScalingAttributeModifier.createModifier(ModifierType.DRAGON_LEVEL, Attributes.MAX_HEALTH, 1, AttributeModifier.Operation.ADD_VALUE)
                ),
                1
        ));
    }

    public static ResourceKey<DragonLevel> key(final ResourceLocation location) {
        return ResourceKey.create(REGISTRY, location);
    }

    private static ResourceKey<DragonLevel> key(final String path) {
        return key(DragonSurvival.res(path));
    }

    public static Component translatableName(final ResourceKey<DragonLevel> dragonLevel) {
        return Component.translatable(Translation.Type.LEVEL.wrap(dragonLevel.location().getNamespace(), dragonLevel.location().getPath()));
    }

    public static Component translatableDescription(final ResourceKey<DragonLevel> dragonLevel) {
        return Component.translatable(Translation.Type.LEVEL_DESCRIPTION.wrap(dragonLevel.location().getNamespace(), dragonLevel.location().getPath()));
    }

    public static String name(final Holder<DragonLevel> level) {
        return level.getRegisteredName().replace(":", ".");
    }

    public static String name(final ResourceKey<DragonLevel> level) {
        return level.location().toString().replace(":", ".");
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

    public static Holder<DragonLevel> getLevel(@Nullable final HolderLookup.Provider provider, double size) {
        HolderLookup.RegistryLookup<DragonLevel> registry;

        if (provider == null) {
            registry = CommonHooks.resolveLookup(REGISTRY);
        } else {
            registry = provider.lookupOrThrow(REGISTRY);
        }

        Holder<DragonLevel> youngest = null;
        Holder<DragonLevel> oldest = null;

        //noinspection DataFlowIssue -> registry is expected to be present
        for (Holder.Reference<DragonLevel> level : registry.listElements().toList()) {
            if (level.value().sizeRange().matches(size)) {
                return level;
            }

            if (min(youngest) > min(level)) {
                youngest = level;
            }

            if (max(oldest) < max(level)) {
                oldest = level;
            }
        }

        // If no level matches find the one closest to the size as a fallback
        double minDifference = Math.abs(min(youngest) - size);
        double maxDifference = Math.abs(max(oldest) - size);

        if (youngest != null && minDifference > maxDifference) {
            return youngest;
        }

        if (oldest != null && maxDifference > minDifference) {
            return oldest;
        }

        throw new IllegalStateException("There is no valid dragon level for the supplied size [" + size + "]");
    }

    public static double min(@Nullable final Holder<DragonLevel> dragonLevel) {
        if (dragonLevel == null) {
            return 0;
        }

        return dragonLevel.value().sizeRange().min().orElse(0d);
    }

    public static double max(@Nullable final Holder<DragonLevel> dragonLevel) {
        if (dragonLevel == null) {
            return Double.MAX_VALUE;
        }

        return dragonLevel.value().sizeRange().min().orElse(Double.MAX_VALUE);
    }
}
