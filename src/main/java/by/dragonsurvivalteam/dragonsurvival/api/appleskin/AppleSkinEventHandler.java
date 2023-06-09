package by.dragonsurvivalteam.dragonsurvival.api.appleskin;

import by.dragonsurvivalteam.dragonsurvival.config.ClientConfig;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import squeek.appleskin.api.event.TooltipOverlayEvent;

@OnlyIn(Dist.CLIENT)
public class AppleSkinEventHandler {
    @SubscribeEvent
    public void onEvent(final TooltipOverlayEvent.Pre event) {
        if (ClientConfig.hideAppleskinTooltip && DragonUtils.isDragon(Minecraft.getInstance().player)) {
            event.setCanceled(true);
        }
    }
}