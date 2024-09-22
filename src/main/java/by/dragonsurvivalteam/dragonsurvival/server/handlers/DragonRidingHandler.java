package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncDragonHandler;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncDragonPassengerID;
import by.dragonsurvivalteam.dragonsurvival.network.status.RefreshDragon;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class DragonRidingHandler {

	private static boolean playerCanRideDragon(Player rider, Player mount) {
		DragonStateHandler riderCap = DragonStateProvider.getOrGenerateHandler(rider);
		DragonStateHandler mountCap = DragonStateProvider.getOrGenerateHandler(mount);
		double sizeRatio = riderCap.getSize() / mountCap.getSize();
		boolean dragonIsTooSmallToRide = sizeRatio >= 0.5;
		return mountCap.isDragon()
				&& (!dragonIsTooSmallToRide || !riderCap.isDragon())
				&& (!rider.isSpectator() || !mount.isSpectator())
				&& (!rider.isSleeping() || !mount.isSleeping());
	}

	/** Mounting a dragon */
	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event){
		Entity ent = event.getTarget();

		if(event.getHand() != InteractionHand.MAIN_HAND){
			return;
		}


		if(ent instanceof ServerPlayer target){
			Player self = event.getEntity();

			DragonStateProvider.getCap(target).ifPresent(targetCap -> {
				DragonStateProvider.getCap(self).ifPresent(selfCap -> {
					if(playerCanRideDragon(self, target) && !target.isVehicle()) {
						if(target.getPose() == Pose.CROUCHING) {
							self.startRiding(target);
							target.connection.send(new ClientboundSetPassengersPacket(target));
							targetCap.setPassengerId(self.getId());
							PacketDistributor.sendToPlayersTrackingEntityAndSelf(target, new SyncDragonPassengerID.Data(target.getId(), self.getId()));
							event.setCancellationResult(InteractionResult.SUCCESS);
							event.setCanceled(true);
						} else {
							self.sendSystemMessage(Component.translatable("ds.riding.target_not_crouching"));
						}
					} else {
						self.sendSystemMessage(Component.translatable("ds.riding.self_too_big", String.format("%.0f", selfCap.getSize()), String.format("%.0f", targetCap.getSize())));
					}
				});
			});
		}
	}

	@SubscribeEvent
	public static void onServerPlayerTick(PlayerTickEvent.Post event){
		if(event.getEntity() instanceof ServerPlayer player){
			DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
				int passengerId = dragonStateHandler.getPassengerId();
				if(passengerId == -1){
					return;
				}

				Entity passenger = player.level().getEntity(passengerId);
				// Check for any way that riding could have been interrupted and update our internal state tracking
				if(passenger == null || !player.hasPassenger(passenger) || passenger.getRootVehicle() != player.getRootVehicle() || !player.isVehicle()) {
					dragonStateHandler.setPassengerId(-1);
					PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncDragonPassengerID.Data(player.getId(), -1));
					return;
				}

				if(passenger instanceof Player playerPassenger){
					// In addition, if any of the conditions to allow a player to ride a dragon are no longer met, dismount the player
					if(playerCanRideDragon(playerPassenger, player)) {
						return;
					}

					dragonStateHandler.setPassengerId(-1);
					PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncDragonPassengerID.Data(player.getId(), -1));
					passenger.stopRiding();
					player.connection.send(new ClientboundSetPassengersPacket(player));
				}
			});
		}
	}

	@SubscribeEvent
	public static void dismountOnPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
		if (event.getEntity() instanceof ServerPlayer player && player.getVehicle() instanceof ServerPlayer vehicle) {
			DragonStateProvider.getCap(vehicle).ifPresent(handler -> {
				player.stopRiding();
				vehicle.connection.send(new ClientboundSetPassengersPacket(vehicle));
				handler.setPassengerId(-1);
				PacketDistributor.sendToPlayersTrackingEntityAndSelf(vehicle, new SyncDragonPassengerID.Data(vehicle.getId(), -1));
			});
		}
	}
}