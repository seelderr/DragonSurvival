package by.jackraidenph.dragonsurvival.jei;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
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
}
