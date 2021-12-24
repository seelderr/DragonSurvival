package by.jackraidenph.dragonsurvival.network.nest;

import by.jackraidenph.dragonsurvival.server.tileentity.BaseBlockTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

public class NestPlaceHolder extends BaseBlockTileEntity
{
    public NestPlaceHolder(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public BlockPos rootPos = BlockPos.ZERO;

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putLong("Root", rootPos.asLong());
        return super.save(compound);
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        rootPos = BlockPos.of(compound.getLong("Root"));
    }
}
