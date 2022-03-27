package by.dragonsurvivalteam.dragonsurvival.api.jei;

import by.dragonsurvivalteam.dragonsurvival.client.gui.DragonScreen;
import by.dragonsurvivalteam.dragonsurvival.server.containers.DragonContainer;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;

import java.util.ArrayList;
import java.util.List;

public class DragonInventoryGUIHandler implements IRecipeTransferInfo, IGuiContainerHandler<DragonScreen>{
	@Override
	public Class getContainerClass(){
		return DragonContainer.class;
	}

	@Override
	public Class getRecipeClass(){
		return CraftingRecipe.class;
	}

	@Override
	public ResourceLocation getRecipeCategoryUid(){
		return VanillaRecipeCategoryUid.CRAFTING;
	}

	@Override
	public boolean canHandle(AbstractContainerMenu container, Object recipe){
		return container instanceof DragonContainer;
	}

	@Override
	public List<Slot> getRecipeSlots(AbstractContainerMenu container, Object recipe){
		return ((DragonContainer)container).craftingSlots;
	}

	@Override
	public List<Slot> getInventorySlots(AbstractContainerMenu container, Object recipe){
		return ((DragonContainer)container).inventorySlots;
	}

	@Override
	public List<Rect2i> getGuiExtraAreas(DragonScreen screen){
		List<Rect2i> list = new ArrayList<>();
		if(screen.clawsMenu){
			int size = 80;
			list.add(new Rect2i(screen.getLeftPos() - size, screen.getGuiTop() - 70, size, screen.height + 70));
		}
		return list;
	}
}