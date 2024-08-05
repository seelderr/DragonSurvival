package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class DSTrades {
	public static final DeferredRegister<PoiType> DS_POI_TYPES = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, DragonSurvivalMod.MODID);
	public static final DeferredRegister<VillagerProfession> DS_VILLAGER_PROFESSIONS = DeferredRegister.create(BuiltInRegistries.VILLAGER_PROFESSION, DragonSurvivalMod.MODID);

	// Custom POI for the Dragon Rider Villager
	public static final Holder<PoiType> DRAGON_RIDER_POI = DS_POI_TYPES.register(
			"dragon_rider_poi",
			() -> new PoiType(ImmutableSet.copyOf(DSBlocks.DRAGON_RIDER_WORKBENCH.get().getStateDefinition().getPossibleStates()),
					1, 1));

	public static final Holder<VillagerProfession> DRAGON_RIDER_PROFESSION = DS_VILLAGER_PROFESSIONS.register(
			"dragon_rider",
			() -> new VillagerProfession("dragon_rider",
					holder -> holder.value() == DRAGON_RIDER_POI.value(),
					poiTypeHolder -> poiTypeHolder.value() == DRAGON_RIDER_POI.value(),
					ImmutableSet.of(),
					ImmutableSet.of(),
					SoundEvents.ENDER_DRAGON_GROWL));

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

		@Nullable @Override
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

	// Copied from VillagerTrades.java
	static class EnchantBookForEmeralds implements VillagerTrades.ItemListing {
		private final int villagerXp;
		private final TagKey<Enchantment> tradeableEnchantments;
		private final int minLevel;
		private final int maxLevel;

		public EnchantBookForEmeralds(int pVillagerXp, TagKey<Enchantment> pTradeableEnchantments) {
			this(pVillagerXp, 0, Integer.MAX_VALUE, pTradeableEnchantments);
		}

		public EnchantBookForEmeralds(int pVillagerXp, int pMinLevel, int pMaxLevel, TagKey<Enchantment> pTradeableEnchantments) {
			this.minLevel = pMinLevel;
			this.maxLevel = pMaxLevel;
			this.villagerXp = pVillagerXp;
			this.tradeableEnchantments = pTradeableEnchantments;
		}

		@Override
		public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {
			Optional<Holder<Enchantment>> optional = pTrader.level()
					.registryAccess()
					.registryOrThrow(Registries.ENCHANTMENT)
					.getRandomElementOf(this.tradeableEnchantments, pRandom);
			int i;
			ItemStack itemstack;
			if (!optional.isEmpty()) {
				Holder<Enchantment> holder = optional.get();
				Enchantment enchantment = holder.value();
				int j = Math.max(enchantment.getMinLevel(), this.minLevel);
				int k = Math.min(enchantment.getMaxLevel(), this.maxLevel);
				int l = Mth.nextInt(pRandom, j, k);
				itemstack = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(holder, l));
				i = 2 + pRandom.nextInt(5 + l * 10) + 3 * l;
				if (holder.is(EnchantmentTags.DOUBLE_TRADE_PRICE)) {
					i *= 2;
				}

				if (i > 64) {
					i = 64;
				}
			} else {
				i = 1;
				itemstack = new ItemStack(Items.BOOK);
			}

			return new MerchantOffer(new ItemCost(Items.EMERALD, i), Optional.of(new ItemCost(Items.BOOK)), itemstack, 12, this.villagerXp, 0.2F);
		}
	}

	public static final Int2ObjectMap<VillagerTrades.ItemListing[]> LEADER_TRADES = new Int2ObjectOpenHashMap<>();

	// This is for adding trades to villagers that are within the vanilla framework. We only do this for the dragon rider, as the leader is a completely custom entity that extends Villager and does some special things.
	@SubscribeEvent
	public static void addCustomTrades(final VillagerTradesEvent event) {
		if(event.getType() == DSTrades.DRAGON_RIDER_PROFESSION.value()) {
			Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

			trades.get(1).add(
					new EnchantBookForEmeralds(15, TagKey.create(DSEnchantments.COMBAT_RECOVERY.registryKey(), DSEnchantments.COMBAT_RECOVERY.location()))
			);

			trades.get(1).add(
					new EnchantBookForEmeralds(15, TagKey.create(DSEnchantments.UNBREAKABLE_SPIRIT.registryKey(), DSEnchantments.UNBREAKABLE_SPIRIT.location()))
			);

			trades.get(1).add(
					new EnchantBookForEmeralds(15, TagKey.create(DSEnchantments.AERODYNAMIC_MASTERY.registryKey(), DSEnchantments.AERODYNAMIC_MASTERY.location()))
			);

			trades.get(1).add(
					new EnchantBookForEmeralds(15, TagKey.create(DSEnchantments.SACRED_SCALES.registryKey(), DSEnchantments.SACRED_SCALES.location()))
			);

			trades.get(2).add(
					new ItemTrade(new ItemStack(Items.EMERALD, 32), new ItemStack(DSItems.GOOD_DRAGON_KEY, 1), 12, 150)
			);

			// Declare the leader trades in here, since this event only fires once and if we do it statically it might try to initialize in cases where we don't actually have a minecraft instance yet.
			final TagKey<Structure> ON_DRAGON_HUNTERS_CASTLE_MAPS = TagKey.create(Registries.STRUCTURE, ResourceLocation.withDefaultNamespace("dragon_hunters_castle"));

			final List<ItemListing> LEADER_TRADES_LEVEL_1 = Lists.newArrayList(
					new TreasureMapForEmeralds(1, ON_DRAGON_HUNTERS_CASTLE_MAPS, "filled_map.dragon_hunters_castle", DSMapDecorationTypes.DRAGON_HUNTER, 1, 10),
					new ItemTrade(new ItemStack(Items.EMERALD, 1), new ItemStack(DSItems.DRAGON_HEART_SHARD, 1), 16, 1, 1)
			);

			final List<ItemListing> LEADER_TRADES_LEVEL_2 = Lists.newArrayList(
					new ItemTrade(new ItemStack(Items.EMERALD, 5), new ItemStack(DSItems.WEAK_DRAGON_HEART, 1), 16, 1, 10)
					//new ItemTrade(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.EMERALD, 1), 16, 1, 1) // FIXME: Make the bolas crossbow enchantment and add its trade here
			);

			final List<ItemListing> LEADER_TRADES_LEVEL_3 = Lists.newArrayList(
					new ItemTrade(new ItemStack(Items.EMERALD, 8), new ItemStack(DSItems.ELDER_DRAGON_HEART, 1), 16, 1, 10),
					new EnchantBookForEmeralds(15, TagKey.create(DSEnchantments.DRAGONSBANE.registryKey(), DSEnchantments.DRAGONSBANE.location()))
			);

			final List<ItemListing> LEADER_TRADES_LEVEL_4 = Lists.newArrayList(
					new ItemTrade(new ItemStack(Items.EMERALD, 32), new ItemStack(DSItems.HUNTER_DRAGON_KEY, 1), 12, 1, 15)
			);

			final List<ItemListing> LEADER_TRADES_LEVEL_5 = Lists.newArrayList(
					new ItemTrade(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.EMERALD, 1), 16, 1, 1)
					// FIXME: Make enchantment to steal growth stages from dragons and add its trade here
			);

			LEADER_TRADES.put(1, LEADER_TRADES_LEVEL_1.toArray(new VillagerTrades.ItemListing[0]));
			LEADER_TRADES.put(2, LEADER_TRADES_LEVEL_2.toArray(new VillagerTrades.ItemListing[0]));
			LEADER_TRADES.put(3, LEADER_TRADES_LEVEL_3.toArray(new VillagerTrades.ItemListing[0]));
			LEADER_TRADES.put(4, LEADER_TRADES_LEVEL_4.toArray(new VillagerTrades.ItemListing[0]));
			LEADER_TRADES.put(5, LEADER_TRADES_LEVEL_5.toArray(new VillagerTrades.ItemListing[0]));
		}
	}
}