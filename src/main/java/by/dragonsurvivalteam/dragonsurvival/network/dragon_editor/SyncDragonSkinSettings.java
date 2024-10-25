package by.dragonsurvivalteam.dragonsurvival.network.dragon_editor;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncDragonSkinSettings implements IMessage<SyncDragonSkinSettings.Data> {
	public static void handleClient(final SyncDragonSkinSettings.Data message, final IPayloadContext context) {
		context.enqueueWork(() -> ClientProxy.handleSyncDragonSkinSettings(message));
	}

	public static void handleServer(final SyncDragonSkinSettings.Data message, final IPayloadContext context) {
		Player sender = context.player();

		context.enqueueWork(() ->
				DragonStateProvider.getOptional(sender).ifPresent(handler -> {
					handler.getSkinData().renderNewborn = message.newborn();
					handler.getSkinData().renderYoung = message.young();
					handler.getSkinData().renderAdult = message.adult();
				})
		).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(sender, message));

	}

	public record Data(int playerId, boolean newborn, boolean young, boolean adult) implements CustomPacketPayload {

		public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "dragon_skin_settings"));

		public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT,
				Data::playerId,
				ByteBufCodecs.BOOL,
				Data::newborn,
				ByteBufCodecs.BOOL,
				Data::young,
				ByteBufCodecs.BOOL,
				Data::adult,
				Data::new
		);

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}