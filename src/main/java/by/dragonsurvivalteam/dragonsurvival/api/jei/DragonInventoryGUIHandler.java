package by.dragonsurvivalteam.dragonsurvival.api.jei;

import by.dragonsurvivalteam.dragonsurvival.client.gui.DragonScreen;
import by.dragonsurvivalteam.dragonsurvival.server.containers.DragonContainer;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DragonInventoryGUIHandler implements IRecipeTransferInfo, IGuiContainerHandler<DragonScreen>{
	@Override
	public @NotNull Class getContainerClass(){
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
	public boolean canHandle(@NotNull AbstractContainerMenu container, @NotNull Object recipe){
		return container instanceof DragonContainer;
	}

	@Override
	public @NotNull List<Slot> getRecipeSlots(@NotNull AbstractContainerMenu container, @NotNull Object recipe){
		return ((DragonContainer)container).craftingSlots;
	}

	@Override
	public @NotNull List<Slot> getInventorySlots(@NotNull AbstractContainerMenu container, @NotNull Object recipe){
		return ((DragonContainer)container).inventorySlots;
	}
}