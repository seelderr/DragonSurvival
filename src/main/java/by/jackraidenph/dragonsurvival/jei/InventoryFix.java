package by.jackraidenph.dragonsurvival.jei;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.gui.magic.DragonScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
@SuppressWarnings("unused")
public class InventoryFix implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(DragonSurvivalMod.MODID, "fix");
    }
    
    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
    {
        registration.addRecipeTransferHandler(new DragonInventoryGUIHandler());
    }
    
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration)
    {
        registration.addGuiContainerHandler(DragonScreen.class, new DragonInventoryGUIHandler());
    }
}
