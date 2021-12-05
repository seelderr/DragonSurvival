package by.jackraidenph.dragonsurvival.network.emotes;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.emotes.Emote;
import by.jackraidenph.dragonsurvival.emotes.EmoteRegistry;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.function.Supplier;

public class SyncEmoteStatsServer implements IMessage<SyncEmoteStatsServer>
{

    private boolean menuOpen;
    private HashMap<String, Integer> usage;
    
    public SyncEmoteStatsServer(boolean menuOpen, HashMap<String, Integer> usage)
    {
        this.menuOpen = menuOpen;
        this.usage = usage;
    }
    
    public SyncEmoteStatsServer() {
    }
    
    @Override
    public void encode(SyncEmoteStatsServer message, PacketBuffer buffer) {
        buffer.writeBoolean(message.menuOpen);
        
        CompoundNBT emoteUsage = new CompoundNBT();
    
        for(Emote emote : EmoteRegistry.EMOTES){
            if(message.usage.containsKey(emote.name)){
                emoteUsage.putInt(emote.name, message.usage.get(emote.name));
            }
        }
        
        buffer.writeNbt(emoteUsage);
    }

    @Override
    public SyncEmoteStatsServer decode(PacketBuffer buffer) {
        boolean menuOpen = buffer.readBoolean();
    
        CompoundNBT emoteUsage = buffer.readNbt();
        HashMap<String, Integer> usage = new HashMap<>();
        for(Emote emote : EmoteRegistry.EMOTES){
            if(emoteUsage.contains(emote.name)){
                usage.put(emote.name, emoteUsage.getInt(emote.name));
            }
        }
        
        return new SyncEmoteStatsServer(menuOpen, usage);
    }

    @Override
    public void handle(SyncEmoteStatsServer message, Supplier<NetworkEvent.Context> supplier) {
        ServerPlayerEntity playerEntity = supplier.get().getSender();

        if(playerEntity == null)
            return;
        
        DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
            dragonStateHandler.getEmotes().emoteMenuOpen = message.menuOpen;
            dragonStateHandler.getEmotes().emoteUsage = message.usage;
        });
    }
}
