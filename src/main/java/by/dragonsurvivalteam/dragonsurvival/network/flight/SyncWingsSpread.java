package by.dragonsurvivalteam.dragonsurvival.network.flight;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.FlightData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class SyncWingsSpread implements IMessage<SyncWingsSpread.Data> {

    public static void handleClient(final SyncWingsSpread.Data message, final IPayloadContext context) {
        context.enqueueWork(() -> ClientProxy.handleSyncWingsSpread(message));
    }

    public static void handleServer(final SyncWingsSpread.Data message, final IPayloadContext context) {
        Player sender = context.player();
        context.enqueueWork(() -> {
            FlightData data = FlightData.getData(sender);
            data.areWingsSpread = message.state();
        }).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(sender, message));
    }

    public record Data(int playerId, boolean state) implements CustomPacketPayload {
        public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "flying_status"));

        public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
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