package by.dragonsurvivalteam.dragonsurvival.common.capability;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.hitbox.DragonHitBox;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.hitbox.DragonHitboxPart;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.entity.player.SynchronizeDragonCap;
import by.dragonsurvivalteam.dragonsurvival.network.status.DiggingStatus;
import by.dragonsurvivalteam.dragonsurvival.network.status.RefreshDragons;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber
public class CapabilityController{

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent playerTickEvent){
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

	/**
	 * Mounting a dragon
	 */
	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event){
		Entity ent = event.getEntity();

		if(ent instanceof DragonHitBox){
			ent = ((DragonHitBox)ent).player;
		}else if(ent instanceof DragonHitboxPart){
			ent = (((DragonHitboxPart)ent).parentMob).player;
		}

		if(!(ent instanceof Player) || event.getHand() != InteractionHand.MAIN_HAND){
			return;
		}
		Player target = (Player)ent;
		Player self = event.getPlayer();
		DragonStateProvider.getCap(target).ifPresent(targetCap -> {
			if(targetCap.isDragon() && target.getPose() == Pose.CROUCHING && targetCap.getSize() >= 40 && !target.isVehicle()){
				DragonStateProvider.getCap(self).ifPresent(selfCap -> {
					if(!selfCap.isDragon() || selfCap.getLevel() == DragonLevel.BABY){
						if(event.getTarget() instanceof ServerPlayer){
							self.startRiding(target);
							((ServerPlayer)event.getTarget()).connection.send(new ClientboundSetPassengersPacket(target));
							targetCap.setPassengerId(self.getId());
							NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> target), new SynchronizeDragonCap(target.getId(), targetCap.isHiding(), targetCap.getType(), targetCap.getSize(), targetCap.hasWings(), targetCap.getLavaAirSupply(), self.getId()));
						}
						event.setCancellationResult(InteractionResult.SUCCESS);
						event.setCanceled(true);
					}
				});
			}
		});
	}

	@SubscribeEvent
	public static void onServerPlayerTick(TickEvent.PlayerTickEvent event){ // TODO: Find a better way of doing this.
		if(!(event.player instanceof ServerPlayer)){
			return;
		}
		ServerPlayer player = (ServerPlayer)event.player;
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			int passengerId = dragonStateHandler.getPassengerId();
			Entity passenger = player.level.getEntity(passengerId);
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
				DragonStateHandler passengerCap = DragonUtils.getHandler(passenger);
				if(passengerCap.isDragon() && passengerCap.getLevel() != DragonLevel.BABY){
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
				dragonStateHandler.setPassengerId(0);
				NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SynchronizeDragonCap(player.getId(), dragonStateHandler.isHiding(), dragonStateHandler.getType(), dragonStateHandler.getSize(), dragonStateHandler.hasWings(), dragonStateHandler.getLavaAirSupply(), 0));
			}
		});
	}

	@SubscribeEvent
	public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event){
		ServerPlayer player = (ServerPlayer)event.getPlayer();
		if(player.getVehicle() == null || !(player.getVehicle() instanceof ServerPlayer)){
			return;
		}
		ServerPlayer vehicle = (ServerPlayer)player.getVehicle();
		DragonStateProvider.getCap(player).ifPresent(playerCap -> {
			DragonStateProvider.getCap(vehicle).ifPresent(vehicleCap -> {
				player.stopRiding();
				vehicle.connection.send(new ClientboundSetPassengersPacket(vehicle));
				vehicleCap.setPassengerId(0);
				NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> vehicle), new SynchronizeDragonCap(player.getId(), vehicleCap.isHiding(), vehicleCap.getType(), vehicleCap.getSize(), vehicleCap.hasWings(), vehicleCap.getLavaAirSupply(), 0));
			});
		});
	}

	@SubscribeEvent
	public static void changedDimension(PlayerEvent.PlayerChangedDimensionEvent changedDimensionEvent){
		Player player = changedDimensionEvent.getPlayer();
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SynchronizeDragonCap(player.getId(), dragonStateHandler.isHiding(), dragonStateHandler.getType(), dragonStateHandler.getSize(), dragonStateHandler.hasWings(), dragonStateHandler.getLavaAirSupply(), 0));
			NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new RefreshDragons(player.getId()));
		});
	}
}