package by.dragonsurvivalteam.dragonsurvival.network.player;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class SyncDestructionEnabled implements IMessage<SyncDestructionEnabled.Data> {
    public static void handleClient(final Data message, final IPayloadContext context) {
        context.enqueueWork(() -> ClientProxy.handleSyncDestructionEnabled(message));
    }

    public static void handleServer(final Data message, final IPayloadContext context) {
        Entity entity = context.player();
        context.enqueueWork(() -> {
            DragonStateProvider.getCap(entity).ifPresent(handler -> {
                handler.setDestructionEnabled(message.destructionEnabled);
            });
        }).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntity(entity, message));
    }

    public record Data(int playerId, boolean destructionEnabled) implements CustomPacketPayload
    {
        public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "dragon_destruction_enabled"));

        public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                Data::playerId,
                ByteBufCodecs.BOOL,
                Data::destructionEnabled,
                Data::new
        );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
