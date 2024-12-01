package by.dragonsurvivalteam.dragonsurvival.network.player;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DragonMovementData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class SyncDragonMovement implements IMessage<SyncDragonMovement.Data> {

    public static void handleClient(final Data message, final IPayloadContext context) {
        context.enqueueWork(() -> ClientProxy.handleSyncDragonMovement(message));
    }

    public static void handleServer(final Data message, final IPayloadContext context) {
        Entity entity = context.player();
        context.enqueueWork(() -> {
            if(DragonStateProvider.isDragon(entity)) {
                DragonMovementData data = DragonMovementData.getData(entity);
                data.setFirstPerson(message.isFirstPerson);
                data.setBite(message.bite);
                data.setFreeLook(message.isFreeLook);
                data.setDesiredMoveVec(new Vec2(message.desiredMoveVecX, message.desiredMoveVecY));
            }
        }).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntity(entity, message));
    }

    public record Data(
            int playerId,
            boolean isFirstPerson,
            boolean bite,
            boolean isFreeLook,
            float desiredMoveVecX,
            float desiredMoveVecY
    ) implements CustomPacketPayload {
        public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "dragon_movement"));

        public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                Data::playerId,
                ByteBufCodecs.BOOL,
                Data::isFirstPerson,
                ByteBufCodecs.BOOL,
                Data::bite,
                ByteBufCodecs.BOOL,
                Data::isFreeLook,
                ByteBufCodecs.FLOAT,
                Data::desiredMoveVecX,
                ByteBufCodecs.FLOAT,
                Data::desiredMoveVecY,
                Data::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}