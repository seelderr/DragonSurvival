package by.jackraidenph.dragonsurvival.jei;

import by.jackraidenph.dragonsurvival.containers.DragonContainer;
import by.jackraidenph.dragonsurvival.gui.magic.DragonScreen;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class DragonInventoryGUIHandler implements IRecipeTransferInfo, IGuiContainerHandler<DragonScreen>
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
	
	
	@Override
	public List<Rectangle2d> getGuiExtraAreas(DragonScreen screen)
	{
		List<Rectangle2d> list = new ArrayList<>();
		if(screen.getRecipeBookComponent().isVisible()){
			int size = 147 + 40;
			list.add(new Rectangle2d(screen.getLeftPos() - size, screen.getGuiTop(), size, screen.height));
		}
		
		if(screen.clawsMenu){
			int size = 80;
			list.add(new Rectangle2d(screen.getLeftPos() - size, screen.getGuiTop(), size, screen.height));
		}
		return list;
	}
}
