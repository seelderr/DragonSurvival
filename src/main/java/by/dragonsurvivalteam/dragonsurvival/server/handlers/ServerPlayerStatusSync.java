package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.syncing.CompleteDataSync;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber
public class ServerPlayerStatusSync {

	// TLDR: This function is a failsafe that sync the player's data with the server after a certain period of time if it somehow gets out of sync.

	// TODO: This function is removed for now, since it could potentially write to the player's NBT data before the player has read it.
	// Once that is fixed, we should re-enable this function, but include some error reporting that says what went out of sync if the server and client data are not the same.
	/*@SubscribeEvent
	public static void onServerTick(PlayerTickEvent event){
		if(event.side == LogicalSide.CLIENT || event.phase != Phase.START || serverSyncTime == -1) return;

		Player player = event.player;

		if(player.isAddedToWorld() && player.isAlive()){
			if(DragonUtils.isDragon(player)){
				DragonStateHandler handler = DragonUtils.getHandler(player);
				if(handler.lastSync == 0 || player.tickCount >= handler.lastSync + serverSyncTime){
					handler.lastSync = player.tickCount;
				}
			}
		}
	}*/
}