package by.dragonsurvivalteam.dragonsurvival.registry.dragon;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.DietEntry;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.GrowthIcon;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.MiscDragonTextures;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.abilities.CaveDragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.abilities.ForestDragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.abilities.SeaDragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.datapacks.AncientDatapack;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.stage.DragonStages;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.food.FoodProperties;

import java.util.List;
import java.util.Optional;

public class DragonTypes {
    // TODO: Translation key here, also handle translation keys now for this data type
    public static final ResourceKey<DragonType> CAVE = key("cave");
    public static final ResourceKey<DragonType> FOREST = key("forest");
    public static final ResourceKey<DragonType> SEA = key("sea");

    public static void registerTypes(final BootstrapContext<DragonType> context) {
        context.register(CAVE, new DragonType(
                Optional.empty(),
                Optional.empty(),
                HolderSet.empty(),
                HolderSet.direct(
                        // Active
                        context.lookup(DragonAbility.REGISTRY).getOrThrow(CaveDragonAbilities.NETHER_BREATH),
                        context.lookup(DragonAbility.REGISTRY).getOrThrow(CaveDragonAbilities.FIRE_BALL),
                        context.lookup(DragonAbility.REGISTRY).getOrThrow(CaveDragonAbilities.LAVA_VISION),
                        context.lookup(DragonAbility.REGISTRY).getOrThrow(CaveDragonAbilities.TOUGH_SKIN),
                        // Passive
                        context.lookup(DragonAbility.REGISTRY).getOrThrow(CaveDragonAbilities.CAVE_ATHLETICS),
                        context.lookup(DragonAbility.REGISTRY).getOrThrow(CaveDragonAbilities.BURN),
                        context.lookup(DragonAbility.REGISTRY).getOrThrow(CaveDragonAbilities.CAVE_MAGIC),
                        context.lookup(DragonAbility.REGISTRY).getOrThrow(CaveDragonAbilities.CONTRAST_SHOWER)
                ),
                List.of(),
                List.of(),
                List.of(DietEntry.from("#minecraft:doors", new FoodProperties(2, 1, true, 1, Optional.empty(), List.of()))),
                new MiscDragonTextures(
                        DragonSurvival.res("textures/gui/food_icons/cave_food_icons.png"),
                        DragonSurvival.res("textures/gui/mana_icons/cave_mana_icons.png"),
                        DragonSurvival.res("textures/gui/dragon_altar/cave_altar_icon.png"),
                        DragonSurvival.res("textures/gui/source_of_magic/cave_source_of_magic_0.png"),
                        DragonSurvival.res("textures/gui/source_of_magic/cave_source_of_magic_1.png"),
                        DragonSurvival.res("textures/gui/casting_bars/cave_cast_bar.png"),
                        DragonSurvival.res("textures/gui/help_button/cave_help_button.png"),
                        DragonSurvival.res("textures/gui/growth/circle_cave.png"),
                        List.of(new GrowthIcon(
                                        DragonSurvival.res("textures/gui/growth/cave/newborn.png"),
                                        DragonStages.newborn
                                ),
                                new GrowthIcon(
                                        DragonSurvival.res("textures/gui/growth/cave/young.png"),
                                        DragonStages.young
                                ),
                                new GrowthIcon(
                                        DragonSurvival.res("textures/gui/growth/cave/adult.png"),
                                        DragonStages.adult
                                ),
                                new GrowthIcon(
                                        DragonSurvival.res("textures/gui/growth/cave/ancient.png"),
                                        AncientDatapack.ancient
                                )
                        ),
                        new ColorRGBA(16711680),
                        new ColorRGBA(16711680)
                )
        ));

        context.register(SEA, new DragonType(
                Optional.empty(),
                Optional.empty(),
                HolderSet.empty(),
                HolderSet.direct(context.lookup(DragonAbility.REGISTRY).getOrThrow(SeaDragonAbilities.BALL_LIGHTNING)),
                List.of(),
                List.of(),
                List.of(),
                new MiscDragonTextures(
                        DragonSurvival.res("textures/gui/food_icons/cave_food_icons.png"),
                        DragonSurvival.res("textures/gui/mana_icons/cave_mana_icons.png"),
                        DragonSurvival.res("textures/gui/dragon_altar/sea_altar_icon.png"),
                        DragonSurvival.res("textures/gui/source_of_magic/cave_source_of_magic_0.png"),
                        DragonSurvival.res("textures/gui/source_of_magic/cave_source_of_magic_1.png"),
                        DragonSurvival.res("textures/gui/casting_bars/cave_cast_bar.png"),
                        DragonSurvival.res("textures/gui/help_button/cave_help_button.png"),
                        DragonSurvival.res("textures/gui/growth/circle_cave.png"),
                        List.of(new GrowthIcon(
                                        DragonSurvival.res("textures/gui/growth/sea/newborn.png"),
                                        DragonStages.newborn
                                ),
                                new GrowthIcon(
                                        DragonSurvival.res("textures/gui/growth/sea/young.png"),
                                        DragonStages.young
                                ),
                                new GrowthIcon(
                                        DragonSurvival.res("textures/gui/growth/sea/adult.png"),
                                        DragonStages.adult
                                ),
                                new GrowthIcon(
                                        DragonSurvival.res("textures/gui/growth/sea/ancient.png"),
                                        AncientDatapack.ancient
                                )
                        ),
                        new ColorRGBA(16711680),
                        new ColorRGBA(16711680)
                )
        ));

        context.register(FOREST, new DragonType(
                Optional.empty(),
                Optional.empty(),
                HolderSet.empty(),
                HolderSet.direct(context.lookup(DragonAbility.REGISTRY).getOrThrow(ForestDragonAbilities.FOREST_SPIKE)),
                List.of(),
                List.of(),
                List.of(),
                new MiscDragonTextures(
                        DragonSurvival.res("textures/gui/food_icons/cave_food_icons.png"),
                        DragonSurvival.res("textures/gui/mana_icons/cave_mana_icons.png"),
                        DragonSurvival.res("textures/gui/dragon_altar/forest_altar_icon.png"),
                        DragonSurvival.res("textures/gui/source_of_magic/cave_source_of_magic_0.png"),
                        DragonSurvival.res("textures/gui/source_of_magic/cave_source_of_magic_1.png"),
                        DragonSurvival.res("textures/gui/casting_bars/cave_cast_bar.png"),
                        DragonSurvival.res("textures/gui/help_button/cave_help_button.png"),
                        DragonSurvival.res("textures/gui/growth/circle_cave.png"),
                        List.of(new GrowthIcon(
                                        DragonSurvival.res("textures/gui/growth/forest/newborn.png"),
                                        DragonStages.newborn
                                ),
                                new GrowthIcon(
                                        DragonSurvival.res("textures/gui/growth/forest/young.png"),
                                        DragonStages.young
                                ),
                                new GrowthIcon(
                                        DragonSurvival.res("textures/gui/growth/forest/adult.png"),
                                        DragonStages.adult
                                ),
                                new GrowthIcon(
                                        DragonSurvival.res("textures/gui/growth/forest/ancient.png"),
                                        AncientDatapack.ancient
                                )
                        ),
                        new ColorRGBA(16711680),
                        new ColorRGBA(16711680)
                )
        ));
    }

    public static ResourceKey<DragonType> key(final ResourceLocation location) {
        return ResourceKey.create(DragonType.REGISTRY, location);
    }

    private static ResourceKey<DragonType> key(final String path) {
        return key(DragonSurvival.res(path));
    }
}
