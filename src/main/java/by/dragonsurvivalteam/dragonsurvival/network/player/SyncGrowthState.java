package by.dragonsurvivalteam.dragonsurvival.network.player;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class SyncGrowthState implements IMessage<SyncGrowthState.Data> {
	public static void handleClient(final SyncGrowthState.Data message, final IPayloadContext context) {
		context.enqueueWork(() -> ClientProxy.handleSyncGrowthState(message));
	}

	public record Data(boolean growing) implements CustomPacketPayload {
		public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "growth_state"));

		public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.BOOL,
				Data::growing,
				Data::new
		);

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}