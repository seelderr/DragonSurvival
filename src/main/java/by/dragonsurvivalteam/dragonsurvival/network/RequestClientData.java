package by.dragonsurvivalteam.dragonsurvival.network;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RequestClientData implements IMessage<RequestClientData.Data> {
	public static void handleClient(final RequestClientData.Data message, final IPayloadContext context) {
		context.enqueueWork(() -> {
			ClientProxy.sendClientData(context);
		});
	}

	public record Data() implements CustomPacketPayload {

		public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "request_client_data"));

		public static final StreamCodec<ByteBuf, Data> STREAM_CODEC = new StreamCodec<>() {
			@Override
			public void encode(ByteBuf pBuffer, Data pValue) {
			}

			@Override
			public Data decode(ByteBuf pBuffer) {
				return new Data();
			}
		};

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}