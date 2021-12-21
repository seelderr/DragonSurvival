package by.jackraidenph.dragonsurvival.handlers.ServerSide;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.*;
import by.jackraidenph.dragonsurvival.network.status.DiggingStatus;
import by.jackraidenph.dragonsurvival.network.claw.SyncDragonClawRender;
import by.jackraidenph.dragonsurvival.network.claw.SyncDragonClawsMenu;
import by.jackraidenph.dragonsurvival.network.emotes.SyncEmote;
import by.jackraidenph.dragonsurvival.network.emotes.SyncEmoteStats;
import by.jackraidenph.dragonsurvival.network.magic.SyncMagicAbilities;
import by.jackraidenph.dragonsurvival.network.magic.SyncMagicStats;
import by.jackraidenph.dragonsurvival.network.status.SyncFlyingStatus;
import by.jackraidenph.dragonsurvival.network.status.SyncSpinStatus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;

@Mod.EventBusSubscriber
@SuppressWarnings("unused")
public class SynchronizationController {
    /**
     * Synchronizes capability among players
     */
    
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinWorldEvent event){
        Entity ent = event.getEntity();
        
        if(ent instanceof PlayerEntity){
            PlayerEntity player = (PlayerEntity)ent;
            
            if(player.level.isClientSide){
                NetworkHandler.CHANNEL.sendToServer(new SyncDragonClawRender(player.getId(), ConfigHandler.CLIENT.renderDragonClaws.get()));
                NetworkHandler.CHANNEL.sendToServer(new SyncDragonSkinSettings(player.getId(), ConfigHandler.CLIENT.renderNewbornSkin.get(), ConfigHandler.CLIENT.renderYoungSkin.get(), ConfigHandler.CLIENT.renderAdultSkin.get()));
            }
        }
    }
    
    @SubscribeEvent
    public static void onLoggedIn(PlayerEvent.PlayerLoggedInEvent loggedInEvent) {
        PlayerEntity player = loggedInEvent.getPlayer();
        if (!player.level.isClientSide) {
            DragonStateProvider.getCap(player).ifPresent(cap -> {
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SynchronizeDragonCap(player.getId(), cap.isHiding(), cap.getType(), cap.getSize(), cap.hasWings(), cap.getLavaAirSupply(), 0));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketSyncCapabilityMovement(player.getId(), cap.getMovementData().bodyYaw, cap.getMovementData().headYaw, cap.getMovementData().headPitch, cap.getMovementData().bite));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncCapabilityDebuff(player.getId(), cap.getDebuffData().timeWithoutWater, cap.getDebuffData().timeInDarkness, cap.getDebuffData().timeInRain));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SyncFlyingStatus(player.getId(), cap.isWingsSpread()));
    
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncDragonClawsMenu(player.getId(), cap.getClawInventory().isClawsMenuOpen(), cap.getClawInventory().getClawsInventory()));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncEmoteStats(player.getId(), cap.getEmotes().emoteMenuOpen));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncMagicStats(player.getId(), cap.getMagic().getSelectedAbilitySlot(), cap.getMagic().getCurrentMana(), cap.getMagic().renderAbilityHotbar()));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncMagicAbilities(player.getId(), cap.getMagic().getAbilities()));
    
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncDragonClawRender(player.getId(), cap.getClawInventory().renderClaws));
    
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SyncGrowthState(cap.growing));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() ->  (ServerPlayerEntity) player), new SyncSpinStatus(player.getId(), cap.getMovementData().spinAttack, cap.getMovementData().spinCooldown, cap.getMovementData().spinLearned));
    
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
            DragonStateProvider.getCap(player).ifPresent(cap -> {
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SynchronizeDragonCap(player.getId(), cap.isHiding(), cap.getType(), cap.getSize(), cap.hasWings(), cap.getLavaAirSupply(), 0));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketSyncCapabilityMovement(player.getId(), cap.getMovementData().bodyYaw, cap.getMovementData().headYaw, cap.getMovementData().headPitch, cap.getMovementData().bite));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncCapabilityDebuff(player.getId(), cap.getDebuffData().timeWithoutWater, cap.getDebuffData().timeInDarkness, cap.getDebuffData().timeInRain));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SyncFlyingStatus(player.getId(), cap.isWingsSpread()));

                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncDragonClawsMenu(player.getId(), cap.getClawInventory().isClawsMenuOpen(), cap.getClawInventory().getClawsInventory()));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncEmoteStats(player.getId(), cap.getEmotes().emoteMenuOpen));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncMagicStats(player.getId(), cap.getMagic().getSelectedAbilitySlot(), cap.getMagic().getCurrentMana(), cap.getMagic().renderAbilityHotbar()));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncMagicAbilities(player.getId(), cap.getMagic().getAbilities()));
    
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncDragonClawRender(player.getId(), cap.getClawInventory().renderClaws));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncDragonSkinSettings(player.getId(), cap.getSkin().renderNewborn, cap.getSkin().renderYoung, cap.getSkin().renderAdult));
    
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SyncGrowthState(cap.growing));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() ->  (ServerPlayerEntity) player), new SyncSpinStatus(player.getId(), 0, cap.getMovementData().spinCooldown, cap.getMovementData().spinLearned));
    
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
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)trackingPlayer), new PacketSyncCapabilityMovement(trackedEntity.getId(), dragonStateHandler.getMovementData().bodyYaw, dragonStateHandler.getMovementData().headYaw, dragonStateHandler.getMovementData().headPitch, dragonStateHandler.getMovementData().bite));
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)trackingPlayer), new SyncCapabilityDebuff(trackedEntity.getId(), dragonStateHandler.getDebuffData().timeWithoutWater, dragonStateHandler.getDebuffData().timeInDarkness, dragonStateHandler.getDebuffData().timeInRain));
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)trackingPlayer), new SyncFlyingStatus(trackedEntity.getId(), dragonStateHandler.isWingsSpread()));
    
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) trackingPlayer), new DiggingStatus(trackedEntity.getId(), dragonStateHandler.getMovementData().dig));
                    
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) trackingPlayer), new SyncMagicAbilities(trackedEntity.getId(), dragonStateHandler.getMagic().getAbilities()));
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) trackingPlayer), new SyncDragonClawsMenu(trackedEntity.getId(), dragonStateHandler.getClawInventory().isClawsMenuOpen(), dragonStateHandler.getClawInventory().getClawsInventory()));
    
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) trackingPlayer), new SyncDragonSkinSettings(trackedEntity.getId(), dragonStateHandler.getSkin().renderNewborn, dragonStateHandler.getSkin().renderYoung, dragonStateHandler.getSkin().renderAdult));
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() ->  (ServerPlayerEntity) trackingPlayer), new SyncSpinStatus(trackingPlayer.getId(), dragonStateHandler.getMovementData().spinAttack, dragonStateHandler.getMovementData().spinCooldown, dragonStateHandler.getMovementData().spinLearned));
    
                    if(ConfigHandler.SERVER.syncClawRender.get()) {
                        NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)trackingPlayer), new SyncDragonClawRender(trackedEntity.getId(), dragonStateHandler.getClawInventory().renderClaws));
                    }
                    
                    if(dragonStateHandler.getEmotes().serverEmote != null) {
                        NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)trackingPlayer), new SyncEmote(trackedEntity.getId(), dragonStateHandler.getEmotes().serverEmote, trackedEntity.tickCount - dragonStateHandler.getEmotes().serverTick));
                    }
                });
            }
        }
    }
    
    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player.level.isClientSide())
            return;
    
        
        List<PlayerEntity> players = player.level.getNearbyPlayers(EntityPredicate.DEFAULT, player, new AxisAlignedBB(player.position().subtract(32, 32, 32), player.position().add(32, 32, 32)));
        
        for(PlayerEntity playerEntity : players){
            DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncEmoteStats(player.getId(), dragonStateHandler.getEmotes().emoteMenuOpen));
            });
        }
        
        DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SynchronizeDragonCap(player.getId(), dragonStateHandler.isHiding(), dragonStateHandler.getType(), dragonStateHandler.getSize(), dragonStateHandler.hasWings(), dragonStateHandler.getLavaAirSupply(), 0));
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new PacketSyncCapabilityMovement(player.getId(), dragonStateHandler.getMovementData().bodyYaw, dragonStateHandler.getMovementData().headYaw, dragonStateHandler.getMovementData().headPitch, dragonStateHandler.getMovementData().bite));
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncCapabilityDebuff(player.getId(), dragonStateHandler.getDebuffData().timeWithoutWater, dragonStateHandler.getDebuffData().timeInDarkness, dragonStateHandler.getDebuffData().timeInRain));
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncFlyingStatus(player.getId(), dragonStateHandler.isWingsSpread()));
    
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncMagicStats(player.getId(), dragonStateHandler.getMagic().getSelectedAbilitySlot(), dragonStateHandler.getMagic().getCurrentMana(), dragonStateHandler.getMagic().renderAbilityHotbar()));
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncMagicAbilities(player.getId(), dragonStateHandler.getMagic().getAbilities()));
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncDragonClawsMenu(player.getId(), dragonStateHandler.getClawInventory().isClawsMenuOpen(), dragonStateHandler.getClawInventory().getClawsInventory()));
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncEmoteStats(player.getId(), dragonStateHandler.getEmotes().emoteMenuOpen));
    
            if(dragonStateHandler.getEmotes().serverEmote != null) {
                NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncEmote(player.getId(), dragonStateHandler.getEmotes().serverEmote, player.tickCount - dragonStateHandler.getEmotes().serverTick));
            }
    
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncDragonClawRender(player.getId(), dragonStateHandler.getClawInventory().renderClaws));
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncDragonSkinSettings(player.getId(), dragonStateHandler.getSkin().renderNewborn, dragonStateHandler.getSkin().renderYoung, dragonStateHandler.getSkin().renderAdult));
    
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncSpinStatus(player.getId(), dragonStateHandler.getMovementData().spinAttack, dragonStateHandler.getMovementData().spinCooldown, dragonStateHandler.getMovementData().spinLearned));
    
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncGrowthState(dragonStateHandler.growing));
        });
    }
}
