package by.jackraidenph.dragonsurvival.common.blocks;

import by.jackraidenph.dragonsurvival.server.tileentity.DSTileEntities;
import by.jackraidenph.dragonsurvival.server.tileentity.SourceOfMagicPlaceholder;
import by.jackraidenph.dragonsurvival.server.tileentity.SourceOfMagicTileEntity;
import by.jackraidenph.dragonsurvival.util.Functions;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class SourceOfMagicBlock extends HorizontalBlock implements IWaterLoggable {
    
    public static final VoxelShape SHAPE = VoxelShapes.box(0, 0, 0, 1, 0.25, 1);
    public static final VoxelShape OUTLINE = VoxelShapes.box(0, 0, 0, 1, 0.5, 1);
    public static final VoxelShape FULL_OUTLINE = VoxelShapes.box(0, 0, 0, 1, 0.99, 1);
    
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    static final BooleanProperty PRIMARY_BLOCK = BooleanProperty.create("primary");
    
    static final BooleanProperty BACK_BLOCK = BooleanProperty.create("back");
    static final BooleanProperty TOP_BLOCK = BooleanProperty.create("top");

    public SourceOfMagicBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any()
                                     .setValue(WATERLOGGED, false)
                                     .setValue(PRIMARY_BLOCK, true)
                                     .setValue(BACK_BLOCK, false)
                                     .setValue(TOP_BLOCK, false));
    }
    
    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, WATERLOGGED, PRIMARY_BLOCK, BACK_BLOCK, TOP_BLOCK);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        if (!state.getValue(PRIMARY_BLOCK))
            return DSTileEntities.sourceOfMagicPlaceholder.create();
        return DSTileEntities.sourceOfMagicTileEntity.create();
    }

    public SourceOfMagicTileEntity getBlockEntity(World world, BlockPos pos) {
        TileEntity entity = world.getBlockEntity(pos);
        return entity instanceof SourceOfMagicTileEntity ? (SourceOfMagicTileEntity)entity : null;
    }
    
    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        UUID uuid = player.getUUID();
        TileEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof SourceOfMagicTileEntity) {
            final Direction playerHorizontalFacing = player.getDirection();
            final Direction placementDirection = playerHorizontalFacing.getOpposite();
            if (state.getBlock().getClass() == SourceOfMagicBlock.class) {
                if (Functions.isAirOrFluid(pos.north(), worldIn, player, hit) && Functions.isAirOrFluid(pos.south(), worldIn, player, hit) &&
                    Functions.isAirOrFluid(pos.west(), worldIn, player, hit) && Functions.isAirOrFluid(pos.east(), worldIn, player, hit)
                    && Functions.isAirOrFluid(pos.north().west(), worldIn, player, hit) && Functions.isAirOrFluid(pos.north().east(), worldIn, player, hit)
                    && Functions.isAirOrFluid(pos.south().east(), worldIn, player, hit) && Functions.isAirOrFluid(pos.south().west(), worldIn, player, hit)) {
                    CompoundNBT compoundNBT = blockEntity.save(new CompoundNBT());
                    worldIn.setBlockAndUpdate(pos, state.setValue(FACING, placementDirection));
                    SourceOfMagicTileEntity nestEntity = getBlockEntity(worldIn, pos);
                    BlockState blockState = worldIn.getBlockState(pos);
                    nestEntity.load(blockState, compoundNBT);
                    blockState.getBlock().setPlacedBy(worldIn, pos, blockState, player, player.getItemInHand(handIn));
                    return ActionResultType.SUCCESS;
                }
            }
        }
        if (player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui((ServerPlayerEntity) player, getBlockEntity(worldIn, pos), packetBuffer -> packetBuffer.writeBlockPos(pos));
        }
        return ActionResultType.SUCCESS;
    }
    
    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return state.getValue(TOP_BLOCK) ? FULL_OUTLINE : SHAPE;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return state.getValue(TOP_BLOCK) || state.getValue(BACK_BLOCK) ? FULL_OUTLINE : OUTLINE;
    }

    @Override
    public boolean triggerEvent(BlockState state, World worldIn, BlockPos pos, int id, int param) {
        TileEntity tileentity = worldIn.getBlockEntity(pos);
        return tileentity != null && tileentity.triggerEvent(id, param);
    }

    @Override
    public boolean isBed(BlockState state, IBlockReader world, BlockPos pos, @Nullable Entity player) {
        return true;
    }

    @Override
    public void setBedOccupied(BlockState state, World world, BlockPos pos, LivingEntity sleeper, boolean occupied) {}
    
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState1, IWorld world, BlockPos blockPos, BlockPos blockPos1) {
        if (blockState.getValue(WATERLOGGED)) {
            world.getLiquidTicks().scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return super.updateShape(blockState, direction, blockState1, world, blockPos, blockPos1);
    }

    public FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState superState = null;
        BlockPos blockPos = context.getClickedPos();
        World world = context.getLevel();
        PlayerEntity playerEntity = context.getPlayer();
        Direction direction = playerEntity.getDirection();
        if (Functions.isAirOrFluid(blockPos.relative(direction), world, context) && Functions.isAirOrFluid(blockPos.relative(direction.getCounterClockWise()), world, context)
            && Functions.isAirOrFluid(blockPos.relative(direction).relative(direction.getCounterClockWise()), world, context))
            superState = super.getStateForPlacement(context).setValue(FACING, direction.getOpposite());
        
        if (superState != null) {
            if (Functions.isAirOrFluid(blockPos.relative(direction.getOpposite()), world, context) &&
                Functions.isAirOrFluid(blockPos.relative(direction).relative(direction.getClockWise()), world, context) &&
                Functions.isAirOrFluid(blockPos.relative(direction.getClockWise()), world, context) &&
                Functions.isAirOrFluid(blockPos.relative(direction.getOpposite()).relative(direction.getCounterClockWise()), world, context) &&
                Functions.isAirOrFluid(blockPos.relative(direction.getOpposite()).relative(direction.getClockWise()), world, context) &&
                Functions.isAirOrFluid(blockPos.relative(direction).above(), world, context) &&
                Functions.isAirOrFluid(blockPos.relative(direction).above().relative(direction.getClockWise()), world, context) &&
                Functions.isAirOrFluid(blockPos.relative(direction).above().relative(direction.getCounterClockWise()), world, context)
            )
                return superState;
        }
    
        if (world.isClientSide) {
            playerEntity.sendMessage(new TranslationTextComponent("ds.space.occupied"), playerEntity.getUUID());
        }
        
        return null;
    }
    
    
    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        
        if (placer != null) {
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
    
    private static void setPlaceholder(World world, BlockState state, BlockPos root, BlockPos newPos){
        world.setBlockAndUpdate(newPos, state.setValue(PRIMARY_BLOCK, false));
        SourceOfMagicPlaceholder placeHolder6 = (SourceOfMagicPlaceholder) world.getBlockEntity(newPos);
        placeHolder6.rootPos = root;
    }
    
    private static void breakBlock(World world, BlockPos pos){
        world.destroyBlock(pos,false);
        world.removeBlockEntity(pos);
    }
    
    
    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return state.getValue(PRIMARY_BLOCK) ? BlockRenderType.MODEL : BlockRenderType.INVISIBLE;
    }
    
    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if(!(newState.getBlock() instanceof SourceOfMagicBlock)) {
            if (state.getValue(PRIMARY_BLOCK)) {
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
        
            } else {
                TileEntity tile = worldIn.getBlockEntity(pos);
                if (tile instanceof SourceOfMagicPlaceholder) {
                    SourceOfMagicPlaceholder placeholder = (SourceOfMagicPlaceholder)tile;
                    BlockPos rootPos = placeholder.rootPos;
            
                    if (worldIn.getBlockEntity(rootPos) instanceof SourceOfMagicTileEntity) {
                        onRemove(worldIn.getBlockState(rootPos), worldIn, rootPos, Blocks.AIR.defaultBlockState(), isMoving);
                    }
                }
            }
        }
    }
    
    public boolean placeLiquid(IWorld p_204509_1_, BlockPos p_204509_2_, BlockState p_204509_3_, FluidState p_204509_4_) {
        BlockPos rootPos = null;
        
        TileEntity tile = p_204509_1_.getBlockEntity(p_204509_2_);
        if (tile instanceof SourceOfMagicPlaceholder) {
            SourceOfMagicPlaceholder placeholder = (SourceOfMagicPlaceholder)tile;
            rootPos = placeholder.rootPos;
        }
        
        if (!p_204509_3_.getValue(BlockStateProperties.WATERLOGGED) && p_204509_4_.getType() == Fluids.WATER) {
            if (!p_204509_1_.isClientSide()) {
                p_204509_1_.setBlock(p_204509_2_, p_204509_3_.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true)), 3);
                p_204509_1_.getLiquidTicks().scheduleTick(p_204509_2_, p_204509_4_.getType(), p_204509_4_.getType().getTickDelay(p_204509_1_));
                
                if(rootPos != null){
                    SourceOfMagicPlaceholder placeHolder = (SourceOfMagicPlaceholder) p_204509_1_.getBlockEntity(p_204509_2_);
                    placeHolder.rootPos = rootPos;
                }
            }
            
            return true;
        } else {
            return false;
        }
    }
    
    public Fluid takeLiquid(IWorld p_204508_1_, BlockPos p_204508_2_, BlockState p_204508_3_) {
        BlockPos rootPos = null;
    
        TileEntity tile = p_204508_1_.getBlockEntity(p_204508_2_);
        if (tile instanceof SourceOfMagicPlaceholder) {
            SourceOfMagicPlaceholder placeholder = (SourceOfMagicPlaceholder)tile;
            rootPos = placeholder.rootPos;
        }
        
        if (p_204508_3_.getValue(BlockStateProperties.WATERLOGGED)) {
            p_204508_1_.setBlock(p_204508_2_, p_204508_3_.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false)), 3);
    
            if(rootPos != null){
                SourceOfMagicPlaceholder placeHolder = (SourceOfMagicPlaceholder) p_204508_1_.getBlockEntity(p_204508_2_);
                placeHolder.rootPos = rootPos;
            }
            
            return Fluids.WATER;
        } else {
            return Fluids.EMPTY;
        }
    }
}
