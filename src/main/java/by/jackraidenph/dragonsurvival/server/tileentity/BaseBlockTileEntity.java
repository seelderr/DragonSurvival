package by.jackraidenph.dragonsurvival.server.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BaseBlockTileEntity extends BlockEntity
{
    public BaseBlockTileEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState)
    {
        super(pType, pWorldPosition, pBlockState);
    }
    
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }


    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag supertag = super.getUpdateTag();
        save(supertag);
        return supertag;
    }
    
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
    {
        super.onDataPacket(net, pkt);
        load(pkt.getTag());
    }
    

    public int getX() {
        return getBlockPos().getX();
    }

    public int getY() {
        return getBlockPos().getY();
    }

    public int getZ() {
        return getBlockPos().getZ();
    }
}
