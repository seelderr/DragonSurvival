package by.dragonsurvivalteam.dragonsurvival.network.container;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RequestOpenDragonEditor implements IMessage<RequestOpenDragonEditor.Data> {
	public static void handleClient(final RequestOpenDragonEditor.Data message, final IPayloadContext context) {
		context.enqueueWork(ClientProxy::handleOpenDragonEditorPacket);
	}

	public record Data() implements CustomPacketPayload {

		public static final Type<RequestOpenDragonEditor.Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "open_dragon_editor"));

		public static final StreamCodec<ByteBuf, RequestOpenDragonEditor.Data> STREAM_CODEC = new StreamCodec<>(){
			@Override
			public void encode(ByteBuf pBuffer, RequestOpenDragonEditor.Data pValue) {}

			@Override
			public RequestOpenDragonEditor.Data decode(ByteBuf pBuffer) { return new RequestOpenDragonEditor.Data(); }
		};

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}