package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SyncHunterStacksRemoval(int playerId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncHunterStacksRemoval> TYPE = new CustomPacketPayload.Type<>(DragonSurvival.res("sync_hunter_stacks_removal"));
    public static final StreamCodec<ByteBuf, SyncHunterStacksRemoval> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncHunterStacksRemoval::playerId,
            SyncHunterStacksRemoval::new
    );

    public static void handleClient(final SyncHunterStacksRemoval packet, final IPayloadContext context) {
        context.enqueueWork(() -> DragonStateProvider.getOptional(context.player().level().getEntity(packet.playerId())).ifPresent(DragonStateHandler::clearHunterStacks));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
