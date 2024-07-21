package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.syncing.SyncComplete;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class ServerPlayerStatusSync {

	// This function is a failsafe that sync the player's data with the server after a certain period of time if it somehow gets out of sync.

	// TODO: Include some error reporting that says what went out of sync if the server and client data are not the same.
	@SubscribeEvent
	public static void onServerTick(PlayerTickEvent.Post event){
        int syncTicks = Functions.secondsToTicks(600);
		if(event.getEntity().level().isClientSide()) return;

		Player player = event.getEntity();

		if(player.isAddedToLevel() && player.isAlive()){
			if(DragonStateProvider.isDragon(player)){
				DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);
				if(player.tickCount >= handler.lastSync + syncTicks){
                    // We don't do an initial sync here since it could result in the player syncing before their data is loaded, causing data loss.
					handler.lastSync = player.tickCount;
					PacketDistributor.sendToPlayersTrackingEntity(player, new SyncComplete.Data(player.getId(), handler.serializeNBT(player.registryAccess())));
				}
			}
		}
	}
}