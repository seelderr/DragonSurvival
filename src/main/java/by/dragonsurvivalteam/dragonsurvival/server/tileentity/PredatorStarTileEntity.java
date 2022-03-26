package by.dragonsurvivalteam.dragonsurvival.server.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PredatorStarTileEntity extends BlockEntity{
	private int ticksExisted;
	private float activeRotation;

	public PredatorStarTileEntity(BlockPos pWorldPosition, BlockState pBlockState){
		super(DSTileEntities.PREDATOR_STAR_TILE_ENTITY_TYPE, pWorldPosition, pBlockState);

		ticksExisted = 0;
		activeRotation = 0;
	}

	public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, PredatorStarTileEntity pBlockEntity){
		++pBlockEntity.ticksExisted;

		if(pLevel.isClientSide){
			++pBlockEntity.activeRotation;
		}
	}

	public int getTicksExisted(){
		return ticksExisted;
	}

	public float getActiveRotation(float partialTicks){
		return (this.activeRotation + partialTicks) * -0.0375F;
	}
}