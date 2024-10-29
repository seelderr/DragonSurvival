package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.ToolTipHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigUtils;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.config.types.FoodConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.tags.DSItemTags;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
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

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

@EventBusSubscriber
public class DragonFoodHandler { // TODO :: create some tier-based tags for grouping, like 'light_cave_dragon_food' which has low nutrition values for easier config?
    // Food general
    @ConfigOption(side = ConfigSide.SERVER, category = "food", key = "requireDragonFood", comment = "Force dragons to eat a unique diet for their type.")
    public static Boolean requireDragonFood = true;

    // Dragon Food List
    @ConfigOption(side = ConfigSide.SERVER, category = {"food", "cave_dragon"}, key = "caveDragonFoods", comment = {"Dragon food formatting: namespace:path:nutrition:saturation (prefix namespace with # for tags)", "Nutrition / saturation values are optional - human values will be used if they are missing.", "Saturation can be defined with decimals (e.g. 0.3)"})
    public static List<FoodConfig> caveDragonFoods = Arrays.asList(
            FoodConfig.of(ConfigUtils.location(ItemTags.COALS), 1, 1),
            FoodConfig.of(ConfigUtils.location(Items.CHARCOAL), 1, 2),
            FoodConfig.of(ConfigUtils.location(DSItems.CHARGED_COAL), 6, 1),
            FoodConfig.of(ConfigUtils.location(DSItems.CHARRED_MEAT), 8, 10),
            FoodConfig.of(ConfigUtils.location(DSItems.CAVE_DRAGON_TREAT), 4, 8),
            FoodConfig.of(ConfigUtils.location(DSItems.CHARRED_SEAFOOD), 7, 11),
            FoodConfig.of(ConfigUtils.location(DSItems.CHARRED_VEGETABLE), 8, 9),
            FoodConfig.of(ConfigUtils.location(DSItems.CHARRED_MUSHROOM), 9, 9),
            FoodConfig.of(ConfigUtils.location(DSItems.CHARGED_SOUP), 15, 15),
            FoodConfig.of(ConfigUtils.location(DSItems.HOT_DRAGON_ROD), 4, 15),
            FoodConfig.of(ConfigUtils.location(DSItems.EXPLOSIVE_COPPER), 6, 4),
            FoodConfig.of(ConfigUtils.location(DSItems.DOUBLE_QUARTZ), 8, 6),
            FoodConfig.of(ConfigUtils.location(DSItems.QUARTZ_EXPLOSIVE_COPPER), 12, 18),
            FoodConfig.of("netherdepthsupgrade:blazefish", 6, 7),
            FoodConfig.of("netherdepthsupgrade:cooked_magmacubefish_slice", 2, 2),
            FoodConfig.of("netherdepthsupgrade:blazefish_slice", 2, 2),
            FoodConfig.of("netherdepthsupgrade:magmacubefish", 6, 7),
            FoodConfig.of("netherdepthsupgrade:fortress_grouper", 3, 1),
            FoodConfig.of("desolation:cinder_fruit", 6, 7),
            FoodConfig.of("desolation:powered_cinder_fruit", 8, 12),
            FoodConfig.of("desolation:activatedcharcoal", 2, 2),
            FoodConfig.of("desolation:infused_powder", 10, 10),
            FoodConfig.of("desolation:primed_ash", 7, 8),
            FoodConfig.of("undergarden:ditchbulb", 5, 6),
            FoodConfig.of("xreliquary:molten_core", 1, 1),
            FoodConfig.of("mekanism:dust_coal", 1, 1),
            FoodConfig.of("mekanism:dust_charcoal", 1, 1),
            FoodConfig.of("thermal:coal_coke", 1, 1),
            FoodConfig.of("thermal:basalz_rod", 2, 4),
            FoodConfig.of("thermal:basalz_powder", 1, 2),
            FoodConfig.of("create:blaze_cake", 10, 10),
            FoodConfig.of("create:creative_blaze_cake", 50, 50),
            FoodConfig.of("nethersdelight:nether_skewer", 6, 8)
    );

