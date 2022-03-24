package by.jackraidenph.dragonsurvival.common.blocks;

import by.jackraidenph.dragonsurvival.server.tileentity.HelmetTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BrokenKnightHelmet extends SkullBlock
{
	public BrokenKnightHelmet(Properties p_56319_)
	{
		super(Types.BROKEN_KNIGHT, p_56319_);
	}
	
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new HelmetTileEntity(pPos, pState);
	}
	
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return null;
	}
	
	public static enum Types implements SkullBlock.Type {
		BROKEN_KNIGHT
	}
}
