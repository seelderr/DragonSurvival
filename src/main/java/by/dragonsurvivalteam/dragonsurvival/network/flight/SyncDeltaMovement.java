package by.dragonsurvivalteam.dragonsurvival.network.flight;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class SyncDeltaMovement implements IMessage<SyncDeltaMovement.Data> {
    public static void handleClient(final SyncDeltaMovement.Data message, final IPayloadContext context) {
        context.enqueueWork(() -> ClientProxy.handleSyncDeltaMovement(message));
    }

    public static void handleServer(final SyncDeltaMovement.Data message, final IPayloadContext context) {
        Player sender = context.player();
        // This needs to be set so that it can be read back in some server side logic that uses deltamovement (e.g. DragonDestructionHandler)
        sender.setDeltaMovement(message.speedX(), message.speedY(), message.speedZ());
        PacketDistributor.sendToPlayersTrackingEntity(sender, new SyncDeltaMovement.Data(sender.getId(), message.speedX(), message.speedY(), message.speedZ()));
    }

    public record Data(int playerId, double speedX, double speedY, double speedZ) implements CustomPacketPayload {
        public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "flight_speed"));

        public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                Data::playerId,
                ByteBufCodecs.DOUBLE,
                Data::speedX,
                ByteBufCodecs.DOUBLE,
                Data::speedY,
                ByteBufCodecs.DOUBLE,
                Data::speedZ,
                Data::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}