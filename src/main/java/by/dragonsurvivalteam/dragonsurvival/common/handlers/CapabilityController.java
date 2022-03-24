package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.hitbox.DragonHitBox;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.hitbox.DragonHitboxPart;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.entity.player.SynchronizeDragonCap;
import by.dragonsurvivalteam.dragonsurvival.network.status.DiggingStatus;
import by.dragonsurvivalteam.dragonsurvival.network.status.RefreshDragons;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.PacketDistributor;

@EventBusSubscriber
public class CapabilityController{
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent playerTickEvent){
		if(playerTickEvent.phase != TickEvent.Phase.START){
			return;
		}
		PlayerEntity playerEntity = playerTickEvent.player;
		DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				if(playerEntity instanceof ServerPlayerEntity){
					PlayerInteractionManager interactionManager = ((ServerPlayerEntity)playerEntity).gameMode;
					boolean isMining = interactionManager.isDestroyingBlock && playerEntity.swinging;

					if(isMining != dragonStateHandler.getMovementData().dig){
						dragonStateHandler.getMovementData().dig = isMining;
						NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> playerEntity), new DiggingStatus(playerEntity.getId(), isMining));
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

		if(!(ent instanceof PlayerEntity) || event.getHand() != Hand.MAIN_HAND){
			return;
		}
		PlayerEntity target = (PlayerEntity)ent;
		PlayerEntity self = event.getPlayer();
		DragonStateProvider.getCap(target).ifPresent(targetCap -> {
			if(targetCap.isDragon() && target.getPose() == Pose.CROUCHING && targetCap.getSize() >= 40 && !target.isVehicle()){
				DragonStateProvider.getCap(self).ifPresent(selfCap -> {
					if(!selfCap.isDragon() || selfCap.getLevel() == DragonLevel.BABY){
						if(event.getTarget() instanceof ServerPlayerEntity){
							self.startRiding(target);
							((ServerPlayerEntity)event.getTarget()).connection.send(new SSetPassengersPacket(target));
							targetCap.setPassengerId(self.getId());
							NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> target), new SynchronizeDragonCap(target.getId(), targetCap.isHiding(), targetCap.getType(), targetCap.getSize(), targetCap.hasWings(), targetCap.getLavaAirSupply(), self.getId()));
						}
						event.setCancellationResult(ActionResultType.SUCCESS);
						event.setCanceled(true);
					}
				});
			}
		});
	}

	@SubscribeEvent
	public static void onServerPlayerTick(TickEvent.PlayerTickEvent event){ // TODO: Find a better way of doing this.
		if(!(event.player instanceof ServerPlayerEntity)){
			return;
		}
		ServerPlayerEntity player = (ServerPlayerEntity)event.player;
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			int passengerId = dragonStateHandler.getPassengerId();
			Entity passenger = player.level.getEntity(passengerId);
			boolean flag = false;
			if(!dragonStateHandler.isDragon() && player.isVehicle() && player.getPassengers().get(0) instanceof ServerPlayerEntity){
				flag = true;
				player.getPassengers().get(0).stopRiding();
				player.connection.send(new SSetPassengersPacket(player));
			}else if(player.isSpectator() && passenger != null && player.getPassengers().get(0) instanceof ServerPlayerEntity){
				flag = true;
				player.getPassengers().get(0).stopRiding();
				player.connection.send(new SSetPassengersPacket(player));
			}else if(dragonStateHandler.isDragon() && dragonStateHandler.getSize() < 40 && player.isVehicle() && player.getPassengers().get(0) instanceof ServerPlayerEntity){
				flag = true;
				player.getPassengers().get(0).stopRiding();
				player.connection.send(new SSetPassengersPacket(player));
			}else if(player.isSleeping() && player.isVehicle() && player.getPassengers().get(0) instanceof ServerPlayerEntity){
				flag = true;
				player.getPassengers().get(0).stopRiding();
				player.connection.send(new SSetPassengersPacket(player));
			}
			if(passenger instanceof ServerPlayerEntity){
				DragonStateHandler passengerCap = DragonStateProvider.getCap(passenger).orElseGet(null);
				if(passengerCap.isDragon() && passengerCap.getLevel() != DragonLevel.BABY){
					flag = true;
					passenger.stopRiding();
					player.connection.send(new SSetPassengersPacket(player));
				}else if(passenger.getRootVehicle() != player.getRootVehicle()){
					flag = true;
					passenger.stopRiding();
					player.connection.send(new SSetPassengersPacket(player));
				}
			}
			if(flag || passenger == null || !player.hasPassenger(passenger) || passenger.isSpectator() || player.isSpectator()){
				dragonStateHandler.setPassengerId(0);
			}
		});
	}

	@SubscribeEvent
	public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event){
		ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
		if(player.getVehicle() == null || !(player.getVehicle() instanceof ServerPlayerEntity)){
			return;
		}
		ServerPlayerEntity vehicle = (ServerPlayerEntity)player.getVehicle();
		DragonStateProvider.getCap(player).ifPresent(playerCap -> {
			DragonStateProvider.getCap(vehicle).ifPresent(vehicleCap -> {
				player.stopRiding();
				vehicle.connection.send(new SSetPassengersPacket(vehicle));
				vehicleCap.setPassengerId(0);
				NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> vehicle), new SynchronizeDragonCap(player.getId(), vehicleCap.isHiding(), vehicleCap.getType(), vehicleCap.getSize(), vehicleCap.hasWings(), vehicleCap.getLavaAirSupply(), 0));
			});
		});
	}

	@SubscribeEvent
	public static void changedDimension(PlayerEvent.PlayerChangedDimensionEvent changedDimensionEvent){
		PlayerEntity playerEntity = changedDimensionEvent.getPlayer();
		DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
			NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SynchronizeDragonCap(playerEntity.getId(), dragonStateHandler.isHiding(), dragonStateHandler.getType(), dragonStateHandler.getSize(), dragonStateHandler.hasWings(), dragonStateHandler.getLavaAirSupply(), 0));
			NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new RefreshDragons(playerEntity.getId()));
		});
	}
}