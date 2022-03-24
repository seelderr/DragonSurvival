package by.dragonsurvivalteam.dragonsurvival.common.blocks;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/blocks/TreasureBlock.java
import by.jackraidenph.dragonsurvival.client.particles.TreasureParticleData;
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.status.SyncTreasureRestStatus;
import by.jackraidenph.dragonsurvival.util.DragonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
=======
import by.dragonsurvivalteam.dragonsurvival.client.particles.TreasureParticleData;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.status.SyncTreasureRestStatus;
import net.minecraft.block.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/blocks/TreasureBlock.java
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.List;
import java.util.Optional;
import java.util.Random;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/blocks/TreasureBlock.java
public class TreasureBlock extends FallingBlock implements SimpleWaterloggedBlock
{
	public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	protected static final VoxelShape[] SHAPE_BY_LAYER = new VoxelShape[]{Shapes.empty(), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};
	
	private Color effectColor;
	
	public TreasureBlock(Color c, Properties p_i48328_1_)
	{
=======
public class TreasureBlock extends FallingBlock implements IWaterLoggable{
	public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	protected static final VoxelShape[] SHAPE_BY_LAYER = new VoxelShape[]{VoxelShapes.empty(), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

	private final Color effectColor;

	public TreasureBlock(Color c, Properties p_i48328_1_){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/blocks/TreasureBlock.java
		super(p_i48328_1_);
		this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, Integer.valueOf(1)).setValue(WATERLOGGED, false));
		this.effectColor = c;
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/blocks/TreasureBlock.java
	
	@Override
	public void appendHoverText(ItemStack pStack, @org.jetbrains.annotations.Nullable BlockGetter pLevel, List<net.minecraft.network.chat.Component> pTooltip, TooltipFlag pFlag)
	{
		super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
		pTooltip.add(new TranslatableComponent("ds.description.treasures"));
	}
	
	@Override
	public boolean isBed(BlockState state, BlockGetter world, BlockPos pos, @Nullable
			Entity player)
	{
		return true;
	}
	
	public Optional<Vec3> getBedSpawnPosition(EntityType<?> entityType, BlockState state, LevelReader world, BlockPos pos, float orientation, @Nullable
			LivingEntity sleeper)
	{
		if (world instanceof Level)
		{
=======

	@Override
	public boolean isBed(BlockState state, IBlockReader world, BlockPos pos,
		@Nullable
			Entity player){
		return true;
	}

	public Optional<Vector3d> getBedSpawnPosition(EntityType<?> entityType, BlockState state, IWorldReader world, BlockPos pos, float orientation,
		@Nullable
			LivingEntity sleeper){
		if(world instanceof World){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/blocks/TreasureBlock.java
			return RespawnAnchorBlock.findStandUpPosition(entityType, world, pos);
		}

		return Optional.empty();
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/blocks/TreasureBlock.java
	
	public boolean isPossibleToRespawnInThis() {
		return true;
	}
	
	public void tick(BlockState p_225534_1_, ServerLevel p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
		boolean belowEmpty = p_225534_2_.isEmptyBlock(p_225534_3_.below()) || (isFree(p_225534_2_.getBlockState(p_225534_3_.below()))) && p_225534_3_.getY() >= 0;
		boolean lowerLayer = p_225534_2_.getBlockState(p_225534_3_.below()).getBlock() == p_225534_1_.getBlock() && p_225534_2_.getBlockState(p_225534_3_.below()).getValue(LAYERS) < 8;
		if (belowEmpty || lowerLayer) {
			FallingBlockEntity fallingblockentity = new FallingBlockEntity(p_225534_2_, (double)p_225534_3_.getX() + 0.5D, (double)p_225534_3_.getY(), (double)p_225534_3_.getZ() + 0.5D, p_225534_2_.getBlockState(p_225534_3_)){
				@Override
				public void tick()
				{
					BlockState state = level.getBlockState(blockPosition().below());
					
					if(state.getBlock() == getBlockState().getBlock()){
						int i = state.getValue(LAYERS);
						
						if(i > 0 && i < 8){
							int missingLayers = 8 - i;
							int newLayers = getBlockState().getValue(LAYERS);
							int leftOver = 0;
							
							if(newLayers > missingLayers){
								leftOver = newLayers - missingLayers;
								newLayers = missingLayers;
							}
							
							level.setBlockAndUpdate(blockPosition().below(), state.setValue(LAYERS, Integer.valueOf(Math.min(8, i + newLayers))));
							
							if(leftOver > 0){
								p_225534_2_.setBlock(blockPosition(), getBlockState().setValue(LAYERS, Integer.valueOf(Math.min(8, leftOver))), 3);
								
							}else {
								p_225534_2_.setBlock(p_225534_3_, Blocks.AIR.defaultBlockState(), 3);
							}
							
							this.remove(RemovalReason.DISCARDED);
							return;
						}
					}
					
					super.tick();
				}
			};
			this.falling(fallingblockentity);
			p_225534_2_.addFreshEntity(fallingblockentity);
			
		}
	}
	
	public boolean isPathfindable(BlockState p_196266_1_, BlockGetter p_196266_2_, BlockPos p_196266_3_, PathComputationType p_196266_4_) {
		switch(p_196266_4_) {
=======

	public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_){
		switch(p_196266_4_){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/blocks/TreasureBlock.java
			case LAND:
				return p_196266_1_.getValue(LAYERS) < 5;
			case WATER:
				return false;
			case AIR:
				return false;
			default:
				return false;
		}
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/blocks/TreasureBlock.java
	
	public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
		return SHAPE_BY_LAYER[p_220053_1_.getValue(LAYERS)];
	}
	
	public VoxelShape getCollisionShape(BlockState p_220071_1_, BlockGetter p_220071_2_, BlockPos p_220071_3_, CollisionContext p_220071_4_) {
		if(p_220071_4_ instanceof EntityCollisionContext && ((EntityCollisionContext)p_220071_4_).getEntity() instanceof FallingBlockEntity){
			return SHAPE_BY_LAYER[p_220071_1_.getValue(LAYERS)];
		}
		
		return SHAPE_BY_LAYER[Math.max(p_220071_1_.getValue(LAYERS)-1, 0)];
	}
	
	public VoxelShape getBlockSupportShape(BlockState p_230335_1_, BlockGetter p_230335_2_, BlockPos p_230335_3_) {
		return SHAPE_BY_LAYER[p_230335_1_.getValue(LAYERS)];
	}
	
	public VoxelShape getVisualShape(BlockState p_230322_1_, BlockGetter p_230322_2_, BlockPos p_230322_3_, CollisionContext p_230322_4_) {
		return Shapes.empty();
	}
	
	@OnlyIn(Dist.CLIENT)
	public float getShadeBrightness(BlockState p_220080_1_, BlockGetter p_220080_2_, BlockPos p_220080_3_) {
		return 1.0F;
	}
	
	public boolean propagatesSkylightDown(BlockState p_200123_1_, BlockGetter p_200123_2_, BlockPos p_200123_3_) {
		return true;
	}
	
	public boolean useShapeForLightOcclusion(BlockState p_220074_1_) {
		return true;
	}
	
	@Override
	public InteractionResult use(BlockState p_225533_1_, Level  world, BlockPos p_225533_3_, Player player, InteractionHand hand, BlockHitResult p_225533_6_)
	{
		if(DragonUtils.isDragon(player) && player.getItemInHand(hand).isEmpty()){
			if(player.getFeetBlockState().getBlock() == p_225533_1_.getBlock()) {
				DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
				
				if(handler != null) {
					if (!handler.treasureResting) {
						if (world.isClientSide) {
							NetworkHandler.CHANNEL.sendToServer(new SyncTreasureRestStatus(player.getId(), true));
						}
						
						return InteractionResult.SUCCESS;
=======

	@Override
	public ActionResultType use(BlockState p_225533_1_, World world, BlockPos p_225533_3_, PlayerEntity player, Hand hand, BlockRayTraceResult p_225533_6_){
		if(DragonUtils.isDragon(player) && player.getItemInHand(hand).isEmpty()){
			if(player.getFeetBlockState().getBlock() == p_225533_1_.getBlock()){
				DragonStateHandler handler = DragonUtils.getHandler(player);

				if(handler != null){
					if(!handler.treasureResting){
						if(world.isClientSide){
							NetworkHandler.CHANNEL.sendToServer(new SyncTreasureRestStatus(player.getId(), true));
						}

						return ActionResultType.SUCCESS;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/blocks/TreasureBlock.java
					}

					if(!world.isClientSide){
						player.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/blocks/TreasureBlock.java
						ServerPlayer serverplayerentity = (ServerPlayer)player;
						if (serverplayerentity.getRespawnPosition() == null
						    || serverplayerentity.getRespawnDimension() != world.dimension()
						    || serverplayerentity.getRespawnPosition() != null && !serverplayerentity.getRespawnPosition().equals(p_225533_3_) && serverplayerentity.getRespawnPosition().distSqr(p_225533_3_) > 40) {
=======
						ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)player;
						if(serverplayerentity.getRespawnPosition() == null || serverplayerentity.getRespawnDimension() != world.dimension() || !(serverplayerentity.level.getBlockState(serverplayerentity.getRespawnPosition()).getBlock() instanceof TreasureBlock) || serverplayerentity.getRespawnPosition() != null && !serverplayerentity.getRespawnPosition().equals(p_225533_3_) && serverplayerentity.getRespawnPosition().distSqr(p_225533_3_) > 40){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/blocks/TreasureBlock.java
							serverplayerentity.setRespawnPosition(world.dimension(), p_225533_3_, 0.0F, false, true);
							return InteractionResult.SUCCESS;
						}
					}
				}
			}
		}

		return super.use(p_225533_1_, world, p_225533_3_, player, hand, p_225533_6_);
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/blocks/TreasureBlock.java
	
	public boolean canBeReplaced(BlockState p_196253_1_, BlockPlaceContext p_196253_2_) {
=======

	public boolean useShapeForLightOcclusion(BlockState p_220074_1_){
		return true;
	}

	@Override
	public FluidState getFluidState(BlockState state){
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	public boolean canBeReplaced(BlockState p_196253_1_, BlockItemUseContext p_196253_2_){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/blocks/TreasureBlock.java
		int i = p_196253_1_.getValue(LAYERS);

		if(p_196253_2_.getItemInHand().getItem() == this.asItem() && i < 8){
			if(p_196253_2_.replacingClickedOnBlock()){
				return p_196253_2_.getClickedFace() == Direction.UP;
			}
		}

		return false;
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/blocks/TreasureBlock.java
	
	@Override
	public void onBrokenAfterFall(Level world, BlockPos pos, FallingBlockEntity entity)
	{
		BlockState state = world.getBlockState(pos);
		if(state.getBlock() instanceof TreasureBlock){
			if(state.getBlock() == entity.getBlockState().getBlock()){
				int i = state.getValue(LAYERS);
				world.setBlockAndUpdate(pos, state.setValue(LAYERS, Integer.valueOf(Math.min(8, i + entity.getBlockState().getValue(LAYERS)))));
			}
=======

	public VoxelShape getBlockSupportShape(BlockState p_230335_1_, IBlockReader p_230335_2_, BlockPos p_230335_3_){
		return SHAPE_BY_LAYER[p_230335_1_.getValue(LAYERS)];
	}

	@OnlyIn( Dist.CLIENT )
	public float getShadeBrightness(BlockState p_220080_1_, IBlockReader p_220080_2_, BlockPos p_220080_3_){
		return 1.0F;
	}

	public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_){
		return SHAPE_BY_LAYER[p_220053_1_.getValue(LAYERS)];
	}

	public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_){
		if(p_220071_4_.getEntity() instanceof FallingBlockEntity){
			return SHAPE_BY_LAYER[p_220071_1_.getValue(LAYERS)];
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/blocks/TreasureBlock.java
		}

		return SHAPE_BY_LAYER[Math.max(p_220071_1_.getValue(LAYERS) - 1, 0)];
	}

	public VoxelShape getVisualShape(BlockState p_230322_1_, IBlockReader p_230322_2_, BlockPos p_230322_3_, ISelectionContext p_230322_4_){
		return VoxelShapes.empty();
	}

	public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_){
		return true;
	}

	@Nullable
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/blocks/TreasureBlock.java
	public BlockState getStateForPlacement(BlockPlaceContext p_196258_1_) {
=======
	public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/blocks/TreasureBlock.java
		BlockState blockstate = p_196258_1_.getLevel().getBlockState(p_196258_1_.getClickedPos());
		if(blockstate.is(this)){
			int i = blockstate.getValue(LAYERS);
			return blockstate.setValue(LAYERS, Integer.valueOf(Math.min(8, i + 1))).setValue(WATERLOGGED, p_196258_1_.getLevel().getFluidState(p_196258_1_.getClickedPos()).getType() == Fluids.WATER);
		}else{
			return super.getStateForPlacement(p_196258_1_).setValue(WATERLOGGED, p_196258_1_.getLevel().getFluidState(p_196258_1_.getClickedPos()).getType() == Fluids.WATER);
		}
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/blocks/TreasureBlock.java
	
	
	protected void createBlockStateDefinition(Builder<Block, BlockState> p_206840_1_) {
=======

	public boolean isPossibleToRespawnInThis(){
		return true;
	}

	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/blocks/TreasureBlock.java
		p_206840_1_.add(LAYERS);
		p_206840_1_.add(WATERLOGGED);
	}

	@Override
	public void appendHoverText(ItemStack p_190948_1_,
		@Nullable
			IBlockReader p_190948_2_, List<ITextComponent> p_190948_3_, ITooltipFlag p_190948_4_){
		super.appendHoverText(p_190948_1_, p_190948_2_, p_190948_3_, p_190948_4_);
		p_190948_3_.add(new TranslationTextComponent("ds.description.treasures"));
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/blocks/TreasureBlock.java
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos)
	{
		if (pState.getValue(WATERLOGGED)) {
			pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
=======
	public BlockState updateShape(BlockState state, Direction dir, BlockState state2, IWorld level, BlockPos pos, BlockPos pos2){
		if(state.getValue(WATERLOGGED)){
			level.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/blocks/TreasureBlock.java
		}
		return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/blocks/TreasureBlock.java
	
	@Override
	@OnlyIn( Dist.CLIENT)
	public void animateTick(BlockState block, Level  world, BlockPos pos, Random random)
	{
=======

	public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_){
		boolean belowEmpty = p_225534_2_.isEmptyBlock(p_225534_3_.below()) || (isFree(p_225534_2_.getBlockState(p_225534_3_.below()))) && p_225534_3_.getY() >= 0;
		boolean lowerLayer = p_225534_2_.getBlockState(p_225534_3_.below()).getBlock() == p_225534_1_.getBlock() && p_225534_2_.getBlockState(p_225534_3_.below()).getValue(LAYERS) < 8;
		if(belowEmpty || lowerLayer){
			FallingBlockEntity fallingblockentity = new FallingBlockEntity(p_225534_2_, (double)p_225534_3_.getX() + 0.5D, p_225534_3_.getY(), (double)p_225534_3_.getZ() + 0.5D, p_225534_2_.getBlockState(p_225534_3_)){
				@Override
				public void tick(){
					BlockState state = level.getBlockState(blockPosition().below());

					if(state.getBlock() == getBlockState().getBlock()){
						int i = state.getValue(LAYERS);

						if(i > 0 && i < 8){
							int missingLayers = 8 - i;
							int newLayers = getBlockState().getValue(LAYERS);
							int leftOver = 0;

							if(newLayers > missingLayers){
								leftOver = newLayers - missingLayers;
								newLayers = missingLayers;
							}

							level.setBlockAndUpdate(blockPosition().below(), state.setValue(LAYERS, Integer.valueOf(Math.min(8, i + newLayers))));

							if(leftOver > 0){
								p_225534_2_.setBlock(blockPosition(), getBlock().defaultBlockState().setValue(LAYERS, Integer.valueOf(Math.min(8, leftOver))), 3);
							}else{
								p_225534_2_.setBlock(p_225534_3_, Blocks.AIR.defaultBlockState(), 3);
							}

							this.remove();
							return;
						}
					}

					super.tick();
				}
			};
			this.falling(fallingblockentity);
			p_225534_2_.addFreshEntity(fallingblockentity);
		}
	}

	@Override
	public void onBroken(World world, BlockPos pos, FallingBlockEntity entity){
		BlockState state = world.getBlockState(pos);
		if(state.getBlock() instanceof TreasureBlock){
			if(state.getBlock() == entity.getBlockState().getBlock()){
				int i = state.getValue(LAYERS);
				world.setBlockAndUpdate(pos, state.setValue(LAYERS, Integer.valueOf(Math.min(8, i + entity.getBlockState().getValue(LAYERS)))));
			}
		}
	}

	@OnlyIn( Dist.CLIENT )
	public void animateTick(BlockState block, World world, BlockPos pos, Random random){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/blocks/TreasureBlock.java
		double d1 = random.nextDouble();
		double d2 = (block.getValue(LAYERS)) * (1.0 / 8) + .1;
		double d3 = random.nextDouble();

		if(world.isEmptyBlock(pos.above())){
			if(random.nextInt(100) < 35){
				world.addParticle(new TreasureParticleData(effectColor.getRed() / 255F, effectColor.getGreen() / 255F, effectColor.getBlue() / 255F, 1F), (double)pos.getX() + d1, (double)pos.getY() + d2, (double)pos.getZ() + d3, 0.0D, 0.0D, 0.0D);
			}
		}
	}
}