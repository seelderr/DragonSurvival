package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.status.DiggingStatus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;
@EventBusSubscriber
public class MiningTickHandler{
	@SubscribeEvent
	public static void updateMiningTick(TickEvent.PlayerTickEvent playerTickEvent){
		if(playerTickEvent.phase != TickEvent.Phase.START){
			return;
		}
		Player player = playerTickEvent.player;
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				if(player instanceof ServerPlayer){
					ServerPlayerGameMode interactionManager = ((ServerPlayer)player).gameMode;
					boolean isMining = interactionManager.isDestroyingBlock && player.swinging;

					if(isMining != dragonStateHandler.getMovementData().dig){
						dragonStateHandler.getMovementData().dig = isMining;
						NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new DiggingStatus(player.getId(), isMining));
					}
				}
			}
		});
	}
}