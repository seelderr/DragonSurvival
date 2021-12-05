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
    public int playerId;
    private String emote;
    public int emoteTick;
    
    public SyncEmote(int playerId, String emote, int emoteTick)
    {
        this.emote = emote;
        this.playerId = playerId;
        this.emoteTick = emoteTick;
    }
    
    public SyncEmote() {
    }
    
    @Override
    public void encode(SyncEmote message, PacketBuffer buffer) {
        buffer.writeInt(message.playerId);
        buffer.writeUtf(message.emote);
        buffer.writeInt(message.emoteTick);
    }

    @Override
    public SyncEmote decode(PacketBuffer buffer) {
        int playerId = buffer.readInt();
        String emote = buffer.readUtf();
        int emoteTick = buffer.readInt();
        return new SyncEmote(playerId, emote, emoteTick);
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
                                    dragonStateHandler.getEmotes().emoteTick = message.emoteTick;
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
