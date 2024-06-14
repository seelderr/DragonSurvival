package by.dragonsurvivalteam.dragonsurvival.network.claw;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;
import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.DRAGON_HANDLER;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
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

public class SyncDragonClawRender implements IMessage<SyncDragonClawRender.Data> {

	public static void handleClient(final SyncDragonClawRender.Data message, final IPayloadContext context) {
		context.enqueueWork(() -> ClientProxy.handleSyncDragonClawRender(message));
	}

	public static void handleServer(final SyncDragonClawRender.Data message, final IPayloadContext context) {
		if(ServerConfig.syncClawRender) {
			Player sender = context.player();
			DragonStateHandler handler = sender.getData(DRAGON_HANDLER);
			handler.getClawToolData().shouldRenderClaws = message.state;
			PacketDistributor.sendToPlayersTrackingEntityAndSelf(sender, message);
		}
	}

	public record Data(int playerId, boolean state) implements CustomPacketPayload {
		public static final Type<SyncDragonClawRender.Data> TYPE = new Type<>(new ResourceLocation(MODID, "dragon_claw_render"));

		public static final StreamCodec<FriendlyByteBuf, SyncDragonClawRender.Data> STREAM_CODEC = StreamCodec.composite(
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