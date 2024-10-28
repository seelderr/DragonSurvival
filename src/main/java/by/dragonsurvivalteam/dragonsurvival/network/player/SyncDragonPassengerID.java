package by.dragonsurvivalteam.dragonsurvival.network.player;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
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

public class SyncDragonPassengerID implements IMessage<SyncDragonPassengerID.Data> {
	public static void handleClient(final Data message, final IPayloadContext context) {
		context.enqueueWork(() -> ClientProxy.handleSyncPassengerID(message));
	}

	public static void handleServer(final Data message, final IPayloadContext context) {
		Entity entity = context.player();
		context.enqueueWork(() -> {
			DragonStateProvider.getOptional(entity).ifPresent(handler -> {
				handler.setPassengerId(message.passengerId);
			});
		}).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntity(entity, message));
	}

	public record Data(int playerId, int passengerId) implements CustomPacketPayload {
		public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "dragon_passenger_id"));

		public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT,
				Data::playerId,
				ByteBufCodecs.VAR_INT,
				Data::passengerId,
				Data::new
		);

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}
