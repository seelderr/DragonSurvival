package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.util.EnchantmentUtils;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.NotNull;

public class DSTrades {
	public static class ItemTrade implements VillagerTrades.ItemListing{
		private ItemCost baseCostA = new ItemCost(ItemStack.EMPTY.getItem(), 0);
		private Optional<ItemCost> costB = Optional.empty();
		private final ItemStack result;
		private final int maxUses;
		private float priceMultiplier = 0;
		private int xp = 1;

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

	// Copied from VillagerTrades.java
	public static class TreasureMapForEmeralds implements VillagerTrades.ItemListing {
		private final int emeraldCost;
		private final TagKey<Structure> destination;
		private final String displayName;
		private final Holder<MapDecorationType> destinationType;
		private final int maxUses;
		private final int villagerXp;

		public TreasureMapForEmeralds(
				int pEmeraldCost, TagKey<Structure> pDestination, String pDisplayName, Holder<MapDecorationType> pDestinationType, int pMaxUses, int pVillagerXp
		) {
			this.emeraldCost = pEmeraldCost;
			this.destination = pDestination;
			this.displayName = pDisplayName;
			this.destinationType = pDestinationType;
			this.maxUses = pMaxUses;
			this.villagerXp = pVillagerXp;
		}

		@Nullable
		@Override
		public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {
			if (!(pTrader.level() instanceof ServerLevel)) {
				return null;
			} else {
				ServerLevel serverlevel = (ServerLevel)pTrader.level();
				BlockPos blockpos = serverlevel.findNearestMapStructure(this.destination, pTrader.blockPosition(), 100, true);
				if (blockpos != null) {
					ItemStack itemstack = MapItem.create(serverlevel, blockpos.getX(), blockpos.getZ(), (byte)2, true, true);
					MapItem.renderBiomePreviewMap(serverlevel, itemstack);
					MapItemSavedData.addTargetDecoration(itemstack, blockpos, "+", this.destinationType);
					itemstack.set(DataComponents.ITEM_NAME, Component.translatable(this.displayName));
					return new MerchantOffer(
							new ItemCost(Items.EMERALD, this.emeraldCost), Optional.of(new ItemCost(Items.COMPASS)), itemstack, this.maxUses, this.villagerXp, 0.2F
					);
				} else {
					return null;
				}
			}
		}
	}

	private static final TagKey<Structure> ON_DRAGON_HUNTERS_CASTLE_MAPS = TagKey.create(Registries.STRUCTURE, ResourceLocation.withDefaultNamespace("dragon_hunters_castle"));
	private static final ItemStack DRAGONSBANE_ENCHANTED_BOOK = new ItemStack(Items.ENCHANTED_BOOK);
	static {
		DRAGONSBANE_ENCHANTED_BOOK.enchant(EnchantmentUtils.getHolder(DSEnchantments.DRAGONSBANE), 1);
	}

	private static final List<ItemListing> LEADER_TRADES_LEVEL_1 = Lists.newArrayList(
			new TreasureMapForEmeralds(1, ON_DRAGON_HUNTERS_CASTLE_MAPS, "filled_map.dragon_hunters_castle", MapDecorationTypes.TARGET_POINT, 1, 10),
			new ItemTrade(new ItemStack(Items.EMERALD, 1), new ItemStack(DSItems.DRAGON_HEART_SHARD, 1), 16, 1, 1)
	);

	private static final List<ItemListing> LEADER_TRADES_LEVEL_2 = Lists.newArrayList(
			new ItemTrade(new ItemStack(Items.EMERALD, 5), new ItemStack(DSItems.WEAK_DRAGON_HEART, 1), 16, 1, 10)
			//new ItemTrade(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.EMERALD, 1), 16, 1, 1) // FIXME: Make the bolas crossbow enchantment and add its trade here
	);

	private static final List<ItemListing> LEADER_TRADES_LEVEL_3 = Lists.newArrayList(
			new ItemTrade(new ItemStack(Items.EMERALD, 8), new ItemStack(DSItems.ELDER_DRAGON_HEART, 1), 16, 1, 10),
			new ItemTrade(new ItemStack(Items.EMERALD, 32), DRAGONSBANE_ENCHANTED_BOOK, 12, 1, 15)
	);

	private static final List<ItemListing> LEADER_TRADES_LEVEL_4 = Lists.newArrayList(
			new ItemTrade(new ItemStack(Items.EMERALD, 32), new ItemStack(DSItems.HUNTER_DRAGON_KEY, 1), 12, 1, 15)
	);

	private static final List<ItemListing> LEADER_TRADES_LEVEL_5 = Lists.newArrayList(
			new ItemTrade(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.EMERALD, 1), 16, 1, 1)
			// FIXME: Make enchantment to steal growth stages from dragons and add its trade here
	);

	public static final Int2ObjectMap<VillagerTrades.ItemListing[]> LEADER_TRADES = new Int2ObjectOpenHashMap<>();
	static {
		LEADER_TRADES.put(1, LEADER_TRADES_LEVEL_1.toArray(new VillagerTrades.ItemListing[0]));
		LEADER_TRADES.put(2, LEADER_TRADES_LEVEL_2.toArray(new VillagerTrades.ItemListing[0]));
		LEADER_TRADES.put(3, LEADER_TRADES_LEVEL_3.toArray(new VillagerTrades.ItemListing[0]));
		LEADER_TRADES.put(4, LEADER_TRADES_LEVEL_4.toArray(new VillagerTrades.ItemListing[0]));
		LEADER_TRADES.put(5, LEADER_TRADES_LEVEL_5.toArray(new VillagerTrades.ItemListing[0]));
	}
}