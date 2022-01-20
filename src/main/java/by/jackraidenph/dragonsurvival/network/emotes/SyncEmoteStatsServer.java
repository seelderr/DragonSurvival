package by.jackraidenph.dragonsurvival.network.emotes;

import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncEmoteStatsServer implements IMessage<SyncEmoteStatsServer>
{

    private boolean menuOpen;
    
    public SyncEmoteStatsServer(boolean menuOpen)
    {
        this.menuOpen = menuOpen;
    }
    
    public SyncEmoteStatsServer() {
    }
    
    @Override
    public void encode(SyncEmoteStatsServer message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.menuOpen);
    }

    @Override
    public SyncEmoteStatsServer decode(FriendlyByteBuf buffer) {
        boolean menuOpen = buffer.readBoolean();
        return new SyncEmoteStatsServer(menuOpen);
    }

    @Override
    public void handle(SyncEmoteStatsServer message, Supplier<NetworkEvent.Context> supplier) {
        ServerPlayer playerEntity = supplier.get().getSender();

        if(playerEntity == null)
            return;
        
        DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
            dragonStateHandler.getEmotes().emoteMenuOpen = message.menuOpen;
        });
    }
}
