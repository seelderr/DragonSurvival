package by.jackraidenph.dragonsurvival.network.emotes;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.emotes.Emote;
import by.jackraidenph.dragonsurvival.emotes.EmoteRegistry;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class SyncEmote implements IMessage<SyncEmote>
{

    private String emote;
    public int playerId;
    
    
    public SyncEmote(int playerId, String emote)
    {
        this.emote = emote;
        this.playerId = playerId;
    }
    
    public SyncEmote() {
    }
    
    @Override
    public void encode(SyncEmote message, PacketBuffer buffer) {
        buffer.writeInt(message.playerId);
        buffer.writeUtf(message.emote);
    }

    @Override
    public SyncEmote decode(PacketBuffer buffer) {
        int playerId = buffer.readInt();
        String emote = buffer.readUtf();
        return new SyncEmote(playerId, emote);
    }
    
    @Override
    public void handle(SyncEmote message, Supplier<NetworkEvent.Context> supplier) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));
    }
    
    @OnlyIn(Dist.CLIENT)
    public void run(SyncEmote message, Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            PlayerEntity thisPlayer = Minecraft.getInstance().player;
            if (thisPlayer != null) {
                World world = thisPlayer.level;
                Entity entity = world.getEntity(message.playerId);
                if (entity instanceof PlayerEntity) {
                    DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
                        if(!message.emote.equals("nil")){
                            for(Emote emote : EmoteRegistry.EMOTES){
                                if(Objects.equals(emote.name, message.emote)){
                                    dragonStateHandler.getEmotes().setCurrentEmote(emote);
                                    break;
                                }
                            }
                        }else{
                            dragonStateHandler.getEmotes().setCurrentEmote(null);
                        }
                    });
                }
            }
            context.setPacketHandled(true);
        });
    }
}
