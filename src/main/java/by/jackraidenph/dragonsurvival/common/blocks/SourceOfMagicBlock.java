package by.jackraidenph.dragonsurvival.common.blocks;

import by.jackraidenph.dragonsurvival.network.nest.SourceOfMagicPlaceholder;
import by.jackraidenph.dragonsurvival.util.Functions;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.server.tileentity.SourceOfMagicTileEntity;
import by.jackraidenph.dragonsurvival.server.tileentity.DSTileEntities;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
    
    public static final VoxelShape SHAPE = VoxelShapes.box(0, 0, 0, 1, 0.1, 1);
    public static final VoxelShape OUTLINE = VoxelShapes.box(0, 0, 0, 1, 0.5, 1);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    static final BooleanProperty PRIMARY_BLOCK = BooleanProperty.create("primary");

    public SourceOfMagicBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false).setValue(PRIMARY_BLOCK, true));
    }
    
    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, WATERLOGGED, PRIMARY_BLOCK);
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

    /**
     * Prevent anyone from breaking the nest
     */
    @Override
    public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
        return 0;
    }

    
    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        UUID uuid = player.getUUID();
        DragonStateHandler dragonStateHandler = player.getCapability(DragonStateProvider.DRAGON_CAPABILITY).orElse(null);
        DragonLevel dragonLevel = dragonStateHandler.getLevel();
        DragonType dragonType = dragonStateHandler.getType();
        TileEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof SourceOfMagicTileEntity && uuid.equals(((SourceOfMagicTileEntity) blockEntity).ownerUUID)) {
            final Direction playerHorizontalFacing = player.getDirection();
            final Direction placementDirection = playerHorizontalFacing.getOpposite();
            if (state.getBlock().getClass() == SourceOfMagicBlock.class && dragonLevel == DragonLevel.YOUNG) {

                if (Functions.isAirOrFluid(pos.relative(playerHorizontalFacing), worldIn) &&
                        Functions.isAirOrFluid(pos.relative(playerHorizontalFacing.getCounterClockWise()), worldIn) &&
                        Functions.isAirOrFluid(pos.relative(playerHorizontalFacing).relative(playerHorizontalFacing.getCounterClockWise()), worldIn)) {
                    CompoundNBT compoundNBT = blockEntity.save(new CompoundNBT());
                    switch (dragonType) {
                        case SEA:
                            worldIn.setBlockAndUpdate(pos, DSBlocks.seaSourceOfMagic.defaultBlockState().setValue(FACING, placementDirection));
                            break;
                        case FOREST:
                            worldIn.setBlockAndUpdate(pos, DSBlocks.forestSourceOfMagic.defaultBlockState().setValue(FACING, placementDirection));
                            break;
                        case CAVE:
                            worldIn.setBlockAndUpdate(pos, DSBlocks.caveSourceOfMagic.defaultBlockState().setValue(FACING, placementDirection));
                    }
                    SourceOfMagicTileEntity nestEntity = getBlockEntity(worldIn, pos);
                    BlockState blockState = worldIn.getBlockState(pos);
                    nestEntity.load(blockState, compoundNBT);
                    blockState.getBlock().setPlacedBy(worldIn, pos, blockState, player, player.getItemInHand(handIn));
                    return ActionResultType.SUCCESS;
                } else {
                    if (worldIn.isClientSide) {
                        player.sendMessage(new TranslationTextComponent("ds.space.occupied"), player.getUUID());
                    }
                    return ActionResultType.CONSUME;
                }
            } else if (state.getBlock().getClass() == SourceOfMagicBlock.class && dragonLevel == DragonLevel.ADULT) {
                if (Functions.isAirOrFluid(pos.north(), worldIn) && Functions.isAirOrFluid(pos.south(), worldIn) &&
                        Functions.isAirOrFluid(pos.west(), worldIn) && Functions.isAirOrFluid(pos.east(), worldIn)
                        && Functions.isAirOrFluid(pos.north().west(), worldIn) && Functions.isAirOrFluid(pos.north().east(), worldIn)
                        && Functions.isAirOrFluid(pos.south().east(), worldIn) && Functions.isAirOrFluid(pos.south().west(), worldIn)) {
                    CompoundNBT compoundNBT = blockEntity.save(new CompoundNBT());
                    switch (dragonType) {
                        case SEA:
                            worldIn.setBlockAndUpdate(pos, DSBlocks.seaSourceOfMagic.defaultBlockState().setValue(FACING, placementDirection));
                            break;
                        case FOREST:
                            worldIn.setBlockAndUpdate(pos, DSBlocks.forestSourceOfMagic.defaultBlockState().setValue(FACING, placementDirection));
                            break;
                        case CAVE:
                            worldIn.setBlockAndUpdate(pos, DSBlocks.caveSourceOfMagic.defaultBlockState().setValue(FACING, placementDirection));
                    }
                    SourceOfMagicTileEntity nestEntity = getBlockEntity(worldIn, pos);
                    BlockState blockState = worldIn.getBlockState(pos);
                    nestEntity.load(blockState, compoundNBT);
                    blockState.getBlock().setPlacedBy(worldIn, pos, blockState, player, player.getItemInHand(handIn));
                    return ActionResultType.SUCCESS;
                } else {
                    if (worldIn.isClientSide) {
                        player.sendMessage(new TranslationTextComponent("ds.space.occupied"), player.getUUID());
                    }
                    return ActionResultType.CONSUME;
                }
            }
        }
        if (player instanceof ServerPlayerEntity && player.getUUID().equals(getBlockEntity(worldIn, pos).ownerUUID)) {
            NetworkHooks.openGui((ServerPlayerEntity) player, getBlockEntity(worldIn, pos), packetBuffer -> packetBuffer.writeBlockPos(pos));
        }
        return ActionResultType.SUCCESS;
    }
    
    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return OUTLINE;
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
    public void setBedOccupied(BlockState state, World world, BlockPos pos, LivingEntity sleeper, boolean occupied) {

    }

    //methods below are required for waterlogged property to work

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
        if (Functions.isAirOrFluid(blockPos.relative(direction), world) && Functions.isAirOrFluid(blockPos.relative(direction.getCounterClockWise()), world)
            && Functions.isAirOrFluid(blockPos.relative(direction).relative(direction.getCounterClockWise()), world))
            superState = super.getStateForPlacement(context).setValue(FACING, direction.getOpposite());
        
        if (superState != null) {
            if (Functions.isAirOrFluid(blockPos.relative(direction.getOpposite()), world) &&
                Functions.isAirOrFluid(blockPos.relative(direction).relative(direction.getClockWise()), world) &&
                Functions.isAirOrFluid(blockPos.relative(direction.getClockWise()), world) &&
                Functions.isAirOrFluid(blockPos.relative(direction.getOpposite()).relative(direction.getCounterClockWise()), world) &&
                Functions.isAirOrFluid(blockPos.relative(direction.getOpposite()).relative(direction.getClockWise()), world) &&
                Functions.isAirOrFluid(blockPos.relative(direction).above(), world) &&
                Functions.isAirOrFluid(blockPos.relative(direction).above().relative(direction.getClockWise()), world) &&
                Functions.isAirOrFluid(blockPos.relative(direction).above().relative(direction.getCounterClockWise()), world)
            )
                return superState;
        }
        return null;
    }
    
    
    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    
        SourceOfMagicTileEntity nestEntity = getBlockEntity(worldIn, pos);
        if (placer != null) {
            DragonStateProvider.getCap(placer).ifPresent(dragonStateHandler -> {
                if (dragonStateHandler.isDragon()) {
                    if (nestEntity.ownerUUID == null) {
                        nestEntity.ownerUUID = placer.getUUID();
                    }
                    if (nestEntity.type == DragonType.NONE) {
                        nestEntity.type = dragonStateHandler.getType();
                    }
                }
            });
        }
        
        if (placer != null) {
            Direction direction = placer.getDirection();
            final BlockPos pos1 = pos.relative(direction.getOpposite());
            worldIn.setBlockAndUpdate(pos1, state.setValue(PRIMARY_BLOCK, false));
            final BlockPos pos2 = pos.relative(direction).relative(direction.getClockWise());
            worldIn.setBlockAndUpdate(pos2, state.setValue(PRIMARY_BLOCK, false));
            final BlockPos pos3 = pos.relative(direction.getClockWise());
            worldIn.setBlockAndUpdate(pos3, state.setValue(PRIMARY_BLOCK, false));
            final BlockPos pos4 = pos.relative(direction.getOpposite()).relative(direction.getCounterClockWise());
            worldIn.setBlockAndUpdate(pos4, state.setValue(PRIMARY_BLOCK, false));
            final BlockPos pos5 = pos.relative(direction.getOpposite()).relative(direction.getClockWise());
            worldIn.setBlockAndUpdate(pos5, state.setValue(PRIMARY_BLOCK, false));
            SourceOfMagicPlaceholder placeHolder1 = (SourceOfMagicPlaceholder) worldIn.getBlockEntity(pos1);
            placeHolder1.rootPos = pos;
            SourceOfMagicPlaceholder placeHolder2 = (SourceOfMagicPlaceholder) worldIn.getBlockEntity(pos2);
            placeHolder2.rootPos = pos;
            SourceOfMagicPlaceholder placeHolder3 = (SourceOfMagicPlaceholder) worldIn.getBlockEntity(pos3);
            placeHolder3.rootPos = pos;
            SourceOfMagicPlaceholder placeHolder4 = (SourceOfMagicPlaceholder) worldIn.getBlockEntity(pos4);
            placeHolder4.rootPos = pos;
            SourceOfMagicPlaceholder placeHolder5 = (SourceOfMagicPlaceholder) worldIn.getBlockEntity(pos5);
            placeHolder5.rootPos = pos;
            
            final BlockPos pos6 = pos.above().relative(direction);
            final BlockPos pos7 = pos.above().relative(direction).relative(direction.getCounterClockWise());
            final BlockPos pos8 = pos.above().relative(direction).relative(direction.getClockWise());
            worldIn.setBlockAndUpdate(pos6, state.setValue(PRIMARY_BLOCK, false));
            worldIn.setBlockAndUpdate(pos7, state.setValue(PRIMARY_BLOCK, false));
            worldIn.setBlockAndUpdate(pos8, state.setValue(PRIMARY_BLOCK, false));
            SourceOfMagicPlaceholder placeHolder6 = (SourceOfMagicPlaceholder) worldIn.getBlockEntity(pos6);
            placeHolder6.rootPos = pos;
            SourceOfMagicPlaceholder placeHolder7 = (SourceOfMagicPlaceholder) worldIn.getBlockEntity(pos7);
            placeHolder7.rootPos = pos;
            SourceOfMagicPlaceholder placeHolder8 = (SourceOfMagicPlaceholder) worldIn.getBlockEntity(pos8);
            placeHolder8.rootPos = pos;
            
        }
    }
    
    
    
    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return state.getValue(PRIMARY_BLOCK) ? BlockRenderType.MODEL : BlockRenderType.INVISIBLE;
    }
    
    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, worldIn, pos, newState, isMoving);
        if (state.getValue(PRIMARY_BLOCK)) {
            Direction direction = state.getValue(FACING);
            //TODO remove redundant one
            worldIn.destroyBlock(pos.relative(direction), false);
            worldIn.destroyBlock(pos.relative(direction.getOpposite()).relative(direction.getClockWise()), false);
            worldIn.destroyBlock(pos.relative(direction.getOpposite()).relative(direction.getOpposite().getClockWise()), false);
            worldIn.destroyBlock(pos.relative(direction).relative(direction.getCounterClockWise()), false);
            worldIn.destroyBlock(pos.relative(direction).relative(direction.getClockWise()), false);
            worldIn.destroyBlock(pos.relative(direction.getCounterClockWise()), false);
            //upper blocks
            worldIn.destroyBlock(pos.above().relative(direction.getOpposite()), false);
            worldIn.destroyBlock(pos.above().relative(direction.getOpposite()).relative(direction.getCounterClockWise()), false);
            worldIn.destroyBlock(pos.above().relative(direction.getOpposite()).relative(direction.getClockWise()), false);
        }
    }
}
