package by.jackraidenph.dragonsurvival.network.emotes;

import by.jackraidenph.dragonsurvival.client.emotes.Emote;
import by.jackraidenph.dragonsurvival.client.emotes.EmoteRegistry;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.network.NetworkEvent;

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
    public void encode(SyncEmote message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.playerId);
        buffer.writeUtf(message.emote);
        buffer.writeInt(message.emoteTick);
    }

    @Override
    public SyncEmote decode(FriendlyByteBuf buffer) {
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
            Player thisPlayer = Minecraft.getInstance().player;
            if (thisPlayer != null) {
                Level world = thisPlayer.level;
                Entity entity = world.getEntity(message.playerId);
                if (entity instanceof Player) {
                    DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
                        if(!message.emote.equals("nil")){
                            for(Emote emote : EmoteRegistry.EMOTES){
                                if(Objects.equals(emote.id, message.emote)){
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
