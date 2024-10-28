package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class SyncPotionAddedEffect implements IMessage<SyncPotionAddedEffect.Data> {
    public static void handleClient(final SyncPotionAddedEffect.Data message, final IPayloadContext context) {
        context.enqueueWork(() -> ClientProxy.handleSyncPotionAddedEffect(message));
    }

    public record Data(int entityId, int effectId, int duration, int amplifier) implements CustomPacketPayload {
        public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "potion_added_effect"));

        public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                Data::entityId,
                ByteBufCodecs.VAR_INT,
                Data::effectId,
                ByteBufCodecs.VAR_INT,
                Data::duration,
                ByteBufCodecs.VAR_INT,
                Data::amplifier,
                Data::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}