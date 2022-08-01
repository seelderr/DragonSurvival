package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.syncing.CompleteDataSync;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber
public class ServerPlayerStatusSync {

	@ConfigOption( side = ConfigSide.SERVER, key = "serverSyncTime", comment = "How often should player data be synced to other players? -1 disables it")
	public static long serverSyncTime = Functions.secondsToTicks(30);

	@SubscribeEvent
	public static void onServerTick(PlayerTickEvent event){
		if(event.side == LogicalSide.CLIENT || event.phase != Phase.START || serverSyncTime == -1) return;

		Player player = event.player;

		if(player.isAddedToWorld() && player.isAlive()){
			if(DragonUtils.isDragon(player)){
				DragonStateHandler handler = DragonUtils.getHandler(player);
				if(handler.lastSync == 0 || player.tickCount >= handler.lastSync + serverSyncTime){
					handler.lastSync = player.tickCount;
					NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), new CompleteDataSync(player.getId(), handler.writeNBT()));
				}
			}
		}
	}
}