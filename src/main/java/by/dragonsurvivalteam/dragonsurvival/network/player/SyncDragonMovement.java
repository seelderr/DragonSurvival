package by.dragonsurvivalteam.dragonsurvival.network.player;

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
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncDragonMovement implements IMessage<SyncDragonMovement.Data> {

	public static void handleClient(final Data message, final IPayloadContext context) {
		context.enqueueWork(() -> ClientProxy.handlePacketSyncCapabilityMovement(message));
	}

	public static void handleServer(final Data message, final IPayloadContext context) {
		Entity entity = context.player();
		context.enqueueWork(() -> {
			DragonStateHandler handler = entity.getData(DRAGON_HANDLER);
			handler.setMovementData(message.bodyYaw, message.headYaw, message.headPitch, message.bite);
		});
		PacketDistributor.sendToPlayersTrackingEntity(entity, message);
	}

	public record Data(int playerId, double bodyYaw, double headYaw, double headPitch, boolean bite) implements CustomPacketPayload
	{
		public static final Type<Data> TYPE = new Type<>(new ResourceLocation(MODID, "dragon_movement"));

		public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT,
				Data::playerId,
				ByteBufCodecs.DOUBLE,
				Data::bodyYaw,
				ByteBufCodecs.DOUBLE,
				Data::headYaw,
				ByteBufCodecs.DOUBLE,
				Data::headPitch,
				ByteBufCodecs.BOOL,
				Data::bite,
				Data::new
		);

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}