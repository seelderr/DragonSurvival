package by.dragonsurvivalteam.dragonsurvival.network.player;

import by.dragonsurvivalteam.dragonsurvival.commands.DragonCommand;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon.DragonBody;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.Utf8String;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class SyncDragonHandler implements IMessage<SyncDragonHandler.Data> {

    public static void handleClient(final Data message, final IPayloadContext context) {
        context.enqueueWork(() -> ClientProxy.handleSynchronizeDragonCap(message));
    }

    public static void handleServer(final Data message, final IPayloadContext context) {
        Player sender = context.player();
        context.enqueueWork(() -> {
            DragonStateProvider.getOptional(sender).ifPresent(cap -> {
                if (message.dragonType == null && cap.getType() != null) {
                    DragonCommand.reInsertClawTools(sender, cap);
                }

                cap.setIsHiding(message.hiding);
                cap.setType(message.dragonType, sender);
                cap.setBody(message.dragonBody, sender);
                cap.setSize(message.size, sender);
                cap.setHasFlight(message.hasWings);
                cap.setPassengerId(message.passengerId);
            });
        }).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(context.player(), message));
    }

    public record Data(int playerId, boolean hiding, AbstractDragonType dragonType, Holder<DragonBody> dragonBody,
                    double size, boolean hasWings, int passengerId) implements CustomPacketPayload {
        public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "dragon_cap"));

        public static StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public void encode(RegistryFriendlyByteBuf buffer, Data data) {
                buffer.writeInt(data.playerId);
                Utf8String.write(buffer, data.dragonType != null ? data.dragonType.getSubtypeName() : "none", 32);
                ByteBufCodecs.holderRegistry(DragonBody.REGISTRY).encode(buffer, data.dragonBody); // FIXME body :: is null okay?
                buffer.writeBoolean(data.hiding);
                buffer.writeDouble(data.size);
                buffer.writeBoolean(data.hasWings);
                buffer.writeInt(data.passengerId);
            }

            @Override
            public @NotNull Data decode(RegistryFriendlyByteBuf buffer) {
                int id = buffer.readInt();
                String typeS = Utf8String.read(buffer, 32);
                AbstractDragonType type = typeS.equals("none") ? null : DragonTypes.getStaticSubtype(typeS);
                Holder<DragonBody> body = ByteBufCodecs.holderRegistry(DragonBody.REGISTRY).decode(buffer);
                boolean hiding = buffer.readBoolean();
                double size = buffer.readDouble();
                boolean hasWings = buffer.readBoolean();
                int passengerId = buffer.readInt();
                return new Data(id, hiding, type, body, size, hasWings, passengerId);
            }
        };

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}