package by.dragonsurvivalteam.dragonsurvival.common.blocks;

import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class DragonPressurePlates extends PressurePlateBlock implements IWaterLoggable{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public PressurePlateType type;

	public enum PressurePlateType{
		DRAGON,
		HUMAN,
		SEA,
		CAVE,
		FOREST
	}

	public VoxelShape getShape(BlockState pState, IBlockReader pLevel, BlockPos pPos, ISelectionContext pContext) {
		return PRESSED_AABB;
	}

	protected DragonPressurePlates(Properties p_i48445_1_, PressurePlateType type){
		super(Sensitivity.EVERYTHING, p_i48445_1_);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(WATERLOGGED, false));

		this.type = type;
	}

	public BlockState rotate(BlockState pState, Rotation pRotation){
		return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
	}

	public BlockState mirror(BlockState pState, Mirror pMirror){
		return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
	}

	protected int getSignalStrength(World pLevel, BlockPos pPos){
		AxisAlignedBB axisalignedbb = TOUCH_AABB.move(pPos);
		List<? extends Entity> list = pLevel.getEntities(null, axisalignedbb);

		if(!list.isEmpty()){
			for(Entity entity : list){
				if(!entity.isIgnoringBlockTriggers()){
					switch(type){
						case DRAGON:
							return DragonUtils.isDragon(entity) ? 15 : 0;

						case HUMAN:
							return !DragonUtils.isDragon(entity) ? 15 : 0;

						case SEA:
							return DragonUtils.isDragon(entity) && DragonUtils.getDragonType(entity) == DragonType.SEA ? 15 : 0;

						case FOREST:
							return DragonUtils.isDragon(entity) && DragonUtils.getDragonType(entity) == DragonType.FOREST ? 15 : 0;

						case CAVE:
							return DragonUtils.isDragon(entity) && DragonUtils.getDragonType(entity) == DragonType.CAVE ? 15 : 0;
					}
				}
			}
		}
		return 0;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder){
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(FACING);
		pBuilder.add(WATERLOGGED);
	}

	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext pContext){
		return super.getStateForPlacement(pContext).setValue(FACING, pContext.getHorizontalDirection()).setValue(WATERLOGGED, pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	public FluidState getFluidState(BlockState state){
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction dir, BlockState state2, IWorld level, BlockPos pos, BlockPos pos2){
		if(state.getValue(WATERLOGGED)){
			level.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, dir, state2, level, pos, pos2);
	}
}