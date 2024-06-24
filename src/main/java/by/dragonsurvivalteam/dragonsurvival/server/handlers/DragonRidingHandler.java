package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncDragonHandler;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncDragonPassengerID;
import by.dragonsurvivalteam.dragonsurvival.network.status.RefreshDragon;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class DragonRidingHandler{
	/**
	 * Mounting a dragon
	 */
	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event){
		Entity ent = event.getTarget();

		if(event.getHand() != InteractionHand.MAIN_HAND){
			return;
		}


		if(ent instanceof ServerPlayer target){
			Player self = event.getEntity();

			DragonStateProvider.getCap(target).ifPresent(targetCap -> {
				if(targetCap.isDragon() /*&& target.getPose() == Pose.CROUCHING*/ && targetCap.getSize() >= 40 && !target.isVehicle()){
					DragonStateProvider.getCap(self).ifPresent(selfCap -> {
						if(!selfCap.isDragon() || selfCap.getLevel() == DragonLevel.NEWBORN){
							self.startRiding(target);
							target.connection.send(new ClientboundSetPassengersPacket(target));
							targetCap.setPassengerId(self.getId());
							PacketDistributor.sendToPlayersTrackingEntityAndSelf(target, new SyncDragonPassengerID.Data(target.getId(), self.getId()));
							event.setCancellationResult(InteractionResult.SUCCESS);
							event.setCanceled(true);
						}
					});
				}
			});
		}
	}

	@SubscribeEvent
	public static void onServerPlayerTick(PlayerTickEvent.Post event){
		if(!(event.getEntity() instanceof ServerPlayer player)){
			return;
		}
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			int passengerId = dragonStateHandler.getPassengerId();
			Entity passenger = player.level().getEntity(passengerId);
			boolean flag = false;
			if(!dragonStateHandler.isDragon() && player.isVehicle() && player.getPassengers().get(0) instanceof ServerPlayer){
				flag = true;
				player.getPassengers().get(0).stopRiding();
				player.connection.send(new ClientboundSetPassengersPacket(player));
			}else if(player.isSpectator() && passenger != null && player.getPassengers().get(0) instanceof ServerPlayer){
				flag = true;
				player.getPassengers().get(0).stopRiding();
				player.connection.send(new ClientboundSetPassengersPacket(player));
			}else if(dragonStateHandler.isDragon() && dragonStateHandler.getSize() < 40 && player.isVehicle() && player.getPassengers().get(0) instanceof ServerPlayer){
				flag = true;
				player.getPassengers().get(0).stopRiding();
				player.connection.send(new ClientboundSetPassengersPacket(player));
			}else if(player.isSleeping() && player.isVehicle() && player.getPassengers().get(0) instanceof ServerPlayer){
				flag = true;
				player.getPassengers().get(0).stopRiding();
				player.connection.send(new ClientboundSetPassengersPacket(player));
			}
			if(passenger instanceof ServerPlayer){
				DragonStateHandler passengerCap = DragonStateProvider.getOrGenerateHandler(passenger);
				if(passengerCap.isDragon() && passengerCap.getLevel() != DragonLevel.NEWBORN){
					flag = true;
					passenger.stopRiding();
					player.connection.send(new ClientboundSetPassengersPacket(player));
				}else if(passenger.getRootVehicle() != player.getRootVehicle()){
					flag = true;
					passenger.stopRiding();
					player.connection.send(new ClientboundSetPassengersPacket(player));
				}
			}
			if(flag || passenger == null || !player.hasPassenger(passenger) || passenger.isSpectator() || player.isSpectator()){
				if(dragonStateHandler.getPassengerId() != 0){
					dragonStateHandler.setPassengerId(0);
					PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncDragonPassengerID.Data(player.getId(), 0));
				}
			}
		});
	}

	@SubscribeEvent
	public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event){
		ServerPlayer player = (ServerPlayer)event.getEntity();
		if(player.getVehicle() == null || !(player.getVehicle() instanceof ServerPlayer vehicle)){
			return;
		}
		DragonStateProvider.getCap(player).ifPresent(playerCap -> {
			DragonStateProvider.getCap(vehicle).ifPresent(vehicleCap -> {
				player.stopRiding();
				vehicle.connection.send(new ClientboundSetPassengersPacket(vehicle));
				vehicleCap.setPassengerId(0);
				PacketDistributor.sendToPlayersTrackingEntityAndSelf(vehicle, new SyncDragonPassengerID.Data(vehicle.getId(), 0));
			});
		});
	}

	@SubscribeEvent
	public static void changedDimension(PlayerEvent.PlayerChangedDimensionEvent changedDimensionEvent){
		Player player = changedDimensionEvent.getEntity();
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncDragonHandler.Data(player.getId(), dragonStateHandler.isHiding(), dragonStateHandler.getType(), dragonStateHandler.getBody(), dragonStateHandler.getSize(), dragonStateHandler.hasFlight(), 0));
			PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new RefreshDragon.Data(player.getId()));
		});
	}
}