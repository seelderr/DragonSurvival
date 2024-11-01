package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class SyncMagicStats implements IMessage<SyncMagicStats.Data> {

    public static void handleClient(final SyncMagicStats.Data message, final IPayloadContext context) {
        context.enqueueWork(() -> ClientProxy.handleSyncMagicstats(message));
    }

    public record Data(int playerid, int selectedSlot, int currentMana,
                    boolean renderHotbar) implements CustomPacketPayload {
        public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "magic_stats"));

        public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                Data::playerid,
                ByteBufCodecs.VAR_INT,
                Data::selectedSlot,
                ByteBufCodecs.VAR_INT,
                Data::currentMana,
                ByteBufCodecs.BOOL,
                Data::renderHotbar,
                Data::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}