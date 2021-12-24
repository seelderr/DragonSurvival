package by.jackraidenph.dragonsurvival.network.nest;

import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class SleepInNest implements IMessage<SleepInNest>
{
    public SleepInNest() {
    }

    public BlockPos nestPos;

    public SleepInNest(BlockPos nestPos) {
        this.nestPos = nestPos;
    }
    
    @Override
    public void encode(SleepInNest message, PacketBuffer buffer)
    {
        buffer.writeBlockPos(message.nestPos);
    }
    
    @Override
    public SleepInNest decode(PacketBuffer buffer)
    {
        return new SleepInNest(buffer.readBlockPos());
    }
    
    @Override
    public void handle(SleepInNest message, Supplier<Context> supplier)
    {
        ServerPlayerEntity serverPlayerEntity = supplier.get().getSender();
        if (serverPlayerEntity.getLevel().isNight()) {
            serverPlayerEntity.startSleepInBed(message.nestPos);
            serverPlayerEntity.setRespawnPosition(serverPlayerEntity.getLevel().dimension(), message.nestPos, 0.0F, false, true); // Float is respawnAngle
            // check these boolean values, might need to be switched.
        }
    }
}
