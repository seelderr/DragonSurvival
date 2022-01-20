package by.jackraidenph.dragonsurvival.network.entity;

import by.jackraidenph.dragonsurvival.common.entity.monsters.MagicalPredatorEntity;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncPredatorStats implements IMessage<PacketSyncPredatorStats>
{

    public float size;
    public int type;
    public int id;

    public PacketSyncPredatorStats() {
    }

    public PacketSyncPredatorStats(int type, float size, int id) {
        this.type = type;
        this.size = size;
        this.id = id;
    }

    @Override
    public void encode(PacketSyncPredatorStats m, FriendlyByteBuf b) {
        b.writeInt(m.type);
        b.writeFloat(m.size);
        b.writeInt(m.id);
    }

    @Override
    public PacketSyncPredatorStats decode(FriendlyByteBuf b) {
        return new PacketSyncPredatorStats(
                b.readInt(),
                b.readFloat(),
                b.readInt());
    }

    @Override
    public void handle(PacketSyncPredatorStats m, Supplier<NetworkEvent.Context> supplier) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(m, supplier));
    }
    
    @OnlyIn( Dist.CLIENT)
    public void runClient(PacketSyncPredatorStats message, Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Level world = Minecraft.getInstance().level;
    
            if (world != null) {
                Entity entity = world.getEntity(message.id);
                if (entity != null) {
                    ((MagicalPredatorEntity) entity).size = message.size;
                    ((MagicalPredatorEntity) entity).type = message.type;
                }
            }
    
            supplier.get().setPacketHandled(true);
        });
    }
}
