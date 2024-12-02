package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.status.SyncDiggingStatus;
import by.dragonsurvivalteam.dragonsurvival.network.status.SyncTreasureRestStatus;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MovementData;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.TreasureRestData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class MiningTickHandler {
    @SubscribeEvent
    public static void updateMiningTick(PlayerTickEvent.Post playerTickEvent) {
        Player player = playerTickEvent.getEntity();
        DragonStateHandler dragonStateHandler = DragonStateProvider.getData(player);
        if (dragonStateHandler.isDragon()) {
            if (player instanceof ServerPlayer) {
                ServerPlayerGameMode interactionManager = ((ServerPlayer) player).gameMode;
                boolean isMining = interactionManager.isDestroyingBlock && player.swinging;

                MovementData movement = MovementData.getData(player);
                if (isMining != movement.dig) {
                    movement.dig = isMining;
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncDiggingStatus.Data(player.getId(), isMining));
                }

                TreasureRestData treasureRest = TreasureRestData.getData(player);
                if (treasureRest.isResting && isMining) {
                    treasureRest.isResting = false;
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncTreasureRestStatus.Data(player.getId(), false));
                }

                if (dragonStateHandler.getMagicData().onMagicSource && isMining) {
                    SourceOfMagicHandler.cancelSourceOfMagicServer(player);
                }
            }
        }
    }
}