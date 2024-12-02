package by.dragonsurvivalteam.dragonsurvival.network.flight;

import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.SpinData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public record SpinStatus(int playerId, int duration, int cooldown, boolean hasSpin) implements CustomPacketPayload {
    public static final Type<SpinStatus> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "spin_status"));

    public static final StreamCodec<FriendlyByteBuf, SpinStatus> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SpinStatus::playerId,
            ByteBufCodecs.INT, SpinStatus::duration,
            ByteBufCodecs.INT, SpinStatus::cooldown,
            ByteBufCodecs.BOOL, SpinStatus::hasSpin,
            SpinStatus::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(final SpinStatus packet, final IPayloadContext context) {
        context.enqueueWork(() -> ClientProxy.handleSyncSpinStatus(packet));
    }

    public static void handleServer(final SpinStatus packet, final IPayloadContext context) {
        Player sender = context.player();

        context.enqueueWork(() -> {
                SpinData spin = SpinData.getData(sender);
                spin.hasSpin = packet.hasSpin();
                spin.cooldown = packet.cooldown();
                spin.duration = packet.duration();
        }).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(sender, packet));
    }
}