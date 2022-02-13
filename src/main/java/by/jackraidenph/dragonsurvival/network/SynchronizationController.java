package by.jackraidenph.dragonsurvival.network;

import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationLayer;
import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationRegistry;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import by.jackraidenph.dragonsurvival.network.SkinCustomization.SyncPlayerAllCustomization;
import by.jackraidenph.dragonsurvival.network.claw.SyncDragonClawRender;
import by.jackraidenph.dragonsurvival.network.claw.SyncDragonClawsMenu;
import by.jackraidenph.dragonsurvival.network.container.OpenDragonAltar;
import by.jackraidenph.dragonsurvival.network.emotes.SyncEmote;
import by.jackraidenph.dragonsurvival.network.emotes.SyncEmoteStats;
import by.jackraidenph.dragonsurvival.network.entity.player.*;
import by.jackraidenph.dragonsurvival.network.flight.SyncFlyingStatus;
import by.jackraidenph.dragonsurvival.network.flight.SyncSpinStatus;
import by.jackraidenph.dragonsurvival.network.magic.SyncMagicAbilities;
import by.jackraidenph.dragonsurvival.network.magic.SyncMagicStats;
import by.jackraidenph.dragonsurvival.network.status.DiggingStatus;
import by.jackraidenph.dragonsurvival.network.status.SyncMagicSourceStatus;
import by.jackraidenph.dragonsurvival.network.status.SyncTreasureRestStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;

