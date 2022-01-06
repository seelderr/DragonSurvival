package by.jackraidenph.dragonsurvival.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ChargedCoalItem extends Item
{
	public ChargedCoalItem(Properties p_i48487_1_)
	{
		super(p_i48487_1_);
	}
	
	@Override
	public int getBurnTime(ItemStack itemStack) {
		return 4000;
	}
}
