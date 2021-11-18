package by.jackraidenph.dragonsurvival.jei;

import by.jackraidenph.dragonsurvival.containers.DragonContainer;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class DragonInventoryGUIHandler implements IRecipeTransferInfo
{
	@Override
	public Class getContainerClass()
	{
		return DragonContainer.class;
	}
	
	@Override
	public ResourceLocation getRecipeCategoryUid()
	{
		return VanillaRecipeCategoryUid.CRAFTING;
	}
	
	@Override
	public boolean canHandle(Container container)
	{
		return container instanceof DragonContainer;
	}
	
	@Override
	public List<Slot> getRecipeSlots(Container container)
	{
		return ((DragonContainer)container).craftingSlots;
	}
	
	@Override
	public List<Slot> getInventorySlots(Container container)
	{
		return ((DragonContainer)container).inventorySlots;
	}
}
