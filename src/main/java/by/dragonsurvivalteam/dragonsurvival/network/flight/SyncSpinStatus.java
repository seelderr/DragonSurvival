package by.dragonsurvivalteam.dragonsurvival.network.flight;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
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

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class SyncSpinStatus implements IMessage<SyncSpinStatus.Data> {
    public static void handleClient(final SyncSpinStatus.Data message, final IPayloadContext context) {
        context.enqueueWork(() -> ClientProxy.handleSyncSpinStatus(message));
    }

    public static void handleServer(final SyncSpinStatus.Data message, final IPayloadContext context) {
        Player sender = context.player();
        context.enqueueWork(() -> {
            DragonStateProvider.getOptional(sender).ifPresent(handler -> {
                handler.getMovementData().spinAttack = message.spinAttack;
                handler.getMovementData().spinCooldown = message.spinCooldown;
                handler.getMovementData().spinLearned = message.spinLearned;
            });
        }).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(sender, message));
    }

    public record Data(int playerId, int spinAttack, int spinCooldown,
                    boolean spinLearned) implements CustomPacketPayload {
        public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "spin_status"));

        public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                Data::playerId,
                ByteBufCodecs.INT,
                Data::spinAttack,
                ByteBufCodecs.INT,
                Data::spinCooldown,
                ByteBufCodecs.BOOL,
                Data::spinLearned,
                Data::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

    }
}