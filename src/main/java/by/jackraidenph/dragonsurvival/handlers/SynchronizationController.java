package by.jackraidenph.dragonsurvival.handlers;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.capability.DragonStateHandler.DragonDebuffData;
import by.jackraidenph.dragonsurvival.capability.DragonStateHandler.DragonMovementData;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.PacketSyncCapabilityMovement;
import by.jackraidenph.dragonsurvival.network.SyncCapabilityAbility;
import by.jackraidenph.dragonsurvival.network.SyncCapabilityDebuff;
import by.jackraidenph.dragonsurvival.network.SynchronizeDragonCap;
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
    
    private static void syncWithPlayer(PlayerEntity player, ServerPlayerEntity serverPlayerEntity, DragonStateHandler dragonStateHandler)
    {
        DragonSurvivalMod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SynchronizeDragonCap(serverPlayerEntity.getId(), dragonStateHandler.isHiding(), dragonStateHandler.getType(), dragonStateHandler.getSize(), dragonStateHandler.hasWings(), dragonStateHandler.getLavaAirSupply(), 0));
        DragonSurvivalMod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketSyncCapabilityMovement(player.getId(), dragonStateHandler.getMovementData().bodyYaw, dragonStateHandler.getMovementData().headYaw, dragonStateHandler.getMovementData().headPitch, dragonStateHandler.getMovementData().bite));
        DragonSurvivalMod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncCapabilityDebuff(player.getId(), dragonStateHandler.getDebuffData().timeWithoutWater, dragonStateHandler.getDebuffData().timeInDarkness));
        DragonSurvivalMod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncCapabilityAbility(player.getId(), dragonStateHandler.getSelectedAbilitySlot(), dragonStateHandler.getMaxMana(), dragonStateHandler.getCurrentMana(), dragonStateHandler.getAbilities(), dragonStateHandler.renderAbilityHotbar()));
    }
    
    private static void syncToAll(PlayerEntity player)
    {
        DragonStateProvider.getCap(player).ifPresent(cap -> {
            DragonSurvivalMod.CHANNEL.send(PacketDistributor.ALL.noArg(), new SynchronizeDragonCap(player.getId(), cap.isHiding(), cap.getType(), cap.getSize(), cap.hasWings(), cap.getLavaAirSupply(), 0));
            DragonSurvivalMod.CHANNEL.send(PacketDistributor.ALL.noArg(), new PacketSyncCapabilityMovement(player.getId(), cap.getMovementData().bodyYaw, cap.getMovementData().headYaw, cap.getMovementData().headPitch, cap.getMovementData().bite));
            DragonSurvivalMod.CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncCapabilityDebuff(player.getId(), cap.getDebuffData().timeWithoutWater, cap.getDebuffData().timeInDarkness));
            DragonSurvivalMod.CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncCapabilityAbility(player.getId(), cap.getSelectedAbilitySlot(), cap.getMaxMana(), cap.getCurrentMana(), cap.getAbilities(), cap.renderAbilityHotbar()));
        });
    }
    
    private static void syncWithTracked(ServerPlayerEntity trackingPlayer, Entity trackedEntity, DragonStateHandler dragonStateHandler)
    {
        DragonMovementData mData = dragonStateHandler.getMovementData();
        DragonSurvivalMod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> trackingPlayer), new PacketSyncCapabilityMovement(trackedEntity.getId(), mData.bodyYaw, mData.headYaw, mData.headPitch, mData.bite));
        DragonDebuffData dData = dragonStateHandler.getDebuffData();
        DragonSurvivalMod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> trackingPlayer), new SyncCapabilityDebuff(trackedEntity.getId(), dData.timeWithoutWater, dData.timeInDarkness));
        DragonSurvivalMod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> trackingPlayer), new SyncCapabilityAbility(trackedEntity.getId(), dragonStateHandler.getSelectedAbilitySlot(), dragonStateHandler.getMaxMana(), dragonStateHandler.getCurrentMana(), dragonStateHandler.getAbilities(), dragonStateHandler.renderAbilityHotbar()));
    }
    
    /**
     * Synchronizes capability among players
     */
    @SubscribeEvent
    public static void onLoggedIn(PlayerEvent.PlayerLoggedInEvent loggedInEvent) {
        PlayerEntity player = loggedInEvent.getPlayer();
    
        if (!player.level.isClientSide) {
            // send the capability to everyone
            syncToAll(player);
            // receive capability from others
            loggedInEvent.getPlayer().getServer().getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
                DragonStateProvider.getCap(serverPlayerEntity).ifPresent(dragonStateHandler -> {
                    syncWithPlayer(player, serverPlayerEntity, dragonStateHandler);
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
            syncToAll(player);
            // receive capability from others
            playerRespawnEvent.getPlayer().getServer().getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
                DragonStateProvider.getCap(serverPlayerEntity).ifPresent(dragonStateHandler -> {
                    syncWithPlayer(player, serverPlayerEntity, dragonStateHandler);
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
                    DragonSurvivalMod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)trackingPlayer), new SynchronizeDragonCap(trackedEntity.getId(), dragonStateHandler.isHiding(), dragonStateHandler.getType(), dragonStateHandler.getSize(), dragonStateHandler.hasWings(), dragonStateHandler.getLavaAirSupply(), dragonStateHandler.getPassengerId()));
                    syncWithTracked((ServerPlayerEntity)trackingPlayer, trackedEntity, dragonStateHandler);
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
            DragonSurvivalMod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SynchronizeDragonCap(player.getId(), dragonStateHandler.isHiding(), dragonStateHandler.getType(), dragonStateHandler.getSize(), dragonStateHandler.hasWings(), dragonStateHandler.getLavaAirSupply(), 0));
            syncWithTracked((ServerPlayerEntity)player, player, dragonStateHandler);
        });
    }
}
