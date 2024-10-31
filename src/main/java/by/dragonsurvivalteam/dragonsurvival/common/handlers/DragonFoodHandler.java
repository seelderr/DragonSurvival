package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.ToolTipHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigUtils;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.config.types.FoodConfigCollector;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.tags.DSItemTags;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Nullable;

@EventBusSubscriber
public class DragonFoodHandler { // TODO :: create some tier-based tags for grouping, like 'light_cave_dragon_food' which has low nutrition values for easier config?
    // Food general
    @ConfigOption(side = ConfigSide.SERVER, category = "food", key = "requireDragonFood", comment = "Force dragons to eat a unique diet for their type.")
    public static Boolean requireDragonFood = true;

    // Dragon Food List
    @ConfigOption(side = ConfigSide.SERVER, category = {"food", "cave_dragon"}, key = "caveDragonFoods", comment = {"Dragon food formatting: namespace:path:nutrition:saturation (prefix namespace with # for tags)", "Nutrition / saturation values are optional - human values will be used if they are missing.", "Saturation can be defined with decimals (e.g. 0.3)"})
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

    @ConfigOption(side = ConfigSide.SERVER, category = {"food", "forest_dragon"}, key = "forestDragonFoods", comment = {"Dragon food formatting: namespace:path:nutrition:saturation (prefix namespace with # for tags)", "Nutrition / saturation values are optional - human values will be used if they are missing.", "Saturation can be defined with decimals (e.g. 0.3)"})
    public static List<FoodConfigCollector> forestDragonFoods = Arrays.asList(
            FoodConfigCollector.of(ConfigUtils.location(Tags.Items.FOODS_RAW_MEAT), 6, 4),
            FoodConfigCollector.of(ConfigUtils.location(Tags.Items.FOODS_BERRY), 2, 1),
            FoodConfigCollector.of(ConfigUtils.location(Tags.Items.MUSHROOMS), 1, 1),
            FoodConfigCollector.of(ConfigUtils.location(Items.ROTTEN_FLESH), 2, 4),
            FoodConfigCollector.of(ConfigUtils.location(Items.SPIDER_EYE), 6, 8),
            FoodConfigCollector.of(ConfigUtils.location(Items.RABBIT), 7, 8),
            FoodConfigCollector.of(ConfigUtils.location(Items.POISONOUS_POTATO), 7, 8),
            FoodConfigCollector.of(ConfigUtils.location(Items.CHORUS_FRUIT), 9, 8),
            FoodConfigCollector.of(ConfigUtils.location(Items.BROWN_MUSHROOM), 2, 1),
            FoodConfigCollector.of(ConfigUtils.location(Items.RED_MUSHROOM), 2, 3),
            FoodConfigCollector.of(ConfigUtils.location(Items.HONEY_BOTTLE), 6, 1),
            FoodConfigCollector.of(ConfigUtils.location(Items.WARPED_FUNGUS), 3, 3),
            FoodConfigCollector.of(ConfigUtils.location(Items.CRIMSON_FUNGUS), 3, 3),
            FoodConfigCollector.of(ConfigUtils.location(DSItems.FOREST_DRAGON_TREAT), 4, 8),
            FoodConfigCollector.of(ConfigUtils.location(DSItems.MEAT_CHORUS_MIX), 12, 8),
            FoodConfigCollector.of(ConfigUtils.location(DSItems.MEAT_WILD_BERRIES), 12, 10),
            FoodConfigCollector.of(ConfigUtils.location(DSItems.SMELLY_MEAT_PORRIDGE), 6, 10),
            FoodConfigCollector.of(ConfigUtils.location(DSItems.DIAMOND_CHORUS), 15, 12),
            FoodConfigCollector.of(ConfigUtils.location(DSItems.LUMINOUS_OINTMENT), 5, 3),
            FoodConfigCollector.of(ConfigUtils.location(DSItems.SWEET_SOUR_RABBIT), 10, 6),
            FoodConfigCollector.of("additionaldragons:cursed_marrow"),
            FoodConfigCollector.of("aquaculture:turtle_soup", 8, 8),
            FoodConfigCollector.of("netherdepthsupgrade:wither_bonefish", 4, 6),
            FoodConfigCollector.of("netherdepthsupgrade:bonefish", 4, 6),
            FoodConfigCollector.of("phantasm:chorus_fruit_salad", 10, 10),
            FoodConfigCollector.of("aoa3:fiery_chops", 6, 7),
            FoodConfigCollector.of("aoa3:raw_chimera_chop", 6, 7),
            FoodConfigCollector.of("aoa3:raw_furlion_chop", 6, 7),
            FoodConfigCollector.of("aoa3:raw_halycon_beef", 7, 8),
            FoodConfigCollector.of("aoa3:raw_charger_shank", 6, 7),
            FoodConfigCollector.of("aoa3:trilliad_leaves", 8, 11),
            // TODO :: check for old, clean stuff up
            FoodConfigCollector.of("pamhc2foodextended:rawtofabbititem"),
            FoodConfigCollector.of("pamhc2foodextended:rawtofickenitem"),
            FoodConfigCollector.of("quark:golden_frog_leg:12:14"),
            FoodConfigCollector.of("pamhc2foodextended:rawtofuttonitem"),
            FoodConfigCollector.of("alexsmobs:kangaroo_meat:5:6"),
            FoodConfigCollector.of("alexsmobs:moose_ribs:6:8"),
            FoodConfigCollector.of("simplefarming:raw_horse_meat:5:6"),
            FoodConfigCollector.of("simplefarming:raw_bacon:3:3"),
            FoodConfigCollector.of("simplefarming:raw_chicken_wings:2:3"),
            FoodConfigCollector.of("simplefarming:raw_sausage:3:4"),
            FoodConfigCollector.of("xenoclustwo:raw_tortice:7:8"),
            FoodConfigCollector.of("unnamedanimalmod:musk_ox_shank:7:8"),
            FoodConfigCollector.of("unnamedanimalmod:frog_legs:5:6"),
            FoodConfigCollector.of("unnamedanimalmod:mangrove_fruit:4:7"),
            FoodConfigCollector.of("betteranimalsplus:venisonraw:7:6"),
            FoodConfigCollector.of("born_in_chaos_v1:corpse_maggot:1:1"),
            FoodConfigCollector.of("born_in_chaos_v1:monster_flesh:1:1"),
            FoodConfigCollector.of("betteranimalsplus:pheasantraw:7:5"),
            FoodConfigCollector.of("betteranimalsplus:turkey_leg_raw:4:5"),
            FoodConfigCollector.of("infernalexp:raw_hogchop:6:7"),
            FoodConfigCollector.of("infernalexp:cured_jerky:10:7"),
            FoodConfigCollector.of("rats:raw_rat:4:5"),
            FoodConfigCollector.of("aquaculture:frog:4:5"),
            FoodConfigCollector.of("aquaculture:frog_legs_raw:4:4"),
            FoodConfigCollector.of("aquaculture:box_turtle:4:5"),
            FoodConfigCollector.of("aquaculture:arrau_turtle:4:5"),
            FoodConfigCollector.of("aquaculture:starshell_turtle:4:5"),
            FoodConfigCollector.of("undergarden:raw_gloomper_leg:4:5"),
            FoodConfigCollector.of("undergarden:raw_dweller_meat:6:7"),
            FoodConfigCollector.of("farmersdelight:chicken_cuts:3:3"),
            FoodConfigCollector.of("farmersdelight:bacon:3:3"),
            FoodConfigCollector.of("farmersdelight:ham:9:10"),
            FoodConfigCollector.of("farmersdelight:minced_beef:5:3"),
            FoodConfigCollector.of("farmersdelight:mutton_chops:5:3"),
            FoodConfigCollector.of("abnormals_delight:duck_fillet:2:3"),
            FoodConfigCollector.of("abnormals_delight:venison_shanks:7:3"),
            FoodConfigCollector.of("autumnity:foul_berries:2:4"),
            FoodConfigCollector.of("autumnity:turkey:7:8"),
            FoodConfigCollector.of("autumnity:turkey_piece:2:4"),
            FoodConfigCollector.of("autumnity:foul_soup:12:8"),
            FoodConfigCollector.of("endergetic:bolloom_fruit:3:4"),
            FoodConfigCollector.of("quark:frog_leg:4:5"),
            FoodConfigCollector.of("nethers_delight:hoglin_loin:8:6"),
            FoodConfigCollector.of("nethers_delight:raw_stuffed_hoglin:18:10"),
            FoodConfigCollector.of("xreliquary:zombie_heart:4:7"),
            FoodConfigCollector.of("xreliquary:bat_wing:2:2"),
            FoodConfigCollector.of("eidolon:zombie_heart:7:7"),
            FoodConfigCollector.of("forbidden_arcanus:bat_wing:5:2"),
            FoodConfigCollector.of("twilightforest:raw_venison:7:7"),
            FoodConfigCollector.of("twilightforest:raw_meef:9:5"),
            FoodConfigCollector.of("twilightforest:hydra_chop"),
            FoodConfigCollector.of("cyclic:chorus_flight"),
            FoodConfigCollector.of("cyclic:chorus_spectral"),
            FoodConfigCollector.of("cyclic:toxic_carrot:15:15"),
            FoodConfigCollector.of("artifacts:everlasting_beef"),
            FoodConfigCollector.of("byg:soul_shroom:9:5"),
            FoodConfigCollector.of("byg:death_cap:9:8"),
            FoodConfigCollector.of("minecolonies:chorus_bread"),
            FoodConfigCollector.of("wyrmroost:raw_lowtier_meat:3:2"),
            FoodConfigCollector.of("wyrmroost:raw_common_meat:5:3"),
            FoodConfigCollector.of("wyrmroost:raw_apex_meat:8:6"),
            FoodConfigCollector.of("wyrmroost:raw_behemoth_meat:11:12"),
            FoodConfigCollector.of("wyrmroost:desert_wyrm:4:3"),
            FoodConfigCollector.of("eanimod:rawchicken_darkbig:9:5"),
            FoodConfigCollector.of("eanimod:rawchicken_dark:5:4"),
            FoodConfigCollector.of("eanimod:rawchicken_darksmall:3:2"),
            FoodConfigCollector.of("eanimod:rawchicken_pale:5:3"),
            FoodConfigCollector.of("eanimod:rawchicken_palesmall:4:3"),
            FoodConfigCollector.of("eanimod:rawrabbit_small:4:4"),
            FoodConfigCollector.of("environmental:duck:4:3"),
            FoodConfigCollector.of("environmental:venison:7:7"),
            FoodConfigCollector.of("cnb:lizard_item_jungle:4:4"),
            FoodConfigCollector.of("cnb:lizard_item_mushroom:4:4"),
            FoodConfigCollector.of("cnb:lizard_item_jungle_2:4:4"),
            FoodConfigCollector.of("cnb:lizard_item_desert_2:4:4"),
            FoodConfigCollector.of("cnb:lizard_egg:5:2"),
            FoodConfigCollector.of("cnb:lizard_item_desert:4:4"),
            FoodConfigCollector.of("snowpig:frozen_porkchop:7:3"),
            FoodConfigCollector.of("snowpig:frozen_ham:5:7"),
            FoodConfigCollector.of("naturalist:venison:7:6"),
            FoodConfigCollector.of("leescreatures:raw_boarlin:6:6"),
            FoodConfigCollector.of("mysticalworld:venison:5:5"),
            FoodConfigCollector.of("toadterror:toad_chops:8:7"),
            FoodConfigCollector.of("prehistoricfauna:raw_large_thyreophoran_meat:7:6"),
            FoodConfigCollector.of("prehistoricfauna:raw_large_marginocephalian_meat:8:6"),
            FoodConfigCollector.of("prehistoricfauna:raw_small_ornithischian_meat:4:3"),
            FoodConfigCollector.of("prehistoricfauna:raw_large_sauropod_meat:11:9"),
            FoodConfigCollector.of("prehistoricfauna:raw_small_sauropod_meat:4:4"),
            FoodConfigCollector.of("prehistoricfauna:raw_large_theropod_meat:7:7"),
            FoodConfigCollector.of("prehistoricfauna:raw_small_theropod_meat:4:4"),
            FoodConfigCollector.of("prehistoricfauna:raw_small_archosauromorph_meat:3:3"),
            FoodConfigCollector.of("prehistoricfauna:raw_large_archosauromorph_meat:6:5"),
            FoodConfigCollector.of("prehistoricfauna:raw_small_reptile_meat:4:3"),
            FoodConfigCollector.of("prehistoricfauna:raw_large_synapsid_meat:5:6"),
            FoodConfigCollector.of("ends_delight:dragon_leg:15:15"),
            FoodConfigCollector.of("ends_delight:raw_dragon_meat:10:10"),
            FoodConfigCollector.of("ends_delight:raw_dragon_meat_cuts:5:2"),
            FoodConfigCollector.of("ends_delight:dragon_breath_and_chorus_soup:15:15"),
            FoodConfigCollector.of("ends_delight:ender_sauce:8:15"),
            FoodConfigCollector.of("ends_delight:raw_ender_mite_meat:1:1"),
            FoodConfigCollector.of("ends_delight:non_hatchable_dragon_egg:8:5"),
            FoodConfigCollector.of("ends_delight:shulker_meat:7:4"),
            FoodConfigCollector.of("unusualend:chorus_juice:2:2"),
            FoodConfigCollector.of("ends_delight:liquid_dragon_egg:3:3"),
            FoodConfigCollector.of("ends_delight:shulker_meat_slice:4:1"),
            FoodConfigCollector.of("unusualend:ender_firefly_egg:2:3"),
            FoodConfigCollector.of("unusualend:chorus_petal:1:1"),
            FoodConfigCollector.of("unusualend:chorus_pie:3:4"),
            FoodConfigCollector.of("unusualend:ender_stew:6:1"),
            FoodConfigCollector.of("unusualprehistory:kentro_eggs:3:3"),
            FoodConfigCollector.of("unusualprehistory:hwacha_eggs:3:3"),
            FoodConfigCollector.of("unusualprehistory:ulugh_eggs:3:3"),
            FoodConfigCollector.of("unusualprehistory:antarcto_eggs:3:3"),
            FoodConfigCollector.of("unusualprehistory:austro_eggs:3:3"),
            FoodConfigCollector.of("unusualprehistory:pachy_eggs:3:3"),
            FoodConfigCollector.of("unusualprehistory:raptor_eggs:3:3"),
            FoodConfigCollector.of("unusualprehistory:trike_eggs:4:4"),
            FoodConfigCollector.of("unusualprehistory:rex_eggs:6:3"),
            FoodConfigCollector.of("unusualprehistory:coty_eggs:3:3"),
            FoodConfigCollector.of("unusualprehistory:meaty_buffet:12:15"),
            FoodConfigCollector.of("unusualprehistory:majunga_eggs:3:3"),
            FoodConfigCollector.of("unusualprehistory:anuro_eggs:3:3"),
            FoodConfigCollector.of("unusualprehistory:raw_austro:6:3"),
            FoodConfigCollector.of("unusualprehistory:raw_coty:5:3"),
            FoodConfigCollector.of("nourished_nether:raw_hoglin:8:8"),
            FoodConfigCollector.of("gothic:meat:7:8"),
            FoodConfigCollector.of("gothic:bug_meat:2:2"),
            FoodConfigCollector.of("gothic:scavenger_egg:3:3"),
            FoodConfigCollector.of("gothic:snapperweed:1:1"),
            FoodConfigCollector.of("gothic:blue_elder:1:1"),
            FoodConfigCollector.of("unusualend:wandering_stew:1:1"),
            FoodConfigCollector.of("nethersdelight:raw_stuffed_hoglin:15:15"),
            FoodConfigCollector.of("nethersdelight:hoglin_ear:1:1"),
            FoodConfigCollector.of("nethersdelight:ground_strider:6:2"),
            FoodConfigCollector.of("nethersdelight:strider_slice:8:8"),
            FoodConfigCollector.of("nethersdelight:hoglin_ear:1:1"),
            FoodConfigCollector.of("nethersdelight:hoglin_loin:6:6"),
            FoodConfigCollector.of("nethersdelight:propelpearl:1:1"),
            FoodConfigCollector.of("orcz:squig_eye:6:6"),
            FoodConfigCollector.of("orcz:orceye:4:4"),
            FoodConfigCollector.of("goated:chevon:6:6"),
            FoodConfigCollector.of("rottencreatures:frozen_rotten_flesh:4:4"),
            FoodConfigCollector.of("regions_unexplored:tall_green_bioshroom:3:3"),
            FoodConfigCollector.of("regions_unexplored:green_bioshroom:3:3"),
            FoodConfigCollector.of("regions_unexplored:blue_bioshroom:3:3"),
            FoodConfigCollector.of("regions_unexplored:tall_blue_bioshroom:3:3"),
            FoodConfigCollector.of("regions_unexplored:tall_pink_bioshroom:3:3"),
            FoodConfigCollector.of("regions_unexplored:pink_bioshroom:3:3"),
            FoodConfigCollector.of("regions_unexplored:yellow_bioshroom:3:3"),
            FoodConfigCollector.of("regions_unexplored:tall_yellow_bioshroom:3:3"),
            FoodConfigCollector.of("quark:glow_shroom:3:3"),
            FoodConfigCollector.of("gothic:cave_mushrooms:3:3"),
            FoodConfigCollector.of("gothic:tall_mushrooms:3:3"),
            FoodConfigCollector.of("gothic:miners_food_wild:3:3"),
            FoodConfigCollector.of("gothic:black_mushroom:3:3"),
            FoodConfigCollector.of("phantasm:putac_shroom:3:3"),
            FoodConfigCollector.of("farmersdelight:red_mushroom_colony:10:3"),
            FoodConfigCollector.of("farmersdelight:brown_mushroom_colony:10:3"),
            FoodConfigCollector.of("gothic:miners_food:3:3"),
            FoodConfigCollector.of("gothic:black_mushroom_item:3:3"),
            FoodConfigCollector.of("gothic:blue_elder:3:3"),
            FoodConfigCollector.of("frozen_delight:truffle_slice:4:8"),
            FoodConfigCollector.of("frozenup:truffle:8:4"),
            FoodConfigCollector.of("frozen_delight:mushroom_mix:16:3"),
            FoodConfigCollector.of("nourished_nether:nether_fungus_stew:7:3"),
            FoodConfigCollector.of("nethersdelight:warped_fungus_colony:10:3"),
            FoodConfigCollector.of("nethersdelight:crimson_fungus_colony:10:3"),
            FoodConfigCollector.of("unusualend:chorus_fungus:3:3"),
            FoodConfigCollector.of("orcz:orcshroom:3:3"),
            FoodConfigCollector.of("orcz:blue_orcshroom:3:3"),
            FoodConfigCollector.of("waterstrainer:worm:1:1"),
            FoodConfigCollector.of("undergarden:veil_mushroom:1:1"),
            FoodConfigCollector.of("undergarden:indigo_mushroom:1:1"),
            FoodConfigCollector.of("undergarden:smogstem_sapling:1:1"),
            FoodConfigCollector.of("undergarden:blood_mushroom:1:1"),
            FoodConfigCollector.of("undergarden:ink_mushroom:1:1"),
            FoodConfigCollector.of("undergarden:blisterberry:1:1"),
            FoodConfigCollector.of("undergarden:rotten_blisterberry:1:1"),
            FoodConfigCollector.of("undergarden:ink_mushroom:1:1"),
            FoodConfigCollector.of("undergarden:bloody_stew:1:1"),
            FoodConfigCollector.of("undergarden:inky_stew:1:1"),
            FoodConfigCollector.of("undergarden:veiled_stew:1:1"),
            FoodConfigCollector.of("undergarden:indigo_stew:1:1"),
            FoodConfigCollector.of("butchersdelight:deadchiken:6:4"),
            FoodConfigCollector.of("butchersdelight:deadstrider:6:4"),
            FoodConfigCollector.of("butchersdelight:deadrabbitbrown:6:4"),
            FoodConfigCollector.of("butchersdelight:deadllama:6:4"),
            FoodConfigCollector.of("butchersdelight:deadhoglin:6:4"),
            FoodConfigCollector.of("butchersdelight:deadgoat:6:4"),
            FoodConfigCollector.of("butchersdelight:deadsheep:6:4"),
            FoodConfigCollector.of("butchersdelight:deadpig:6:4"),
            FoodConfigCollector.of("butchersdelight:dead_cow:6:4"),
            FoodConfigCollector.of("butchersdelightfoods:llama_loin:6:4"),
            FoodConfigCollector.of("butchersdelightfoods:llamma_ribs:6:4"),
            FoodConfigCollector.of("butchersdelightfoods:llama_leg:6:4"),
            FoodConfigCollector.of("butchersdelightfoods:goat_loin:6:4"),
            FoodConfigCollector.of("butchersdelightfoods:goatrack:6:4"),
            FoodConfigCollector.of("butchersdelightfoods:goat_shank:6:4"),
            FoodConfigCollector.of("butchersdelightfoods:porkribs:6:4"),
            FoodConfigCollector.of("butchersdelightfoods:porkloin:6:4"),
            FoodConfigCollector.of("butchersdelightfoods:ham:6:4"),
            FoodConfigCollector.of("butchersdelightfoods:sheeploin:6:4"),
            FoodConfigCollector.of("butchersdelightfoods:sheeprack:6:4"),
            FoodConfigCollector.of("butchersdelightfoods:sheepshank:6:4"),
            FoodConfigCollector.of("butchersdelightfoods:beeftenderloin:6:4"));

