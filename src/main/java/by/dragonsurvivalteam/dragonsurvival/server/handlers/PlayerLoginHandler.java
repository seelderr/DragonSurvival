package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.RequestClientData;
import by.dragonsurvivalteam.dragonsurvival.network.container.AllowOpenDragonAltar;
import by.dragonsurvivalteam.dragonsurvival.network.syncing.SyncComplete;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class PlayerLoginHandler {

    public static void syncComplete(Entity tracker, Entity tracked) {
        if(tracker instanceof ServerPlayer){
            if(tracked instanceof ServerPlayer){
                DragonStateProvider.getCap(tracked).ifPresent(dragonStateHandler -> {
                    PacketDistributor.sendToPlayer((ServerPlayer)tracker, new SyncComplete.Data(tracked.getId(), dragonStateHandler.serializeNBT(tracked.registryAccess())));
                });
            }
        }
    }

    public static void syncComplete(Entity entity) {
        if(entity instanceof ServerPlayer player) {
            DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
                PacketDistributor.sendToPlayer(player, new SyncComplete.Data(player.getId(), dragonStateHandler.serializeNBT(player.registryAccess())));
            });
        }
    }

    @SubscribeEvent
    public static void onTrackingStart(PlayerEvent.StartTracking startTracking){
        Entity tracker = startTracking.getEntity();
        Entity tracked = startTracking.getTarget();
        syncComplete(tracker, tracked);
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event){
        syncComplete(event.getEntity());
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event){
        syncComplete(event.getEntity());
    }

    @SubscribeEvent
    public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event){
        syncComplete(event.getEntity());
    }

    // When we duplicate the player, we need to transfer over the dragon state. We don't sync here though, since the player is not yet added to the world (the entity ID wouldn't be valid).
    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if(event.getOriginal() instanceof ServerPlayer original && event.getEntity() instanceof ServerPlayer player){
            DragonStateProvider.getCap(original).ifPresent(originalCap -> {
                DragonStateHandler newCap = DragonStateProvider.getOrGenerateHandler(player);
                CompoundTag nbt = originalCap.serializeNBT(player.registryAccess());
                newCap.deserializeNBT(player.registryAccess(), nbt);
                player.refreshDimensions();
            });
        }
    }

    @SubscribeEvent
    public static void startWithDragonChoice(PlayerTickEvent.Post event){
        if(!ServerConfig.startWithDragonChoice) return;
        if(event.getEntity().level().isClientSide()) return;

        if(event.getEntity() instanceof ServerPlayer player){
            if(player.isDeadOrDying()) return;

            if(player.tickCount > 5 * 20){
                DragonStateProvider.getCap(player).ifPresent(cap -> {
                    if(!cap.hasUsedAltar && !DragonStateProvider.isDragon(player)){
                        PacketDistributor.sendToPlayer(player, new AllowOpenDragonAltar.Data());
                        cap.hasUsedAltar = true;
                    }

                    if(cap.altarCooldown > 0){
                        cap.altarCooldown--;
                    }
                });
            }
        }
    }
}
