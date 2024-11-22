package by.dragonsurvivalteam.dragonsurvival.registry.dragon;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.Condition;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.MiscCodecs;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.Modifier;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAttributes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.List;
import java.util.Optional;

import static by.dragonsurvivalteam.dragonsurvival.common.codecs.ModifierType.DRAGON_LEVEL;

public class DragonLevels {
    @Translation(type = Translation.Type.LEVEL, comments = "Newborn")
    public static ResourceKey<DragonLevel> newborn = key("newborn");

    @Translation(type = Translation.Type.LEVEL, comments = "Young")
    public static ResourceKey<DragonLevel> young = key("young");

    @Translation(type = Translation.Type.LEVEL, comments = "Adult")
    public static ResourceKey<DragonLevel> adult = key("adult");

    @Translation(type = Translation.Type.LEVEL, comments = "Ancient")
    public static ResourceKey<DragonLevel> ancient = key("ancient");

    public static void registerLevels(final BootstrapContext<DragonLevel> context) {
        context.register(newborn, new DragonLevel(
                new MiscCodecs.Bounds(10, 25),
                Functions.hoursToTicks(10),
                List.of(
                        /* Constant */
                        Modifier.constant(DRAGON_LEVEL, Attributes.SUBMERGED_MINING_SPEED, 1, AttributeModifier.Operation.ADD_VALUE, DragonTypes.SEA.getTypeNameLowerCase()),
                        Modifier.constant(DRAGON_LEVEL, Attributes.MAX_HEALTH, -7, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.constant(DRAGON_LEVEL, Attributes.ATTACK_DAMAGE, 1, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.constant(DRAGON_LEVEL, Attributes.JUMP_STRENGTH, 0.025f, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.constant(DRAGON_LEVEL, Attributes.SAFE_FALL_DISTANCE, 0.25f, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.constant(DRAGON_LEVEL, DSAttributes.DRAGON_BREATH_RANGE, 1.5f, AttributeModifier.Operation.ADD_VALUE),
                        /* Per size */
                        Modifier.perSize(DRAGON_LEVEL, Attributes.MOVEMENT_SPEED, 0.0015f, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.perSize(DRAGON_LEVEL, Attributes.MAX_HEALTH, 0.5f, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.perSize(DRAGON_LEVEL, DSAttributes.DRAGON_BREATH_RANGE, 0.05f, AttributeModifier.Operation.ADD_VALUE)
                ),
                0,
                1.5,
                List.of(
                        MiscCodecs.GrowthItem.create(Functions.hoursToTicks(1), DSItems.ELDER_DRAGON_HEART.value()),
                        MiscCodecs.GrowthItem.create(Functions.minutesToTicks(30), DSItems.WEAK_DRAGON_HEART.value()),
                        MiscCodecs.GrowthItem.create(Functions.minutesToTicks(10), DSItems.DRAGON_HEART_SHARD.value()),
                        MiscCodecs.GrowthItem.create(Functions.hoursToTicks(-1), DSItems.STAR_BONE.value())
                ),
                Condition.naturalGrowth(),
                Optional.empty()
        ));

        context.register(young, new DragonLevel(
                new MiscCodecs.Bounds(25, 40),
                Functions.daysToTicks(3),
                List.of(
                        /* Constant */
                        Modifier.constant(DRAGON_LEVEL, Attributes.SUBMERGED_MINING_SPEED, 2, AttributeModifier.Operation.ADD_VALUE, DragonTypes.SEA.getTypeNameLowerCase()),
                        Modifier.constant(DRAGON_LEVEL, Attributes.STEP_HEIGHT, 0.25f, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.constant(DRAGON_LEVEL, Attributes.ATTACK_DAMAGE, 2, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.constant(DRAGON_LEVEL, Attributes.JUMP_STRENGTH, 0.05f, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.constant(DRAGON_LEVEL, Attributes.SAFE_FALL_DISTANCE, 0.5f, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.constant(DRAGON_LEVEL, DSAttributes.DRAGON_BREATH_RANGE, 2.5f, AttributeModifier.Operation.ADD_VALUE),
                        /* Per size */
                        Modifier.perSize(DRAGON_LEVEL, Attributes.MOVEMENT_SPEED, 0.0015f, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.perSize(DRAGON_LEVEL, Attributes.ENTITY_INTERACTION_RANGE, 0.01f, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.perSize(DRAGON_LEVEL, Attributes.BLOCK_INTERACTION_RANGE, 0.01f, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.perSize(DRAGON_LEVEL, Attributes.MAX_HEALTH, 0.5f, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.perSize(DRAGON_LEVEL, DSAttributes.DRAGON_BREATH_RANGE, 0.05f, AttributeModifier.Operation.ADD_VALUE)
                ),
                1,
                2,
                List.of(
                        MiscCodecs.GrowthItem.create(Functions.hoursToTicks(1), DSItems.ELDER_DRAGON_HEART.value()),
                        MiscCodecs.GrowthItem.create(Functions.minutesToTicks(30), DSItems.WEAK_DRAGON_HEART.value()),
                        MiscCodecs.GrowthItem.create(Functions.hoursToTicks(-1), DSItems.STAR_BONE.value())
                ),
                Condition.naturalGrowth(),
                Optional.empty()
        ));

        context.register(adult, new DragonLevel(
                new MiscCodecs.Bounds(40, 60),
                Functions.daysToTicks(20),
                List.of(
                        /* Constant */
                        Modifier.constant(DRAGON_LEVEL, Attributes.SUBMERGED_MINING_SPEED, 3, AttributeModifier.Operation.ADD_VALUE, DragonTypes.SEA.getTypeNameLowerCase()),
                        Modifier.constant(DRAGON_LEVEL, Attributes.STEP_HEIGHT, 0.5f, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.constant(DRAGON_LEVEL, Attributes.ATTACK_DAMAGE, 3, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.constant(DRAGON_LEVEL, Attributes.JUMP_STRENGTH, 0.1f, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.constant(DRAGON_LEVEL, Attributes.SAFE_FALL_DISTANCE, 1, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.constant(DRAGON_LEVEL, DSAttributes.DRAGON_BREATH_RANGE, 4, AttributeModifier.Operation.ADD_VALUE),
                        /* Per size */
                        Modifier.perSize(DRAGON_LEVEL, Attributes.MOVEMENT_SPEED, 0.0015f, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.perSize(DRAGON_LEVEL, Attributes.ENTITY_INTERACTION_RANGE, 0.01f, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.perSize(DRAGON_LEVEL, Attributes.BLOCK_INTERACTION_RANGE, 0.01f, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.perSize(DRAGON_LEVEL, Attributes.MAX_HEALTH, 0.5f, AttributeModifier.Operation.ADD_VALUE),
                        Modifier.perSize(DRAGON_LEVEL, DSAttributes.DRAGON_BREATH_RANGE, 0.05f, AttributeModifier.Operation.ADD_VALUE)
                ),
                1,
                3,
                List.of(
                        MiscCodecs.GrowthItem.create(Functions.hoursToTicks(1), DSItems.ELDER_DRAGON_HEART.value()),
                        MiscCodecs.GrowthItem.create(Functions.hoursToTicks(-1), DSItems.STAR_BONE.value())
                ),
                Condition.naturalGrowth(),
                Optional.empty()
        ));
    }

    public static ResourceKey<DragonLevel> key(final ResourceLocation location) {
        return ResourceKey.create(DragonLevel.REGISTRY, location);
    }

    private static ResourceKey<DragonLevel> key(final String path) {
        return key(DragonSurvival.res(path));
    }
}
