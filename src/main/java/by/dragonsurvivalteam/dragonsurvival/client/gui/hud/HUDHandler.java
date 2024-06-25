package by.dragonsurvivalteam.dragonsurvival.client.gui.hud;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class HUDHandler {
    @ConfigOption(side = ConfigSide.CLIENT, category = {"ui", "hud"}, key = "vanillaFoodLevel", comment = "Re-enable the vanilla hud for the food level")
    public static Boolean vanillaFoodLevel = false;

    @ConfigOption(side = ConfigSide.CLIENT, category = {"ui", "hud"}, key = "vanillaExperienceBar", comment = "Re-enable the vanilla hud for the experience bar")
    public static Boolean vanillaExperienceBar = false;

    @SubscribeEvent(receiveCanceled = true)
    public static void onRenderOverlay(final RenderGuiLayerEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (event.isCanceled() || minecraft.options.hideGui){
            return;
        }

        int screenWidth = event.getGuiGraphics().guiWidth();
        int screenHeight = event.getGuiGraphics().guiHeight();
        ResourceLocation id = event.getName();

        if (DragonFoodHandler.customDragonFoods && !vanillaFoodLevel && id == VanillaGuiLayers.FOOD_LEVEL) {
            boolean wasRendered = DragonFoodHandler.renderFoodBar(Minecraft.getInstance().gui, event.getGuiGraphics(), screenWidth, screenHeight);

            if (wasRendered) {
                event.setCanceled(true);
            }
        } else if (ServerConfig.consumeEXPAsMana && !vanillaExperienceBar && id == VanillaGuiLayers.EXPERIENCE_BAR) {
            boolean wasRendered = MagicHUD.renderExperienceBar(event.getGuiGraphics(), screenWidth);

            if (wasRendered) {
                event.setCanceled(true);
            }
        } else if (id == VanillaGuiLayers.AIR_LEVEL) {
            DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(ClientProxy.getLocalPlayer());

            if (handler == null || !handler.isDragon()) {
                return;
            }

            // Render dragon specific hud elements (e.g. time in rain for cave dragons or time without water for sea dragons)
            DragonPenaltyHUD.renderDragonPenaltyHUD(handler, Minecraft.getInstance().gui, event.getGuiGraphics());
            // Renders the abilities
            MagicHUD.renderAbilityHUD(handler, event.getGuiGraphics(), screenWidth, screenHeight);
            // Renders the growth icon above the experience bar when an item is selected which grants growth
            GrowthHUD.renderGrowthHUD(handler, event.getGuiGraphics(), screenWidth, screenHeight);
        }
    }
}
