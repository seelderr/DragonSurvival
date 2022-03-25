package by.dragonsurvivalteam.dragonsurvival.common.capability;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.EffectInstance2;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.GenericCapabilityProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.VillageRelationshipsProvider;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonModifiers;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.RequestClientData;
import by.dragonsurvivalteam.dragonsurvival.network.syncing.CompleteDataSync;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;

@EventBusSubscriber
public class Capabilities{

	public static void register(){
		CapabilityManager.INSTANCE.register(DragonStateHandler.class, new IStorage<DragonStateHandler>(){
			@Nullable
			@Override
			public INBT writeNBT(Capability<DragonStateHandler> capability, DragonStateHandler instance, Direction side){
				return instance.writeNBT();
			}

			@Override
			public void readNBT(Capability<DragonStateHandler> capability, DragonStateHandler instance, Direction side, INBT nbt){
				instance.readNBT((CompoundNBT)nbt);
			}
		}, DragonStateHandler::new);

		CapabilityManager.INSTANCE.register(VillageRelationShips.class, new IStorage<VillageRelationShips>(){
			@Nullable
			@Override
			public INBT writeNBT(Capability<VillageRelationShips> capability, VillageRelationShips instance, Direction side){
				return instance.writeNBT();
			}

			@Override
			public void readNBT(Capability<VillageRelationShips> capability, VillageRelationShips instance, Direction side, INBT nbt){
				instance.readNBT((CompoundNBT)nbt);
			}
		}, VillageRelationShips::new);

		CapabilityManager.INSTANCE.register(GenericCapability.class, new IStorage<GenericCapability>(){
			@Nullable
			@Override
			public INBT writeNBT(Capability<GenericCapability> capability, GenericCapability instance, Direction side){
				return instance.writeNBT();
			}

			@Override
			public void readNBT(Capability<GenericCapability> capability, GenericCapability instance, Direction side, INBT nbt){
				instance.readNBT((CompoundNBT)nbt);
			}
		}, GenericCapability::new);
	}

	@SubscribeEvent
	public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event){
		event.addCapability(new ResourceLocation("dragonsurvival", "generic_capability_data"), new GenericCapabilityProvider());

		if(event.getObject() instanceof PlayerEntity){
			event.addCapability(new ResourceLocation("dragonsurvival", "playerstatehandler"), new DragonStateProvider());
			event.addCapability(new ResourceLocation("dragonsurvival", "village_relations"), new VillageRelationshipsProvider());
			DragonSurvivalMod.LOGGER.info("Successfully attached capabilities to the " + event.getObject().getClass().getSimpleName());
		}
	}

	@SubscribeEvent
	public static void onLoggedIn(PlayerEvent.PlayerLoggedInEvent loggedInEvent){
		PlayerEntity player = loggedInEvent.getPlayer();
		if(!player.level.isClientSide){
			DragonStateProvider.getCap(player).ifPresent(cap -> NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new RequestClientData(cap.getType(), cap.getLevel())));
			syncCapability(player);
		}
	}

	public static void syncCapability(PlayerEntity player){
		NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new CompleteDataSync(player));
	}

	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent playerRespawnEvent){
		PlayerEntity player = playerRespawnEvent.getPlayer();
		if(!player.level.isClientSide){
			syncCapability(player);
		}
	}

	@SubscribeEvent
	public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event){
		PlayerEntity player = event.getPlayer();
		if(!player.level.isClientSide()){
			syncCapability(player);
		}
	}

	@SubscribeEvent
	public static void onTrackingStart(PlayerEvent.StartTracking startTracking){
		PlayerEntity trackingPlayer = startTracking.getPlayer();
		if(trackingPlayer instanceof ServerPlayerEntity){
			Entity trackedEntity = startTracking.getTarget();
			if(trackedEntity instanceof ServerPlayerEntity){
				DragonStateProvider.getCap(trackedEntity).ifPresent(dragonStateHandler -> NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)trackingPlayer), new CompleteDataSync(trackedEntity.getId(), dragonStateHandler.writeNBT())));
			}
		}
	}

	@SubscribeEvent
	public static void onClone(PlayerEvent.Clone e){
		PlayerEntity player = e.getPlayer();
		PlayerEntity original = e.getOriginal();
		if(!e.isWasDeath()){
			return;
		}

		DragonStateProvider.getCap(player).ifPresent(capNew -> DragonStateProvider.getCap(original).ifPresent(capOld -> {
			CompoundNBT nbt = capOld.writeNBT();
			capNew.readNBT(nbt);
			NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new CompleteDataSync(player.getId(), nbt));
		}));

		VillageRelationshipsProvider.getVillageRelationships(player).ifPresent(villageRelationShips -> {
			VillageRelationshipsProvider.getVillageRelationships(original).ifPresent(old -> {
				villageRelationShips.readNBT(old.writeNBT());
				if(ConfigHandler.COMMON.preserveEvilDragonEffectAfterDeath.get() && villageRelationShips.evilStatusDuration > 0){
					player.addEffect(new EffectInstance2(DragonEffects.EVIL_DRAGON, villageRelationShips.evilStatusDuration));
				}
			});
		});

		DragonModifiers.updateModifiers(original, player);
		player.refreshDimensions();
	}
}