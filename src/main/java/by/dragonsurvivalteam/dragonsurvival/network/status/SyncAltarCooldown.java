package by.dragonsurvivalteam.dragonsurvival.network.status;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.AltarData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

// TODO: Maybe remove? Only needed in situations when we are calling SyncComplete anyways
public class SyncAltarCooldown implements IMessage<SyncAltarCooldown.Data> {

    public static void handleClient(final Data message, final IPayloadContext context) {
        context.enqueueWork(() -> ClientProxy.handleSyncAltarCooldown(message));
    }

    public static void handleServer(final Data message, final IPayloadContext context) {
        context.enqueueWork(() -> {
            AltarData data = AltarData.getData(context.player());
            data.altarCooldown = message.cooldown;
            data.hasUsedAltar = true;
            data.isInAltar = false;
        }).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(context.player(), message));
    }

    public record Data(int playerId, int cooldown) implements CustomPacketPayload {
        public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "altar_cooldown"));

        public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                Data::playerId,
                ByteBufCodecs.VAR_INT,
                Data::cooldown,
                Data::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}