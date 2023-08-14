package by.dragonsurvivalteam.dragonsurvival.network.player;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncChatEvent implements IMessage<SyncChatEvent> {
    public String signerId = "";
    public String chatId = "";

    public SyncChatEvent() { /* Nothing to do */ }

    public SyncChatEvent(final String signerId, final String chatId) {
        this.signerId = signerId;
        this.chatId = chatId;
    }

    @Override
    public void encode(final SyncChatEvent message, final FriendlyByteBuf buffer) {
        buffer.writeUtf(message.signerId);
        buffer.writeUtf(message.chatId);
    }

    @Override
    public SyncChatEvent decode(final FriendlyByteBuf buffer) {
        SyncChatEvent syncChatEvent = new SyncChatEvent();
        syncChatEvent.signerId = buffer.readUtf();
        syncChatEvent.chatId = buffer.readUtf();
        return syncChatEvent;
    }

    @Override
    public void handle(final SyncChatEvent message, final Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> ClientProxy.handleSyncChatEvent(message));
        }

        context.setPacketHandled(true);
    }
}
