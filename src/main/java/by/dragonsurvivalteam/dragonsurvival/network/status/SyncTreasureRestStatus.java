package by.dragonsurvivalteam.dragonsurvival.network.status;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.TreasureRestData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class SyncTreasureRestStatus implements IMessage<SyncTreasureRestStatus.Data> {
    public static void handleClient(Data message, IPayloadContext context) {
        context.enqueueWork(() -> ClientProxy.handleSyncTreasureRestStatus(message));
    }

    public static void handleServer(Data message, IPayloadContext context) {
        context.enqueueWork(() -> {
            TreasureRestData data = TreasureRestData.getData(context.player());
            if(DragonStateProvider.isDragon(context.player())) {
                boolean update = false;

                if (message.state() != data.isResting) {
                    data.restingTicks = 0;
                    data.sleepingTicks = 0;
                    update = true;
                }

                data.isResting = message.state();

                if (update) {
                    ((ServerLevel) context.player().level()).updateSleepingPlayerList();
                }
            }
        }).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(context.player(), message));
    }

    public record Data(int playerId, boolean state) implements CustomPacketPayload {
        public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "treasure_rest_status"));

        public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                Data::playerId,
                ByteBufCodecs.BOOL,
                Data::state,
                Data::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}