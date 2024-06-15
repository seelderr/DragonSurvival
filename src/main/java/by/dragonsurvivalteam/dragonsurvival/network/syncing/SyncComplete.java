package by.dragonsurvivalteam.dragonsurvival.network.syncing;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.RequestClientData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncComplete implements IMessage<SyncComplete.Data> {
	public static void handleClient(final Data message, final IPayloadContext context) {
		Player player = (Player) context.player().level().getEntity(message.playerId);
		DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);
		handler.deserializeNBT(context.player().registryAccess(), message.nbt);
		player.refreshDimensions();
	}

	public static void handleServer(final Data message, final IPayloadContext context) {
		context.enqueueWork(() -> {
			Player player = context.player();
			DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);
			handler.deserializeNBT(context.player().registryAccess(), message.nbt);
			player.refreshDimensions();
			PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, message);
		}).thenAccept(v  -> context.reply(new RequestClientData.Data()));
	}

	public record Data(int playerId, CompoundTag nbt) implements CustomPacketPayload {

		public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "complete_data"));

		public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT,
				Data::playerId,
				ByteBufCodecs.COMPOUND_TAG,
				Data::nbt,
				Data::new
		);

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}