@Mod.EventBusSubscriber
public class SynchronizationController {
    @OnlyIn(Dist.CLIENT)
    public static void sendClientData(RequestClientData message){
        PlayerEntity player = Minecraft.getInstance().player;
        
        NetworkHandler.CHANNEL.sendToServer(new SyncDragonClawRender(player.getId(), ConfigHandler.CLIENT.renderDragonClaws.get()));
        NetworkHandler.CHANNEL.sendToServer(new SyncDragonSkinSettings(player.getId(), ConfigHandler.CLIENT.renderNewbornSkin.get(), ConfigHandler.CLIENT.renderYoungSkin.get(), ConfigHandler.CLIENT.renderAdultSkin.get()));
    
        DragonStateProvider.getCap(player).ifPresent(cap -> {
            cap.getMagic().getAbilities();
        
            if(CustomizationRegistry.savedCustomizations != null){
                int currentSelected = CustomizationRegistry.savedCustomizations.current.getOrDefault(message.type, new HashMap<>()).getOrDefault(message.level, 0);
                HashMap<DragonLevel, HashMap<CustomizationLayer, String>> map = CustomizationRegistry.savedCustomizations.saved.getOrDefault(message.type, new HashMap<>()).getOrDefault(currentSelected, new HashMap<>());
                NetworkHandler.CHANNEL.sendToServer(new SyncPlayerAllCustomization(player.getId(), map));
            }
        });
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLoggedIn(PlayerEvent.PlayerLoggedInEvent loggedInEvent) {
        PlayerEntity player = loggedInEvent.getPlayer();
        if (!player.level.isClientSide) {
            DragonStateProvider.getCap(player).ifPresent(cap -> {
                cap.hasUsedAltar = cap.hasUsedAltar || cap.isDragon();
                
                if(!cap.hasUsedAltar && ConfigHandler.COMMON.startWithDragonChoice.get()){
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new OpenDragonAltar());
                    cap.hasUsedAltar = true;
                }
                
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new RequestClientData(cap));
    
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SynchronizeDragonCap(player.getId(), cap.isHiding(), cap.getType(), cap.getSize(), cap.hasWings(), cap.getLavaAirSupply(), 0));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketSyncCapabilityMovement(player.getId(), cap.getMovementData().bodyYaw, cap.getMovementData().headYaw, cap.getMovementData().headPitch, cap.getMovementData().bite));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncCapabilityDebuff(player.getId(), cap.getDebuffData().timeWithoutWater, cap.getDebuffData().timeInDarkness, cap.getDebuffData().timeInRain));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncFlyingStatus(player.getId(), cap.isWingsSpread()));
    
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncDragonClawsMenu(player.getId(), cap.getClawInventory().isClawsMenuOpen(), cap.getClawInventory().getClawsInventory()));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncEmoteStats(player.getId(), cap.getEmotes().emoteMenuOpen));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncMagicStats(player.getId(), cap.getMagic().getSelectedAbilitySlot(), cap.getMagic().getCurrentMana(), cap.getMagic().renderAbilityHotbar()));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncMagicAbilities(player.getId(), cap.getMagic().getAbilities()));
    
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncDragonClawRender(player.getId(), cap.getClawInventory().renderClaws));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncPlayerAllCustomization(player.getId(), cap.getSkin().playerSkinLayers));
    
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncTreasureRestStatus(player.getId(), cap.treasureResting));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncMagicSourceStatus(player.getId(), cap.getMagic().onMagicSource, cap.getMagic().magicSourceTimer));
    
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncGrowthState(cap.growing));
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
    
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncPlayerAllCustomization(player.getId(), cap.getSkin().playerSkinLayers));
    
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncTreasureRestStatus(player.getId(), false));
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncMagicSourceStatus(player.getId(), false, 0));
    
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
                    
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) trackingPlayer), new SyncPlayerAllCustomization(trackedEntity.getId(), dragonStateHandler.getSkin().playerSkinLayers));
    
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) trackingPlayer), new SyncDragonSkinSettings(trackedEntity.getId(), dragonStateHandler.getSkin().renderNewborn, dragonStateHandler.getSkin().renderYoung, dragonStateHandler.getSkin().renderAdult));
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() ->  (ServerPlayerEntity) trackingPlayer), new SyncSpinStatus(trackedEntity.getId(), dragonStateHandler.getMovementData().spinAttack, dragonStateHandler.getMovementData().spinCooldown, dragonStateHandler.getMovementData().spinLearned));
    
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) trackingPlayer), new SyncTreasureRestStatus(trackedEntity.getId(), dragonStateHandler.treasureResting));
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) trackingPlayer), new SyncMagicSourceStatus(trackedEntity.getId(), dragonStateHandler.getMagic().onMagicSource, dragonStateHandler.getMagic().magicSourceTimer));
    
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
    
        
        DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SynchronizeDragonCap(player.getId(), dragonStateHandler.isHiding(), dragonStateHandler.getType(), dragonStateHandler.getSize(), dragonStateHandler.hasWings(), dragonStateHandler.getLavaAirSupply(), 0));
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new PacketSyncCapabilityMovement(player.getId(), dragonStateHandler.getMovementData().bodyYaw, dragonStateHandler.getMovementData().headYaw, dragonStateHandler.getMovementData().headPitch, dragonStateHandler.getMovementData().bite));
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncCapabilityDebuff(player.getId(), dragonStateHandler.getDebuffData().timeWithoutWater, dragonStateHandler.getDebuffData().timeInDarkness, dragonStateHandler.getDebuffData().timeInRain));
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncFlyingStatus(player.getId(), dragonStateHandler.isWingsSpread()));
    
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncMagicStats(player.getId(), dragonStateHandler.getMagic().getSelectedAbilitySlot(), dragonStateHandler.getMagic().getCurrentMana(), dragonStateHandler.getMagic().renderAbilityHotbar()));
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncMagicAbilities(player.getId(), dragonStateHandler.getMagic().getAbilities()));
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncDragonClawsMenu(player.getId(), dragonStateHandler.getClawInventory().isClawsMenuOpen(), dragonStateHandler.getClawInventory().getClawsInventory()));
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncEmoteStats(player.getId(), dragonStateHandler.getEmotes().emoteMenuOpen));
    
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncPlayerAllCustomization(player.getId(), dragonStateHandler.getSkin().playerSkinLayers));
    
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncTreasureRestStatus(player.getId(), dragonStateHandler.treasureResting));
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncMagicSourceStatus(player.getId(), dragonStateHandler.getMagic().onMagicSource, dragonStateHandler.getMagic().magicSourceTimer));
    
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
