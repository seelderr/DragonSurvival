package by.dragonsurvivalteam.dragonsurvival.common.blocks;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.registry.DSSounds;
import by.dragonsurvivalteam.dragonsurvival.registry.DSTileEntities;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.DragonBeaconTileEntity;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import by.dragonsurvivalteam.dragonsurvival.util.MobEffectUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import javax.annotation.Nullable;

public class DragonBeacon extends Block implements SimpleWaterloggedBlock, EntityBlock {
    public static BooleanProperty LIT = BlockStateProperties.LIT;
    public static BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public DragonBeacon(Properties p_i48440_1_) {
        super(p_i48440_1_);
        registerDefaultState(getStateDefinition().any().setValue(LIT, false).setValue(WATERLOGGED, false));
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState1, LevelAccessor world, BlockPos blockPos, BlockPos blockPos1) {
        if (blockState.getValue(WATERLOGGED)) {
            world.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return super.updateShape(blockState, direction, blockState1, world, blockPos, blockPos1);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull BlockHitResult pHitResult) {
        Optional<DragonStateHandler> dragonState = DragonStateProvider.getOptional(pPlayer);

        if (dragonState.isPresent()) {
            DragonStateHandler dragonStateHandler = dragonState.orElse(null);

            if (dragonStateHandler.isDragon() && (pPlayer.totalExperience >= 60 || pPlayer.isCreative())) {
                if (this == DSBlocks.PEACE_DRAGON_BEACON.get()) {
                    if (!pLevel.isClientSide()) {
                        ConfigHandler.getResourceElements(MobEffect.class, ServerConfig.peaceBeaconEffects).forEach(effect -> {
                            if (effect != null) {
                                pPlayer.addEffect(new MobEffectInstance(MobEffectUtils.getHolder(effect), Functions.minutesToTicks(ServerConfig.minutesOfDragonEffect)));
                            }
                        });
                    }
                } else if (this == DSBlocks.MAGIC_DRAGON_BEACON.get()) {
                    if (!pLevel.isClientSide()) {
                        ConfigHandler.getResourceElements(MobEffect.class, ServerConfig.magicBeaconEffects).forEach(effect -> {
                            if (effect != null) {
                                pPlayer.addEffect(new MobEffectInstance(MobEffectUtils.getHolder(effect), Functions.minutesToTicks(ServerConfig.minutesOfDragonEffect)));
                            }
                        });
                    }
                } else if (this == DSBlocks.FIRE_DRAGON_BEACON.get()) {
                    if (!pLevel.isClientSide()) {
                        ConfigHandler.getResourceElements(MobEffect.class, ServerConfig.fireBeaconEffects).forEach(effect -> {
                            if (effect != null) {
                                pPlayer.addEffect(new MobEffectInstance(MobEffectUtils.getHolder(effect), Functions.minutesToTicks(ServerConfig.minutesOfDragonEffect)));
                            }
                        });
                    }
                }

                pPlayer.giveExperiencePoints(-60);
                pLevel.playSound(pPlayer, pPos, DSSounds.APPLY_EFFECT.get(), SoundSource.PLAYERS, 1, 1);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.FAIL;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public ItemInteractionResult useItemOn(@NotNull ItemStack pStack, @NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHitResult) {
        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        Item item = itemStack.getItem();
        //upgrading
        if (this == DSBlocks.DRAGON_BEACON.get()) {
            DragonBeaconTileEntity old = (DragonBeaconTileEntity) pLevel.getBlockEntity(pPos);
            if (item == Items.GOLD_BLOCK) {
                pLevel.setBlockAndUpdate(pPos, DSBlocks.PEACE_DRAGON_BEACON.get().defaultBlockState());
                DragonBeaconTileEntity dragonBeaconEntity = (DragonBeaconTileEntity) pLevel.getBlockEntity(pPos);
                dragonBeaconEntity.type = DragonBeaconTileEntity.Type.PEACE;
                dragonBeaconEntity.tick = old.tick;
                itemStack.shrink(1);
                pLevel.playSound(pPlayer, pPos, DSSounds.UPGRADE_BEACON.get(), SoundSource.BLOCKS, 1, 1);
                return ItemInteractionResult.SUCCESS;
            } else if (item == Items.DIAMOND_BLOCK) {
                pLevel.setBlockAndUpdate(pPos, DSBlocks.MAGIC_DRAGON_BEACON.get().defaultBlockState());
                DragonBeaconTileEntity dragonBeaconEntity = (DragonBeaconTileEntity) pLevel.getBlockEntity(pPos);
                dragonBeaconEntity.type = DragonBeaconTileEntity.Type.MAGIC;
                dragonBeaconEntity.tick = old.tick;
                itemStack.shrink(1);
                pLevel.playSound(pPlayer, pPos, DSSounds.UPGRADE_BEACON.get(), SoundSource.BLOCKS, 1, 1);
                return ItemInteractionResult.SUCCESS;
            } else if (item == Items.NETHERITE_INGOT) {
                pLevel.setBlockAndUpdate(pPos, DSBlocks.FIRE_DRAGON_BEACON.get().defaultBlockState());
                DragonBeaconTileEntity dragonBeaconEntity = (DragonBeaconTileEntity) pLevel.getBlockEntity(pPos);
                dragonBeaconEntity.type = DragonBeaconTileEntity.Type.FIRE;
                dragonBeaconEntity.tick = old.tick;
                itemStack.shrink(1);
                pLevel.playSound(pPlayer, pPos, DSSounds.UPGRADE_BEACON.get(), SoundSource.BLOCKS, 1, 1);
                return ItemInteractionResult.SUCCESS;
            }
        }

        if(this == DSBlocks.FIRE_DRAGON_BEACON.get()){
        DragonBeaconTileEntity old = (DragonBeaconTileEntity)pLevel.getBlockEntity(pPos);
        if(item == Items.GOLD_BLOCK){
            pLevel.setBlockAndUpdate(pPos, DSBlocks.PEACE_DRAGON_BEACON.get().defaultBlockState());
            DragonBeaconTileEntity dragonBeaconEntity = (DragonBeaconTileEntity)pLevel.getBlockEntity(pPos);
            dragonBeaconEntity.type = DragonBeaconTileEntity.Type.PEACE;
            dragonBeaconEntity.tick = old.tick;
            itemStack.shrink(1);
            pLevel.playSound(pPlayer, pPos, DSSounds.UPGRADE_BEACON.get(), SoundSource.BLOCKS, 1, 1);
            return ItemInteractionResult.SUCCESS;
        }else if(item == Items.DIAMOND_BLOCK){
            pLevel.setBlockAndUpdate(pPos, DSBlocks.MAGIC_DRAGON_BEACON.get().defaultBlockState());
            DragonBeaconTileEntity dragonBeaconEntity = (DragonBeaconTileEntity)pLevel.getBlockEntity(pPos);
            dragonBeaconEntity.type = DragonBeaconTileEntity.Type.MAGIC;
            dragonBeaconEntity.tick = old.tick;
            itemStack.shrink(1);
            pLevel.playSound(pPlayer, pPos, DSSounds.UPGRADE_BEACON.get(), SoundSource.BLOCKS, 1, 1);
            return ItemInteractionResult.SUCCESS;
            }
        }

        if(this == DSBlocks.PEACE_DRAGON_BEACON.get()){
            DragonBeaconTileEntity old = (DragonBeaconTileEntity)pLevel.getBlockEntity(pPos);
            if(item == Items.NETHERITE_INGOT){
                pLevel.setBlockAndUpdate(pPos, DSBlocks.FIRE_DRAGON_BEACON.get().defaultBlockState());
                DragonBeaconTileEntity dragonBeaconEntity = (DragonBeaconTileEntity)pLevel.getBlockEntity(pPos);
                dragonBeaconEntity.type = DragonBeaconTileEntity.Type.FIRE;
                dragonBeaconEntity.tick = old.tick;
                itemStack.shrink(1);
                pLevel.playSound(pPlayer, pPos, DSSounds.UPGRADE_BEACON.get(), SoundSource.BLOCKS, 1, 1);
                return ItemInteractionResult.SUCCESS;
            }else if(item == Items.DIAMOND_BLOCK){
                pLevel.setBlockAndUpdate(pPos, DSBlocks.MAGIC_DRAGON_BEACON.get().defaultBlockState());
                DragonBeaconTileEntity dragonBeaconEntity = (DragonBeaconTileEntity)pLevel.getBlockEntity(pPos);
                dragonBeaconEntity.type = DragonBeaconTileEntity.Type.MAGIC;
                dragonBeaconEntity.tick = old.tick;
                itemStack.shrink(1);
                pLevel.playSound(pPlayer, pPos, DSSounds.UPGRADE_BEACON.get(), SoundSource.BLOCKS, 1, 1);
                return ItemInteractionResult.SUCCESS;
            }
        }

        if(this == DSBlocks.MAGIC_DRAGON_BEACON.get()){
            DragonBeaconTileEntity old = (DragonBeaconTileEntity)pLevel.getBlockEntity(pPos);
            if(item == Items.GOLD_BLOCK){
                pLevel.setBlockAndUpdate(pPos, DSBlocks.PEACE_DRAGON_BEACON.get().defaultBlockState());
                DragonBeaconTileEntity dragonBeaconEntity = (DragonBeaconTileEntity)pLevel.getBlockEntity(pPos);
                dragonBeaconEntity.type = DragonBeaconTileEntity.Type.PEACE;
                dragonBeaconEntity.tick = old.tick;
                itemStack.shrink(1);
                pLevel.playSound(pPlayer, pPos, DSSounds.UPGRADE_BEACON.get(), SoundSource.BLOCKS, 1, 1);
                return ItemInteractionResult.SUCCESS;
            }else if(item == Items.NETHERITE_INGOT){
                pLevel.setBlockAndUpdate(pPos, DSBlocks.FIRE_DRAGON_BEACON.get().defaultBlockState());
                DragonBeaconTileEntity dragonBeaconEntity = (DragonBeaconTileEntity)pLevel.getBlockEntity(pPos);
                dragonBeaconEntity.type = DragonBeaconTileEntity.Type.FIRE;
                dragonBeaconEntity.tick = old.tick;
                itemStack.shrink(1);
                pLevel.playSound(pPlayer, pPos, DSSounds.UPGRADE_BEACON.get(), SoundSource.BLOCKS, 1, 1);
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public boolean triggerEvent(BlockState pState, Level pLevel, BlockPos pPos, int pId, int pParam) {
        super.triggerEvent(pState, pLevel, pPos, pId, pParam);
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        return blockentity != null && blockentity.triggerEvent(pId, pParam);
    }

    //methods below are required for waterlogged property to work

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    @Override
    @Nullable public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        return blockentity instanceof MenuProvider ? (MenuProvider) blockentity : null;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT, WATERLOGGED);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos position, @NotNull BlockState state) {
        return DSTileEntities.DRAGON_BEACON.get().create(position, state);
    }

    @Override
    @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return level.isClientSide ? null : BaseEntityBlock.createTickerHelper(type, DSTileEntities.DRAGON_BEACON.get(), DragonBeaconTileEntity::serverTick);
    }
}