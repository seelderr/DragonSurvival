package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.container.OpenDragonAltar;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncMagicData;
import by.dragonsurvivalteam.dragonsurvival.network.syncing.SyncComplete;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.AltarData;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MagicData;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.body.DragonBody;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class PlayerLoginHandler {

    public static void syncCompleteSingle(Entity tracker, Entity tracked) {
        if (tracker instanceof ServerPlayer) {
            if (tracked instanceof ServerPlayer) {
                DragonStateProvider.getOptional(tracked).ifPresent(dragonStateHandler -> {
                    PacketDistributor.sendToPlayer((ServerPlayer) tracker, new SyncComplete.Data(tracked.getId(), dragonStateHandler.serializeNBT(tracked.registryAccess())));
                });
            }
        }
    }

    public static void syncCompleteSingle(Entity entity) {
        if(entity instanceof ServerPlayer player) {
            DragonStateProvider.getOptional(player).ifPresent(handler -> {
                if (handler.getType() != null && handler.getBody() == null) {
                    // Otherwise players won't be able to join the world
                    handler.setBody(DragonBody.random(entity.registryAccess()));
                    DragonSurvival.LOGGER.error("Player {} was a dragon but had an invalid dragon body type", player);
                }

                SyncComplete.handleDragonSync(player);
                PacketDistributor.sendToPlayer(player, new SyncComplete.Data(player.getId(), handler.serializeNBT(player.registryAccess())));
            });
        }
    }

    public static void syncCompleteAll(Entity entity) {
        if (entity instanceof ServerPlayer player) {
            DragonStateProvider.getOptional(player).ifPresent(dragonStateHandler -> {
                SyncComplete.handleDragonSync(player);
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncComplete.Data(player.getId(), dragonStateHandler.serializeNBT(player.registryAccess())));
            });
        }
    }


    @SubscribeEvent
    public static void onTrackingStart(PlayerEvent.StartTracking startTracking) {
        Entity tracker = startTracking.getEntity();
        Entity tracked = startTracking.getTarget();
        syncCompleteSingle(tracker, tracked);
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        syncCompleteSingle(event.getEntity());
        // Also sync the magic data, since each player needs to know about their own magic data
        if(!event.getEntity().level().isClientSide()) {
            PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new SyncMagicData.Data(event.getEntity().getId(), MagicData.getData(event.getEntity()).serializeNBT(event.getEntity().registryAccess())));
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            DragonStateProvider.getOptional(player).ifPresent(dragonStateHandler -> {
                // FIXME
                /*AbstractDragonType type = dragonStateHandler.getType();

                if (type instanceof CaveDragonType cave) {
                    cave.rainResistanceSupply = CaveDragonType.getMaxRainResistanceSupply(player);
                    cave.lavaAirSupply = CaveDragonConfig.caveLavaSwimmingTicks;
                }

                if (type instanceof SeaDragonType sea) {
                    sea.timeWithoutWater = 0;
                }

                if (type instanceof ForestDragonType forest) {
                    forest.timeInDarkness = 0;
                }*/

                SyncComplete.handleDragonSync(player);
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncComplete.Data(player.getId(), dragonStateHandler.serializeNBT(player.registryAccess())));
            });
        }
        syncCompleteSingle(event.getEntity());
    }

    @SubscribeEvent
    public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        syncCompleteSingle(event.getEntity());
    }

    @SubscribeEvent
    public static void startWithDragonChoice(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer) || serverPlayer.isDeadOrDying()) {
            return;
        }

        AltarData data = AltarData.getData(serverPlayer);

        if (data.altarCooldown > 0) {
            data.altarCooldown--;
        }

        if (!ServerConfig.startWithDragonChoice || data.hasUsedAltar || data.isInAltar || serverPlayer.tickCount < Functions.secondsToTicks(5)) {
            return;
        }

        PacketDistributor.sendToPlayer(serverPlayer, OpenDragonAltar.INSTANCE);
        data.isInAltar = true;
    }
}
