package by.dragonsurvivalteam.dragonsurvival.client.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ClientConfig;
import by.dragonsurvivalteam.dragonsurvival.input.Keybind;
import by.dragonsurvivalteam.dragonsurvival.network.container.RequestOpenDragonInventory;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncDragonAbilitySlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(Dist.CLIENT)
public class KeyInputHandler {

    @SubscribeEvent
    public static void onKey(InputEvent.Key keyInputEvent) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || !DragonStateProvider.isDragon(minecraft.player))
            return;

        DragonStateHandler dragonStateHandler = DragonStateProvider.getOrGenerateHandler(player);

        if (Keybind.DRAGON_INVENTORY.consumeClick()) {
            if (minecraft.screen == null) {
                PacketDistributor.sendToServer(new RequestOpenDragonInventory.Data());
            } else {
                player.closeContainer();
            }

        }
    }
}