package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.Immunity;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.Immunities;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SyncImmunity(int playerId, Immunity.Instance immunityInstance, boolean remove) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncImmunity> TYPE = new CustomPacketPayload.Type<>(DragonSurvival.res("sync_immunity"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncImmunity> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncImmunity::playerId,
            ByteBufCodecs.fromCodecWithRegistries(Immunity.Instance.CODEC), SyncImmunity::immunityInstance,
            ByteBufCodecs.BOOL, SyncImmunity::remove,
            SyncImmunity::new
    );

    public static void handleClient(final SyncImmunity packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {
                Immunities data = Immunities.getData(player);

                if (packet.remove()) {
                    data.remove(player, packet.immunityInstance());
                } else {
                    data.add(player, packet.immunityInstance());
                }
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
