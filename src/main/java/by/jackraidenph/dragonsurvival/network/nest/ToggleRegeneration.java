package by.jackraidenph.dragonsurvival.network.nest;

import by.jackraidenph.dragonsurvival.network.IMessage;
import by.jackraidenph.dragonsurvival.server.tileentity.NestTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class ToggleRegeneration  implements IMessage<ToggleRegeneration>
{
    public BlockPos nestPos;
    public boolean state;

    public ToggleRegeneration() {
    }

    public ToggleRegeneration(BlockPos nestPos, boolean state) {
        this.nestPos = nestPos;
        this.state = state;
    }
    
    @Override
    public void encode(ToggleRegeneration message, PacketBuffer buffer)
    {
        buffer.writeBlockPos(message.nestPos);
        buffer.writeBoolean(message.state);
    }
    
    @Override
    public ToggleRegeneration decode(PacketBuffer buffer)
    {
        return new ToggleRegeneration(buffer.readBlockPos(), buffer.readBoolean());
    }
    
    @Override
    public void handle(ToggleRegeneration message, Supplier<Context> supplier)
    {
        ServerWorld serverWorld = supplier.get().getSender().getLevel();
        TileEntity tileEntity = serverWorld.getBlockEntity(message.nestPos);
        if (tileEntity instanceof NestTileEntity) {
            ((NestTileEntity) tileEntity).regenerationMode = message.state;
            tileEntity.setChanged();
            supplier.get().setPacketHandled(true);
        }
    }
}