    @ConfigOption(side = ConfigSide.SERVER, category = {"food", "sea_dragon"}, key = "seaDragonFoods", comment = {"Dragon food formatting: namespace:path:nutrition:saturation (prefix namespace with # for tags)", "Nutrition / saturation values are optional - human values will be used if they are missing.", "Saturation can be defined with decimals (e.g. 0.3)"})
    public static List<FoodConfigCollector> seaDragonFoods = Arrays.asList(
            // TODO :: check for old, clean stuff up
            FoodConfigCollector.of("c:foods/raw_fish:6:4"),
            FoodConfigCollector.of("minecraft:kelp:1:1"),
            FoodConfigCollector.of("minecraft:pufferfish:8:8"),
            FoodConfigCollector.of("dragonsurvival:sea_dragon_treat:4:8"),
            FoodConfigCollector.of("dragonsurvival:seasoned_fish:12:10"),
            FoodConfigCollector.of("dragonsurvival:golden_coral_pufferfish:12:14"),
            FoodConfigCollector.of("dragonsurvival:frozen_raw_fish:2:1"),
            FoodConfigCollector.of("dragonsurvival:golden_turtle_egg:15:12"),
            FoodConfigCollector.of("additionaldragons:slippery_sushi:10:8"),
            FoodConfigCollector.of("aoa3:raw_candlefish:9:9"),
            FoodConfigCollector.of("aoa3:raw_crimson_skipper:8:8"),
            FoodConfigCollector.of("aoa3:raw_fingerfish:4:4"),
            FoodConfigCollector.of("aoa3:raw_pearl_stripefish:5:4"),
            FoodConfigCollector.of("aoa3:raw_limefish:5:5"),
            FoodConfigCollector.of("aoa3:raw_sailback:6:5"),
            FoodConfigCollector.of("netherdepthsupgrade:soulsucker:6:7"),
            FoodConfigCollector.of("netherdepthsupgrade:obsidianfish:6:7"),
            FoodConfigCollector.of("netherdepthsupgrade:lava_pufferfish:8:7"),
            FoodConfigCollector.of("netherdepthsupgrade:searing_cod:6:7"),
            FoodConfigCollector.of("netherdepthsupgrade:glowdine:6:7"),
            FoodConfigCollector.of("netherdepthsupgrade:warped_kelp:2:2"),
            FoodConfigCollector.of("netherdepthsupgrade:lava_pufferfish_slice:2:2"),
            FoodConfigCollector.of("netherdepthsupgrade:glowdine_slice:2:2"),
            FoodConfigCollector.of("netherdepthsupgrade:soulsucker_slice:2:2"),
            FoodConfigCollector.of("netherdepthsupgrade:obsidianfish_slice:2:2"),
            FoodConfigCollector.of("netherdepthsupgrade:searing_cod_slice:2:2"),
            FoodConfigCollector.of("crittersandcompanions:clam:10:3"),
            FoodConfigCollector.of("aoa3:raw_golden_gullfish:10:2"),
            FoodConfigCollector.of("aoa3:raw_turquoise_stripefish:7:6"),
            FoodConfigCollector.of("aoa3:raw_violet_skipper:7:7"),
            FoodConfigCollector.of("aoa3:raw_rocketfish:4:10"),
            FoodConfigCollector.of("aoa3:raw_crimson_stripefish:8:7"),
            FoodConfigCollector.of("aoa3:raw_sapphire_strider:9:8"),
            FoodConfigCollector.of("aoa3:raw_dark_hatchetfish:9:9"),
            FoodConfigCollector.of("aoa3:raw_ironback:10:9"),
            FoodConfigCollector.of("aoa3:raw_rainbowfish:11:11"),
            FoodConfigCollector.of("aoa3:raw_razorfish:12:14"),
            FoodConfigCollector.of("alexsmobs:lobster_tail:4:5"),
            FoodConfigCollector.of("alexsmobs:blobfish:8:9"),
            FoodConfigCollector.of("oddwatermobs:raw_ghost_shark:8:8"),
            FoodConfigCollector.of("oddwatermobs:raw_isopod:4:2"),
            FoodConfigCollector.of("oddwatermobs:raw_mudskipper:6:7"),
            FoodConfigCollector.of("oddwatermobs:raw_coelacanth:9:10"),
            FoodConfigCollector.of("oddwatermobs:raw_anglerfish:6:6"),
            FoodConfigCollector.of("oddwatermobs:deep_sea_fish:4:2"),
            FoodConfigCollector.of("oddwatermobs:crab_leg:5:6"),
            FoodConfigCollector.of("simplefarming:raw_calamari:5:6"),
            FoodConfigCollector.of("unnamedanimalmod:elephantnose_fish:5:6"),
            FoodConfigCollector.of("unnamedanimalmod:flashlight_fish:5:6"),
            FoodConfigCollector.of("born_in_chaos_v1:sea_terror_eye:10:4"),
            FoodConfigCollector.of("born_in_chaos_v1:rotten_fish:4:2"),
            FoodConfigCollector.of("unnamedanimalmod:rocket_killifish:5:6"),
            FoodConfigCollector.of("unnamedanimalmod:leafy_seadragon:5:6"),
            FoodConfigCollector.of("unnamedanimalmod:elephantnose_fish:5:6"),
            FoodConfigCollector.of("betteranimalsplus:eel_meat_raw:5:6"),
            FoodConfigCollector.of("betteranimalsplus:calamari_raw:4:5"),
            FoodConfigCollector.of("betteranimalsplus:crab_meat_raw:4:4"),
            FoodConfigCollector.of("aquaculture:fish_fillet_raw:2:2"),
            FoodConfigCollector.of("aquaculture:goldfish:8:4"),
            FoodConfigCollector.of("aquaculture:algae:3:2"),
            FoodConfigCollector.of("betterendforge:end_fish_raw:6:7"),
            FoodConfigCollector.of("betterendforge:hydralux_petal:3:3"),
            FoodConfigCollector.of("betterendforge:charnia_green:2:2"),
            FoodConfigCollector.of("shroomed:raw_shroomfin:5:6"),
            FoodConfigCollector.of("undergarden:raw_gwibling:5:6"),
            FoodConfigCollector.of("bettas:betta_fish:4:5"),
            FoodConfigCollector.of("quark:crab_leg:4:4"),
            FoodConfigCollector.of("pamhc2foodextended:rawtofishitem"),
            FoodConfigCollector.of("fins:banded_redback_shrimp:6:1"),
            FoodConfigCollector.of("fins:night_light_squid:6:2"),
            FoodConfigCollector.of("fins:night_light_squid_tentacle:6:2"),
            FoodConfigCollector.of("fins:emerald_spindly_gem_crab:7:2"),
            FoodConfigCollector.of("fins:amber_spindly_gem_crab:7:2"),
            FoodConfigCollector.of("fins:rubby_spindly_gem_crab:7:2"),
            FoodConfigCollector.of("fins:sapphire_spindly_gem_crab:7:2"),
            FoodConfigCollector.of("fins:pearl_spindly_gem_crab:7:2"),
            FoodConfigCollector.of("fins:papa_wee:6:2"),
            FoodConfigCollector.of("fins:bugmeat:4:2"),
            FoodConfigCollector.of("fins:raw_golden_river_ray_wing:6:2"),
            FoodConfigCollector.of("fins:red_bull_crab_claw:4:4"),
            FoodConfigCollector.of("fins:white_bull_crab_claw:4:4"),
            FoodConfigCollector.of("fins:wherble_fin:1:1"),
            FoodConfigCollector.of("forbidden_arcanus:tentacle:5:2"),
            FoodConfigCollector.of("pneumaticcraft:raw_salmon_tempura:6:10"),
            FoodConfigCollector.of("rats:ratfish:4:2"),
            FoodConfigCollector.of("upgrade_aquatic:purple_pickerelweed:2:2"),
            FoodConfigCollector.of("upgrade_aquatic:blue_pickerelweed:2:2"),
            FoodConfigCollector.of("upgrade_aquatic:polar_kelp:2:2"),
            FoodConfigCollector.of("upgrade_aquatic:tongue_kelp:2:2"),
            FoodConfigCollector.of("upgrade_aquatic:thorny_kelp:2:2"),
            FoodConfigCollector.of("upgrade_aquatic:ochre_kelp:2:2"),
            FoodConfigCollector.of("upgrade_aquatic:lionfish:8:9"),
            FoodConfigCollector.of("aquaculture:sushi:6:5"),
            FoodConfigCollector.of("freshwarriors:fresh_soup:15:10"),
            FoodConfigCollector.of("freshwarriors:beluga_caviar:10:3"),
            FoodConfigCollector.of("freshwarriors:piranha:4:1"),
            FoodConfigCollector.of("freshwarriors:tilapia:4:1"),
            FoodConfigCollector.of("freshwarriors:stuffed_piranha:4:1"),
            FoodConfigCollector.of("freshwarriors:tigerfish:5:5"),
            FoodConfigCollector.of("freshwarriors:toe_biter_leg:3:3"),
            FoodConfigCollector.of("mysticalworld:raw_squid:6:5"),
            FoodConfigCollector.of("aquafina:fresh_soup:10:10"),
            FoodConfigCollector.of("aquafina:beluga_caviar:10:3"),
            FoodConfigCollector.of("aquafina:raw_piranha:4:1"),
            FoodConfigCollector.of("aquafina:raw_tilapia:4:1"),
            FoodConfigCollector.of("aquafina:stuffed_piranha:4:1"),
            FoodConfigCollector.of("aquafina:tigerfish:5:5"),
            FoodConfigCollector.of("aquafina:toe_biter_leg:3:3"),
            FoodConfigCollector.of("aquafina:raw_angelfish:4:1"),
            FoodConfigCollector.of("aquafina:raw_football_fish:4:1"),
            FoodConfigCollector.of("aquafina:raw_foxface_fish:4:1"),
            FoodConfigCollector.of("aquafina:raw_royal_gramma:4:1"),
            FoodConfigCollector.of("aquafina:raw_starfish:4:1"),
            FoodConfigCollector.of("aquafina:spider_crab_leg:4:1"),
            FoodConfigCollector.of("aquafina:raw_stingray_slice:4:1"),
            FoodConfigCollector.of("prehistoricfauna:raw_ceratodus:5:5"),
            FoodConfigCollector.of("prehistoricfauna:raw_cyclurus:4:4"),
            FoodConfigCollector.of("prehistoricfauna:raw_potamoceratodus:5:5"),
            FoodConfigCollector.of("prehistoricfauna:raw_myledaphus:4:4"),
            FoodConfigCollector.of("prehistoricfauna:raw_gar:4:4"),
            FoodConfigCollector.of("prehistoricfauna:raw_oyster:4:3"),
            FoodConfigCollector.of("prehistoric_delight:prehistoric_fillet:3:3"),
            FoodConfigCollector.of("seadwellers:rainbow_trout:10:10"),
            FoodConfigCollector.of("crittersandcompanions:koi_fish:5:5"),
            FoodConfigCollector.of("aquamirae:elodea:3:3"),
            FoodConfigCollector.of("croptopia:clam:3:3"),
            FoodConfigCollector.of("croptopia:calamari:2:3"),
            FoodConfigCollector.of("croptopia:anchovy:3:2"),
            FoodConfigCollector.of("croptopia:crab:6:8"),
            FoodConfigCollector.of("croptopia:glowing_calamari:4:5"),
            FoodConfigCollector.of("croptopia:oyster:2:4"),
            FoodConfigCollector.of("croptopia:roe:1:2"),
            FoodConfigCollector.of("croptopia:shrimp:2:2"),
            FoodConfigCollector.of("croptopia:tuna:6:4"),
            FoodConfigCollector.of("aquamirae:spinefish:4:4"),
            FoodConfigCollector.of("alexsmobs:flying_fish:6:4"),
            FoodConfigCollector.of("netherdepthsupgrade:eyeball:3:3"),
            FoodConfigCollector.of("netherdepthsupgrade:eyeball_fish:3:3"),
            FoodConfigCollector.of("oceansdelight:guardian:4:3"),
            FoodConfigCollector.of("oceansdelight:guardian_tail:1:3"),
            FoodConfigCollector.of("oceansdelight:cut_tentacles:3:1"),
            FoodConfigCollector.of("oceansdelight:tentacles:3:4"),
            FoodConfigCollector.of("oceansdelight:tentacle_on_a_stick:3:4"),
            FoodConfigCollector.of("oceansdelight:fugu_slice:5:4"),
            FoodConfigCollector.of("oceansdelight:elder_guardian_slice:8:6"),
            FoodConfigCollector.of("oceansdelight:elder_guardian_slab:15:15"),
            FoodConfigCollector.of("upgrade_aquatic:elder_eye:15:15"),
            FoodConfigCollector.of("unusualprehistory:golden_scau:15:15"),
            FoodConfigCollector.of("unusualprehistory:raw_scau:4:3"),
            FoodConfigCollector.of("unusualprehistory:raw_stetha:4:3"),
            FoodConfigCollector.of("unusualprehistory:stetha_eggs:4:3"),
            FoodConfigCollector.of("unusualprehistory:beelze_eggs:4:3"),
            FoodConfigCollector.of("unusualprehistory:scau_eggs:4:3"),
            FoodConfigCollector.of("unusualprehistory:ammon_eggs:4:3"),
            FoodConfigCollector.of("unusualprehistory:dunk_eggs:4:3"),
            FoodConfigCollector.of("netherdepthsupgrade:crimson_seagrass:2:2"),
            FoodConfigCollector.of("netherdepthsupgrade:crimson_kelp:2:2"),
            FoodConfigCollector.of("netherdepthsupgrade:warped_seagrass:2:2"),
            FoodConfigCollector.of("undergarden:glitterkelp:2:2"),
            FoodConfigCollector.of("enlightened_end:raw_stalker:10:4")
    );

