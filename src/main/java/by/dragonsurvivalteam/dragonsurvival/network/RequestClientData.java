package by.dragonsurvivalteam.dragonsurvival.network;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record RequestClientData() implements CustomPacketPayload {
    public static final RequestClientData INSTANCE = new RequestClientData();
    public static final StreamCodec<ByteBuf, RequestClientData> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final CustomPacketPayload.Type<RequestClientData> TYPE = new CustomPacketPayload.Type<>(DragonSurvival.res("request_client_data"));

    public static void handleClient(final RequestClientData ignored, final IPayloadContext context) {
        context.enqueueWork(() -> ClientProxy.sendClientData(context));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}