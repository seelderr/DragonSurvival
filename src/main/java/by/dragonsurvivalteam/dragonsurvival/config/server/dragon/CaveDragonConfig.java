package by.dragonsurvivalteam.dragonsurvival.config.server.dragon;

import by.dragonsurvivalteam.dragonsurvival.config.ConfigUtils;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.config.obj.Validation;
import by.dragonsurvivalteam.dragonsurvival.config.types.BlockStateConfig;
import by.dragonsurvivalteam.dragonsurvival.config.types.FoodConfigCollector;
import by.dragonsurvivalteam.dragonsurvival.config.types.ItemHurtConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.Arrays;
import java.util.List;

public class CaveDragonConfig {
    // --- Magic --- //

    @Translation(key = "cave_abilities", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable cave dragon abilities")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic"}, key = "cave_abilities")
    public static Boolean caveDragonAbilities = true;

    @Translation(key = "cave_conditional_mana_blocks", type = Translation.Type.CONFIGURATION, comments = "Blocks that restore mana for cave dragons when under certain conditions (block states) - Formatting: namespace:path:key=value,key=value (prefix namespace with # for tags)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic"}, key = "cave_conditional_mana_blocks")
    public static List<BlockStateConfig> caveConditionalManaBlocks = List.of(
            BlockStateConfig.of("#minecraft:campfires:lit=true"),
            BlockStateConfig.of("#c:player_workstations/furnaces:lit=true"),
            BlockStateConfig.of("minecraft:smoker:lit=true"),
            BlockStateConfig.of("minecraft:blast_furnace:lit=true")
    );

    // --- Bonus --- //

    @Translation(key = "cave_fire_immunity", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable cave dragon fire immunity")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "bonus"}, key = "cave_fire_immunity")
    public static Boolean caveFireImmunity = true;

    @Translation(key = "cave_lava_swimming", type = Translation.Type.CONFIGURATION, comments = "If enabled lava swimming will behave like swimming in water for cave dragons")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "bonus"}, key = "cave_lava_swimming")
    public static Boolean caveLavaSwimming = true;

    @ConfigRange(min = 0, max = 100_000)
    @Translation(key = "cave_lava_swimming_ticks", type = Translation.Type.CONFIGURATION, comments = "The max. amount of ticks (20 ticks = 1 second) cave dragons can swim in lava - if set to 0 the time will be unlimited")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "bonus"}, key = "cave_lava_swimming_ticks")
    public static Integer caveLavaSwimmingTicks = Functions.secondsToTicks(180);

    // --- Penalties --- //

    @ConfigRange(min = 0.0, max = 100.0)
    @Translation(key = "cave_water_damage", type = Translation.Type.CONFIGURATION, comments = "The amount of damage taken (while in contact with water) every 10 ticks (0.5 seconds) - disabled if set to 0")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "penalties"}, key = "cave_water_damage")
    public static Double caveWaterDamage = 1.0;

    @ConfigRange(min = 0.0, max = 100.0)
    @Translation(key = "cave_rain_damage", type = Translation.Type.CONFIGURATION, comments = "The amount of damage taken (while in contact with rain) every 40 ticks (2 seconds) - disabled if set to 0")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "penalties"}, key = "cave_rain_damage")
    public static Double caveRainDamage = 1.0;

    @ConfigRange(min = 0.0, max = 100.0)
    @Translation(key = "cave_splash_damage", type = Translation.Type.CONFIGURATION, comments = "The amount of damage taken when hit with a snowball or water splash potion - disabled if set to 0")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "penalties"}, key = "cave_splash_damage")
    public static Double caveSplashDamage = 2.0;

    // --- Food --- //

    @Translation(key = "cave_hurtful_items", type = Translation.Type.CONFIGURATION, comments = "Items which will cause damage to cave dragons when consumed - Formatting: namespace:path:damage (prefix namespace with # for tags)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "food"}, key = "cave_hurtful_items", validation = Validation.RESOURCE_LOCATION_NUMBER)
    public static List<ItemHurtConfig> caveDragonHurtfulItems = List.of(
            ItemHurtConfig.of("minecraft:potion:2"),
            ItemHurtConfig.of("minecraft:water_bottle:2"),
            ItemHurtConfig.of("minecraft:milk_bucket:2")
    );

    @Translation(key = "cave_foods", type = Translation.Type.CONFIGURATION, comments = {
            "Determines which items a cave dragon can eat - the item doesn't need to be a food item (e.g. you could add an iron block here",
            "Formatting: namespace:path:nutrition:saturation (prefix namespace with # for tags)",
            "Nutrition (whole number) and saturation (can be specified in decimals) are optional - if they're missing the items original values will be used (or 1:0)"
    })
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "food"}, key = "cave_foods")
    public static List<FoodConfigCollector> caveDragonFoods = Arrays.asList(
            FoodConfigCollector.of(ConfigUtils.location(ItemTags.COALS), 1, 1),
            FoodConfigCollector.of(ConfigUtils.location(Items.CHARCOAL), 1, 2),
            FoodConfigCollector.of(ConfigUtils.location(DSItems.CHARGED_COAL), 6, 1),
            FoodConfigCollector.of(ConfigUtils.location(DSItems.CHARRED_MEAT), 8, 10),
            FoodConfigCollector.of(ConfigUtils.location(DSItems.CAVE_DRAGON_TREAT), 4, 8),
            FoodConfigCollector.of(ConfigUtils.location(DSItems.CHARRED_SEAFOOD), 7, 11),
            FoodConfigCollector.of(ConfigUtils.location(DSItems.CHARRED_VEGETABLE), 8, 9),
            FoodConfigCollector.of(ConfigUtils.location(DSItems.CHARRED_MUSHROOM), 9, 9),
            FoodConfigCollector.of(ConfigUtils.location(DSItems.CHARGED_SOUP), 15, 15),
            FoodConfigCollector.of(ConfigUtils.location(DSItems.HOT_DRAGON_ROD), 4, 15),
            FoodConfigCollector.of(ConfigUtils.location(DSItems.EXPLOSIVE_COPPER), 6, 4),
            FoodConfigCollector.of(ConfigUtils.location(DSItems.DOUBLE_QUARTZ), 8, 6),
            FoodConfigCollector.of(ConfigUtils.location(DSItems.QUARTZ_EXPLOSIVE_COPPER), 12, 18),
            FoodConfigCollector.of("netherdepthsupgrade:blazefish", 6, 7),
            FoodConfigCollector.of("netherdepthsupgrade:cooked_magmacubefish_slice", 2, 2),
            FoodConfigCollector.of("netherdepthsupgrade:blazefish_slice", 2, 2),
            FoodConfigCollector.of("netherdepthsupgrade:magmacubefish", 6, 7),
            FoodConfigCollector.of("netherdepthsupgrade:fortress_grouper", 3, 1),
            FoodConfigCollector.of("desolation:cinder_fruit", 6, 7),
            FoodConfigCollector.of("desolation:powered_cinder_fruit", 8, 12),
            FoodConfigCollector.of("desolation:activatedcharcoal", 2, 2),
            FoodConfigCollector.of("desolation:infused_powder", 10, 10),
            FoodConfigCollector.of("desolation:primed_ash", 7, 8),
            FoodConfigCollector.of("undergarden:ditchbulb", 5, 6),
            FoodConfigCollector.of("xreliquary:molten_core", 1, 1),
            FoodConfigCollector.of("mekanism:dust_coal", 1, 1),
            FoodConfigCollector.of("mekanism:dust_charcoal", 1, 1),
            FoodConfigCollector.of("thermal:coal_coke", 1, 1),
            FoodConfigCollector.of("thermal:basalz_rod", 2, 4),
            FoodConfigCollector.of("thermal:basalz_powder", 1, 2),
            FoodConfigCollector.of("create:blaze_cake", 10, 10),
            FoodConfigCollector.of("create:creative_blaze_cake", 50, 50),
            FoodConfigCollector.of("nethersdelight:nether_skewer", 6, 8)
    );
}
