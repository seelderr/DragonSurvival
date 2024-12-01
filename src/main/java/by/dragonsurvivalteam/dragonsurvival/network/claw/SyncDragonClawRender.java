package by.dragonsurvivalteam.dragonsurvival.network.claw;

import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.ClawInventoryData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class SyncDragonClawRender implements IMessage<SyncDragonClawRender.Data> {

    public static void handleClient(final SyncDragonClawRender.Data message, final IPayloadContext context) {
        context.enqueueWork(() -> ClientProxy.handleSyncDragonClawRender(message));
    }

    public static void handleServer(final SyncDragonClawRender.Data message, final IPayloadContext context) {
        if (ServerConfig.syncClawRender) {
            Player sender = context.player();
            context.enqueueWork(() ->
                    ClawInventoryData.getData(sender).shouldRenderClaws = message.state
            ).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(sender, message));
        }
    }

    public record Data(int playerId, boolean state) implements CustomPacketPayload {
        public static final Type<SyncDragonClawRender.Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "dragon_claw_render"));

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