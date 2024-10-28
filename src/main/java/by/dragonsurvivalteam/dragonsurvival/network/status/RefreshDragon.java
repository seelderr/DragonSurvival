package by.dragonsurvivalteam.dragonsurvival.network.status;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;
import static by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy.handleRefreshDragons;

public class RefreshDragon implements IMessage<RefreshDragon.Data> {
	public int playerId;

	public static void handleClient(final Data message, final IPayloadContext context) {
		context.enqueueWork(() -> handleRefreshDragons(message));
	}

	public record Data(int playerId) implements CustomPacketPayload {
		public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "refresh_dragon"));

		public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT,
				Data::playerId,
				Data::new
		);

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}