    // Tooltip maps
    public static CopyOnWriteArrayList<Item> CAVE_DRAGON_FOOD = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<Item> FOREST_DRAGON_FOOD = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<Item> SEA_DRAGON_FOOD = new CopyOnWriteArrayList<>();

    private static ConcurrentHashMap<String, Map<Item, FoodProperties>> DRAGON_FOODS = new ConcurrentHashMap<>();

    public static void rebuildFoodMap() {
        DragonSurvival.LOGGER.debug("Rebuilding food map...");
        DRAGON_FOODS = buildDragonFoodMap();

        // No need to keep them in-memory (they are rebuilt in 'ConfigHandler#handleConfigChange')
        // Currently this method is only called on the server thread meaning there should be no issues
        caveDragonFoods.clear();
        seaDragonFoods.clear();
        forestDragonFoods.clear();

        // Clear tooltip maps
        // TODO :: rebuild them here as well?
        CAVE_DRAGON_FOOD.clear();
        FOREST_DRAGON_FOOD.clear();
        SEA_DRAGON_FOOD.clear();
    }

    private static ConcurrentHashMap<String, Map<Item, FoodProperties>> buildDragonFoodMap() {
        ConcurrentHashMap<String, Map<Item, FoodProperties>> foodMap = new ConcurrentHashMap<>();
        merge(foodMap, caveDragonFoods, DragonTypes.CAVE.getTypeName());
        merge(foodMap, seaDragonFoods, DragonTypes.SEA.getTypeName());
        merge(foodMap, forestDragonFoods, DragonTypes.FOREST.getTypeName());
        return foodMap;
    }

