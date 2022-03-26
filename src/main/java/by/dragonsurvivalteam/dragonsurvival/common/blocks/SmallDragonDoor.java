package by.dragonsurvivalteam.dragonsurvival.common.blocks;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;


public class SmallDragonDoor extends Block  implements IWaterLoggable{
	public static final DirectionProperty FACING = HorizontalBlock.FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final EnumProperty<DragonDoorOpenRequirement> OPEN_REQ = EnumProperty.create("open_req", DragonDoorOpenRequirement.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
	protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);

	public SmallDragonDoor(Properties properties, DragonDoorOpenRequirement openRequirement){
		super(properties);
		registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(HINGE, DoorHingeSide.LEFT).setValue(POWERED, false).setValue(OPEN_REQ, openRequirement).setValue(WATERLOGGED, false));
	}

	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context){
		BlockPos blockpos = context.getClickedPos();
		if(blockpos.getY() < 255){
			World world = context.getLevel();
			boolean flag = world.hasNeighborSignal(blockpos) || world.hasNeighborSignal(blockpos.above());
			return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(HINGE, this.getHinge(context)).setValue(POWERED, flag).setValue(OPEN, flag).setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER && defaultBlockState().getBlock() == DSBlocks.seaDoor);
		}else{
			return null;
		}
	}

	// not sure why inheriting this behaviour doesn't work, but confirmed it doesn't
	public void playerDestroy(World p_180657_1_, PlayerEntity p_180657_2_, BlockPos p_180657_3_, BlockState p_180657_4_,
		@Nullable
			TileEntity p_180657_5_, ItemStack p_180657_6_){
		p_180657_2_.awardStat(Stats.BLOCK_MINED.get(this));
		p_180657_2_.causeFoodExhaustion(0.005F);
		dropResources(p_180657_4_, p_180657_1_, p_180657_3_, p_180657_5_, p_180657_2_, p_180657_6_);
	}

	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
		builder.add(FACING, OPEN, HINGE, POWERED, OPEN_REQ, WATERLOGGED);
	}

	private DoorHingeSide getHinge(BlockItemUseContext blockItemUseContext){
		//TODO Logic handling aligning doors
		IBlockReader iblockreader = blockItemUseContext.getLevel();
		BlockPos blockpos = blockItemUseContext.getClickedPos();
		Direction north = blockItemUseContext.getHorizontalDirection();
		Direction directionCounterClockWiseHorizontal = north.getCounterClockWise();
		BlockPos blockpos2 = blockpos.relative(directionCounterClockWiseHorizontal);
		BlockState blockstate = iblockreader.getBlockState(blockpos2);
		Direction direction2 = north.getClockWise();
		BlockPos blockpos4 = blockpos.relative(direction2);
		BlockState blockstate2 = iblockreader.getBlockState(blockpos4);
		int i = (blockstate.isCollisionShapeFullBlock(iblockreader, blockpos2) ? -1 : 0) + (blockstate2.isCollisionShapeFullBlock(iblockreader, blockpos4) ? 1 : 0);
		boolean flag = blockstate.is(this);
		boolean flag1 = blockstate2.is(this);
		if((!flag || flag1) && i <= 0){
			if((!flag1 || flag) && i >= 0){
				int j = north.getStepX();
				int k = north.getStepZ();
				Vector3d vec3d = blockItemUseContext.getClickLocation();
				double d0 = vec3d.x - (double)blockpos.getX();
				double d1 = vec3d.z - (double)blockpos.getZ();
				return (j >= 0 || !(d1 < 0.5D)) && (j <= 0 || !(d1 > 0.5D)) && (k >= 0 || !(d0 > 0.5D)) && (k <= 0 || !(d0 < 0.5D)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
			}else{
				return DoorHingeSide.LEFT;
			}
		}else{
			return DoorHingeSide.RIGHT;
		}
	}

	/**
	 * Used by {@link net.minecraft.entity.ai.brain.task.InteractWithDoorTask}
	 */
	public void toggleDoor(World worldIn, BlockPos pos, boolean open){ // TODO check this for Open Requirements
		BlockState blockstate = worldIn.getBlockState(pos);
		if(blockstate.getBlock() == this && blockstate.getValue(OPEN) != open){
			worldIn.setBlock(pos, blockstate.setValue(OPEN, open), 10);
			this.playSound(worldIn, pos, open);
		}
	}

	private void playSound(World worldIn, BlockPos pos, boolean isOpening){
		worldIn.levelEvent(null, isOpening ? this.getOpenSound() : this.getCloseSound(), pos, 0);
	}

	public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type){
		switch(type){
			case LAND:
			case AIR:
				return state.getValue(OPEN);
			default:
				return false;
		}
	}

	// handles destruction of door when block underneath destroyed
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos){
		if(facing == Direction.DOWN && !stateIn.canSurvive(worldIn, currentPos)){
			return Blocks.AIR.defaultBlockState();
		}
		if(stateIn.getValue(WATERLOGGED)){
			worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
		}
		return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public FluidState getFluidState(BlockState state){
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}


	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		boolean validPower = state.getValue(OPEN_REQ) == DragonDoorOpenRequirement.NONE || state.getValue(OPEN_REQ) == DragonDoorOpenRequirement.POWER;
		boolean validType = (state.getValue(OPEN_REQ) == DragonDoorOpenRequirement.SEA && blockIn == DSBlocks.seaPressurePlate) || (state.getValue(OPEN_REQ) == DragonDoorOpenRequirement.FOREST && blockIn == DSBlocks.forestPressurePlate) || (state.getValue(OPEN_REQ) == DragonDoorOpenRequirement.CAVE && blockIn == DSBlocks.cavePressurePlate);
		if(validPower || validType){
			boolean flag = worldIn.hasNeighborSignal(pos) || worldIn.hasNeighborSignal(pos.relative(Direction.UP));
			if(blockIn != this && flag != state.getValue(POWERED)){
				if(flag != state.getValue(OPEN)){
					this.playSound(worldIn, pos, flag);
				}

				worldIn.setBlock(pos, state.setValue(POWERED, flag).setValue(OPEN, flag), 2);
			}
		}
	}

	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit){
		LazyOptional<DragonStateHandler> dragonStateHandlerLazyOptional = player.getCapability(DragonStateProvider.DRAGON_CAPABILITY);
		if(dragonStateHandlerLazyOptional.isPresent()){
			DragonStateHandler dragonStateHandler = dragonStateHandlerLazyOptional.orElseGet(() -> null);
			if(state.getValue(OPEN_REQ) == DragonDoorOpenRequirement.NONE || (dragonStateHandler.isDragon() && (state.getValue(OPEN_REQ) == DragonDoorOpenRequirement.CAVE && dragonStateHandler.getType() == DragonType.CAVE) || (state.getValue(OPEN_REQ) == DragonDoorOpenRequirement.FOREST && dragonStateHandler.getType() == DragonType.FOREST) || (state.getValue(OPEN_REQ) == DragonDoorOpenRequirement.SEA && dragonStateHandler.getType() == DragonType.SEA))){
				state = state.cycle(OPEN);
				worldIn.setBlock(pos, state, 10);
				worldIn.levelEvent(player, state.getValue(OPEN) ? this.getOpenSound() : this.getCloseSound(), pos, 0);
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}

	protected int getCloseSound(){
		return this.material == Material.METAL ? 1011 : 1012;
	}

	protected int getOpenSound(){
		return this.material == Material.METAL ? 1005 : 1006;
	}

	public PushReaction getPistonPushReaction(BlockState state){
		return state.getValue(OPEN_REQ) == DragonDoorOpenRequirement.NONE ? PushReaction.DESTROY : PushReaction.IGNORE;
	}

	public BlockState rotate(BlockState state, Rotation rot){
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn){
		return mirrorIn == Mirror.NONE ? state : state.rotate(mirrorIn.getRotation(state.getValue(FACING))).cycle(HINGE);
	}

	public long getSeed(BlockState state, BlockPos pos){
		return MathHelper.getSeed(pos.getX(), pos.below(0).getY(), pos.getZ());
	}

	public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos){
		BlockPos blockpos = pos.below();
		BlockState blockstate = worldIn.getBlockState(blockpos);
		return blockstate.isFaceSturdy(worldIn, blockpos, Direction.UP);
	}

	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		Direction direction = state.getValue(FACING);
		boolean flag = !state.getValue(OPEN);
		boolean flag1 = state.getValue(HINGE) == DoorHingeSide.RIGHT;
		switch(direction){
			case EAST:
			default:
				return flag ? EAST_AABB : (flag1 ? NORTH_AABB : SOUTH_AABB);
			case SOUTH:
				return flag ? SOUTH_AABB : (flag1 ? EAST_AABB : WEST_AABB);
			case WEST:
				return flag ? WEST_AABB : (flag1 ? SOUTH_AABB : NORTH_AABB);
			case NORTH:
				return flag ? NORTH_AABB : (flag1 ? WEST_AABB : EAST_AABB);
		}
	}
}