    @ConfigOption(side = ConfigSide.SERVER, category = {"food", "forest_dragon"}, key = "forestDragonFoods", comment = {"Dragon food formatting: namespace:path:nutrition:saturation (prefix namespace with # for tags)", "Nutrition / saturation values are optional - human values will be used if they are missing.", "Saturation can be defined with decimals (e.g. 0.3)"})
    public static List<FoodConfig> forestDragonFoods = Arrays.asList(
            FoodConfig.of(ConfigUtils.location(Tags.Items.FOODS_RAW_MEAT), 6, 4),
            FoodConfig.of(ConfigUtils.location(Tags.Items.FOODS_BERRY), 2, 1),
            FoodConfig.of(ConfigUtils.location(Tags.Items.MUSHROOMS), 1, 1),
            FoodConfig.of(ConfigUtils.location(Items.ROTTEN_FLESH), 2, 4),
            FoodConfig.of(ConfigUtils.location(Items.SPIDER_EYE), 6, 8),
            FoodConfig.of(ConfigUtils.location(Items.RABBIT), 7, 8),
            FoodConfig.of(ConfigUtils.location(Items.POISONOUS_POTATO), 7, 8),
            FoodConfig.of(ConfigUtils.location(Items.CHORUS_FRUIT), 9, 8),
            FoodConfig.of(ConfigUtils.location(Items.BROWN_MUSHROOM), 2, 1),
            FoodConfig.of(ConfigUtils.location(Items.RED_MUSHROOM), 2, 3),
            FoodConfig.of(ConfigUtils.location(Items.HONEY_BOTTLE), 6, 1),
            FoodConfig.of(ConfigUtils.location(Items.WARPED_FUNGUS), 3, 3),
            FoodConfig.of(ConfigUtils.location(Items.CRIMSON_FUNGUS), 3, 3),
            FoodConfig.of(ConfigUtils.location(DSItems.FOREST_DRAGON_TREAT), 4, 8),
            FoodConfig.of(ConfigUtils.location(DSItems.MEAT_CHORUS_MIX), 12, 8),
            FoodConfig.of(ConfigUtils.location(DSItems.MEAT_WILD_BERRIES), 12, 10),
            FoodConfig.of(ConfigUtils.location(DSItems.SMELLY_MEAT_PORRIDGE), 6, 10),
            FoodConfig.of(ConfigUtils.location(DSItems.DIAMOND_CHORUS), 15, 12),
            FoodConfig.of(ConfigUtils.location(DSItems.LUMINOUS_OINTMENT), 5, 3),
            FoodConfig.of(ConfigUtils.location(DSItems.SWEET_SOUR_RABBIT), 10, 6),
            FoodConfig.of("additionaldragons:cursed_marrow"),
            FoodConfig.of("aquaculture:turtle_soup", 8, 8),
            FoodConfig.of("netherdepthsupgrade:wither_bonefish", 4, 6),
            FoodConfig.of("netherdepthsupgrade:bonefish", 4, 6),
            FoodConfig.of("phantasm:chorus_fruit_salad", 10, 10),
            FoodConfig.of("aoa3:fiery_chops", 6, 7),
            FoodConfig.of("aoa3:raw_chimera_chop", 6, 7),
            FoodConfig.of("aoa3:raw_furlion_chop", 6, 7),
            FoodConfig.of("aoa3:raw_halycon_beef", 7, 8),
            FoodConfig.of("aoa3:raw_charger_shank", 6, 7),
            FoodConfig.of("aoa3:trilliad_leaves", 8, 11),
            // TODO :: check for old, clean stuff up
            FoodConfig.of("pamhc2foodextended:rawtofabbititem"),
            FoodConfig.of("pamhc2foodextended:rawtofickenitem"),
            FoodConfig.of("quark:golden_frog_leg:12:14"),
            FoodConfig.of("pamhc2foodextended:rawtofuttonitem"),
            FoodConfig.of("alexsmobs:kangaroo_meat:5:6"),
            FoodConfig.of("alexsmobs:moose_ribs:6:8"),
            FoodConfig.of("simplefarming:raw_horse_meat:5:6"),
            FoodConfig.of("simplefarming:raw_bacon:3:3"),
            FoodConfig.of("simplefarming:raw_chicken_wings:2:3"),
            FoodConfig.of("simplefarming:raw_sausage:3:4"),
            FoodConfig.of("xenoclustwo:raw_tortice:7:8"),
            FoodConfig.of("unnamedanimalmod:musk_ox_shank:7:8"),
            FoodConfig.of("unnamedanimalmod:frog_legs:5:6"),
            FoodConfig.of("unnamedanimalmod:mangrove_fruit:4:7"),
            FoodConfig.of("betteranimalsplus:venisonraw:7:6"),
            FoodConfig.of("born_in_chaos_v1:corpse_maggot:1:1"),
            FoodConfig.of("born_in_chaos_v1:monster_flesh:1:1"),
            FoodConfig.of("betteranimalsplus:pheasantraw:7:5"),
            FoodConfig.of("betteranimalsplus:turkey_leg_raw:4:5"),
            FoodConfig.of("infernalexp:raw_hogchop:6:7"),
            FoodConfig.of("infernalexp:cured_jerky:10:7"),
            FoodConfig.of("rats:raw_rat:4:5"),
            FoodConfig.of("aquaculture:frog:4:5"),
            FoodConfig.of("aquaculture:frog_legs_raw:4:4"),
            FoodConfig.of("aquaculture:box_turtle:4:5"),
            FoodConfig.of("aquaculture:arrau_turtle:4:5"),
            FoodConfig.of("aquaculture:starshell_turtle:4:5"),
            FoodConfig.of("undergarden:raw_gloomper_leg:4:5"),
            FoodConfig.of("undergarden:raw_dweller_meat:6:7"),
            FoodConfig.of("farmersdelight:chicken_cuts:3:3"),
            FoodConfig.of("farmersdelight:bacon:3:3"),
            FoodConfig.of("farmersdelight:ham:9:10"),
            FoodConfig.of("farmersdelight:minced_beef:5:3"),
            FoodConfig.of("farmersdelight:mutton_chops:5:3"),
            FoodConfig.of("abnormals_delight:duck_fillet:2:3"),
            FoodConfig.of("abnormals_delight:venison_shanks:7:3"),
            FoodConfig.of("autumnity:foul_berries:2:4"),
            FoodConfig.of("autumnity:turkey:7:8"),
            FoodConfig.of("autumnity:turkey_piece:2:4"),
            FoodConfig.of("autumnity:foul_soup:12:8"),
            FoodConfig.of("endergetic:bolloom_fruit:3:4"),
            FoodConfig.of("quark:frog_leg:4:5"),
            FoodConfig.of("nethers_delight:hoglin_loin:8:6"),
            FoodConfig.of("nethers_delight:raw_stuffed_hoglin:18:10"),
            FoodConfig.of("xreliquary:zombie_heart:4:7"),
            FoodConfig.of("xreliquary:bat_wing:2:2"),
            FoodConfig.of("eidolon:zombie_heart:7:7"),
            FoodConfig.of("forbidden_arcanus:bat_wing:5:2"),
            FoodConfig.of("twilightforest:raw_venison:7:7"),
            FoodConfig.of("twilightforest:raw_meef:9:5"),
            FoodConfig.of("twilightforest:hydra_chop"),
            FoodConfig.of("cyclic:chorus_flight"),
            FoodConfig.of("cyclic:chorus_spectral"),
            FoodConfig.of("cyclic:toxic_carrot:15:15"),
            FoodConfig.of("artifacts:everlasting_beef"),
            FoodConfig.of("byg:soul_shroom:9:5"),
            FoodConfig.of("byg:death_cap:9:8"),
            FoodConfig.of("minecolonies:chorus_bread"),
            FoodConfig.of("wyrmroost:raw_lowtier_meat:3:2"),
            FoodConfig.of("wyrmroost:raw_common_meat:5:3"),
            FoodConfig.of("wyrmroost:raw_apex_meat:8:6"),
            FoodConfig.of("wyrmroost:raw_behemoth_meat:11:12"),
            FoodConfig.of("wyrmroost:desert_wyrm:4:3"),
            FoodConfig.of("eanimod:rawchicken_darkbig:9:5"),
            FoodConfig.of("eanimod:rawchicken_dark:5:4"),
            FoodConfig.of("eanimod:rawchicken_darksmall:3:2"),
            FoodConfig.of("eanimod:rawchicken_pale:5:3"),
            FoodConfig.of("eanimod:rawchicken_palesmall:4:3"),
            FoodConfig.of("eanimod:rawrabbit_small:4:4"),
            FoodConfig.of("environmental:duck:4:3"),
            FoodConfig.of("environmental:venison:7:7"),
            FoodConfig.of("cnb:lizard_item_jungle:4:4"),
            FoodConfig.of("cnb:lizard_item_mushroom:4:4"),
            FoodConfig.of("cnb:lizard_item_jungle_2:4:4"),
            FoodConfig.of("cnb:lizard_item_desert_2:4:4"),
            FoodConfig.of("cnb:lizard_egg:5:2"),
            FoodConfig.of("cnb:lizard_item_desert:4:4"),
            FoodConfig.of("snowpig:frozen_porkchop:7:3"),
            FoodConfig.of("snowpig:frozen_ham:5:7"),
            FoodConfig.of("naturalist:venison:7:6"),
            FoodConfig.of("leescreatures:raw_boarlin:6:6"),
            FoodConfig.of("mysticalworld:venison:5:5"),
            FoodConfig.of("toadterror:toad_chops:8:7"),
            FoodConfig.of("prehistoricfauna:raw_large_thyreophoran_meat:7:6"),
            FoodConfig.of("prehistoricfauna:raw_large_marginocephalian_meat:8:6"),
            FoodConfig.of("prehistoricfauna:raw_small_ornithischian_meat:4:3"),
            FoodConfig.of("prehistoricfauna:raw_large_sauropod_meat:11:9"),
            FoodConfig.of("prehistoricfauna:raw_small_sauropod_meat:4:4"),
            FoodConfig.of("prehistoricfauna:raw_large_theropod_meat:7:7"),
            FoodConfig.of("prehistoricfauna:raw_small_theropod_meat:4:4"),
            FoodConfig.of("prehistoricfauna:raw_small_archosauromorph_meat:3:3"),
            FoodConfig.of("prehistoricfauna:raw_large_archosauromorph_meat:6:5"),
            FoodConfig.of("prehistoricfauna:raw_small_reptile_meat:4:3"),
            FoodConfig.of("prehistoricfauna:raw_large_synapsid_meat:5:6"),
            FoodConfig.of("ends_delight:dragon_leg:15:15"),
            FoodConfig.of("ends_delight:raw_dragon_meat:10:10"),
            FoodConfig.of("ends_delight:raw_dragon_meat_cuts:5:2"),
            FoodConfig.of("ends_delight:dragon_breath_and_chorus_soup:15:15"),
            FoodConfig.of("ends_delight:ender_sauce:8:15"),
            FoodConfig.of("ends_delight:raw_ender_mite_meat:1:1"),
            FoodConfig.of("ends_delight:non_hatchable_dragon_egg:8:5"),
            FoodConfig.of("ends_delight:shulker_meat:7:4"),
            FoodConfig.of("unusualend:chorus_juice:2:2"),
            FoodConfig.of("ends_delight:liquid_dragon_egg:3:3"),
            FoodConfig.of("ends_delight:shulker_meat_slice:4:1"),
            FoodConfig.of("unusualend:ender_firefly_egg:2:3"),
            FoodConfig.of("unusualend:chorus_petal:1:1"),
            FoodConfig.of("unusualend:chorus_pie:3:4"),
            FoodConfig.of("unusualend:ender_stew:6:1"),
            FoodConfig.of("unusualprehistory:kentro_eggs:3:3"),
            FoodConfig.of("unusualprehistory:hwacha_eggs:3:3"),
            FoodConfig.of("unusualprehistory:ulugh_eggs:3:3"),
            FoodConfig.of("unusualprehistory:antarcto_eggs:3:3"),
            FoodConfig.of("unusualprehistory:austro_eggs:3:3"),
            FoodConfig.of("unusualprehistory:pachy_eggs:3:3"),
            FoodConfig.of("unusualprehistory:raptor_eggs:3:3"),
            FoodConfig.of("unusualprehistory:trike_eggs:4:4"),
            FoodConfig.of("unusualprehistory:rex_eggs:6:3"),
            FoodConfig.of("unusualprehistory:coty_eggs:3:3"),
            FoodConfig.of("unusualprehistory:meaty_buffet:12:15"),
            FoodConfig.of("unusualprehistory:majunga_eggs:3:3"),
            FoodConfig.of("unusualprehistory:anuro_eggs:3:3"),
            FoodConfig.of("unusualprehistory:raw_austro:6:3"),
            FoodConfig.of("unusualprehistory:raw_coty:5:3"),
            FoodConfig.of("nourished_nether:raw_hoglin:8:8"),
            FoodConfig.of("gothic:meat:7:8"),
            FoodConfig.of("gothic:bug_meat:2:2"),
            FoodConfig.of("gothic:scavenger_egg:3:3"),
            FoodConfig.of("gothic:snapperweed:1:1"),
            FoodConfig.of("gothic:blue_elder:1:1"),
            FoodConfig.of("unusualend:wandering_stew:1:1"),
            FoodConfig.of("nethersdelight:raw_stuffed_hoglin:15:15"),
            FoodConfig.of("nethersdelight:hoglin_ear:1:1"),
            FoodConfig.of("nethersdelight:ground_strider:6:2"),
            FoodConfig.of("nethersdelight:strider_slice:8:8"),
            FoodConfig.of("nethersdelight:hoglin_ear:1:1"),
            FoodConfig.of("nethersdelight:hoglin_loin:6:6"),
            FoodConfig.of("nethersdelight:propelpearl:1:1"),
            FoodConfig.of("orcz:squig_eye:6:6"),
            FoodConfig.of("orcz:orceye:4:4"),
            FoodConfig.of("goated:chevon:6:6"),
            FoodConfig.of("rottencreatures:frozen_rotten_flesh:4:4"),
            FoodConfig.of("regions_unexplored:tall_green_bioshroom:3:3"),
            FoodConfig.of("regions_unexplored:green_bioshroom:3:3"),
            FoodConfig.of("regions_unexplored:blue_bioshroom:3:3"),
            FoodConfig.of("regions_unexplored:tall_blue_bioshroom:3:3"),
            FoodConfig.of("regions_unexplored:tall_pink_bioshroom:3:3"),
            FoodConfig.of("regions_unexplored:pink_bioshroom:3:3"),
            FoodConfig.of("regions_unexplored:yellow_bioshroom:3:3"),
            FoodConfig.of("regions_unexplored:tall_yellow_bioshroom:3:3"),
            FoodConfig.of("quark:glow_shroom:3:3"),
            FoodConfig.of("gothic:cave_mushrooms:3:3"),
            FoodConfig.of("gothic:tall_mushrooms:3:3"),
            FoodConfig.of("gothic:miners_food_wild:3:3"),
            FoodConfig.of("gothic:black_mushroom:3:3"),
            FoodConfig.of("phantasm:putac_shroom:3:3"),
            FoodConfig.of("farmersdelight:red_mushroom_colony:10:3"),
            FoodConfig.of("farmersdelight:brown_mushroom_colony:10:3"),
            FoodConfig.of("gothic:miners_food:3:3"),
            FoodConfig.of("gothic:black_mushroom_item:3:3"),
            FoodConfig.of("gothic:blue_elder:3:3"),
            FoodConfig.of("frozen_delight:truffle_slice:4:8"),
            FoodConfig.of("frozenup:truffle:8:4"),
            FoodConfig.of("frozen_delight:mushroom_mix:16:3"),
            FoodConfig.of("nourished_nether:nether_fungus_stew:7:3"),
            FoodConfig.of("nethersdelight:warped_fungus_colony:10:3"),
            FoodConfig.of("nethersdelight:crimson_fungus_colony:10:3"),
            FoodConfig.of("unusualend:chorus_fungus:3:3"),
            FoodConfig.of("orcz:orcshroom:3:3"),
            FoodConfig.of("orcz:blue_orcshroom:3:3"),
            FoodConfig.of("waterstrainer:worm:1:1"),
            FoodConfig.of("undergarden:veil_mushroom:1:1"),
            FoodConfig.of("undergarden:indigo_mushroom:1:1"),
            FoodConfig.of("undergarden:smogstem_sapling:1:1"),
            FoodConfig.of("undergarden:blood_mushroom:1:1"),
            FoodConfig.of("undergarden:ink_mushroom:1:1"),
            FoodConfig.of("undergarden:blisterberry:1:1"),
            FoodConfig.of("undergarden:rotten_blisterberry:1:1"),
            FoodConfig.of("undergarden:ink_mushroom:1:1"),
            FoodConfig.of("undergarden:bloody_stew:1:1"),
            FoodConfig.of("undergarden:inky_stew:1:1"),
            FoodConfig.of("undergarden:veiled_stew:1:1"),
            FoodConfig.of("undergarden:indigo_stew:1:1"),
            FoodConfig.of("butchersdelight:deadchiken:6:4"),
            FoodConfig.of("butchersdelight:deadstrider:6:4"),
            FoodConfig.of("butchersdelight:deadrabbitbrown:6:4"),
            FoodConfig.of("butchersdelight:deadllama:6:4"),
            FoodConfig.of("butchersdelight:deadhoglin:6:4"),
            FoodConfig.of("butchersdelight:deadgoat:6:4"),
            FoodConfig.of("butchersdelight:deadsheep:6:4"),
            FoodConfig.of("butchersdelight:deadpig:6:4"),
            FoodConfig.of("butchersdelight:dead_cow:6:4"),
            FoodConfig.of("butchersdelightfoods:llama_loin:6:4"),
            FoodConfig.of("butchersdelightfoods:llamma_ribs:6:4"),
            FoodConfig.of("butchersdelightfoods:llama_leg:6:4"),
            FoodConfig.of("butchersdelightfoods:goat_loin:6:4"),
            FoodConfig.of("butchersdelightfoods:goatrack:6:4"),
            FoodConfig.of("butchersdelightfoods:goat_shank:6:4"),
            FoodConfig.of("butchersdelightfoods:porkribs:6:4"),
            FoodConfig.of("butchersdelightfoods:porkloin:6:4"),
            FoodConfig.of("butchersdelightfoods:ham:6:4"),
            FoodConfig.of("butchersdelightfoods:sheeploin:6:4"),
            FoodConfig.of("butchersdelightfoods:sheeprack:6:4"),
            FoodConfig.of("butchersdelightfoods:sheepshank:6:4"),
            FoodConfig.of("butchersdelightfoods:beeftenderloin:6:4"));

