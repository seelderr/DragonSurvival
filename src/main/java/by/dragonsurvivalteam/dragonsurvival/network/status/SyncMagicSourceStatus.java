package by.dragonsurvivalteam.dragonsurvival.network.status;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class SyncMagicSourceStatus implements IMessage<SyncMagicSourceStatus.Data> {
    // FIXME
    public static void handleClient(Data message, IPayloadContext context) {
        //context.enqueueWork(() -> ClientProxy.handleSyncMagicSourceStatus(message));
    }

    public static void handleServer(Data message, IPayloadContext context) {
        /*context.enqueueWork(() -> DragonStateProvider.getOptional(context.player()).ifPresent(cap -> {
            cap.getMagicData().onMagicSource = message.state;
            cap.getMagicData().magicSourceTimer = message.timer;
        })).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(context.player(), message));*/
    }

    public record Data(int playerId, boolean state, int timer) implements CustomPacketPayload {
        public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "magic_source_status"));

        public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                Data::playerId,
                ByteBufCodecs.BOOL,
                Data::state,
                ByteBufCodecs.VAR_INT,
                Data::timer,
                Data::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}