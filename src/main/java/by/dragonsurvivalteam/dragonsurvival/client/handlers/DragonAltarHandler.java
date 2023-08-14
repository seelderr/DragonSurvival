package by.dragonsurvivalteam.dragonsurvival.client.handlers;

import by.dragonsurvivalteam.dragonsurvival.client.gui.DragonAltarGUI;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
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
    public static void onPlayerTick(final TickEvent.PlayerTickEvent.ClientTickEvent event) {
        if (shouldAltarOpen) {
            Minecraft minecraft = Minecraft.getInstance();

            if (minecraft.screen == null && minecraft.player != null && !minecraft.player.isDeadOrDying()) {
                minecraft.setScreen(new DragonAltarGUI());
                shouldAltarOpen = false;
            }
        }
    }
}
