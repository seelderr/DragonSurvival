package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.RequestClientData;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class PlayerLoginHandler {
    static List<MutablePair<ServerPlayer, Integer>> loginDataRequestTimings = new ArrayList<>();

    // TODO: There is probably a more clever way to do this that does not involve waiting. Ask NeoForge devs about how to properly check when a Data Attachment is actually loaded in?

    // We need to delay the syncing of player data a little bit, since if we let the player sync too fast then it is possible for the player
    // to attempt to sync data whilst their data is still being loaded, causing the sync to fail.
    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        loginDataRequestTimings.add(MutablePair.of((ServerPlayer)event.getEntity(), 10));
    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent.Pre event) {
        if(!loginDataRequestTimings.isEmpty()) {
            loginDataRequestTimings.removeIf(pair -> {
                if (pair.getRight() <= 0) {
                    DragonStateProvider.getCap(pair.getLeft()).ifPresent(handler -> {
                        PacketDistributor.sendToPlayer(pair.getLeft(), new RequestClientData.Data(handler.getType(), handler.getBody(), handler.getLevel()));
                    });
                    return true;
                }
                pair.setRight(pair.getRight() - 1);
                return false;
            });
        }
    }
}
