package by.dragonsurvivalteam.dragonsurvival.client;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientEvents;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientGrowthHudHandler;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RenderHudEvents {
    public static ForgeGui getGui()
    {
        return (ForgeGui)Minecraft.getInstance().gui;
    }

    @SubscribeEvent(receiveCanceled = true)
    public static void onRenderOverlay(RenderGuiOverlayEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (event.isCanceled() ||
            minecraft.options.hideGui){
            return;
        }
        int screenWidth = event.getWindow().getGuiScaledWidth();
        int screenHeight = event.getWindow().getGuiScaledHeight();
        ResourceLocation id = event.getOverlay().id();
        if (id == VanillaGuiOverlay.FOOD_LEVEL.id())
        {
            event.setCanceled(true);
            DragonFoodHandler.onRenderFoodBar(getGui(),event.getPoseStack(), event.getPartialTick(),screenWidth, screenHeight);
        } else if (id == VanillaGuiOverlay.EXPERIENCE_BAR.id()) {
            event.setCanceled(true);
            ClientMagicHUDHandler.cancelExpBar(getGui(),event.getPoseStack(), event.getPartialTick(),screenWidth, screenHeight);
        } else if (id == VanillaGuiOverlay.AIR_LEVEL.id()) {
            ClientEvents.onRenderOverlayPreTick(getGui(), event.getPoseStack(), event.getPartialTick(), screenWidth, screenHeight);
            ClientMagicHUDHandler.renderAbilityHud(getGui(), event.getPoseStack(), event.getPartialTick(), screenWidth, screenHeight);
            // Renders the growth icon above the experience bar when an item is selected which grants growth
            ClientGrowthHudHandler.renderGrowth(getGui(), event.getPoseStack(), event.getPartialTick(), screenWidth, screenHeight);
        }
    }
}
