package by.dragonsurvivalteam.dragonsurvival.common.blocks;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.status.SyncMagicSourceStatus;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.registry.DSTileEntities;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.SourceOfMagicPlaceholder;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.SourceOfMagicTileEntity;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.SpawningUtils;
import com.mojang.serialization.MapCodec;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

public class SourceOfMagicBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock, EntityBlock{
	public static final VoxelShape SHAPE = Shapes.box(0, 0, 0, 1, 0.25, 1);
	public static final VoxelShape OUTLINE = Shapes.box(0, 0, 0, 1, 0.5, 1);
	public static final VoxelShape FULL_OUTLINE = Shapes.box(0, 0, 0, 1, 0.99, 1);

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty PRIMARY_BLOCK = BooleanProperty.create("primary");
	public static final BooleanProperty FILLED = BooleanProperty.create("filled");
	static final BooleanProperty BACK_BLOCK = BooleanProperty.create("back");
	static final BooleanProperty TOP_BLOCK = BooleanProperty.create("top");

	public SourceOfMagicBlock(Properties properties){
		super(properties);
		registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false).setValue(PRIMARY_BLOCK, true).setValue(BACK_BLOCK, false).setValue(TOP_BLOCK, false).setValue(FILLED, false));
	}

	// TODO: Is it fine to use a unit codec here?
	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return MapCodec.unit(this);
	}

	private static void breakBlock(Level world, BlockPos pos){
		world.destroyBlock(pos, !(world.getBlockEntity(pos) instanceof SourceOfMagicPlaceholder));
		world.removeBlockEntity(pos);
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource){
		if(blockState.getBlock() == DSBlocks.CAVE_SOURCE_OF_MAGIC.get()){
			if(level.getFluidState(blockPos).is(FluidTags.WATER)){
				double d0 = blockPos.getX();
				double d1 = blockPos.getY();
				double d2 = blockPos.getZ();
				level.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, d0 + 0.5D, d1, d2 + 0.5D, 0.0D, 0.04D, 0.0D);
				level.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, d0 + (double)randomSource.nextFloat(), d1 + (double)randomSource.nextFloat(), d2 + (double)randomSource.nextFloat(), 0.0D, 0.04D, 0.0D);
			}else{
				if(blockState.getValue(FILLED)){
					double d0 = blockPos.getX();
					double d1 = blockPos.getY();
					double d2 = blockPos.getZ();
					//  p_180655_2_.addAlwaysVisibleParticle(ParticleTypes.DRIPPING_LAVA, d0 + 0.5D, d1, d2 + 0.5D, 0.0D, 0.04D, 0.0D);
					level.addAlwaysVisibleParticle(ParticleTypes.LAVA, d0 + (double)randomSource.nextFloat(), d1 + (double)randomSource.nextFloat(), d2 + (double)randomSource.nextFloat(), 0.0D, 0.04D, 0.0D);
				}
			}
		}
	}

	@Nullable @Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		BlockState superState = null;
		BlockPos blockPos = context.getClickedPos();
		Level world = context.getLevel();
		Player playerEntity = context.getPlayer();
		Direction direction = playerEntity.getDirection();

		if(SpawningUtils.isAirOrFluid(blockPos.relative(direction), world, context) && SpawningUtils.isAirOrFluid(blockPos.relative(direction.getCounterClockWise()), world, context) && SpawningUtils.isAirOrFluid(blockPos.relative(direction).relative(direction.getCounterClockWise()), world, context)){
			superState = super.getStateForPlacement(context).setValue(FACING, direction.getOpposite());
		}

		if(superState != null){
			if(SpawningUtils.isAirOrFluid(blockPos.relative(direction.getOpposite()), world, context) && SpawningUtils.isAirOrFluid(blockPos.relative(direction).relative(direction.getClockWise()), world, context) && SpawningUtils.isAirOrFluid(blockPos.relative(direction.getClockWise()), world, context) && SpawningUtils.isAirOrFluid(blockPos.relative(direction.getOpposite()).relative(direction.getCounterClockWise()), world, context) && SpawningUtils.isAirOrFluid(blockPos.relative(direction.getOpposite()).relative(direction.getClockWise()), world, context) && SpawningUtils.isAirOrFluid(blockPos.relative(direction).above(), world, context) && SpawningUtils.isAirOrFluid(blockPos.relative(direction).above().relative(direction.getClockWise()), world, context) && SpawningUtils.isAirOrFluid(blockPos.relative(direction).above().relative(direction.getCounterClockWise()), world, context)){
				return superState;
			}

			if(world.isClientSide()){
				playerEntity.sendSystemMessage(Component.translatable("ds.space.occupied"));
			}
		}

		return null;
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state,
		@Nullable LivingEntity placer, ItemStack stack){
		super.setPlacedBy(worldIn, pos, state, placer, stack);

		if(placer != null){
			Direction direction = placer.getDirection();
			setPlaceholder(worldIn, state, pos, pos.relative(direction.getOpposite()));
			setPlaceholder(worldIn, state.setValue(BACK_BLOCK, true), pos, pos.relative(direction));

			setPlaceholder(worldIn, state, pos, pos.relative(direction.getClockWise()));
			setPlaceholder(worldIn, state, pos, pos.relative(direction.getCounterClockWise()));

			setPlaceholder(worldIn, state.setValue(BACK_BLOCK, true), pos, pos.relative(direction).relative(direction.getClockWise()));
			setPlaceholder(worldIn, state.setValue(BACK_BLOCK, true), pos, pos.relative(direction).relative(direction.getCounterClockWise()));

			setPlaceholder(worldIn, state, pos, pos.relative(direction.getOpposite()).relative(direction.getCounterClockWise()));
			setPlaceholder(worldIn, state, pos, pos.relative(direction.getOpposite()).relative(direction.getClockWise()));

			setPlaceholder(worldIn, state.setValue(TOP_BLOCK, true), pos, pos.above().relative(direction));
			setPlaceholder(worldIn, state, pos, pos.above().relative(direction).relative(direction.getCounterClockWise()));
			setPlaceholder(worldIn, state, pos, pos.above().relative(direction).relative(direction.getClockWise()));
		}
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder){
		super.createBlockStateDefinition(builder);
		builder.add(FACING, WATERLOGGED, PRIMARY_BLOCK, BACK_BLOCK, TOP_BLOCK, FILLED);
	}

	private static void setPlaceholder(Level world, BlockState state, BlockPos root, BlockPos newPos){
		world.setBlockAndUpdate(newPos, state.setValue(PRIMARY_BLOCK, false));
		SourceOfMagicPlaceholder placeHolder6 = (SourceOfMagicPlaceholder)world.getBlockEntity(newPos);
		placeHolder6.rootPos = root;
	}

	@Override
	public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState1, LevelAccessor world, BlockPos blockPos, BlockPos blockPos1){
		if(blockState.getValue(WATERLOGGED)){
			world.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		return super.updateShape(blockState, direction, blockState1, world, blockPos, blockPos1);
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving){
		if(!(newState.getBlock() instanceof SourceOfMagicBlock)){
			if(state.getValue(PRIMARY_BLOCK)){
				BlockEntity tileentity = worldIn.getBlockEntity(pos);
				if(tileentity instanceof Container){
					Containers.dropContents(worldIn, pos, (Container)tileentity);
					worldIn.updateNeighbourForOutputSignal(pos, this);
				}

				super.onRemove(state, worldIn, pos, newState, isMoving);

				Direction direction = state.getValue(FACING).getOpposite();

				breakBlock(worldIn, pos);

				breakBlock(worldIn, pos.relative(direction.getOpposite()));
				breakBlock(worldIn, pos.relative(direction));

				breakBlock(worldIn, pos.relative(direction.getClockWise()));
				breakBlock(worldIn, pos.relative(direction.getCounterClockWise()));

				breakBlock(worldIn, pos.relative(direction).relative(direction.getClockWise()));
				breakBlock(worldIn, pos.relative(direction).relative(direction.getCounterClockWise()));

				breakBlock(worldIn, pos.relative(direction.getOpposite()).relative(direction.getCounterClockWise()));
				breakBlock(worldIn, pos.relative(direction.getOpposite()).relative(direction.getClockWise()));

				breakBlock(worldIn, pos.above().relative(direction));
				breakBlock(worldIn, pos.above().relative(direction).relative(direction.getCounterClockWise()));
				breakBlock(worldIn, pos.above().relative(direction).relative(direction.getClockWise()));
			}else{
				BlockEntity tile = worldIn.getBlockEntity(pos);
				if(tile instanceof SourceOfMagicPlaceholder placeholder){
					BlockPos rootPos = placeholder.rootPos;

					if(worldIn.getBlockEntity(rootPos) instanceof SourceOfMagicTileEntity){
						onRemove(worldIn.getBlockState(rootPos), worldIn, rootPos, Blocks.BUBBLE_COLUMN.defaultBlockState(), isMoving);
					}
				}
			}
		}
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player, BlockHitResult hit){
		BlockEntity blockEntity = worldIn.getBlockEntity(pos);
		BlockPos pos1 = pos;

		if(blockEntity instanceof SourceOfMagicPlaceholder){
			pos1 = ((SourceOfMagicPlaceholder)blockEntity).rootPos;
		}

		if(!player.isCrouching()){
			if(player instanceof ServerPlayer serverPlayer){
				BlockPos finalPos = pos1;
				BlockEntity blockEntity1 = getBlockEntity(worldIn, pos1);
				serverPlayer.openMenu((MenuProvider)blockEntity1, packetBuffer -> packetBuffer.writeBlockPos(finalPos));
			}
		}else{
			if(DragonStateProvider.isDragon(player) && player.getMainHandItem().isEmpty()){
				if(player.getBlockStateOn().getBlock() == state.getBlock()){
					DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);

					if(!handler.getMagicData().onMagicSource){
						SourceOfMagicTileEntity source = getBlockEntity(worldIn, pos1);

						if(source != null && !source.isEmpty()){
							if(worldIn.isClientSide()){
								PacketDistributor.sendToServer(new SyncMagicSourceStatus.Data(player.getId(), true, 0));
							}
						}
					}
				}
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public boolean triggerEvent(BlockState pState, Level pLevel, BlockPos pPos, int pId, int pParam){
		super.triggerEvent(pState, pLevel, pPos, pId, pParam);
		BlockEntity blockentity = pLevel.getBlockEntity(pPos);
		return blockentity != null && blockentity.triggerEvent(pId, pParam);
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return state.getValue(PRIMARY_BLOCK) ? RenderShape.MODEL : RenderShape.INVISIBLE;
	}

	@Override
	public FluidState getFluidState(BlockState blockState){
		return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
	}

	@Override
	@Nullable public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos){
		BlockEntity blockentity = pLevel.getBlockEntity(pPos);
		return blockentity instanceof MenuProvider ? (MenuProvider)blockentity : null;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return state.getValue(TOP_BLOCK) || state.getValue(BACK_BLOCK) ? FULL_OUTLINE : OUTLINE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return state.getValue(TOP_BLOCK) ? FULL_OUTLINE : SHAPE;
	}

	@Override
	public void randomTick(BlockState p_225542_1_, ServerLevel world, BlockPos pos, RandomSource p_225542_4_){
		BlockPos blockpos = pos.above();
		if(world.getFluidState(pos).is(FluidTags.WATER)){
			world.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
			world.sendParticles(ParticleTypes.LARGE_SMOKE, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.25D, (double)blockpos.getZ() + 0.5D, 8, 0.5D, 0.25D, 0.5D, 0.0D);
		}
	}

	@Override
	public void entityInside(BlockState pState, Level world, BlockPos pos, Entity entity){
		BlockEntity blockEntity = world.getBlockEntity(pos);
		BlockPos pos1 = pos;

		if(blockEntity instanceof SourceOfMagicPlaceholder){
			pos1 = ((SourceOfMagicPlaceholder)blockEntity).rootPos;
		}

		SourceOfMagicTileEntity source = getBlockEntity(world, pos1);

		if(source != null){
			if(shouldHarmPlayer(pState, entity)){
				if(entity instanceof ItemEntity itemE){
					ItemStack stack = itemE.getItem();
					ItemStack tileStack = source.getItem(0);
					if(SourceOfMagicTileEntity.consumables.containsKey(stack.getItem())){
						if(source.isEmpty()){
							source.setItem(0, stack);
							itemE.kill();
						}else if(ItemStack.isSameItem(tileStack, stack) && tileStack.getCount() < tileStack.getMaxStackSize()){
							int left = tileStack.getMaxStackSize() - tileStack.getCount();
							int toAdd = Math.min(stack.getCount(), left);
							itemE.getItem().shrink(toAdd);
							tileStack.setCount(tileStack.getCount() + toAdd);
						}
						return;
					}
				}

				if(ServerConfig.damageWrongSourceOfMagic){
					entity.hurt(pState.getBlock() == DSBlocks.CAVE_SOURCE_OF_MAGIC.get() ? entity.damageSources().hotFloor() : pState.getBlock() == DSBlocks.SEA_SOURCE_OF_MAGIC.get() ? entity.damageSources().drown() : entity.damageSources().cactus(), 1F);
				}
			}
		}
		super.entityInside(pState, world, pos, entity);
	}
	
	public static boolean shouldHarmPlayer(BlockState pState, Entity entity)
	{
		boolean harm = false;
		AbstractDragonType type = DragonUtils.getDragonType(entity);
		
		if(!Objects.equals(type, DragonTypes.CAVE) && pState.getBlock() == DSBlocks.CAVE_SOURCE_OF_MAGIC.get()){
			harm = true;
		}
		if(!Objects.equals(type, DragonTypes.SEA) && pState.getBlock() == DSBlocks.SEA_SOURCE_OF_MAGIC.get()){
			harm = true;
		}
		if(!Objects.equals(type, DragonTypes.FOREST) && pState.getBlock() == DSBlocks.FOREST_SOURCE_OF_MAGIC.get()){
			harm = true;
		}
		
		return harm;
	}
	
	public SourceOfMagicTileEntity getBlockEntity(Level world, BlockPos pos){
		BlockEntity entity = world.getBlockEntity(pos);
		return entity instanceof SourceOfMagicTileEntity ? (SourceOfMagicTileEntity)entity : null;
	}

	@Override
	public boolean placeLiquid(LevelAccessor p_204509_1_, BlockPos p_204509_2_, BlockState p_204509_3_, FluidState p_204509_4_){
		BlockPos rootPos = null;

		BlockEntity tile = p_204509_1_.getBlockEntity(p_204509_2_);
		if(tile instanceof SourceOfMagicPlaceholder placeholder){
			rootPos = placeholder.rootPos;
		}

		if(!p_204509_3_.getValue(BlockStateProperties.WATERLOGGED) && p_204509_4_.getType() == Fluids.WATER){
			if(!p_204509_1_.isClientSide()){
				p_204509_1_.setBlock(p_204509_2_, p_204509_3_.setValue(BlockStateProperties.WATERLOGGED, Boolean.TRUE), Block.UPDATE_ALL);
				p_204509_1_.scheduleTick(p_204509_2_, p_204509_4_.getType(), p_204509_4_.getType().getTickDelay(p_204509_1_));

				if(rootPos != null){
					SourceOfMagicPlaceholder placeHolder = (SourceOfMagicPlaceholder)p_204509_1_.getBlockEntity(p_204509_2_);
					placeHolder.rootPos = rootPos;
				}
			}

			return true;
		}else{
			return false;
		}
	}

	public Fluid takeLiquid(LevelAccessor p_204508_1_, BlockPos p_204508_2_, BlockState p_204508_3_){
		BlockPos rootPos = null;

		BlockEntity tile = p_204508_1_.getBlockEntity(p_204508_2_);
		if(tile instanceof SourceOfMagicPlaceholder placeholder){
			rootPos = placeholder.rootPos;
		}

		if(p_204508_3_.getValue(BlockStateProperties.WATERLOGGED)){
			p_204508_1_.setBlock(p_204508_2_, p_204508_3_.setValue(BlockStateProperties.WATERLOGGED, Boolean.FALSE), Block.UPDATE_ALL);

			if(rootPos != null){
				SourceOfMagicPlaceholder placeHolder = (SourceOfMagicPlaceholder)p_204508_1_.getBlockEntity(p_204508_2_);
				placeHolder.rootPos = rootPos;
			}

			return Fluids.WATER;
		}else{
			return Fluids.EMPTY;
		}
	}

	@org.jetbrains.annotations.Nullable @Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState){
		if(!pState.getValue(PRIMARY_BLOCK)){
			return DSTileEntities.SOURCE_OF_MAGIC_PLACEHOLDER.get().create(pPos, pState);
		}
		return DSTileEntities.SOURCE_OF_MAGIC_TILE_ENTITY.get().create(pPos, pState);
	}

	@Override
	@Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType){
		return pLevel.isClientSide ? null : BaseEntityBlock.createTickerHelper(pBlockEntityType, DSTileEntities.SOURCE_OF_MAGIC_TILE_ENTITY.get(), SourceOfMagicTileEntity::serverTick);
	}
}