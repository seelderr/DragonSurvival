package by.dragonsurvivalteam.dragonsurvival.network.container;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class RequestOpenDragonAltar implements IMessage<RequestOpenDragonAltar.Data> {

	public static void handleClient(final Data message, final IPayloadContext context) {
		context.enqueueWork(ClientProxy::handleOpenDragonAltar);
	}

	public record Data() implements CustomPacketPayload {

		public static final Type<RequestOpenDragonAltar.Data> TYPE = new Type<>(new ResourceLocation(MODID, "open_dragon_altar"));

		public static final StreamCodec<ByteBuf, RequestOpenDragonAltar.Data> STREAM_CODEC = new StreamCodec<>(){
			@Override
			public void encode(ByteBuf pBuffer, RequestOpenDragonAltar.Data pValue) {}

			@Override
			public RequestOpenDragonAltar.Data decode(ByteBuf pBuffer) { return new RequestOpenDragonAltar.Data(); }
		};

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}