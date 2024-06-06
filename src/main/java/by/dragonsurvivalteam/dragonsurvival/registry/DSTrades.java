package by.dragonsurvivalteam.dragonsurvival.registry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;

public class DSTrades {

		static final ItemTrade PRINCE_RED_FLOWER = new ItemTrade(new ItemStack(Items.RED_TULIP, 32), new ItemStack(Items.EXPERIENCE_BOTTLE, 2), 3, 20);
		static final ItemTrade PRINCE_YELLOW_FLOWER = new ItemTrade(new ItemStack(Items.DANDELION, 32), new ItemStack(Items.EXPERIENCE_BOTTLE, 2), 3, 20);
		static final ItemTrade PRINCE_BLACK_FLOWER = new ItemTrade(new ItemStack(Items.WITHER_ROSE, 32), new ItemStack(Items.EXPERIENCE_BOTTLE, 2), 3, 20);
		static final ItemTrade PRINCE_BLUE_FLOWER = new ItemTrade(new ItemStack(Items.BLUE_ORCHID, 32), new ItemStack(Items.EXPERIENCE_BOTTLE, 2), 3, 20);
		static final ItemTrade PRINCE_PURPLE_FLOWER = new ItemTrade(new ItemStack(Items.LILAC, 32), new ItemStack(Items.EXPERIENCE_BOTTLE, 2), 3, 20);
		static final ItemTrade PRINCE_WHITE_FLOWER = new ItemTrade(new ItemStack(Items.OXEYE_DAISY, 32), new ItemStack(Items.EXPERIENCE_BOTTLE, 2), 3, 20);
		static final ItemTrade PRINCE3_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 4), new ItemStack(DSItems.ELDER_DRAGON_BONE, 1), 16, 5);
		static final ItemTrade PRINCE4_1 = new ItemTrade(new ItemStack(DSItems.ELDER_DRAGON_BONE, 4), new ItemStack(DSItems.STAR_BONE, 1), 32, 10);
		static final ItemTrade PRINCE5_1 = new ItemTrade(new ItemStack(DSItems.ELDER_DRAGON_HEART, 4), new ItemStack(DSItems.STAR_HEART, 1), 12, 20);
		static final ItemTrade PRINCE1_1 = new ItemTrade(new ItemStack(Items.NETHERITE_SWORD, 1), new ItemStack(Items.NETHERITE_SCRAP, 1), 12, 50);
		static final ItemTrade PRINCE1_2 = new ItemTrade(new ItemStack(Items.DIAMOND_SWORD, 1), new ItemStack(Items.DIAMOND, 1), 12, 30);
		static final ItemTrade PRINCE1_3 = new ItemTrade(new ItemStack(Items.IRON_SWORD, 1), new ItemStack(Items.IRON_NUGGET, 3), 12, 10);
		static final ItemTrade PRINCE1_4 = new ItemTrade(new ItemStack(Items.GOLDEN_SWORD, 1), new ItemStack(Items.GOLD_NUGGET, 3), 12, 5);
		static final ItemTrade PRINCE1_5 = new ItemTrade(new ItemStack(Items.STONE_SWORD, 1), new ItemStack(Items.COBBLESTONE, 1), 12, 2);
		static final ItemTrade PRINCE1_6 = new ItemTrade(new ItemStack(Items.WOODEN_SWORD, 1), new ItemStack(Items.STICK, 1), 12, 1);
		static final ItemTrade PRINCE1_7 = new ItemTrade(new ItemStack(Items.SHIELD, 1), new ItemStack(Items.IRON_NUGGET, 1), 12, 10);
		static final ItemTrade PRINCE1_8 = new ItemTrade(new ItemStack(Items.BOW, 1), new ItemStack(Items.STRING, 1), 12, 5);
		static final ItemTrade PRINCE1_9 = new ItemTrade(new ItemStack(Items.TRIDENT, 1), new ItemStack(Items.ENDER_PEARL, 1), 12, 20);
		static final ItemTrade PRINCE1_10 = new ItemTrade(new ItemStack(Items.CROSSBOW, 1), new ItemStack(Items.STRING, 1), 12, 5);
		static final ItemTrade PRINCE1_11 = new ItemTrade(new ItemStack(Items.BREAD, 32), new ItemStack(Items.GOLD_NUGGET, 1), 24, 5);


		public static Map<Integer, Int2ObjectMap<VillagerTrades.ItemListing[]>> princeTrades = Util.make(Maps.newHashMap(), integerInt2ObjectMapMap -> {
			integerInt2ObjectMapMap.put(DyeColor.RED.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new VillagerTrades.ItemListing[]{DSTrades.PRINCE1_1, DSTrades.PRINCE1_2, DSTrades.PRINCE1_3, DSTrades.PRINCE1_4, DSTrades.PRINCE1_5, DSTrades.PRINCE1_6, DSTrades.PRINCE1_7, DSTrades.PRINCE1_8, DSTrades.PRINCE1_9, DSTrades.PRINCE1_10, DSTrades.PRINCE1_11}, 2, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_RED_FLOWER}, 3, new VillagerTrades.ItemListing[]{DSTrades.PRINCE3_1}, 4, new VillagerTrades.ItemListing[]{DSTrades.PRINCE4_1}, 5, new VillagerTrades.ItemListing[]{DSTrades.PRINCE5_1})));
			integerInt2ObjectMapMap.put(DyeColor.YELLOW.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new VillagerTrades.ItemListing[]{DSTrades.PRINCE1_1, DSTrades.PRINCE1_2, DSTrades.PRINCE1_3, DSTrades.PRINCE1_4, DSTrades.PRINCE1_5, DSTrades.PRINCE1_6, DSTrades.PRINCE1_7, DSTrades.PRINCE1_8, DSTrades.PRINCE1_9, DSTrades.PRINCE1_10, DSTrades.PRINCE1_11}, 2, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_YELLOW_FLOWER}, 3, new VillagerTrades.ItemListing[]{DSTrades.PRINCE3_1}, 4, new VillagerTrades.ItemListing[]{DSTrades.PRINCE4_1}, 5, new VillagerTrades.ItemListing[]{DSTrades.PRINCE5_1})));
			integerInt2ObjectMapMap.put(DyeColor.BLACK.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new VillagerTrades.ItemListing[]{DSTrades.PRINCE1_1, DSTrades.PRINCE1_2, DSTrades.PRINCE1_3, DSTrades.PRINCE1_4, DSTrades.PRINCE1_5, DSTrades.PRINCE1_6, DSTrades.PRINCE1_7, DSTrades.PRINCE1_8, DSTrades.PRINCE1_9, DSTrades.PRINCE1_10, DSTrades.PRINCE1_11}, 2, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_BLACK_FLOWER}, 3, new VillagerTrades.ItemListing[]{DSTrades.PRINCE3_1}, 4, new VillagerTrades.ItemListing[]{DSTrades.PRINCE4_1}, 5, new VillagerTrades.ItemListing[]{DSTrades.PRINCE5_1})));
			integerInt2ObjectMapMap.put(DyeColor.BLUE.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new VillagerTrades.ItemListing[]{DSTrades.PRINCE1_1, DSTrades.PRINCE1_2, DSTrades.PRINCE1_3, DSTrades.PRINCE1_4, DSTrades.PRINCE1_5, DSTrades.PRINCE1_6, DSTrades.PRINCE1_7, DSTrades.PRINCE1_8, DSTrades.PRINCE1_9, DSTrades.PRINCE1_10, DSTrades.PRINCE1_11}, 2, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_BLUE_FLOWER}, 3, new VillagerTrades.ItemListing[]{DSTrades.PRINCE3_1}, 4, new VillagerTrades.ItemListing[]{DSTrades.PRINCE4_1}, 5, new VillagerTrades.ItemListing[]{DSTrades.PRINCE5_1})));
			integerInt2ObjectMapMap.put(DyeColor.PURPLE.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new VillagerTrades.ItemListing[]{DSTrades.PRINCE1_1, DSTrades.PRINCE1_2, DSTrades.PRINCE1_3, DSTrades.PRINCE1_4, DSTrades.PRINCE1_5, DSTrades.PRINCE1_6, DSTrades.PRINCE1_7, DSTrades.PRINCE1_8, DSTrades.PRINCE1_9, DSTrades.PRINCE1_10, DSTrades.PRINCE1_11}, 2, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_PURPLE_FLOWER}, 3, new VillagerTrades.ItemListing[]{DSTrades.PRINCE3_1}, 4, new VillagerTrades.ItemListing[]{DSTrades.PRINCE4_1}, 5, new VillagerTrades.ItemListing[]{DSTrades.PRINCE5_1})));
			integerInt2ObjectMapMap.put(DyeColor.WHITE.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new VillagerTrades.ItemListing[]{DSTrades.PRINCE1_1, DSTrades.PRINCE1_2, DSTrades.PRINCE1_3, DSTrades.PRINCE1_4, DSTrades.PRINCE1_5, DSTrades.PRINCE1_6, DSTrades.PRINCE1_7, DSTrades.PRINCE1_8, DSTrades.PRINCE1_9, DSTrades.PRINCE1_10, DSTrades.PRINCE1_11}, 2, new VillagerTrades.ItemListing[]{DSTrades.PRINCE_WHITE_FLOWER}, 3, new VillagerTrades.ItemListing[]{DSTrades.PRINCE3_1}, 4, new VillagerTrades.ItemListing[]{DSTrades.PRINCE4_1}, 5, new VillagerTrades.ItemListing[]{DSTrades.PRINCE5_1})));
		});

		public static ItemTrade PRINCESS_RED1_1 = new ItemTrade(new ItemStack(Items.ROSE_BUSH, 16), new ItemStack(DSItems.EXPLOSIVE_COPPER, 1), 64, 5);
		public static ItemTrade PRINCESS_RED1_2 = new ItemTrade(new ItemStack(Items.RED_TULIP, 16), new ItemStack(DSItems.DOUBLE_QUARTZ, 1), 64, 5);
		public static ItemTrade PRINCESS_RED1_3 = new ItemTrade(new ItemStack(Items.POPPY, 16), new ItemStack(DSItems.DOUBLE_QUARTZ, 1), 64, 5);
		public static ItemTrade PRINCESS_RED2_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 8), new ItemStack(DSItems.CHARGED_COAL, 1), 64, 10);
		public static ItemTrade PRINCESS_RED2_2 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 8), new ItemStack(DSItems.HOT_DRAGON_ROD, 1), 64, 10);
		public static ItemTrade PRINCESS_RED3_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 16), new ItemStack(DSItems.CHARGED_SOUP, 1), 64, 10);
		public static ItemTrade PRINCESS_RED4_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 16), new ItemStack(DSItems.CAVE_DRAGON_TREAT, 1), 64, 10);
		public static ItemTrade PRINCESS_RED5_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 32), new ItemStack(DSItems.QUARTZ_EXPLOSIVE_COPPER, 1), 64, 20);

		public static ItemTrade PRINCESS_YELLOW1_1 = new ItemTrade(new ItemStack(Items.SUNFLOWER, 16), new ItemStack(DSItems.SMELLY_MEAT_PORRIDGE, 1), 64, 5);
		public static ItemTrade PRINCESS_YELLOW1_2 = new ItemTrade(new ItemStack(Items.DANDELION, 16), new ItemStack(DSItems.SMELLY_MEAT_PORRIDGE, 1), 64, 5);
		public static ItemTrade PRINCESS_YELLOW1_3 = new ItemTrade(new ItemStack(Items.ORANGE_TULIP, 16), new ItemStack(DSItems.SMELLY_MEAT_PORRIDGE, 1), 64, 5);
		public static ItemTrade PRINCESS_YELLOW2_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 8), new ItemStack(DSItems.SWEET_SOUR_RABBIT, 1), 64, 10);
		public static ItemTrade PRINCESS_YELLOW3_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 16), new ItemStack(DSItems.LUMINOUS_OINTMENT, 1), 64, 10);
		public static ItemTrade PRINCESS_YELLOW4_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 16), new ItemStack(DSItems.FOREST_DRAGON_TREAT, 1), 64, 10);
		public static ItemTrade PRINCESS_YELLOW5_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 32), new ItemStack(DSItems.DIAMOND_CHORUS, 1), 64, 20);
		public static ItemTrade PRINCESS_PURPLE1_1 = new ItemTrade(new ItemStack(Items.LILAC, 16), new ItemStack(DSItems.MEAT_WILD_BERRIES, 1), 64, 5);
		public static ItemTrade PRINCESS_PURPLE1_2 = new ItemTrade(new ItemStack(Items.PEONY, 16), new ItemStack(DSItems.MEAT_WILD_BERRIES, 1), 64, 5);
		public static ItemTrade PRINCESS_PURPLE1_3 = new ItemTrade(new ItemStack(Items.PINK_TULIP, 16), new ItemStack(DSItems.MEAT_WILD_BERRIES, 1), 64, 5);
		public static ItemTrade PRINCESS_PURPLE1_4 = new ItemTrade(new ItemStack(Items.ALLIUM, 16), new ItemStack(DSItems.MEAT_WILD_BERRIES, 1), 64, 5);
		public static ItemTrade PRINCESS_PURPLE2_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 8), new ItemStack(DSItems.SWEET_SOUR_RABBIT, 1), 64, 10);
		public static ItemTrade PRINCESS_PURPLE3_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 8), new ItemStack(DSItems.MEAT_CHORUS_MIX, 1), 64, 10);
		public static ItemTrade PRINCESS_PURPLE4_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 16), new ItemStack(DSItems.FOREST_DRAGON_TREAT, 1), 64, 10);
		public static ItemTrade PRINCESS_PURPLE5_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 32), new ItemStack(DSItems.DIAMOND_CHORUS, 1), 64, 20);
		public static ItemTrade PRINCESS_BLUE1_1 = new ItemTrade(new ItemStack(Items.BLUE_ORCHID, 16), new ItemStack(DSItems.SEASONED_FISH, 1), 64, 5);
		public static ItemTrade PRINCESS_BLUE1_2 = new ItemTrade(new ItemStack(Items.CORNFLOWER, 16), new ItemStack(DSItems.SEASONED_FISH, 1), 64, 5);
		public static ItemTrade PRINCESS_BLUE2_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 8), new ItemStack(DSItems.FROZEN_RAW_FISH, 1), 64, 10);
		public static ItemTrade PRINCESS_BLUE3_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 16), new ItemStack(DSItems.GOLDEN_CORAL_PUFFERFISH, 1), 64, 10);
		public static ItemTrade PRINCESS_BLUE4_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 16), new ItemStack(DSItems.SEA_DRAGON_TREAT, 1), 64, 10);
		public static ItemTrade PRINCESS_BLUE5_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 32), new ItemStack(DSItems.GOLDEN_TURTLE_EGG, 1), 64, 20);
		public static ItemTrade PRINCESS_WHITE1_1 = new ItemTrade(new ItemStack(Items.OXEYE_DAISY, 64), new ItemStack(Items.GOLD_INGOT, 1), 3, 10);
		public static ItemTrade PRINCESS_WHITE1_2 = new ItemTrade(new ItemStack(Items.AZURE_BLUET, 64), new ItemStack(Items.GOLD_INGOT, 1), 3, 10);
		public static ItemTrade PRINCESS_WHITE1_3 = new ItemTrade(new ItemStack(Items.LILY_OF_THE_VALLEY, 64), new ItemStack(Items.GOLD_INGOT, 1), 3, 10);
		public static ItemTrade PRINCESS_WHITE1_4 = new ItemTrade(new ItemStack(Items.WHITE_TULIP, 64), new ItemStack(Items.GOLD_INGOT, 1), 3, 10);
		public static ItemTrade PRINCESS_WHITE2_1 = new ItemTrade(new ItemStack(DSBlocks.HELMET_BLOCK_2.get(), 1), new ItemStack(Items.RAW_GOLD, 1), 64, 20);
		public static ItemTrade PRINCESS_WHITE2_2 = new ItemTrade(new ItemStack(DSBlocks.HELMET_BLOCK_1.get(), 1), new ItemStack(Items.RAW_IRON, 1), 64, 20);
		public static ItemTrade PRINCESS_WHITE2_3 = new ItemTrade(new ItemStack(DSBlocks.HELMET_BLOCK_3.get(), 1), new ItemStack(DSItems.CHARGED_COAL, 1), 64, 20);
		public static ItemTrade PRINCESS_WHITE3_1 = new ItemTrade(new ItemStack(Items.GOLDEN_APPLE, 1), new ItemStack(Items.GOLD_INGOT, 2), 64, 20);
		public static ItemTrade PRINCESS_WHITE3_2 = new ItemTrade(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1), new ItemStack(Items.GOLD_INGOT, 4), 64, 40);
		public static ItemTrade PRINCESS_WHITE4_1 = new ItemTrade(new ItemStack(Items.ELYTRA, 1), new ItemStack(Items.NETHERITE_SCRAP, 1), 3, 40);
		public static ItemTrade PRINCESS_WHITE4_2 = new ItemTrade(new ItemStack(Items.PHANTOM_MEMBRANE, 4), new ItemStack(Items.RAW_IRON, 2), 64, 10);
		public static ItemTrade PRINCESS_WHITE5_1 = new ItemTrade(new ItemStack(DSItems.ELDER_DRAGON_HEART, 4), new ItemStack(Items.TOTEM_OF_UNDYING, 1), 3, 30);
		public static ItemTrade PRINCESS_WHITE5_2 = new ItemTrade(new ItemStack(Items.HEART_OF_THE_SEA, 1), new ItemStack(DSItems.ELDER_DRAGON_HEART, 1), 4, 30);
		public static ItemTrade PRINCESS_BLACK1_1 = new ItemTrade(new ItemStack(Items.WITHER_ROSE, 8), new ItemStack(DSItems.CHARRED_VEGETABLE, 1), 24, 5);
		public static ItemTrade PRINCESS_BLACK1_2 = new ItemTrade(new ItemStack(Items.WITHER_ROSE, 8), new ItemStack(DSItems.CHARRED_MUSHROOM, 1), 24, 5);
		public static ItemTrade PRINCESS_BLACK1_3 = new ItemTrade(new ItemStack(Items.WITHER_ROSE, 8), new ItemStack(DSItems.CHARRED_MEAT, 1), 24, 5);
		public static ItemTrade PRINCESS_BLACK1_4 = new ItemTrade(new ItemStack(Items.WITHER_ROSE, 8), new ItemStack(DSItems.CHARRED_SEAFOOD, 1), 24, 5);
		public static ItemTrade PRINCESS_BLACK2_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 8), new ItemStack(DSItems.CHARGED_COAL, 1), 64, 10);
		public static ItemTrade PRINCESS_BLACK2_2 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 8), new ItemStack(DSItems.HOT_DRAGON_ROD, 1), 64, 10);
		public static ItemTrade PRINCESS_BLACK3_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 16), new ItemStack(DSItems.CHARGED_SOUP, 1), 64, 10);
		public static ItemTrade PRINCESS_BLACK4_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 16), new ItemStack(DSItems.CAVE_DRAGON_TREAT, 1), 64, 10);
		public static ItemTrade PRINCESS_BLACK5_1 = new ItemTrade(new ItemStack(Items.GOLD_INGOT, 32), new ItemStack(DSItems.QUARTZ_EXPLOSIVE_COPPER, 1), 64, 20);

		public static Map<Integer, Int2ObjectMap<ItemListing[]>> princessColorCodes = Util.make(Maps.newHashMap(), objectObjectHashMap -> {
			objectObjectHashMap.put(DyeColor.RED.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new ItemListing[]{DSTrades.PRINCESS_RED1_1, DSTrades.PRINCESS_RED1_2, DSTrades.PRINCESS_RED1_3}, 2, new ItemListing[]{DSTrades.PRINCESS_RED2_1, DSTrades.PRINCESS_RED2_2}, 3, new ItemListing[]{PRINCESS_RED3_1}, 4, new ItemListing[]{PRINCESS_RED4_1}, 5, new ItemListing[]{PRINCESS_RED5_1})));
			objectObjectHashMap.put(DyeColor.YELLOW.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new ItemListing[]{DSTrades.PRINCESS_YELLOW1_1, DSTrades.PRINCESS_YELLOW1_2, DSTrades.PRINCESS_YELLOW1_3}, 2, new ItemListing[]{DSTrades.PRINCESS_YELLOW2_1}, 3, new ItemListing[]{DSTrades.PRINCESS_YELLOW3_1}, 4, new ItemListing[]{DSTrades.PRINCESS_YELLOW4_1}, 5, new ItemListing[]{DSTrades.PRINCESS_YELLOW5_1})));
			objectObjectHashMap.put(DyeColor.PURPLE.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new ItemListing[]{DSTrades.PRINCESS_PURPLE1_1, DSTrades.PRINCESS_PURPLE1_2, DSTrades.PRINCESS_PURPLE1_3, DSTrades.PRINCESS_PURPLE1_4}, 2, new ItemListing[]{DSTrades.PRINCESS_PURPLE2_1}, 3, new ItemListing[]{DSTrades.PRINCESS_PURPLE3_1}, 4, new ItemListing[]{DSTrades.PRINCESS_PURPLE4_1}, 5, new ItemListing[]{DSTrades.PRINCESS_PURPLE5_1})));
			objectObjectHashMap.put(DyeColor.BLUE.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new ItemListing[]{DSTrades.PRINCESS_BLUE1_1, DSTrades.PRINCESS_BLUE1_2}, 2, new ItemListing[]{DSTrades.PRINCESS_BLUE2_1}, 3, new ItemListing[]{DSTrades.PRINCESS_BLUE3_1}, 4, new ItemListing[]{DSTrades.PRINCESS_BLUE4_1}, 5, new ItemListing[]{DSTrades.PRINCESS_BLUE5_1})));
			objectObjectHashMap.put(DyeColor.WHITE.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new ItemListing[]{DSTrades.PRINCESS_WHITE1_1, DSTrades.PRINCESS_WHITE1_2, DSTrades.PRINCESS_WHITE1_3, DSTrades.PRINCESS_WHITE1_4}, 2, new ItemListing[]{DSTrades.PRINCESS_WHITE2_1, DSTrades.PRINCESS_WHITE2_2, DSTrades.PRINCESS_WHITE2_3}, 3, new ItemListing[]{DSTrades.PRINCESS_WHITE3_1, DSTrades.PRINCESS_WHITE3_2}, 4, new ItemListing[]{DSTrades.PRINCESS_WHITE4_1, DSTrades.PRINCESS_WHITE4_2}, 5, new ItemListing[]{DSTrades.PRINCESS_WHITE5_1, DSTrades.PRINCESS_WHITE5_2})));
			objectObjectHashMap.put(DyeColor.BLACK.getId(), ItemTrade.toIntMap(ImmutableMap.of(1, new ItemListing[]{DSTrades.PRINCESS_BLACK1_1, DSTrades.PRINCESS_BLACK1_2, DSTrades.PRINCESS_BLACK1_3, DSTrades.PRINCESS_BLACK1_4}, 2, new ItemListing[]{DSTrades.PRINCESS_BLACK2_1, DSTrades.PRINCESS_BLACK2_2}, 3, new ItemListing[]{DSTrades.PRINCESS_BLACK3_1}, 4, new ItemListing[]{DSTrades.PRINCESS_BLACK4_1}, 5, new ItemListing[]{DSTrades.PRINCESS_BLACK5_1})));
		});



	public static class ItemTrade implements VillagerTrades.ItemListing{
		private ItemCost baseCostA = new ItemCost(ItemStack.EMPTY.getItem(), 0);
		private Optional<ItemCost> costB = Optional.of(new ItemCost(ItemStack.EMPTY.getItem(), 0));
		private final ItemStack result;
		private final int maxUses;
		private float priceMultiplier = 0;
		private int xp = 1;

		public static Int2ObjectMap<ItemListing[]> toIntMap(ImmutableMap<Integer, ItemListing[]> p_221238_0_){
			return new Int2ObjectOpenHashMap<>(p_221238_0_);
		}

		public ItemTrade(ItemStack baseCostA, ItemStack costB, ItemStack result, int maxUses, float priceMultiplier, int xp){
			this.baseCostA = new ItemCost(baseCostA.getItem(), baseCostA.getCount());
			this.costB = Optional.of(new ItemCost(costB.getItem(), costB.getCount()));
			this.result = result;
			this.maxUses = maxUses;
			this.priceMultiplier = priceMultiplier;
			this.xp = xp;
		}

		public ItemTrade(ItemStack baseCostA, ItemStack costB, ItemStack result, int maxUses, int xp){
			this.baseCostA = new ItemCost(baseCostA.getItem(), baseCostA.getCount());
			this.costB = Optional.of(new ItemCost(costB.getItem(), costB.getCount()));
			this.result = result;
			this.maxUses = maxUses;
			this.xp = xp;
		}

		public ItemTrade(ItemStack baseCostA, ItemStack result, int maxUses, int xp){
			this.baseCostA = new ItemCost(baseCostA.getItem(), baseCostA.getCount());
			this.result = result;
			this.maxUses = maxUses;
            this.xp = xp;
		}

		public ItemTrade(ItemStack baseCostA, ItemStack result, int maxUses, float priceMultiplier, int xp){
			this.baseCostA = new ItemCost(baseCostA.getItem(), baseCostA.getCount());
			this.result = result;
			this.maxUses = maxUses;
			this.priceMultiplier = priceMultiplier;
			this.xp = xp;
		}

		@Nullable @Override
		public MerchantOffer getOffer(@NotNull Entity entity, @NotNull RandomSource random){
			return new MerchantOffer(baseCostA, costB, result, maxUses, xp, priceMultiplier);
		}
	}
}