    @ConfigOption(side = ConfigSide.SERVER, category = {"food", "sea_dragon"}, key = "seaDragonFoods", comment = {"Dragon food formatting: namespace:path:nutrition:saturation (prefix namespace with # for tags)", "Nutrition / saturation values are optional - human values will be used if they are missing.", "Saturation can be defined with decimals (e.g. 0.3)"})
    public static List<FoodConfig> seaDragonFoods = Arrays.asList(
            // TODO :: check for old, clean stuff up
            FoodConfig.of("c:foods/raw_fish:6:4"),
            FoodConfig.of("minecraft:kelp:1:1"),
            FoodConfig.of("minecraft:pufferfish:8:8"),
            FoodConfig.of("dragonsurvival:sea_dragon_treat:4:8"),
            FoodConfig.of("dragonsurvival:seasoned_fish:12:10"),
            FoodConfig.of("dragonsurvival:golden_coral_pufferfish:12:14"),
            FoodConfig.of("dragonsurvival:frozen_raw_fish:2:1"),
            FoodConfig.of("dragonsurvival:golden_turtle_egg:15:12"),
            FoodConfig.of("additionaldragons:slippery_sushi:10:8"),
            FoodConfig.of("aoa3:raw_candlefish:9:9"),
            FoodConfig.of("aoa3:raw_crimson_skipper:8:8"),
            FoodConfig.of("aoa3:raw_fingerfish:4:4"),
            FoodConfig.of("aoa3:raw_pearl_stripefish:5:4"),
            FoodConfig.of("aoa3:raw_limefish:5:5"),
            FoodConfig.of("aoa3:raw_sailback:6:5"),
            FoodConfig.of("netherdepthsupgrade:soulsucker:6:7"),
            FoodConfig.of("netherdepthsupgrade:obsidianfish:6:7"),
            FoodConfig.of("netherdepthsupgrade:lava_pufferfish:8:7"),
            FoodConfig.of("netherdepthsupgrade:searing_cod:6:7"),
            FoodConfig.of("netherdepthsupgrade:glowdine:6:7"),
            FoodConfig.of("netherdepthsupgrade:warped_kelp:2:2"),
            FoodConfig.of("netherdepthsupgrade:lava_pufferfish_slice:2:2"),
            FoodConfig.of("netherdepthsupgrade:glowdine_slice:2:2"),
            FoodConfig.of("netherdepthsupgrade:soulsucker_slice:2:2"),
            FoodConfig.of("netherdepthsupgrade:obsidianfish_slice:2:2"),
            FoodConfig.of("netherdepthsupgrade:searing_cod_slice:2:2"),
            FoodConfig.of("crittersandcompanions:clam:10:3"),
            FoodConfig.of("aoa3:raw_golden_gullfish:10:2"),
            FoodConfig.of("aoa3:raw_turquoise_stripefish:7:6"),
            FoodConfig.of("aoa3:raw_violet_skipper:7:7"),
            FoodConfig.of("aoa3:raw_rocketfish:4:10"),
            FoodConfig.of("aoa3:raw_crimson_stripefish:8:7"),
            FoodConfig.of("aoa3:raw_sapphire_strider:9:8"),
            FoodConfig.of("aoa3:raw_dark_hatchetfish:9:9"),
            FoodConfig.of("aoa3:raw_ironback:10:9"),
            FoodConfig.of("aoa3:raw_rainbowfish:11:11"),
            FoodConfig.of("aoa3:raw_razorfish:12:14"),
            FoodConfig.of("alexsmobs:lobster_tail:4:5"),
            FoodConfig.of("alexsmobs:blobfish:8:9"),
            FoodConfig.of("oddwatermobs:raw_ghost_shark:8:8"),
            FoodConfig.of("oddwatermobs:raw_isopod:4:2"),
            FoodConfig.of("oddwatermobs:raw_mudskipper:6:7"),
            FoodConfig.of("oddwatermobs:raw_coelacanth:9:10"),
            FoodConfig.of("oddwatermobs:raw_anglerfish:6:6"),
            FoodConfig.of("oddwatermobs:deep_sea_fish:4:2"),
            FoodConfig.of("oddwatermobs:crab_leg:5:6"),
            FoodConfig.of("simplefarming:raw_calamari:5:6"),
            FoodConfig.of("unnamedanimalmod:elephantnose_fish:5:6"),
            FoodConfig.of("unnamedanimalmod:flashlight_fish:5:6"),
            FoodConfig.of("born_in_chaos_v1:sea_terror_eye:10:4"),
            FoodConfig.of("born_in_chaos_v1:rotten_fish:4:2"),
            FoodConfig.of("unnamedanimalmod:rocket_killifish:5:6"),
            FoodConfig.of("unnamedanimalmod:leafy_seadragon:5:6"),
            FoodConfig.of("unnamedanimalmod:elephantnose_fish:5:6"),
            FoodConfig.of("betteranimalsplus:eel_meat_raw:5:6"),
            FoodConfig.of("betteranimalsplus:calamari_raw:4:5"),
            FoodConfig.of("betteranimalsplus:crab_meat_raw:4:4"),
            FoodConfig.of("aquaculture:fish_fillet_raw:2:2"),
            FoodConfig.of("aquaculture:goldfish:8:4"),
            FoodConfig.of("aquaculture:algae:3:2"),
            FoodConfig.of("betterendforge:end_fish_raw:6:7"),
            FoodConfig.of("betterendforge:hydralux_petal:3:3"),
            FoodConfig.of("betterendforge:charnia_green:2:2"),
            FoodConfig.of("shroomed:raw_shroomfin:5:6"),
            FoodConfig.of("undergarden:raw_gwibling:5:6"),
            FoodConfig.of("bettas:betta_fish:4:5"),
            FoodConfig.of("quark:crab_leg:4:4"),
            FoodConfig.of("pamhc2foodextended:rawtofishitem"),
            FoodConfig.of("fins:banded_redback_shrimp:6:1"),
            FoodConfig.of("fins:night_light_squid:6:2"),
            FoodConfig.of("fins:night_light_squid_tentacle:6:2"),
            FoodConfig.of("fins:emerald_spindly_gem_crab:7:2"),
            FoodConfig.of("fins:amber_spindly_gem_crab:7:2"),
            FoodConfig.of("fins:rubby_spindly_gem_crab:7:2"),
            FoodConfig.of("fins:sapphire_spindly_gem_crab:7:2"),
            FoodConfig.of("fins:pearl_spindly_gem_crab:7:2"),
            FoodConfig.of("fins:papa_wee:6:2"),
            FoodConfig.of("fins:bugmeat:4:2"),
            FoodConfig.of("fins:raw_golden_river_ray_wing:6:2"),
            FoodConfig.of("fins:red_bull_crab_claw:4:4"),
            FoodConfig.of("fins:white_bull_crab_claw:4:4"),
            FoodConfig.of("fins:wherble_fin:1:1"),
            FoodConfig.of("forbidden_arcanus:tentacle:5:2"),
            FoodConfig.of("pneumaticcraft:raw_salmon_tempura:6:10"),
            FoodConfig.of("rats:ratfish:4:2"),
            FoodConfig.of("upgrade_aquatic:purple_pickerelweed:2:2"),
            FoodConfig.of("upgrade_aquatic:blue_pickerelweed:2:2"),
            FoodConfig.of("upgrade_aquatic:polar_kelp:2:2"),
            FoodConfig.of("upgrade_aquatic:tongue_kelp:2:2"),
            FoodConfig.of("upgrade_aquatic:thorny_kelp:2:2"),
            FoodConfig.of("upgrade_aquatic:ochre_kelp:2:2"),
            FoodConfig.of("upgrade_aquatic:lionfish:8:9"),
            FoodConfig.of("aquaculture:sushi:6:5"),
            FoodConfig.of("freshwarriors:fresh_soup:15:10"),
            FoodConfig.of("freshwarriors:beluga_caviar:10:3"),
            FoodConfig.of("freshwarriors:piranha:4:1"),
            FoodConfig.of("freshwarriors:tilapia:4:1"),
            FoodConfig.of("freshwarriors:stuffed_piranha:4:1"),
            FoodConfig.of("freshwarriors:tigerfish:5:5"),
            FoodConfig.of("freshwarriors:toe_biter_leg:3:3"),
            FoodConfig.of("mysticalworld:raw_squid:6:5"),
            FoodConfig.of("aquafina:fresh_soup:10:10"),
            FoodConfig.of("aquafina:beluga_caviar:10:3"),
            FoodConfig.of("aquafina:raw_piranha:4:1"),
            FoodConfig.of("aquafina:raw_tilapia:4:1"),
            FoodConfig.of("aquafina:stuffed_piranha:4:1"),
            FoodConfig.of("aquafina:tigerfish:5:5"),
            FoodConfig.of("aquafina:toe_biter_leg:3:3"),
            FoodConfig.of("aquafina:raw_angelfish:4:1"),
            FoodConfig.of("aquafina:raw_football_fish:4:1"),
            FoodConfig.of("aquafina:raw_foxface_fish:4:1"),
            FoodConfig.of("aquafina:raw_royal_gramma:4:1"),
            FoodConfig.of("aquafina:raw_starfish:4:1"),
            FoodConfig.of("aquafina:spider_crab_leg:4:1"),
            FoodConfig.of("aquafina:raw_stingray_slice:4:1"),
            FoodConfig.of("prehistoricfauna:raw_ceratodus:5:5"),
            FoodConfig.of("prehistoricfauna:raw_cyclurus:4:4"),
            FoodConfig.of("prehistoricfauna:raw_potamoceratodus:5:5"),
            FoodConfig.of("prehistoricfauna:raw_myledaphus:4:4"),
            FoodConfig.of("prehistoricfauna:raw_gar:4:4"),
            FoodConfig.of("prehistoricfauna:raw_oyster:4:3"),
            FoodConfig.of("prehistoric_delight:prehistoric_fillet:3:3"),
            FoodConfig.of("seadwellers:rainbow_trout:10:10"),
            FoodConfig.of("crittersandcompanions:koi_fish:5:5"),
            FoodConfig.of("aquamirae:elodea:3:3"),
            FoodConfig.of("croptopia:clam:3:3"),
            FoodConfig.of("croptopia:calamari:2:3"),
            FoodConfig.of("croptopia:anchovy:3:2"),
            FoodConfig.of("croptopia:crab:6:8"),
            FoodConfig.of("croptopia:glowing_calamari:4:5"),
            FoodConfig.of("croptopia:oyster:2:4"),
            FoodConfig.of("croptopia:roe:1:2"),
            FoodConfig.of("croptopia:shrimp:2:2"),
            FoodConfig.of("croptopia:tuna:6:4"),
            FoodConfig.of("aquamirae:spinefish:4:4"),
            FoodConfig.of("alexsmobs:flying_fish:6:4"),
            FoodConfig.of("netherdepthsupgrade:eyeball:3:3"),
            FoodConfig.of("netherdepthsupgrade:eyeball_fish:3:3"),
            FoodConfig.of("oceansdelight:guardian:4:3"),
            FoodConfig.of("oceansdelight:guardian_tail:1:3"),
            FoodConfig.of("oceansdelight:cut_tentacles:3:1"),
            FoodConfig.of("oceansdelight:tentacles:3:4"),
            FoodConfig.of("oceansdelight:tentacle_on_a_stick:3:4"),
            FoodConfig.of("oceansdelight:fugu_slice:5:4"),
            FoodConfig.of("oceansdelight:elder_guardian_slice:8:6"),
            FoodConfig.of("oceansdelight:elder_guardian_slab:15:15"),
            FoodConfig.of("upgrade_aquatic:elder_eye:15:15"),
            FoodConfig.of("unusualprehistory:golden_scau:15:15"),
            FoodConfig.of("unusualprehistory:raw_scau:4:3"),
            FoodConfig.of("unusualprehistory:raw_stetha:4:3"),
            FoodConfig.of("unusualprehistory:stetha_eggs:4:3"),
            FoodConfig.of("unusualprehistory:beelze_eggs:4:3"),
            FoodConfig.of("unusualprehistory:scau_eggs:4:3"),
            FoodConfig.of("unusualprehistory:ammon_eggs:4:3"),
            FoodConfig.of("unusualprehistory:dunk_eggs:4:3"),
            FoodConfig.of("netherdepthsupgrade:crimson_seagrass:2:2"),
            FoodConfig.of("netherdepthsupgrade:crimson_kelp:2:2"),
            FoodConfig.of("netherdepthsupgrade:warped_seagrass:2:2"),
            FoodConfig.of("undergarden:glitterkelp:2:2"),
            FoodConfig.of("enlightened_end:raw_stalker:10:4")
    );

