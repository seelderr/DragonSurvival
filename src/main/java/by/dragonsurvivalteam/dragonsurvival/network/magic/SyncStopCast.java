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

public record SyncStopCast(int playerId) implements CustomPacketPayload {
    public static final Type<SyncStopCast> TYPE = new CustomPacketPayload.Type<>(DragonSurvival.res("sync_stop_cast"));

    public static final StreamCodec<FriendlyByteBuf, SyncStopCast> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SyncStopCast::playerId,
            SyncStopCast::new
    );

    public static void handleClient(final SyncStopCast packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player().level().getEntity(packet.playerId) instanceof Player player) {
                // In this case, the cast has been denied by the server. So prevent future cast attempts until the player actually releases and presses the cast key again.
                MagicData magicData = MagicData.getData(player);
                magicData.denyCast();
            }
        });
    }

    public static void handleServer(final SyncStopCast packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player().level().getEntity(packet.playerId) instanceof Player player) {
                MagicData magicData = MagicData.getData(player);
                magicData.stopCasting();
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
