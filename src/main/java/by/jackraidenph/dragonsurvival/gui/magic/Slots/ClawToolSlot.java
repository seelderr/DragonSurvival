package by.jackraidenph.dragonsurvival.gui.magic.Slots;

import by.jackraidenph.dragonsurvival.containers.DragonContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraftforge.common.ToolType;

public class ClawToolSlot extends Slot
{
	public ToolType type;
	DragonContainer dragonContainer;
	
	public ClawToolSlot(DragonContainer container, IInventory inv, ToolType type, int index, int x, int y)
	{
		super(inv, index, x, y);
		this.type = type;
		this.dragonContainer = container;
	}
	
	@Override
	public boolean isActive()
	{
		return dragonContainer.menuStatus == 1;
	}
	
	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return type == null && stack.getItem() instanceof SwordItem || stack.getItem().getToolTypes(stack).contains(type);
	}
}
