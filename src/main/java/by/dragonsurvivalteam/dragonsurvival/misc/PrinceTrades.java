package by.dragonsurvivalteam.dragonsurvival.misc;

import by.dragonsurvivalteam.dragonsurvival.common.items.DSItems;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.Util;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;

import java.util.Map;

public class PrinceTrades{
	private static final ItemForItemTrade GOLD_NUGGETS = new ItemForItemTrade(Items.IRON_SWORD, 1, Items.GOLD_NUGGET, 3, 10, 10);
	private static final ItemForItemTrade RED_FLOWER = new ItemForItemTrade(Items.RED_TULIP, 32, Items.EXPERIENCE_BOTTLE, 1, 1, 20);
	private static final ItemForItemTrade YELLOW_FLOWER = new ItemForItemTrade(Items.DANDELION, 32, Items.EXPERIENCE_BOTTLE, 1, 1, 20);
	private static final ItemForItemTrade BLACK_FLOWER = new ItemForItemTrade(Items.WITHER_ROSE, 5, Items.EXPERIENCE_BOTTLE, 1, 5, 26);
	private static final ItemForItemTrade BLUE_FLOWER = new ItemForItemTrade(Items.BLUE_ORCHID, 32, Items.EXPERIENCE_BOTTLE, 1, 1, 20);
	private static final ItemForItemTrade PURPLE_FLOWER = new ItemForItemTrade(Items.LILAC, 32, Items.EXPERIENCE_BOTTLE, 1, 1, 20);
	private static final ItemForItemTrade WHITE_FLOWER = new ItemForItemTrade(Items.OXEYE_DAISY, 32, Items.EXPERIENCE_BOTTLE, 1, 1, 20);
	private static final ItemFor2ItemsTrade ELDER_DRAGON_DUST = new ItemFor2ItemsTrade(Items.GOLD_INGOT, 5, Items.EMERALD, 5, DSItems.elderDragonDust, 1, 12, 8);
	public static Map<Integer, Int2ObjectMap<ItemListing[]>> colorToTrades;
	static{
		colorToTrades = Util.make(Maps.newHashMap(), integerInt2ObjectMapMap -> {
			integerInt2ObjectMapMap.put(DyeColor.RED.getId(), ItemForItemTrade.toIntMap(ImmutableMap.of(1, new ItemListing[]{GOLD_NUGGETS}, 2, new ItemListing[]{RED_FLOWER}, 3, new ItemListing[]{ELDER_DRAGON_DUST})));
			integerInt2ObjectMapMap.put(DyeColor.YELLOW.getId(), ItemForItemTrade.toIntMap(ImmutableMap.of(1, new ItemListing[]{GOLD_NUGGETS}, 2, new ItemListing[]{YELLOW_FLOWER}, 3, new ItemListing[]{ELDER_DRAGON_DUST})));
			integerInt2ObjectMapMap.put(DyeColor.BLACK.getId(), ItemForItemTrade.toIntMap(ImmutableMap.of(1, new ItemListing[]{GOLD_NUGGETS}, 2, new ItemListing[]{BLACK_FLOWER}, 3, new ItemListing[]{ELDER_DRAGON_DUST})));
			integerInt2ObjectMapMap.put(DyeColor.BLUE.getId(), ItemForItemTrade.toIntMap(ImmutableMap.of(1, new ItemListing[]{GOLD_NUGGETS}, 2, new ItemListing[]{BLUE_FLOWER}, 3, new ItemListing[]{ELDER_DRAGON_DUST})));
			integerInt2ObjectMapMap.put(DyeColor.PURPLE.getId(), ItemForItemTrade.toIntMap(ImmutableMap.of(1, new ItemListing[]{GOLD_NUGGETS}, 2, new ItemListing[]{PURPLE_FLOWER}, 3, new ItemListing[]{ELDER_DRAGON_DUST})));
			integerInt2ObjectMapMap.put(DyeColor.WHITE.getId(), ItemForItemTrade.toIntMap(ImmutableMap.of(1, new ItemListing[]{GOLD_NUGGETS}, 2, new ItemListing[]{WHITE_FLOWER}, 3, new ItemListing[]{ELDER_DRAGON_DUST})));
		});
	}
}