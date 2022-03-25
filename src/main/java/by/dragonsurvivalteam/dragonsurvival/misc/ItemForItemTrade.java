package by.dragonsurvivalteam.dragonsurvival.misc;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

import javax.annotation.Nullable;
import java.util.Random;

public class ItemForItemTrade implements VillagerTrades.ItemListing{
	private final Item item;
	private final int cost;
	private final int maxUses;
	private final int xpReward;
	private final Item itemOut;
	private final int count;

	public ItemForItemTrade(Item itemIn, int cost, Item itemOut, int count, int maxUses, int xpReward){
		this.item = itemIn;
		this.cost = cost;
		this.maxUses = maxUses;
		this.xpReward = xpReward;
		this.itemOut = itemOut;
		this.count = count;
	}

	public static Int2ObjectMap<VillagerTrades.ItemListing[]> toIntMap(ImmutableMap<Integer, VillagerTrades.ItemListing[]> p_221238_0_){
		return new Int2ObjectOpenHashMap<>(p_221238_0_);
	}

	@Nullable
	public MerchantOffer getOffer(Entity entity, Random random){
		ItemStack buyIng = new ItemStack(this.item, this.cost);
		return new MerchantOffer(buyIng, new ItemStack(this.itemOut, this.count), this.maxUses, this.xpReward, 0.0F);
	}
}