    // Tooltip maps
    public static CopyOnWriteArrayList<Item> CAVE_DRAGON_FOOD;
    public static CopyOnWriteArrayList<Item> FOREST_DRAGON_FOOD;
    public static CopyOnWriteArrayList<Item> SEA_DRAGON_FOOD;
    public static int rightHeight = 0;

    private static ConcurrentHashMap<String, Map<Item, FoodProperties>> DRAGON_FOODS = new ConcurrentHashMap<>();

    public static void rebuildFoodMap() {
        DragonSurvivalMod.LOGGER.debug("Rebuilding food map...");
        DRAGON_FOODS = buildDragonFoodMap();

        // No need to keep them in-memory (they are rebuilt in 'ConfigHandler#handleConfigChange')
        // Currently this method is only called on the server thread meaning there should be no issues
        caveDragonFoods.clear();
        seaDragonFoods.clear();
        forestDragonFoods.clear();

        clearTooltipMaps();
    }

    public static void clearTooltipMaps() {
        if (CAVE_DRAGON_FOOD != null) {
            CAVE_DRAGON_FOOD.clear();
        }

        if (FOREST_DRAGON_FOOD != null) {
            FOREST_DRAGON_FOOD.clear();
        }

        if (SEA_DRAGON_FOOD != null) {
            SEA_DRAGON_FOOD.clear();
        }
    }

