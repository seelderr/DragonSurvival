package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.RequestClientData;
import by.dragonsurvivalteam.dragonsurvival.network.flight.SyncSpinStatus;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncDragonHandler;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncSize;
import by.dragonsurvivalteam.dragonsurvival.network.status.SyncAltarCooldown;
import by.dragonsurvivalteam.dragonsurvival.network.syncing.SyncComplete;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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

    @SubscribeEvent
    public static void onTrackingStart(PlayerEvent.StartTracking startTracking){
        Player trackingPlayer = startTracking.getEntity();
        if(trackingPlayer instanceof ServerPlayer target){
            Entity tracked = startTracking.getTarget();
            if(tracked instanceof ServerPlayer){
                DragonStateProvider.getCap(tracked).ifPresent(dragonStateHandler -> {
                    PacketDistributor.sendToPlayer(target, new SyncDragonHandler.Data(tracked.getId(), dragonStateHandler.isHiding(), dragonStateHandler.getType(), dragonStateHandler.getBody(), dragonStateHandler.getSize(), dragonStateHandler.hasFlight(), 0));
                    PacketDistributor.sendToPlayer(target, new SyncSpinStatus.Data(tracked.getId(), dragonStateHandler.getMovementData().spinAttack, dragonStateHandler.getMovementData().spinCooldown, dragonStateHandler.getMovementData().spinLearned));
                    PacketDistributor.sendToPlayer(target, new RequestClientData.Data(dragonStateHandler.getType(), dragonStateHandler.getBody(), dragonStateHandler.getLevel()));
                });
            }
        }
    }
}