    /**
     * Add the collected entries for the dragon type to the global food map (replacing previously added food properties for items if a better one (higher nutrition / saturation) was found)
     */
    private static void merge(final ConcurrentHashMap<String, Map<Item, FoodProperties>> foodMap, final List<FoodConfigCollector> collectors, final String type) {
        foodMap.put(type, new ConcurrentHashMap<>()); // The logic which comes after this needs at least an empty map per dragon type

        for (FoodConfigCollector collector : collectors) {
            Map<Item, FoodProperties> collectedData = collector.collectFoodData();

            for (Item item : collectedData.keySet()) {
                foodMap.get(type).merge(item, calculate(item, collectedData.get(item)), (oldData, newData) -> {
                    if (newData.nutrition() > oldData.nutrition() || newData.nutrition() == oldData.nutrition() && newData.saturation() > oldData.saturation()) {
                        return newData;
                    }

                    return oldData;
                });
            }
        }
    }

    private static FoodProperties calculate(final Item item, final FoodProperties properties) {
        FoodProperties.Builder builder = new FoodProperties.Builder();
        boolean shouldKeepEffects = item.builtInRegistryHolder().is(DSItemTags.KEEP_EFFECTS);

        // saturation is calculated in 'FoodConstants#saturationByModifier' when the properties are built
        float saturation = (properties.saturation() / properties.nutrition()) / 2;

        if (properties.canAlwaysEat()) {
            builder.alwaysEdible();
        }

        // eat duration in ticks is 16 when fast eating is enabled
        if (properties.eatDurationTicks() <= 16) {
            builder.fast();
        }

        properties.effects().forEach(possibleEffect -> {
            if (shouldKeepEffects || possibleEffect.effect().getEffect().value().isBeneficial()) {
                builder.effect(possibleEffect.effectSupplier(), possibleEffect.probability());
            }
        });

        builder.nutrition(properties.nutrition()).saturationModifier(saturation);
        return builder.build();
    }

