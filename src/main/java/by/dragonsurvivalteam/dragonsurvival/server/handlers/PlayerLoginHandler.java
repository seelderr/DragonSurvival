package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.RequestClientData;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class PlayerLoginHandler {
    // We don't need to sync the player capability data anymore on login, but we do need to request skin data.
    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            DragonStateProvider.getCap(serverPlayer).ifPresent(handler -> {
                PacketDistributor.sendToPlayer(serverPlayer, new RequestClientData.Data(handler.getType(), handler.getBody(), handler.getLevel()));
            });
        }
    }
}
