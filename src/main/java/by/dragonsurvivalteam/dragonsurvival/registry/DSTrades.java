package by.dragonsurvivalteam.dragonsurvival.registry;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
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

	private static final List<ItemListing> LEADER_TRADES_LEVEL_1 = Lists.newArrayList(
			new ItemTrade(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.EMERALD, 1), 16, 1, 1)
	);

	private static final List<ItemListing> LEADER_TRADES_LEVEL_2 = Lists.newArrayList(
			new ItemTrade(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.EMERALD, 1), 16, 1, 1)
	);

	private static final List<ItemListing> LEADER_TRADES_LEVEL_3 = Lists.newArrayList(
			new ItemTrade(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.EMERALD, 1), 16, 1, 1)
	);

	private static final List<ItemListing> LEADER_TRADES_LEVEL_4 = Lists.newArrayList(
			new ItemTrade(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.EMERALD, 1), 16, 1, 1)
	);

	private static final List<ItemListing> LEADER_TRADES_LEVEL_5 = Lists.newArrayList(
			new ItemTrade(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.EMERALD, 1), 16, 1, 1)
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