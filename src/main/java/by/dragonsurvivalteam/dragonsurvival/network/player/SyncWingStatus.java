package by.dragonsurvivalteam.dragonsurvival.network.player;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SyncWingStatus(int playerId, boolean hasWings) implements CustomPacketPayload {
    public static final Type<SyncWingStatus> TYPE = new Type<>(DragonSurvival.res("sync_wing_status"));

    public static StreamCodec<RegistryFriendlyByteBuf, SyncWingStatus> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncWingStatus::playerId,
            ByteBufCodecs.BOOL, SyncWingStatus::hasWings,
            SyncWingStatus::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(final SyncWingStatus packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {
                DragonStateProvider.getData(player).setHasFlight(packet.hasWings());
            }
        });
    }

    public static void handleServer(final SyncWingStatus packet, final IPayloadContext context) {
        context.enqueueWork(() -> DragonStateProvider.getData(context.player()).setHasFlight(packet.hasWings()))
                .thenRun(() -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(context.player(), packet));
    }
}