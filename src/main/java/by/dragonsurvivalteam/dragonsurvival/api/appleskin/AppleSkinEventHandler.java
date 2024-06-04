package by.dragonsurvivalteam.dragonsurvival.api.appleskin;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.ToolTipHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import squeek.appleskin.api.event.TooltipOverlayEvent;

@OnlyIn(Dist.CLIENT)
public class AppleSkinEventHandler {
    @SubscribeEvent
    public void onEvent(final TooltipOverlayEvent.Pre event) {
        if (ToolTipHandler.hideAppleskinTooltip && DragonStateProvider.isDragon(Minecraft.getInstance().player)) {
            event.setCanceled(true);
        }
    }
}