package by.dragonsurvivalteam.dragonsurvival.network.flight;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.FlightData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SpinDurationAndCooldown(int playerId, int duration, int cooldown) implements CustomPacketPayload {
    public static final Type<SpinDurationAndCooldown> TYPE = new Type<>(DragonSurvival.res("spin_duration_and_cooldown"));

    public static final StreamCodec<FriendlyByteBuf, SpinDurationAndCooldown> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SpinDurationAndCooldown::playerId,
            ByteBufCodecs.INT, SpinDurationAndCooldown::duration,
            ByteBufCodecs.INT, SpinDurationAndCooldown::cooldown,
            SpinDurationAndCooldown::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(final SpinDurationAndCooldown packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            FlightData data = FlightData.getData(context.player());
            data.duration = packet.duration();
            data.cooldown = packet.cooldown();
        });
    }

    public static void handleServer(final SpinDurationAndCooldown packet, final IPayloadContext context) {
        Player sender = context.player();

        context.enqueueWork(() -> {
            FlightData spin = FlightData.getData(sender);
            spin.cooldown = packet.cooldown();
            spin.duration = packet.duration();
        }).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(sender, packet));
    }
}
