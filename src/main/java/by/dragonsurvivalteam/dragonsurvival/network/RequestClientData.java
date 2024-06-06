package by.dragonsurvivalteam.dragonsurvival.network;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonBodies;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RequestClientData implements IMessage<RequestClientData.Data> {
	public static void handleClient(final RequestClientData.Data message, final IPayloadContext context) {
		context.enqueueWork(() -> ClientProxy.handleRequestClientData(message));
	}

	public record Data(AbstractDragonType dragonType, AbstractDragonBody body, DragonLevel level) implements CustomPacketPayload {
		public static final Type<Data> TYPE = new Type<>(new ResourceLocation(MODID, "client_data"));

		public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = new StreamCodec<>() {
			@Override
			public Data decode(FriendlyByteBuf pBuffer) {
				String type = pBuffer.readUtf();
				String body = pBuffer.readUtf();
				return new Data(type.equals("none") ? null : DragonTypes.getStatic(type), DragonBodies.getStatic(body), pBuffer.readEnum(DragonLevel.class));
			}

			@Override
			public void encode(FriendlyByteBuf pBuffer, Data pValue) {
				pBuffer.writeUtf(pValue.dragonType != null ? pValue.dragonType.getTypeName() : "none");
				pBuffer.writeUtf(pValue.body != null ? pValue.body.getBodyName() : "central");
				pBuffer.writeEnum(pValue.level);
			}
		};

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}