    private static ConcurrentHashMap<String, Map<Item, FoodProperties>> buildDragonFoodMap() {
        ConcurrentHashMap<String, Map<Item, FoodProperties>> map = new ConcurrentHashMap<>();

        // TODO :: change predicate to function (registry as input and food config searches based on item or tag)?
        for (Map.Entry<ResourceKey<Item>, Item> registryEntry : BuiltInRegistries.ITEM.entrySet()) {
            Item item = registryEntry.getValue();

            if (item == Items.AIR) {
                continue;
            }

            createEntry(caveDragonFoods, item, DragonTypes.CAVE)
                    .ifPresent(entry -> map.computeIfAbsent(DragonTypes.CAVE.getTypeName(), key -> new ConcurrentHashMap<>())
                            .put(entry.first(), entry.second()));

            createEntry(seaDragonFoods, item, DragonTypes.SEA)
                    .ifPresent(entry -> map.computeIfAbsent(DragonTypes.SEA.getTypeName(), key -> new ConcurrentHashMap<>())
                            .put(entry.first(), entry.second()));

            createEntry(forestDragonFoods, item, DragonTypes.FOREST)
                    .ifPresent(entry -> map.computeIfAbsent(DragonTypes.FOREST.getTypeName(), key -> new ConcurrentHashMap<>())
                            .put(entry.first(), entry.second()));
        }

        return map;
    }

