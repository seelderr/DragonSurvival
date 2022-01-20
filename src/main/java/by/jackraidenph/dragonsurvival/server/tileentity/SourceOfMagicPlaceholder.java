package by.jackraidenph.dragonsurvival.server.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class SourceOfMagicPlaceholder extends BaseBlockTileEntity
{
    public SourceOfMagicPlaceholder(BlockPos pWorldPosition, BlockState pBlockState)
    {
        super(DSTileEntities.sourceOfMagicPlaceholder, pWorldPosition, pBlockState);
    }
    
    public BlockPos rootPos = BlockPos.ZERO;
    
    @Override
    protected void saveAdditional(CompoundTag pTag)
    {
        pTag.putLong("Root", rootPos.asLong());
    }
    
    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        rootPos = BlockPos.of(compound.getLong("Root"));
    }
}
