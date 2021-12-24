package by.jackraidenph.dragonsurvival.network.emotes;

import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
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

import java.util.function.Supplier;

public class SyncEmoteStats implements IMessage<SyncEmoteStats>
{

    private int playerId;
    private boolean menuOpen;
    
    public SyncEmoteStats(int playerId, boolean menuOpen)
    {
        this.playerId = playerId;
        this.menuOpen = menuOpen;
    }
    
    public SyncEmoteStats() {
    }
    
    @Override
    public void encode(SyncEmoteStats message, PacketBuffer buffer) {
        buffer.writeInt(message.playerId);
        buffer.writeBoolean(message.menuOpen);
    }

    @Override
    public SyncEmoteStats decode(PacketBuffer buffer) {
        int playerId = buffer.readInt();
        boolean menuOpen = buffer.readBoolean();
        return new SyncEmoteStats(playerId, menuOpen);
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
                        dragonStateHandler.getEmotes().emoteMenuOpen = message.menuOpen;
                    });
                }
            }
            context.setPacketHandled(true);
        });
    }
}
