package by.dragonsurvivalteam.dragonsurvival.server.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

public class SourceOfMagicPlaceholder extends BaseBlockTileEntity{
	public BlockPos rootPos = BlockPos.ZERO;

	public SourceOfMagicPlaceholder(TileEntityType<?> tileEntityTypeIn){
		super(tileEntityTypeIn);
	}

	@Override
	public void load(BlockState state, CompoundNBT compound){
		super.load(state, compound);
		rootPos = BlockPos.of(compound.getLong("Root"));
	}

	@Override
	public CompoundNBT save(CompoundNBT compound){
		compound.putLong("Root", rootPos.asLong());
		return super.save(compound);
	}
}