    private static FoodProperties getBadFoodProperties() {
        FoodProperties.Builder builder = new FoodProperties.Builder();
        builder.effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 1.0F);
        builder.effect(() -> new MobEffectInstance(MobEffects.POISON, 600, 0), 0.5F);
        builder.nutrition(1);
        return builder.build();
    }

    public static List<Item> getEdibleFoods(final AbstractDragonType type) {
        if (type == null) {
            return List.of();
        }

        if (DragonUtils.isDragonType(type, DragonTypes.FOREST) && !FOREST_DRAGON_FOOD.isEmpty()) {
            return FOREST_DRAGON_FOOD;
        } else if (DragonUtils.isDragonType(type, DragonTypes.SEA) && !SEA_DRAGON_FOOD.isEmpty()) {
            return SEA_DRAGON_FOOD;
        } else if (DragonUtils.isDragonType(type, DragonTypes.CAVE) && !CAVE_DRAGON_FOOD.isEmpty()) {
            return CAVE_DRAGON_FOOD;
        }

        CopyOnWriteArrayList<Item> foods = new CopyOnWriteArrayList<>();

        for (Item item : DRAGON_FOODS.get(type.getTypeName()).keySet()) {
            final FoodProperties foodProperties = DRAGON_FOODS.get(type.getTypeName()).get(item);
            boolean isSafe = true;

            if (foodProperties != null) {
                for (FoodProperties.PossibleEffect possibleEffect : foodProperties.effects()) {
                    if (ToolTipHandler.hideUnsafeFood && !possibleEffect.effectSupplier().get().getEffect().value().isBeneficial()) {
                        isSafe = false;
                        break;
                    }
                }

                if (isSafe && (foodProperties.nutrition() > 0 || foodProperties.saturation() > 0)) {
                    foods.add(item);
                }
            }
        }

        if (DragonUtils.isDragonType(type, DragonTypes.FOREST)) {
            FOREST_DRAGON_FOOD = foods;
        } else if (DragonUtils.isDragonType(type, DragonTypes.CAVE)) {
            CAVE_DRAGON_FOOD = foods;
        } else if (DragonUtils.isDragonType(type, DragonTypes.SEA)) {
            SEA_DRAGON_FOOD = foods;
        }

        return foods;
    }

    public static @Nullable FoodProperties getDragonFoodProperties(final ItemStack stack, final AbstractDragonType type) {
        FoodProperties properties = DRAGON_FOODS.get(type.getTypeName()).get(stack.getItem());

        if (properties != null) {
            return properties;
        }

        FoodProperties baseProperties = stack.getFoodProperties(null);
        if (baseProperties != null) {
            if (requireDragonFood) {
                return getBadFoodProperties();
            } else {
                return baseProperties;
            }
        }

        return null;
    }

    public static @Nullable FoodProperties getDragonFoodProperties(final Item item, final AbstractDragonType type) {
        FoodProperties properties = DRAGON_FOODS.get(type.getTypeName()).get(item);

        if (properties != null) {
            return properties;
        }

        FoodProperties baseProperties = item.getFoodProperties(new ItemStack(item), null);
        if (baseProperties != null) {
            if (requireDragonFood) {
                return getBadFoodProperties();
            } else {
                return baseProperties;
            }
        }

        return null;
    }

    public static boolean isEdible(final ItemStack stack, final AbstractDragonType type) {
        if (requireDragonFood && type != null && DRAGON_FOODS.get(type.getTypeName()).containsKey(stack.getItem())) {
            return true;
        }

        return stack.getFoodProperties(null) != null;
    }

    public static int getUseDuration(final ItemStack itemStack, final Player entity) {
        FoodProperties foodProperties = getDragonFoodProperties(itemStack.getItem(), DragonStateProvider.getData(entity).getType());

        if (foodProperties != null) {
            return foodProperties.eatDurationTicks();
        } else {
            return itemStack.getUseDuration(entity);
        }
    }

    @SubscribeEvent
    public static void setDragonFoodUseDuration(final LivingEntityUseItemEvent.Start event) {
        DragonStateProvider.getOptional(event.getEntity()).ifPresent(handler -> {
            if (handler.isDragon()) {
                if (DragonFoodHandler.isEdible(event.getItem(), handler.getType())) {
                    FoodProperties foodProperties = DragonFoodHandler.getDragonFoodProperties(event.getItem().getItem(), handler.getType());
                    if (foodProperties != null) {
                        event.setDuration(foodProperties.eatDurationTicks());
                    }
                }
            }
        });
    }
}