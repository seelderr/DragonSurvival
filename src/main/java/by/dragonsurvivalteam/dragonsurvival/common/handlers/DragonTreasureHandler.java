package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.blocks.TreasureBlock;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.status.SyncTreasureRestStatus;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static by.dragonsurvivalteam.dragonsurvival.registry.DSAdvancementTriggers.SLEEP_ON_TREASURE;

/** See {@link by.dragonsurvivalteam.dragonsurvival.client.handlers.DragonTreasureHandler} for client-specific handling */
@EventBusSubscriber
public class DragonTreasureHandler {
    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        Player player = event.getEntity();

        if (DragonStateProvider.isDragon(player)) {
            DragonStateHandler handler = DragonStateProvider.getData(player);

            if (handler.treasureResting) {
                if (player.isCrouching() || !(player.getBlockStateOn().getBlock() instanceof TreasureBlock) || handler.getMovementData().bite) {
                    handler.treasureResting = false;
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncTreasureRestStatus.Data(player.getId(), false));
                    return;
                }

                handler.treasureSleepTimer++;

                if (ServerConfig.treasureHealthRegen) {
                    int horizontalRange = 16;
                    int verticalRange = 9;
                    int treasureNearby = 0;

                    for (int x = -(horizontalRange / 2); x < horizontalRange / 2; x++) {
                        for (int y = -(verticalRange / 2); y < verticalRange / 2; y++) {
                            for (int z = -(horizontalRange / 2); z < horizontalRange / 2; z++) {
                                BlockPos pos = player.blockPosition().offset(x, y, z);
                                BlockState state = player.level().getBlockState(pos);

                                if (state.getBlock() instanceof TreasureBlock) {
                                    int layers = state.getValue(TreasureBlock.LAYERS);
                                    treasureNearby += layers;
                                }
                            }
                        }
                    }
                    treasureNearby = Mth.clamp(treasureNearby, 0, ServerConfig.maxTreasures);
                    SLEEP_ON_TREASURE.get().trigger((ServerPlayer) event.getEntity(), treasureNearby);

                    int totalTime = ServerConfig.treasureRegenTicks;
                    int restTimer = totalTime - ServerConfig.treasureRegenTicksReduce * treasureNearby;

                    if (handler.treasureRestTimer >= restTimer) {
                        handler.treasureRestTimer = 0;

                        if (player.getHealth() < player.getMaxHealth() + 1) {
                            player.heal(1);
                        }
                    } else {
                        handler.treasureRestTimer++;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerAttacked(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity instanceof Player player) {

            if (!player.level().isClientSide()) {
                DragonStateProvider.getOptional(player).ifPresent(cap -> {
                    if (cap.treasureResting) {
                        cap.treasureResting = false;
                        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncTreasureRestStatus.Data(player.getId(), false));
                    }
                });
            }
        }
    }
}