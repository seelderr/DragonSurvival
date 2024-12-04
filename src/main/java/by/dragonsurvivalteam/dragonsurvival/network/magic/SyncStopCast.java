package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MagicData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * @param playerId The player that stopped casting
 * @param wasDenied Forces the client to re-press the cast button to initiate another cast
 */
public record SyncStopCast(int playerId, boolean wasDenied) implements CustomPacketPayload {
    public static final Type<SyncStopCast> TYPE = new CustomPacketPayload.Type<>(DragonSurvival.res("sync_stop_cast"));

    public static final StreamCodec<FriendlyByteBuf, SyncStopCast> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncStopCast::playerId,
            ByteBufCodecs.BOOL, SyncStopCast::wasDenied,
            SyncStopCast::new
    );

    public static void handleClient(final SyncStopCast packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId) instanceof Player player) {
                MagicData magic = MagicData.getData(player);

                if (packet.wasDenied()) {
                    magic.denyCast();
                } else {
                    magic.stopCasting(player);
                }
            }
        });
    }

    public static void handleServer(final SyncStopCast packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId) instanceof Player player) {
                MagicData.getData(player).stopCasting(player);
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
