package by.dragonsurvivalteam.dragonsurvival.client.handlers;

import by.dragonsurvivalteam.dragonsurvival.client.gui.DragonAltarGUI;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class DragonAltarHandler {
    public static boolean shouldAltarOpen = false;

    public static void openAltar() {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().screen == null && !Minecraft.getInstance().player.isDeadOrDying()) {
            Minecraft.getInstance().setScreen(new DragonAltarGUI());
        } else {
            shouldAltarOpen = true;
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(final ClientTickEvent event) {
        if (shouldAltarOpen) {
            Minecraft minecraft = Minecraft.getInstance();

            if (minecraft.screen == null && minecraft.player != null && !minecraft.player.isDeadOrDying()) {
                minecraft.setScreen(new DragonAltarGUI());
                shouldAltarOpen = false;
            }
        }
    }
}
