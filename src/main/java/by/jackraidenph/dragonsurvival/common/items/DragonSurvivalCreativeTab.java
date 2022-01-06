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
		List<IItemProvider> list = Arrays.asList(DSItems.starBone, DSBlocks.PREDATOR_STAR_BLOCK);
		Comparator<ItemStack> c = Ordering.explicit(list).onResultOf(ItemStack::getItem);
		items.sort((c1, c2) -> list.contains(c1.getItem()) && !list.contains(c2.getItem()) ? -1 :
				!list.contains(c1.getItem()) && list.contains(c2.getItem()) ? 1 :
				list.contains(c1.getItem()) && list.contains(c2.getItem()) ? c.compare(c1, c2) : 0);
	}
}