    private static Optional<Pair<Item, FoodProperties>> createEntry(final List<FoodConfig> foodConfigs, final Item item, final AbstractDragonType type) {
        FoodProperties properties = null;

        for (FoodConfig config : foodConfigs) {
            Optional<FoodProperties> optional = config.getFoodData(item);

            if (optional.isPresent()) {
                FoodProperties newProperties = calculate(item, optional.get());

                // Select the configuration which grants more nutrition / saturation (in case an item is present in different tags etc.)
                if (properties == null || newProperties.nutrition() > properties.nutrition() || newProperties.nutrition() == properties.nutrition() && newProperties.saturation() > properties.saturation()) {
                    properties = newProperties;
                }
            }
        }

        if (properties == null) {
            return Optional.empty();
        }

        return Optional.of(Pair.of(item, properties));
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

    public static CopyOnWriteArrayList<Item> getEdibleFoods(final AbstractDragonType type) {
        if (type == null) {
            return new CopyOnWriteArrayList<>();
        }

        if (DragonUtils.isDragonType(type, DragonTypes.FOREST) && FOREST_DRAGON_FOOD != null && !FOREST_DRAGON_FOOD.isEmpty()) {
            return FOREST_DRAGON_FOOD;
        } else if (DragonUtils.isDragonType(type, DragonTypes.SEA) && SEA_DRAGON_FOOD != null && !SEA_DRAGON_FOOD.isEmpty()) {
            return SEA_DRAGON_FOOD;
        } else if (DragonUtils.isDragonType(type, DragonTypes.CAVE) && CAVE_DRAGON_FOOD != null && !CAVE_DRAGON_FOOD.isEmpty()) {
            return CAVE_DRAGON_FOOD;
        }

        CopyOnWriteArrayList<Item> foods = new CopyOnWriteArrayList<>();

        for (Item item : DRAGON_FOODS.get(type.getTypeName()).keySet()) {
            final FoodProperties foodProperties = DRAGON_FOODS.get(type.getTypeName()).get(item);
            boolean isSafe = true;

            if (foodProperties != null) {
                for (FoodProperties.PossibleEffect possibleEffect : foodProperties.effects()) {
                    Supplier<MobEffectInstance> mobEffect = possibleEffect.effectSupplier();
                    // Because we decided to leave confusion on pufferfish
                    if (ToolTipHandler.hideUnsafeFood && !mobEffect.get().getEffect().value().isBeneficial() && mobEffect.get().getEffect() != MobEffects.CONFUSION) {
                        isSafe = false;
                        break;
                    }
                }

                if (isSafe && (foodProperties.nutrition() > 0 || foodProperties.saturation() > 0)) {
                    foods.add(item);
                }
            }
        }

        if (DragonUtils.isDragonType(type, DragonTypes.FOREST) && FOREST_DRAGON_FOOD == null) {
            FOREST_DRAGON_FOOD = foods;
        } else if (DragonUtils.isDragonType(type, DragonTypes.CAVE) && CAVE_DRAGON_FOOD == null) {
            CAVE_DRAGON_FOOD = foods;
        } else if (DragonUtils.isDragonType(type, DragonTypes.SEA) && SEA_DRAGON_FOOD == null) {
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
        if (requireDragonFood && type != null && DRAGON_FOODS != null && DRAGON_FOODS.get(type.getTypeName()).containsKey(stack.getItem())) {
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