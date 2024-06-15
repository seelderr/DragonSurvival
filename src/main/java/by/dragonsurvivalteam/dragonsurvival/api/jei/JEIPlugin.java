/*package by.dragonsurvivalteam.dragonsurvival.api.jei;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.DragonScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
@SuppressWarnings( "unused" )
public class JEIPlugin implements IModPlugin{
	@Override
	public @NotNull ResourceLocation getPluginUid(){
		return ResourceLocation.fromNamespaceAndPath(MODID, "fix");
	}
	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration){
		registration.addRecipeTransferHandler(new DragonInventoryGUIHandler());
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration){
		registration.addGuiContainerHandler(DragonScreen.class, new DragonInventoryGUIHandler());
	}
}*/