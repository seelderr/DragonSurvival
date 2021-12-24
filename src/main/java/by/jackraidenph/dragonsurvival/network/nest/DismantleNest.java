package by.jackraidenph.dragonsurvival.network.nest;

import by.jackraidenph.dragonsurvival.network.IMessage;
import by.jackraidenph.dragonsurvival.server.tileentity.NestTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class DismantleNest implements IMessage<DismantleNest>
{
    public DismantleNest() {
    }

    public DismantleNest(BlockPos nestPos) {
        this.nestPos = nestPos;
        ;
    }

    public BlockPos nestPos;
    
    @Override
    public void encode(DismantleNest message, PacketBuffer buffer)
    {
        buffer.writeBlockPos(message.nestPos);
    }
    
    @Override
    public DismantleNest decode(PacketBuffer buffer)
    {
        return new DismantleNest(buffer.readBlockPos());
    }
    
    @Override
    public void handle(DismantleNest message, Supplier<Context> supplier)
    {
        ServerWorld serverWorld = supplier.get().getSender().getLevel();
        TileEntity tileEntity = serverWorld.getBlockEntity(message.nestPos);
        if (tileEntity instanceof NestTileEntity) {
            serverWorld.destroyBlock(message.nestPos, true);
            supplier.get().setPacketHandled(true);
        }
    }
}
