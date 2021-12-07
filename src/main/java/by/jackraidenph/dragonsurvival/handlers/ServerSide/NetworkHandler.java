package by.jackraidenph.dragonsurvival.handlers.ServerSide;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.PacketProxy;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.gecko.entity.DragonEntity;
import by.jackraidenph.dragonsurvival.handlers.ClientSide.ClientDragonRender;
import by.jackraidenph.dragonsurvival.handlers.ClientSide.ClientEvents;
import by.jackraidenph.dragonsurvival.nest.DismantleNest;
import by.jackraidenph.dragonsurvival.nest.NestEntity;
import by.jackraidenph.dragonsurvival.nest.SleepInNest;
import by.jackraidenph.dragonsurvival.nest.ToggleRegeneration;
import by.jackraidenph.dragonsurvival.network.*;
import by.jackraidenph.dragonsurvival.network.emotes.SyncEmote;
import by.jackraidenph.dragonsurvival.network.emotes.SyncEmoteServer;
import by.jackraidenph.dragonsurvival.network.emotes.SyncEmoteStats;
import by.jackraidenph.dragonsurvival.network.emotes.SyncEmoteStatsServer;
import by.jackraidenph.dragonsurvival.network.magic.*;
import by.jackraidenph.dragonsurvival.registration.BlockInit;
import by.jackraidenph.dragonsurvival.registration.EntityTypesInit;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.concurrent.atomic.AtomicReference;

public class NetworkHandler
{
	private static final String PROTOCOL_VERSION = "2";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(DragonSurvivalMod.MODID, "main"),
	                                                                             () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	public static int nextPacketId = 0;
	
	public static <T> void register(Class<T> clazz, IMessage<T> message) {
     CHANNEL.registerMessage(nextPacketId++, clazz, message::encode, message::decode, message::handle);
     }
	 
