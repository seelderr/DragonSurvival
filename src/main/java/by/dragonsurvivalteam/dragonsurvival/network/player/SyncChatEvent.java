package by.dragonsurvivalteam.dragonsurvival.network.player;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncChatEvent implements IMessage<SyncChatEvent.Data> {

    public static void handleClient(Data message, final IPayloadContext context) {
        context.enqueueWork(() -> ClientProxy.handleSyncChatEvent(message));
    }

    public record Data(String signerId, String chatId) implements CustomPacketPayload {
        public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "chat_event"));

        public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                Data::signerId,
                ByteBufCodecs.STRING_UTF8,
                Data::chatId,
                Data::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
