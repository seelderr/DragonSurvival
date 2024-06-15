package by.dragonsurvivalteam.dragonsurvival.network.player;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Synchronizes dragon level and size */
public class SyncSize implements IMessage<SyncSize.Data> {
	public static void handleClient(final SyncSize.Data message, final IPayloadContext context) {
		context.enqueueWork(() -> ClientProxy.handleSyncSize(message));
	}

	public record Data(int playerId, double size) implements CustomPacketPayload {
		public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "size"));

		public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT,
				Data::playerId,
				ByteBufCodecs.DOUBLE,
				Data::size,
				Data::new
		);

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}