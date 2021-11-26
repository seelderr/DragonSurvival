package by.jackraidenph.dragonsurvival.network.magic;

import by.jackraidenph.dragonsurvival.PacketProxy;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncAbilityActivation implements IMessage<SyncAbilityActivation>
{
    
    public int playerId;
    public int slot;

    public SyncAbilityActivation(int playerId, int slot) {
        this.playerId = playerId;
        this.slot = slot;
    }

    public SyncAbilityActivation() {
    }
    
    @Override
    public void encode(SyncAbilityActivation message, PacketBuffer buffer) {
        buffer.writeInt(message.playerId);
        buffer.writeInt(message.slot);
    }

    @Override
    public SyncAbilityActivation decode(PacketBuffer buffer) {
        int playerId = buffer.readInt();
        int slot = buffer.readInt();
        return new SyncAbilityActivation(playerId, slot);
    }

    @Override
    public void handle(SyncAbilityActivation message, Supplier<NetworkEvent.Context> supplier) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> new PacketProxy().handleClientSideAbility(message, supplier));
    }
}
