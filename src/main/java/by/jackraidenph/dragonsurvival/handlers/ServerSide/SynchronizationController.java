package by.jackraidenph.dragonsurvival.handlers.ServerSide;

import by.jackraidenph.dragonsurvival.capability.DragonStateHandler.DragonDebuffData;
import by.jackraidenph.dragonsurvival.capability.DragonStateHandler.DragonMovementData;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.PacketSyncCapabilityMovement;
import by.jackraidenph.dragonsurvival.network.SyncCapabilityDebuff;
import by.jackraidenph.dragonsurvival.network.SynchronizeDragonCap;
import by.jackraidenph.dragonsurvival.network.magic.SyncDragonClawsMenu;
import by.jackraidenph.dragonsurvival.network.magic.SyncMagicAbilities;
import by.jackraidenph.dragonsurvival.network.magic.SyncMagicStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber
@SuppressWarnings("unused")
public class SynchronizationController {
    /**
     * Synchronizes capability among players
     */
    @SubscribeEvent
    public static void onLoggedIn(PlayerEvent.PlayerLoggedInEvent loggedInEvent) {
        PlayerEntity player = loggedInEvent.getPlayer();
        if (!player.level.isClientSide) {
            // send the capability to everyone
            DragonStateProvider.getCap(player).ifPresent(cap -> {
                NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SynchronizeDragonCap(player.getId(), cap.isHiding(), cap.getType(), cap.getSize(), cap.hasWings(), cap.getLavaAirSupply(), 0));
                NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new PacketSyncCapabilityMovement(player.getId(), cap.getMovementData().bodyYaw, cap.getMovementData().headYaw, cap.getMovementData().headPitch, cap.getMovementData().bite));
                NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncCapabilityDebuff(player.getId(), cap.getDebuffData().timeWithoutWater, cap.getDebuffData().timeInDarkness, cap.getDebuffData().timeInRain));
                NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncDragonClawsMenu(player.getId(), cap.clawsMenuOpen, cap.clawsInventory));
    
            });
            // receive capability from others
            loggedInEvent.getPlayer().getServer().getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
                DragonStateProvider.getCap(serverPlayerEntity).ifPresent(dragonStateHandler -> {
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SynchronizeDragonCap(serverPlayerEntity.getId(), dragonStateHandler.isHiding(), dragonStateHandler.getType(), dragonStateHandler.getSize(), dragonStateHandler.hasWings(), dragonStateHandler.getLavaAirSupply(), 0));
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketSyncCapabilityMovement(player.getId(), dragonStateHandler.getMovementData().bodyYaw, dragonStateHandler.getMovementData().headYaw, dragonStateHandler.getMovementData().headPitch, dragonStateHandler.getMovementData().bite));
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncCapabilityDebuff(player.getId(), dragonStateHandler.getDebuffData().timeWithoutWater, dragonStateHandler.getDebuffData().timeInDarkness, dragonStateHandler.getDebuffData().timeInRain));
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncMagicStats(player.getId(), dragonStateHandler.getSelectedAbilitySlot(), dragonStateHandler.getCurrentMana(), dragonStateHandler.renderAbilityHotbar()));
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncMagicAbilities(player.getId(), dragonStateHandler.getAbilities()));
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncDragonClawsMenu(player.getId(), dragonStateHandler.clawsMenuOpen, dragonStateHandler.clawsInventory));
                });
            });
        }
    }
    
    /**
     * Synchronizes the capability after death
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent playerRespawnEvent) {
        PlayerEntity player = playerRespawnEvent.getPlayer();
        if (!player.level.isClientSide) {
            // send the capability to everyone
            DragonStateProvider.getCap(player).ifPresent(cap -> {
                NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SynchronizeDragonCap(player.getId(), cap.isHiding(), cap.getType(), cap.getSize(), cap.hasWings(), cap.getLavaAirSupply(), 0));
                NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new PacketSyncCapabilityMovement(player.getId(), cap.getMovementData().bodyYaw, cap.getMovementData().headYaw, cap.getMovementData().headPitch, cap.getMovementData().bite));
                NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncCapabilityDebuff(player.getId(), cap.getDebuffData().timeWithoutWater, cap.getDebuffData().timeInDarkness, cap.getDebuffData().timeInRain));
                NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncDragonClawsMenu(player.getId(), cap.clawsMenuOpen, cap.clawsInventory));
            });
            // receive capability from others
            playerRespawnEvent.getPlayer().getServer().getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
                DragonStateProvider.getCap(serverPlayerEntity).ifPresent(dragonStateHandler -> {
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SynchronizeDragonCap(serverPlayerEntity.getId(), dragonStateHandler.isHiding(), dragonStateHandler.getType(), dragonStateHandler.getSize(), dragonStateHandler.hasWings(), dragonStateHandler.getLavaAirSupply(), 0));
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketSyncCapabilityMovement(player.getId(), dragonStateHandler.getMovementData().bodyYaw, dragonStateHandler.getMovementData().headYaw, dragonStateHandler.getMovementData().headPitch, dragonStateHandler.getMovementData().bite));
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncCapabilityDebuff(player.getId(), dragonStateHandler.getDebuffData().timeWithoutWater, dragonStateHandler.getDebuffData().timeInDarkness, dragonStateHandler.getDebuffData().timeInRain));
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncMagicStats(player.getId(), dragonStateHandler.getSelectedAbilitySlot(), dragonStateHandler.getCurrentMana(), dragonStateHandler.renderAbilityHotbar()));
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncMagicAbilities(player.getId(), dragonStateHandler.getAbilities()));
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncDragonClawsMenu(player.getId(), dragonStateHandler.clawsMenuOpen, dragonStateHandler.clawsInventory));
    
                });
            });
        }
    }
    
    @SubscribeEvent
    public static void onTrackingStart(PlayerEvent.StartTracking startTracking) {
        PlayerEntity trackingPlayer = startTracking.getPlayer();
        if (trackingPlayer instanceof ServerPlayerEntity) {
            Entity trackedEntity = startTracking.getTarget();
            if (trackedEntity instanceof ServerPlayerEntity) {
                DragonStateProvider.getCap(trackedEntity).ifPresent(dragonStateHandler -> {
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)trackingPlayer), new SynchronizeDragonCap(trackedEntity.getId(), dragonStateHandler.isHiding(), dragonStateHandler.getType(), dragonStateHandler.getSize(), dragonStateHandler.hasWings(), dragonStateHandler.getLavaAirSupply(), dragonStateHandler.getPassengerId()));
                    DragonMovementData mData = dragonStateHandler.getMovementData();
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)trackingPlayer), new PacketSyncCapabilityMovement(trackedEntity.getId(), mData.bodyYaw, mData.headYaw, mData.headPitch, mData.bite));
                    DragonDebuffData dData = dragonStateHandler.getDebuffData();
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)trackingPlayer), new SyncCapabilityDebuff(trackedEntity.getId(), dData.timeWithoutWater, dData.timeInDarkness, dData.timeInRain));
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) trackingPlayer), new SyncMagicStats(trackingPlayer.getId(), dragonStateHandler.getSelectedAbilitySlot(), dragonStateHandler.getCurrentMana(), dragonStateHandler.renderAbilityHotbar()));
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) trackingPlayer), new SyncDragonClawsMenu(trackingPlayer.getId(), dragonStateHandler.clawsMenuOpen, dragonStateHandler.clawsInventory));
                });
            }
        }
    }
    
    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player.level.isClientSide())
            return;
        DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SynchronizeDragonCap(player.getId(), dragonStateHandler.isHiding(), dragonStateHandler.getType(), dragonStateHandler.getSize(), dragonStateHandler.hasWings(), dragonStateHandler.getLavaAirSupply(), 0));
            DragonMovementData mData = dragonStateHandler.getMovementData();
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new PacketSyncCapabilityMovement(player.getId(), mData.bodyYaw, mData.headYaw, mData.headPitch, mData.bite));
            DragonDebuffData dData = dragonStateHandler.getDebuffData();
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SyncCapabilityDebuff(player.getId(), dData.timeWithoutWater, dData.timeInDarkness, dData.timeInRain));
    
//            DragonSurvivalMod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncMagicStats(player.getId(), dragonStateHandler.getSelectedAbilitySlot(), dragonStateHandler.getCurrentMana(), dragonStateHandler.renderAbilityHotbar()));
//            DragonSurvivalMod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncMagicAbilities(player.getId(), dragonStateHandler.getAbilities()));
//            DragonSurvivalMod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncDragonClawsMenu(player.getId(), dragonStateHandler.clawsMenuOpen, dragonStateHandler.clawsInventory));
        });
    }
}
