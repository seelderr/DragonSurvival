package by.jackraidenph.dragonsurvival.common.blocks.nest;

import by.jackraidenph.dragonsurvival.common.blocks.DSBlocks;
import by.jackraidenph.dragonsurvival.util.Functions;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.server.tileentity.NestTileEntity;
import by.jackraidenph.dragonsurvival.server.tileentity.DSTileEntities;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
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

public class NestBlock extends HorizontalBlock implements IWaterLoggable {

    public static final VoxelShape SHAPE = VoxelShapes.box(0, 0, 0, 1, 0.1, 1);
    public static final VoxelShape OUTLINE = VoxelShapes.box(0, 0, 0, 1, 0.5, 1);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public NestBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return DSTileEntities.nestEntityTile.create();
    }

    public NestTileEntity getBlockEntity(World world, BlockPos pos) {
        return (NestTileEntity) world.getBlockEntity(pos);
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
        if (blockEntity instanceof NestTileEntity && uuid.equals(((NestTileEntity) blockEntity).ownerUUID)) {
            final Direction playerHorizontalFacing = player.getDirection();
            final Direction placementDirection = playerHorizontalFacing.getOpposite();
            if (state.getBlock().getClass() == NestBlock.class && dragonLevel == DragonLevel.YOUNG) {

                if (Functions.isAirOrFluid(pos.relative(playerHorizontalFacing), worldIn) &&
                        Functions.isAirOrFluid(pos.relative(playerHorizontalFacing.getCounterClockWise()), worldIn) &&
                        Functions.isAirOrFluid(pos.relative(playerHorizontalFacing).relative(playerHorizontalFacing.getCounterClockWise()), worldIn)) {
                    CompoundNBT compoundNBT = blockEntity.save(new CompoundNBT());
                    switch (dragonType) {
                        case SEA:
                            worldIn.setBlockAndUpdate(pos, DSBlocks.mediumSeaNest.defaultBlockState().setValue(FACING, placementDirection));
                            break;
                        case FOREST:
                            worldIn.setBlockAndUpdate(pos, DSBlocks.mediumForestNest.defaultBlockState().setValue(FACING, placementDirection));
                            break;
                        case CAVE:
                            worldIn.setBlockAndUpdate(pos, DSBlocks.mediumCaveNest.defaultBlockState().setValue(FACING, placementDirection));
                    }
                    NestTileEntity nestEntity = getBlockEntity(worldIn, pos);
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
            } else if (state.getBlock().getClass() == NestBlock.class && dragonLevel == DragonLevel.ADULT) {
                if (Functions.isAirOrFluid(pos.north(), worldIn) && Functions.isAirOrFluid(pos.south(), worldIn) &&
                        Functions.isAirOrFluid(pos.west(), worldIn) && Functions.isAirOrFluid(pos.east(), worldIn)
                        && Functions.isAirOrFluid(pos.north().west(), worldIn) && Functions.isAirOrFluid(pos.north().east(), worldIn)
                        && Functions.isAirOrFluid(pos.south().east(), worldIn) && Functions.isAirOrFluid(pos.south().west(), worldIn)) {
                    CompoundNBT compoundNBT = blockEntity.save(new CompoundNBT());
                    switch (dragonType) {
                        case SEA:
                            worldIn.setBlockAndUpdate(pos, DSBlocks.bigSeaNest.defaultBlockState().setValue(FACING, placementDirection));
                            break;
                        case FOREST:
                            worldIn.setBlockAndUpdate(pos, DSBlocks.bigForestNest.defaultBlockState().setValue(FACING, placementDirection));
                            break;
                        case CAVE:
                            worldIn.setBlockAndUpdate(pos, DSBlocks.bigCaveNest.defaultBlockState().setValue(FACING, placementDirection));
                    }
                    NestTileEntity nestEntity = getBlockEntity(worldIn, pos);
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

    /**
     * Setting owner and type
     */
    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        NestTileEntity nestEntity = getBlockEntity(worldIn, pos);
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
    public BlockState getStateForPlacement(BlockItemUseContext blockItemUseContext) {
        FluidState fluidstate = blockItemUseContext.getLevel().getFluidState(blockItemUseContext.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER;
        return super.getStateForPlacement(blockItemUseContext).setValue(WATERLOGGED, flag);
    }
}
