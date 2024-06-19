package by.dragonsurvivalteam.dragonsurvival.network.status;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;
import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.DRAGON_HANDLER;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncTreasureRestStatus implements IMessage<SyncTreasureRestStatus.Data> {
	public static void handleClient(Data message, IPayloadContext context) {
		context.enqueueWork(() -> ClientProxy.handleSyncTreasureRestStatus(message));
	}

	public static void handleServer(Data message, IPayloadContext context) {
		context.enqueueWork(() -> {
			DragonStateProvider.getCap(context.player()).ifPresent(handler -> {
				if (handler.isDragon()) {
					boolean update = false;

					if (message.state() != handler.treasureResting) {
						handler.treasureRestTimer = 0;
						handler.treasureSleepTimer = 0;
						update = true;
					}

					handler.treasureResting = message.state();

					if (update) {
						((ServerLevel) context.player().level()).updateSleepingPlayerList();
					}
				}
			});
		}).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(context.player(), message));
	}

	public record Data(int playerId, boolean state) implements CustomPacketPayload
	{
		public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "treasure_rest_status"));

		public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT,
				Data::playerId,
				ByteBufCodecs.BOOL,
				Data::state,
				Data::new
		);

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}