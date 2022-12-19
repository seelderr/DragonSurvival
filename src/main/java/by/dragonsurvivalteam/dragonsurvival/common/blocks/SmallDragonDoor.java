package by.dragonsurvivalteam.dragonsurvival.common.blocks;

import by.dragonsurvivalteam.dragonsurvival.common.capability.Capabilities;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.Objects;


public class SmallDragonDoor extends Block implements SimpleWaterloggedBlock{
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final EnumProperty<DragonDoor.DragonDoorOpenRequirement> OPEN_REQ = EnumProperty.create("open_req", DragonDoor.DragonDoorOpenRequirement.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
	protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);

	public SmallDragonDoor(Properties properties, DragonDoor.DragonDoorOpenRequirement openRequirement){
		super(properties);
		registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(HINGE, DoorHingeSide.LEFT).setValue(POWERED, false).setValue(OPEN_REQ, openRequirement).setValue(WATERLOGGED, false));
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context){
		BlockPos blockpos = context.getClickedPos();
		if(blockpos.getY() < 255){
			Level world = context.getLevel();
			boolean flag = world.hasNeighborSignal(blockpos) || world.hasNeighborSignal(blockpos.above());
			return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(HINGE, this.getHinge(context)).setValue(POWERED, flag).setValue(OPEN, flag).setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
		}else{
			return null;
		}
	}

	private DoorHingeSide getHinge(BlockPlaceContext blockItemUseContext){
		//TODO Logic handling aligning doors
		BlockGetter iblockreader = blockItemUseContext.getLevel();
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
				Vec3 vec3d = blockItemUseContext.getClickLocation();
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

	// not sure why inheriting this behaviour doesn't work, but confirmed it doesn't
	public void playerDestroy(Level p_180657_1_, Player p_180657_2_, BlockPos p_180657_3_, BlockState p_180657_4_,
		@Nullable
			BlockEntity p_180657_5_, ItemStack p_180657_6_){
		p_180657_2_.awardStat(Stats.BLOCK_MINED.get(this));
		p_180657_2_.causeFoodExhaustion(0.005F);
		dropResources(p_180657_4_, p_180657_1_, p_180657_3_, p_180657_5_, p_180657_2_, p_180657_6_);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(FACING, OPEN, HINGE, POWERED, OPEN_REQ, WATERLOGGED);
	}

	public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type){
		switch(type){
			case LAND:
			case AIR:
				return state.getValue(OPEN);
			default:
				return false;
		}
	}

	// handles destruction of door when block underneath destroyed
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos){
		if(facing == Direction.DOWN && !stateIn.canSurvive(worldIn, currentPos)){
			return Blocks.AIR.defaultBlockState();
		}
		if(stateIn.getValue(WATERLOGGED)){
			worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
		}
		return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		boolean validPower = state.getValue(OPEN_REQ) == DragonDoor.DragonDoorOpenRequirement.NONE || state.getValue(OPEN_REQ) == DragonDoor.DragonDoorOpenRequirement.POWER;
		boolean validType = (state.getValue(OPEN_REQ) == DragonDoor.DragonDoorOpenRequirement.SEA && blockIn == DSBlocks.seaPressurePlate) || (state.getValue(OPEN_REQ) == DragonDoor.DragonDoorOpenRequirement.FOREST && blockIn == DSBlocks.forestPressurePlate) || (state.getValue(OPEN_REQ) == DragonDoor.DragonDoorOpenRequirement.CAVE && blockIn == DSBlocks.cavePressurePlate);
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

	private void playSound(Level worldIn, BlockPos pos, boolean isOpening){
		worldIn.levelEvent(null, isOpening ? this.getOpenSound() : this.getCloseSound(), pos, 0);
	}

	protected int getCloseSound(){
		return this.material == Material.METAL ? 1011 : 1012;
	}

	protected int getOpenSound(){
		return this.material == Material.METAL ? 1005 : 1006;
	}

	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit){
		LazyOptional<DragonStateHandler> dragonStateHandlerLazyOptional = player.getCapability(Capabilities.DRAGON_CAPABILITY);
		if(dragonStateHandlerLazyOptional.isPresent()){
			DragonStateHandler dragonStateHandler = dragonStateHandlerLazyOptional.orElseGet(() -> null);
			if(state.getValue(OPEN_REQ) == DragonDoor.DragonDoorOpenRequirement.NONE || dragonStateHandler.isDragon() && state.getValue(OPEN_REQ) == DragonDoor.DragonDoorOpenRequirement.CAVE && Objects.equals(dragonStateHandler.getType(), DragonTypes.CAVE) || state.getValue(
					OPEN_REQ) == DragonDoor.DragonDoorOpenRequirement.FOREST && Objects.equals(dragonStateHandler.getType(), DragonTypes.FOREST) || state.getValue(OPEN_REQ) == DragonDoor.DragonDoorOpenRequirement.SEA && Objects.equals(dragonStateHandler.getType(), DragonTypes.SEA)){
				state = state.cycle(OPEN);
				worldIn.setBlock(pos, state, 10);
				worldIn.levelEvent(player, state.getValue(OPEN) ? this.getOpenSound() : this.getCloseSound(), pos, 0);
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	public PushReaction getPistonPushReaction(BlockState state){
		return state.getValue(OPEN_REQ) == DragonDoor.DragonDoorOpenRequirement.NONE ? PushReaction.DESTROY : PushReaction.IGNORE;
	}

	@Override
	public FluidState getFluidState(BlockState state){
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	public BlockState rotate(BlockState state, Rotation rot){
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn){
		return mirrorIn == Mirror.NONE ? state : state.rotate(mirrorIn.getRotation(state.getValue(FACING))).cycle(HINGE);
	}

	public long getSeed(BlockState state, BlockPos pos){
		return Mth.getSeed(pos.getX(), pos.below(0).getY(), pos.getZ());
	}

	public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos){
		BlockPos blockpos = pos.below();
		BlockState blockstate = worldIn.getBlockState(blockpos);
		return blockstate.isFaceSturdy(worldIn, blockpos, Direction.UP);
	}

	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
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