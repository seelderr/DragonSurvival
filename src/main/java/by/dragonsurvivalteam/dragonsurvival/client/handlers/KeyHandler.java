package by.dragonsurvivalteam.dragonsurvival.client.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.input.Keybind;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncDestructionEnabled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(Dist.CLIENT)
public class KeyHandler {
    @SubscribeEvent
    public static void toggleDestructionMode(final InputEvent.Key event) {
        if (!ServerConfig.allowLargeBlockDestruction && !ServerConfig.allowCrushing) {
            return;
        }

        if (Minecraft.getInstance().screen != null || event.getAction() != Keybind.KEY_PRESSED || !Keybind.DISABLE_DESTRUCTION.isKey(event.getKey())) {
            return;
        }

        Player player = Minecraft.getInstance().player;

        if (player == null) {
            return;
        }

        DragonStateHandler data = DragonStateProvider.getData(player);

        if (!data.isDragon()) {
            return;
        }

        Keybind.DISABLE_DESTRUCTION.consumeClick();
        data.setDestructionEnabled(!data.getDestructionEnabled());
        PacketDistributor.sendToServer(new SyncDestructionEnabled.Data(player.getId(), data.getDestructionEnabled()));
        player.displayClientMessage(Component.translatable(data.getDestructionEnabled() ? "ds.destruction.toggled_on" : "ds.destruction.toggled_off"), true);
    }
}
