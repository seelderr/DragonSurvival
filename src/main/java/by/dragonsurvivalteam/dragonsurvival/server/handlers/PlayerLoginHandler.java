package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.syncing.SyncComplete;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class PlayerLoginHandler {
    @SubscribeEvent
    public static void onTrackingStart(PlayerEvent.StartTracking startTracking){
        Player trackingPlayer = startTracking.getEntity();
        if(trackingPlayer instanceof ServerPlayer target){
            Entity tracked = startTracking.getTarget();
            if(tracked instanceof ServerPlayer){
                DragonStateProvider.getCap(tracked).ifPresent(dragonStateHandler -> {
                    PacketDistributor.sendToPlayer(target, new SyncComplete.Data(tracked.getId(), dragonStateHandler.serializeNBT(tracked.registryAccess())));
                });
            }
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event){
        if(event.getEntity() instanceof ServerPlayer player) {
            DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
                PacketDistributor.sendToPlayer(player, new SyncComplete.Data(player.getId(), dragonStateHandler.serializeNBT(player.registryAccess())));
            });
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event){
        // Heal the player to full health on respawn as the player's max health attribute being set doesn't change the player's starting health on respawn
        if(event.getEntity() instanceof ServerPlayer player){
            player.setHealth(player.getMaxHealth());
        }
    }
}
