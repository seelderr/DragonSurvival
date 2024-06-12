package by.dragonsurvivalteam.dragonsurvival.network.status;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;
import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.DRAGON_HANDLER;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncMagicSourceStatus implements IMessage<SyncMagicSourceStatus.Data> {
	public static void handleClient(Data message, IPayloadContext context) {
		context.enqueueWork(() -> ClientProxy.handleSyncMagicSourceStatus(message));
	}

	public static void handleServer(Data message, IPayloadContext context) {
		DragonStateHandler handler = context.player().getData(DRAGON_HANDLER);
		handler.getMagicData().onMagicSource = message.state;
		handler.getMagicData().magicSourceTimer = message.timer;
		PacketDistributor.sendToPlayersTrackingEntityAndSelf(context.player(), message);
	}

	public record Data(int playerId, boolean state, int timer) implements CustomPacketPayload
	{
		public static final Type<Data> TYPE = new Type<>(new ResourceLocation(MODID, "magic_source_status"));

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