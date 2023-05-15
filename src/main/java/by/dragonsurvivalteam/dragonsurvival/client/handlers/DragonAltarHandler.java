package by.dragonsurvivalteam.dragonsurvival.client.handlers;

import by.dragonsurvivalteam.dragonsurvival.client.gui.DragonAltarGUI;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class DragonAltarHandler {
    public static boolean openingAltar = false;
    public static void OpenAltar()
    {
        if (Minecraft.getInstance().player != null
                && Minecraft.getInstance().screen == null
                && !Minecraft.getInstance().player.isDeadOrDying())
            Minecraft.getInstance().setScreen(new DragonAltarGUI());
        else
            openingAltar = true;
    }
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent tickEvent)
    {
        if (openingAltar){
            Minecraft minecraft = Minecraft.getInstance();
            if(
                    minecraft.screen == null &&
                    minecraft.player != null && !minecraft.player.isDeadOrDying()
            ){
                minecraft.setScreen(new DragonAltarGUI());
                openingAltar = false;
            }
        }
    }
}