	 public static void setup(){
		
		//Generic packets
		 register(PacketSyncCapabilityMovement.class, new PacketSyncCapabilityMovement());
		 register(SyncCapabilityDebuff.class, new SyncCapabilityDebuff());
		 register(PacketSyncXPDevour.class, new PacketSyncXPDevour());
		 register(PacketSyncPredatorStats.class, new PacketSyncPredatorStats());
		 register(SynchronizeNest.class, new SynchronizeNest());
		 register(SyncSize.class, new SyncSize());
		 register(ToggleWings.class, new ToggleWings());
		// register(OpenCrafting.class, new OpenCrafting());
		 register(OpenInventory.class, new OpenInventory());
		 
		 //Ability packets
		 register(OpenDragonInventory.class, new OpenDragonInventory());
		 register(ChangeSkillLevel.class, new ChangeSkillLevel());
		 register(SyncMagicStats.class, new SyncMagicStats());
		 register(SyncMagicAbilities.class, new SyncMagicAbilities());
		 register(SyncDragonAbilitySlot.class, new SyncDragonAbilitySlot());
		 register(SyncCurrentAbilityCasting.class, new SyncCurrentAbilityCasting());
		 register(SyncAbilityCastingToServer.class, new SyncAbilityCastingToServer());
		 
		 register(SyncPotionRemovedEffect.class, new SyncPotionRemovedEffect());
		 register(SyncPotionAddedEffect.class, new SyncPotionAddedEffect());
		 
		 register(DragonClawsMenuToggle.class, new DragonClawsMenuToggle());
		 register(SyncDragonClawsMenu.class, new SyncDragonClawsMenu());
		
		 //Emote packets
		 register(SyncEmoteServer.class, new SyncEmoteServer());
		 register(SyncEmote.class, new SyncEmote());
		 register(SyncEmoteStatsServer.class, new SyncEmoteStatsServer());
		 register(SyncEmoteStats.class, new SyncEmoteStats());
		
		
		 CHANNEL.registerMessage(nextPacketId++, SynchronizeDragonCap.class, (synchronizeDragonCap, packetBuffer) -> {
			 packetBuffer.writeInt(synchronizeDragonCap.playerId);
			 packetBuffer.writeByte(synchronizeDragonCap.dragonType.ordinal());
			 packetBuffer.writeBoolean(synchronizeDragonCap.hiding);
			 packetBuffer.writeFloat(synchronizeDragonCap.size);
			 packetBuffer.writeBoolean(synchronizeDragonCap.hasWings);
			 packetBuffer.writeInt(synchronizeDragonCap.lavaAirSupply);
			 packetBuffer.writeInt(synchronizeDragonCap.passengerId);
		 }, packetBuffer -> {
			 int id = packetBuffer.readInt();
			 DragonType type = DragonType.values()[packetBuffer.readByte()];
			 boolean hiding = packetBuffer.readBoolean();
			 float size = packetBuffer.readFloat();
			 boolean hasWings = packetBuffer.readBoolean();
			 int lavaAirSupply = packetBuffer.readInt();
			 int passengerId = packetBuffer.readInt();
			 return new SynchronizeDragonCap(id, hiding, type, size, hasWings, lavaAirSupply, passengerId);
		 }, (synchronizeDragonCap, contextSupplier) -> {
			 if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
				 CHANNEL.send(PacketDistributor.ALL.noArg(), synchronizeDragonCap);
				 ServerPlayerEntity serverPlayerEntity = contextSupplier.get().getSender();
				 DragonStateProvider.getCap(serverPlayerEntity).ifPresent(dragonStateHandler -> {
					 dragonStateHandler.setIsHiding(synchronizeDragonCap.hiding);
					 dragonStateHandler.setType(synchronizeDragonCap.dragonType);
					 dragonStateHandler.setSize(synchronizeDragonCap.size, serverPlayerEntity);
					 dragonStateHandler.setHasWings(synchronizeDragonCap.hasWings);
					 dragonStateHandler.setLavaAirSupply(synchronizeDragonCap.lavaAirSupply);
					 dragonStateHandler.setPassengerId(synchronizeDragonCap.passengerId);
					 serverPlayerEntity.setForcedPose(null);
					 serverPlayerEntity.refreshDimensions();
				 });
			 } else {
				 DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> new PacketProxy().refreshInstances(synchronizeDragonCap, contextSupplier));
			 }
		 });
		
		 CHANNEL.registerMessage(nextPacketId++, ToggleRegeneration.class, (toggleRegeneration, packetBuffer) -> {
			 packetBuffer.writeBlockPos(toggleRegeneration.nestPos);
			 packetBuffer.writeBoolean(toggleRegeneration.state);
		 }, packetBuffer -> new ToggleRegeneration(packetBuffer.readBlockPos(), packetBuffer.readBoolean()), (toggleRegeneration, contextSupplier) -> {
			 ServerWorld serverWorld = contextSupplier.get().getSender().getLevel();
			 TileEntity tileEntity = serverWorld.getBlockEntity(toggleRegeneration.nestPos);
			 if (tileEntity instanceof NestEntity) {
				 ((NestEntity) tileEntity).regenerationMode = toggleRegeneration.state;
				 tileEntity.setChanged();
				 contextSupplier.get().setPacketHandled(true);
			 }
		 });
		
		 CHANNEL.registerMessage(nextPacketId++, DismantleNest.class, (dismantleNest, packetBuffer) -> {
			 packetBuffer.writeBlockPos(dismantleNest.nestPos);
		 }, packetBuffer -> new DismantleNest(packetBuffer.readBlockPos()), (dismantleNest, contextSupplier) -> {
			 ServerWorld serverWorld = contextSupplier.get().getSender().getLevel();
			 TileEntity tileEntity = serverWorld.getBlockEntity(dismantleNest.nestPos);
			 if (tileEntity instanceof NestEntity) {
				 serverWorld.destroyBlock(dismantleNest.nestPos, true);
				 contextSupplier.get().setPacketHandled(true);
			 }
		 });
		
		 CHANNEL.registerMessage(nextPacketId++, SleepInNest.class, (sleepInNest, packetBuffer) -> {
			 packetBuffer.writeBlockPos(sleepInNest.nestPos);
		 }, packetBuffer -> new SleepInNest(packetBuffer.readBlockPos()), (sleepInNest, contextSupplier) -> {
			 ServerPlayerEntity serverPlayerEntity = contextSupplier.get().getSender();
			 if (serverPlayerEntity.getLevel().isNight()) {
				 serverPlayerEntity.startSleepInBed(sleepInNest.nestPos);
				 serverPlayerEntity.setRespawnPosition(serverPlayerEntity.getLevel().dimension(), sleepInNest.nestPos, 0.0F, false, true); // Float is respawnAngle
				 // check these boolean values, might need to be switched.
			 }
			
		 });
		
		 CHANNEL.registerMessage(nextPacketId++, GiveNest.class, (giveNest, packetBuffer) -> {
			                                        packetBuffer.writeEnum(giveNest.dragonType);
		                                        },
		                                        packetBuffer -> new GiveNest(packetBuffer.readEnum(DragonType.class)), (giveNest, contextSupplier) -> {
					 ServerPlayerEntity playerEntity = contextSupplier.get().getSender();
					 Block item;
					 switch (giveNest.dragonType) {
						 case CAVE:
							 item = BlockInit.smallCaveNest;
							 break;
						 case FOREST:
							 item = BlockInit.smallForestNest;
							 break;
						 case SEA:
							 item = BlockInit.smallSeaNest;
							 break;
						 default:
							 item = null;
					 }
					 ItemStack itemStack = new ItemStack(item);
					 if (playerEntity.getOffhandItem().isEmpty()) {
						 playerEntity.setItemInHand(Hand.OFF_HAND, itemStack);
					 } else {
						 ItemStack stack = playerEntity.getOffhandItem().copy();
						 playerEntity.setItemInHand(Hand.OFF_HAND, itemStack);
						 if (!playerEntity.inventory.add(stack)) {
							 playerEntity.drop(stack, false, false);
						 }
					 }
				 });
		
		 CHANNEL.registerMessage(nextPacketId++, SetFlyState.class, (setFlyState, packetBuffer) -> {
	                packetBuffer.writeInt(setFlyState.playerid);
	                packetBuffer.writeBoolean(setFlyState.flying);
	            },
	            packetBuffer -> new SetFlyState(packetBuffer.readInt(), packetBuffer.readBoolean()),
	            (setFlyState, contextSupplier) -> {
	                if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
	                    ClientDragonRender.dragonsFlying.put(setFlyState.playerid, setFlyState.flying);
	
	                }
	                contextSupplier.get().setPacketHandled(true);
	            });
		
		 CHANNEL.registerMessage(nextPacketId++, DiggingStatus.class, (diggingStatus, packetBuffer) -> {
                packetBuffer.writeInt(diggingStatus.playerId);
                packetBuffer.writeBoolean(diggingStatus.status);
            },
            packetBuffer -> new DiggingStatus(packetBuffer.readInt(), packetBuffer.readBoolean()),
            (diggingStatus, contextSupplier) -> {
                if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                    Minecraft minecraft = Minecraft.getInstance();
                    Entity entity = minecraft.level.getEntity(diggingStatus.playerId);
                    if (entity instanceof PlayerEntity) {
                        ClientEvents.dragonsDigging.put(entity.getId(), diggingStatus.status);
                    }
                }
                contextSupplier.get().setPacketHandled(true);
            });
		
		 CHANNEL.registerMessage(nextPacketId++, StartJump.class, (startJump, packetBuffer) -> {
	            packetBuffer.writeInt(startJump.playerId);
	            packetBuffer.writeByte(startJump.ticks);
	        },
	        packetBuffer -> new StartJump(packetBuffer.readInt(), packetBuffer.readByte()),
	        (startJump, contextSupplier) -> {
	            if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
	                Entity entity = Minecraft.getInstance().level.getEntity(startJump.playerId);
	                if (entity instanceof PlayerEntity) {
	                    ClientEvents.dragonsJumpingTicks.put(entity.getId(), startJump.ticks);
	                }
	            }
	            //the spam source was in this handler
	            contextSupplier.get().setPacketHandled(true);
	        });
		
		 CHANNEL.registerMessage(nextPacketId++, RefreshDragons.class, (refreshDragons, packetBuffer) -> {
                    packetBuffer.writeInt(refreshDragons.playerId);
                },
                packetBuffer -> new RefreshDragons(packetBuffer.readInt()),
                (refreshDragons, contextSupplier) -> {
                    if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                        Thread thread = new Thread(() -> {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ClientPlayerEntity myPlayer = Minecraft.getInstance().player;
                            ClientDragonRender.dragonEntity = new AtomicReference<>(EntityTypesInit.DRAGON.create(myPlayer.level));
                            ClientDragonRender.dragonEntity.get().player = myPlayer.getId();
                            ClientDragonRender.dragonArmor = EntityTypesInit.DRAGON_ARMOR.create(myPlayer.level);
                            if (ClientDragonRender.dragonArmor != null)
                                ClientDragonRender.dragonArmor.player = myPlayer.getId();
                            PlayerEntity thatPlayer = (PlayerEntity) myPlayer.level.getEntity(refreshDragons.playerId);
                            if (thatPlayer != null) {
                                DragonEntity dragonEntity = EntityTypesInit.DRAGON.create(myPlayer.level);
                                dragonEntity.player = thatPlayer.getId();
                                ClientDragonRender.playerDragonHashMap.computeIfAbsent(thatPlayer.getId(), integer -> new AtomicReference<>(dragonEntity)).getAndSet(dragonEntity);
                                DragonEntity dragonArmor = EntityTypesInit.DRAGON_ARMOR.create(myPlayer.level);
                                dragonArmor.player = thatPlayer.getId();
                                ClientDragonRender.playerArmorMap.computeIfAbsent(thatPlayer.getId(), integer -> dragonArmor);
                            }
                        });
                        thread.start();
                    }
                    contextSupplier.get().setPacketHandled(true);
                });
		 
	 }
}
