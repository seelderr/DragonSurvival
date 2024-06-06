package by.dragonsurvivalteam.dragonsurvival.network.config;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncBooleanConfig implements IMessage<SyncBooleanConfig.Data> {

	public static void handleClient(final SyncBooleanConfig.Data message, final IPayloadContext context) {
		context.enqueueWork(() -> ConfigHandler.updateConfigValue(message.path, message.value));

		if (message.path.equals("tooltips.hideUnsafeFood")) {
			DragonFoodHandler.clearTooltipMaps();
		}
	}

	public static void handleServer(final SyncBooleanConfig.Data message, final IPayloadContext context) {
		Player sender = context.player();

		if (!sender.hasPermissions(2)) {
			return;
		}

		PacketDistributor.sendToAllPlayers(message);

		context.enqueueWork(() -> ConfigHandler.updateConfigValue(message.path, message.value));

		if (message.path.equals("tooltips.hideUnsafeFood")) {
			DragonFoodHandler.clearTooltipMaps();
		}
	}

	public record Data(boolean value, String path) implements CustomPacketPayload {
		public static final Type<Data> TYPE = new Type<>(new ResourceLocation(MODID, "boolean_config"));

		public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.BOOL,
				Data::value,
				ByteBufCodecs.STRING_UTF8,
				Data::path,
				Data::new
		);

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}