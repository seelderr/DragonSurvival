package by.dragonsurvivalteam.dragonsurvival.network.player;

import by.dragonsurvivalteam.dragonsurvival.common.handlers.WingObtainmentController;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncChatEvent implements IMessage<SyncChatEvent> {
    public String signerId = "";
    public String chatId = "";
    public SyncChatEvent(){
    }
    public SyncChatEvent(String signerId, String chatId)
    {
        this.signerId = signerId;
        this.chatId = chatId;
    }
    @Override
    public void encode(SyncChatEvent message, FriendlyByteBuf buffer) {
        buffer.writeUtf(message.signerId);
        buffer.writeUtf(message.chatId);
    }

    @Override
    public SyncChatEvent decode(FriendlyByteBuf buffer) {
        SyncChatEvent syncChatEvent = new SyncChatEvent();
        syncChatEvent.signerId = buffer.readUtf();
        syncChatEvent.chatId = buffer.readUtf();
        return syncChatEvent;
    }

    @Override
    public void handle(SyncChatEvent message, Supplier<NetworkEvent.Context> supplier) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (DistExecutor.SafeRunnable)() -> runClient(message, supplier));
        supplier.get().setPacketHandled(true);
    }

    @OnlyIn( Dist.CLIENT )
    public void runClient(SyncChatEvent message, Supplier<NetworkEvent.Context> supplier) {
        Player player = Minecraft.getInstance().player;
        if (player == null)
            return;
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            WingObtainmentController.clientMessageRecieved(message);
            context.setPacketHandled(true);
        });
    }
}
