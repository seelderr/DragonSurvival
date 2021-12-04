package by.jackraidenph.dragonsurvival.network.emotes;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.emotes.Emote;
import by.jackraidenph.dragonsurvival.emotes.EmoteRegistry;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.function.Supplier;

public class SyncEmoteStats implements IMessage<SyncEmoteStats>
{

    private int playerId;
    private boolean menuOpen;
    private HashMap<String, Integer> usage;
    
    public SyncEmoteStats(int playerId, boolean menuOpen, HashMap<String, Integer> usage)
    {
        this.playerId = playerId;
        this.menuOpen = menuOpen;
        this.usage = usage;
    }
    
    public SyncEmoteStats() {
    }
    
    @Override
    public void encode(SyncEmoteStats message, PacketBuffer buffer) {
        buffer.writeInt(message.playerId);
        buffer.writeBoolean(message.menuOpen);
        
        CompoundNBT emoteUsage = new CompoundNBT();
    
        for(Emote emote : EmoteRegistry.EMOTES){
            if(message.usage.containsKey(emote.name)){
                emoteUsage.putInt(emote.name, message.usage.get(emote.name));
            }
        }
        
        buffer.writeNbt(emoteUsage);}

    @Override
    public SyncEmoteStats decode(PacketBuffer buffer) {
        int playerId = buffer.readInt();
        boolean menuOpen = buffer.readBoolean();
    
        CompoundNBT emoteUsage = buffer.readNbt();
        HashMap<String, Integer> usage = new HashMap<>();
        for(Emote emote : EmoteRegistry.EMOTES){
            if(emoteUsage.contains(emote.name)){
                usage.put(emote.name, emoteUsage.getInt(emote.name));
            }
        }
        
        return new SyncEmoteStats(playerId, menuOpen, usage);
    }
    
    @Override
    public void handle(SyncEmoteStats message, Supplier<NetworkEvent.Context> supplier) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));
    }
    
    @OnlyIn(Dist.CLIENT)
    public void run(SyncEmoteStats message, Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            PlayerEntity thisPlayer = Minecraft.getInstance().player;
            if (thisPlayer != null) {
                World world = thisPlayer.level;
                Entity entity = world.getEntity(message.playerId);
                if (entity instanceof PlayerEntity) {
                    DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
                        dragonStateHandler.emoteMenuOpen = message.menuOpen;
                        dragonStateHandler.emoteUsage = message.usage;
                    });
                }
            }
            context.setPacketHandled(true);
        });
    }
}
