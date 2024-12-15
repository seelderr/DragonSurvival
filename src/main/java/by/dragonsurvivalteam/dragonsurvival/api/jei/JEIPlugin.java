package by.dragonsurvivalteam.dragonsurvival.api.jei;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.DragonInventoryScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

@JeiPlugin
@SuppressWarnings("unused")
public class JEIPlugin implements IModPlugin {
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(MODID, "fix");
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new DragonInventoryGUIHandler());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(DragonInventoryScreen.class, new DragonInventoryGUIHandler());
        registration.addGuiContainerHandler(InventoryScreen.class, new InventoryScreenGUIHandler());
        registration.addGuiContainerHandler(CreativeModeInventoryScreen.class, new CreativeModeInventoryScreenGUIHandler());
    }
}