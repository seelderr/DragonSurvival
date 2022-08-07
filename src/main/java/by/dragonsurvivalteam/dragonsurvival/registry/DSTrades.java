package by.dragonsurvivalteam.dragonsurvival.registry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

public class DSTrades {

	static final ItemTrade PRINCE_GOLD_NUGGETS = new ItemTrade(new ItemStack(Items.IRON_SWORD, 1), new ItemStack(Items.GOLD_NUGGET, 3), 10, 10);
	static final ItemTrade PRINCE_RED_FLOWER = new ItemTrade(new ItemStack(Items.RED_TULIP, 32), new ItemStack(Items.EXPERIENCE_BOTTLE, 1), 1, 20);
	static final ItemTrade PRINCE_YELLOW_FLOWER = new ItemTrade(new ItemStack(Items.DANDELION, 32), new ItemStack(Items.EXPERIENCE_BOTTLE, 1), 1, 20);
	static final ItemTrade PRINCE_BLACK_FLOWER = new ItemTrade(new ItemStack(Items.WITHER_ROSE, 5), new ItemStack(Items.EXPERIENCE_BOTTLE, 1), 5, 26);
	static final ItemTrade PRINCE_BLUE_FLOWER = new ItemTrade(new ItemStack(Items.BLUE_ORCHID, 32), new ItemStack(Items.EXPERIENCE_BOTTLE, 1), 1, 20);
	static final ItemTrade PRINCE_PURPLE_FLOWER = new ItemTrade(new ItemStack(Items.LILAC, 32), new ItemStack(Items.EXPERIENCE_BOTTLE, 1), 1, 20);
	static final ItemTrade PRINCE_WHITE_FLOWER = new ItemTrade(new ItemStack(Items.OXEYE_DAISY, 32), new ItemStack(Items.EXPERIENCE_BOTTLE, 1), 1, 20);
	static final ItemTrade PRINCE_ELDER_DRAGON_DUST = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 5), new ItemStack(Items.EMERALD, 5), new ItemStack(DSItems.elderDragonDust, 1), 12, 8);

	public static Map<Integer, Int2ObjectMap<VillagerTrades.ItemListing[]>> princeTrades = Util.make(Maps.newHashMap(), integerInt2ObjectMapMap -> {
		integerInt2ObjectMapMap.put(DyeColor.RED.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_GOLD_NUGGETS}, 2, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_RED_FLOWER}, 3, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_ELDER_DRAGON_DUST})));
		integerInt2ObjectMapMap.put(DyeColor.YELLOW.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_GOLD_NUGGETS}, 2, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_YELLOW_FLOWER}, 3, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_ELDER_DRAGON_DUST})));
		integerInt2ObjectMapMap.put(DyeColor.BLACK.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_GOLD_NUGGETS}, 2, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_BLACK_FLOWER}, 3, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_ELDER_DRAGON_DUST})));
		integerInt2ObjectMapMap.put(DyeColor.BLUE.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_GOLD_NUGGETS}, 2, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_BLUE_FLOWER}, 3, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_ELDER_DRAGON_DUST})));
		integerInt2ObjectMapMap.put(DyeColor.PURPLE.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_GOLD_NUGGETS}, 2, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_PURPLE_FLOWER}, 3, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_ELDER_DRAGON_DUST})));
		integerInt2ObjectMapMap.put(DyeColor.WHITE.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_GOLD_NUGGETS}, 2, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_WHITE_FLOWER}, 3, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_ELDER_DRAGON_DUST})));
	});

	public static ItemTrade PRINCESS_RED1 = new ItemTrade(new ItemStack(Items.POPPY, 5), new ItemStack(Items.GOLD_NUGGET, 1), 16, 6);
	public static ItemTrade PRINCESS_RED2 = new ItemTrade(new ItemStack(Items.RED_TULIP, 5), new ItemStack(Items.GOLD_NUGGET, 1), 16, 6);
	public static ItemTrade PRINCESS_RED3 = new ItemTrade(new ItemStack(Items.ROSE_BUSH, 5), new ItemStack(Items.GOLD_NUGGET, 1), 16, 6);
	public static ItemTrade PRINCESS_YELLOW1 = new ItemTrade(new ItemStack(Items.DANDELION, 5), new ItemStack(Items.GOLD_NUGGET, 1), 16, 6);
	public static ItemTrade PRINCESS_YELLOW2 = new ItemTrade(new ItemStack(Items.SUNFLOWER, 5), new ItemStack(Items.GOLD_NUGGET, 1), 16, 6);
	public static ItemTrade PRINCESS_YELLOW3 = new ItemTrade(new ItemStack(Items.ORANGE_TULIP, 5), new ItemStack(Items.GOLD_NUGGET, 1), 16, 6);
	public static ItemTrade PRINCESS_PURPLE1 = new ItemTrade(new ItemStack(Items.LILAC, 5), new ItemStack(Items.GOLD_NUGGET, 1), 16, 6);
	public static ItemTrade PRINCESS_PURPLE2 = new ItemTrade(new ItemStack(Items.ALLIUM, 5), new ItemStack(Items.GOLD_NUGGET, 1), 16, 6);
	public static ItemTrade PRINCESS_PURPLE3 = new ItemTrade(new ItemStack(Items.PEONY, 5), new ItemStack(Items.GOLD_NUGGET, 1), 16, 6);
	public static ItemTrade PRINCESS_BLUE1 = new ItemTrade(new ItemStack(Items.BLUE_ORCHID, 5), new ItemStack(Items.GOLD_NUGGET, 1), 16, 6);
	public static ItemTrade PRINCESS_BLUE2 = new ItemTrade(new ItemStack(Items.CORNFLOWER, 5), new ItemStack(Items.GOLD_NUGGET, 1), 16, 6);
	public static ItemTrade PRINCESS_WHITE1 = new ItemTrade(new ItemStack(Items.OXEYE_DAISY, 5), new ItemStack(Items.GOLD_NUGGET, 1), 16, 6);
	public static ItemTrade PRINCESS_WHITE2 = new ItemTrade(new ItemStack(Items.AZURE_BLUET, 5), new ItemStack(Items.GOLD_NUGGET, 1), 16, 6);
	public static ItemTrade PRINCESS_WHITE3 = new ItemTrade(new ItemStack(Items.LILY_OF_THE_VALLEY, 5), new ItemStack(Items.GOLD_NUGGET, 1), 16, 6);
	public static ItemTrade PRINCESS_WHITE4 = new ItemTrade(new ItemStack(Items.WHITE_TULIP, 5), new ItemStack(Items.GOLD_NUGGET, 1), 16, 6);
	public static ItemTrade PRINCESS_BLACK = new ItemTrade(new ItemStack(Items.WITHER_ROSE, 1), new ItemStack(Items.GOLD_NUGGET, 5), 10, 15);
	public static ItemTrade PRINCESS_XP_BOTTLE1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 2), new ItemStack(DSItems.elderDragonDust), new ItemStack(Items.EXPERIENCE_BOTTLE), 12, 10);
	public static ItemTrade PRINCESS_ELDER_DRAGON_DUST = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 5), new ItemStack(Items.DIAMOND), new ItemStack(DSItems.elderDragonDust), 12, 15);

	public static Map<Integer, Int2ObjectMap<ItemListing[]>> princessColorCodes = Util.make(Maps.newHashMap(), objectObjectHashMap -> {
		objectObjectHashMap.put(DyeColor.RED.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new ItemListing[]{DSTrades.PRINCESS_RED1, DSTrades.PRINCESS_RED2, DSTrades.PRINCESS_RED3}, 2, new ItemListing[]{DSTrades.PRINCESS_XP_BOTTLE1}, 3, new ItemListing[]{DSTrades.PRINCESS_ELDER_DRAGON_DUST})));
		objectObjectHashMap.put(DyeColor.YELLOW.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new ItemListing[]{DSTrades.PRINCESS_YELLOW1, DSTrades.PRINCESS_YELLOW2, DSTrades.PRINCESS_YELLOW3}, 2, new ItemListing[]{DSTrades.PRINCESS_XP_BOTTLE1}, 3, new ItemListing[]{DSTrades.PRINCESS_ELDER_DRAGON_DUST})));
		objectObjectHashMap.put(DyeColor.PURPLE.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new ItemListing[]{DSTrades.PRINCESS_PURPLE1, DSTrades.PRINCESS_PURPLE2, DSTrades.PRINCESS_PURPLE3}, 2, new ItemListing[]{DSTrades.PRINCESS_XP_BOTTLE1}, 3, new ItemListing[]{DSTrades.PRINCESS_ELDER_DRAGON_DUST})));
		objectObjectHashMap.put(DyeColor.BLUE.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new ItemListing[]{DSTrades.PRINCESS_BLUE1, DSTrades.PRINCESS_BLUE2}, 2, new ItemListing[]{DSTrades.PRINCESS_XP_BOTTLE1}, 3, new ItemListing[]{DSTrades.PRINCESS_ELDER_DRAGON_DUST})));
		objectObjectHashMap.put(DyeColor.WHITE.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new ItemListing[]{DSTrades.PRINCESS_WHITE1, DSTrades.PRINCESS_WHITE2, DSTrades.PRINCESS_WHITE3, DSTrades.PRINCESS_WHITE4}, 2, new ItemListing[]{DSTrades.PRINCESS_XP_BOTTLE1}, 3, new ItemListing[]{DSTrades.PRINCESS_ELDER_DRAGON_DUST})));
		objectObjectHashMap.put(DyeColor.BLACK.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new ItemListing[]{DSTrades.PRINCESS_BLACK}, 2, new ItemListing[]{DSTrades.PRINCESS_XP_BOTTLE1}, 3, new ItemListing[]{DSTrades.PRINCESS_ELDER_DRAGON_DUST})));
	});



	public static class ItemTrade implements VillagerTrades.ItemListing{

		private ItemStack baseCostA = ItemStack.EMPTY;
		private ItemStack costB = ItemStack.EMPTY;
		private final ItemStack result;
		private final int maxUses;
		private float priceMultiplier = 0;
		private int xp = 1;

		public static Int2ObjectMap<ItemListing[]> toIntMap(ImmutableMap<Integer, ItemListing[]> p_221238_0_){
			return new Int2ObjectOpenHashMap<>(p_221238_0_);
		}

		public ItemTrade(ItemStack baseCostA, ItemStack costB, ItemStack result, int maxUses, float priceMultiplier, int xp){
			this.baseCostA = baseCostA;
			this.costB = costB;
			this.result = result;
			this.maxUses = maxUses;
			this.priceMultiplier = priceMultiplier;
			this.xp = xp;
		}

		public ItemTrade(ItemStack baseCostA, ItemStack costB, ItemStack result, int maxUses, int xp){
			this.baseCostA = baseCostA;
			this.costB = costB;
			this.result = result;
			this.maxUses = maxUses;
			this.xp = xp;
		}

		public ItemTrade(ItemStack baseCostA, ItemStack result, int maxUses, int xp){
			this.baseCostA = baseCostA;
			this.result = result;
			this.maxUses = maxUses;
			this.priceMultiplier = priceMultiplier;
			this.xp = xp;
		}

		public ItemTrade(ItemStack baseCostA, ItemStack result, int maxUses, float priceMultiplier, int xp){
			this.baseCostA = baseCostA;
			this.result = result;
			this.maxUses = maxUses;
			this.priceMultiplier = priceMultiplier;
			this.xp = xp;
		}

		@Nullable
		@Override
		public MerchantOffer getOffer(Entity entity, Random random){
			return new MerchantOffer(baseCostA, costB, result, maxUses, xp, priceMultiplier);
		}
	}
}