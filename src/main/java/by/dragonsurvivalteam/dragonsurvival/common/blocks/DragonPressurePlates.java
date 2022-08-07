package by.dragonsurvivalteam.dragonsurvival.common.blocks;

import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.DragonType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class DragonPressurePlates extends PressurePlateBlock implements SimpleWaterloggedBlock{
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

	public DragonPressurePlates(Properties p_i48445_1_, PressurePlateType type){
		super(Sensitivity.EVERYTHING, p_i48445_1_);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(WATERLOGGED, false));

		this.type = type;
	}

	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext){
		return PRESSED_AABB;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction dir, BlockState state2, LevelAccessor level, BlockPos pos, BlockPos pos2){
		if(state.getValue(WATERLOGGED)){
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, dir, state2, level, pos, pos2);
	}

	protected int getSignalStrength(Level pLevel, BlockPos pPos){
		net.minecraft.world.phys.AABB axisalignedbb = TOUCH_AABB.move(pPos);
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
	public BlockState getStateForPlacement(BlockPlaceContext pContext){
		return super.getStateForPlacement(pContext).setValue(FACING, pContext.getHorizontalDirection()).setValue(WATERLOGGED, pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	public FluidState getFluidState(BlockState state){
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	public BlockState rotate(BlockState pState, Rotation pRotation){
		return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
	}

	public BlockState mirror(BlockState pState, Mirror pMirror){
		return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
	}
}