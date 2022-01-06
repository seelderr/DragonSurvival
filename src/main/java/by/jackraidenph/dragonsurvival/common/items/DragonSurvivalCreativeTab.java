package by.jackraidenph.dragonsurvival.common.items;

import by.jackraidenph.dragonsurvival.common.blocks.DSBlocks;
import com.google.common.collect.Ordering;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DragonSurvivalCreativeTab extends ItemGroup
{
	public DragonSurvivalCreativeTab(String label)
	{
		super(label);
	}
	
	@Override
	public ItemStack makeIcon()
	{
		return new ItemStack(DSItems.elderDragonBone);
	}
	
	@Override
	public void fillItemList(NonNullList<ItemStack> items)
	{
		super.fillItemList(items);
		List<IItemProvider> list = Arrays.asList(
				DSBlocks.dragon_altar_stone, DSBlocks.dragon_altar_sandstone, DSBlocks.dragon_altar_red_sandstone, DSBlocks.dragon_altar_purpur_block,
				DSBlocks.dragon_altar_oak_log, DSBlocks.dragon_altar_nether_bricks, DSBlocks.dragon_altar_mossy_cobblestone, DSBlocks.dragon_altar_blackstone,
				DSBlocks.dragon_altar_birch_log, DSItems.starBone, DSItems.starHeart, DSItems.elderDragonDust, DSItems.elderDragonBone, DSItems.dragonHeartShard,
				DSItems.weakDragonHeart, DSItems.elderDragonHeart, DSBlocks.PREDATOR_STAR_BLOCK, DSBlocks.dragonMemoryBlock, DSBlocks.dragonBeacon, DSBlocks.peaceDragonBeacon,
				DSBlocks.magicDragonBeacon, DSBlocks.fireDragonBeacon, DSBlocks.forestSourceOfMagic, DSBlocks.caveSourceOfMagic, DSBlocks.seaSourceOfMagic,
				DSItems.wingGrantItem, DSItems.spinGrantItem, DSItems.seaDragonTreat, DSItems.forestDragonTreat, DSItems.caveDragonTreat,
				DSItems.charredMeat, DSItems.charredVegetable, DSItems.charredMushroom, DSItems.charredSeafood, DSItems.chargedCoal, DSItems.chargedSoup,
				DSBlocks.treasureDebris, DSBlocks.treasureDiamond, DSBlocks.treasureEmerald, DSBlocks.treasureCopper, DSBlocks.treasureGold, DSBlocks.treasureIron,
				DSBlocks.helmet2, DSBlocks.helmet1, DSBlocks.helmet3, DSBlocks.caveDoor, DSBlocks.forestDoor, DSBlocks.seaDoor, DSBlocks.spruceDoor,
				DSBlocks.acaciaDoor, DSBlocks.birchDoor, DSBlocks.jungleDoor, DSBlocks.oakDoor, DSBlocks.darkOakDoor, DSBlocks.crimsonDoor,
				DSBlocks.warpedDoor, DSBlocks.legacyDoor, DSBlocks.ironDoor, DSBlocks.murdererDoor, DSBlocks.sleeperDoor, DSBlocks.stoneDoor);
		Comparator<ItemStack> c = Ordering.explicit(list).onResultOf(ItemStack::getItem);
		items.sort((c1, c2) -> list.contains(c1.getItem()) && !list.contains(c2.getItem()) ? -1 :
				!list.contains(c1.getItem()) && list.contains(c2.getItem()) ? 1 :
				list.contains(c1.getItem()) && list.contains(c2.getItem()) ? c.compare(c1, c2) : 0);
	}
}
