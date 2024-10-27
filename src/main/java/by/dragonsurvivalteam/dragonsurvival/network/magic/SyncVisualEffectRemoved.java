package by.dragonsurvivalteam.dragonsurvival.network.magic;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class SyncVisualEffectRemoved implements IMessage<SyncVisualEffectRemoved.Data> {
	public static void handleClient(final SyncVisualEffectRemoved.Data message, final IPayloadContext context) {
		context.enqueueWork(() -> ClientProxy.handleSyncPotionRemovedEffect(message));
	}

	public record Data(int playerId, int effectId) implements CustomPacketPayload {
		public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "potion_removed_effect"));

		public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, Data::playerId,
			ByteBufCodecs.VAR_INT, Data::effectId,
			Data::new
		);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}