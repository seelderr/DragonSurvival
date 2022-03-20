package by.dragonsurvivalteam.dragonsurvival.common.blocks;

import by.dragonsurvivalteam.dragonsurvival.client.sounds.SoundRegistry;
import by.dragonsurvivalteam.dragonsurvival.common.EffectInstance2;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.DSTileEntities;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.DragonBeaconTileEntity;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class DragonBeacon extends Block implements IWaterLoggable{
	public static BooleanProperty LIT = BlockStateProperties.LIT;
	public static BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public DragonBeacon(Properties p_i48440_1_){
		super(p_i48440_1_);
		registerDefaultState(getStateDefinition().any().setValue(LIT, false).setValue(WATERLOGGED, false));
	}

	@Override
	public boolean hasTileEntity(BlockState state){
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world){
		return DSTileEntities.dragonBeacon.create();
	}

	public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState1, IWorld world, BlockPos blockPos, BlockPos blockPos1){
		if(blockState.getValue(WATERLOGGED)){
			world.getLiquidTicks().scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		return super.updateShape(blockState, direction, blockState1, world, blockPos, blockPos1);
	}

	@Override
	public ActionResultType use(BlockState blockState, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult p_225533_6_){
		ItemStack itemStack = playerEntity.getItemInHand(hand);
		Item item = itemStack.getItem();
		//upgrading
		if(this == DSBlocks.dragonBeacon){
			DragonBeaconTileEntity old = (DragonBeaconTileEntity)world.getBlockEntity(pos);
			if(item == Items.GOLD_BLOCK){
				world.setBlockAndUpdate(pos, DSBlocks.peaceDragonBeacon.defaultBlockState());
				DragonBeaconTileEntity dragonBeaconEntity = (DragonBeaconTileEntity)world.getBlockEntity(pos);
				dragonBeaconEntity.type = DragonBeaconTileEntity.Type.PEACE;
				dragonBeaconEntity.tick = old.tick;
				itemStack.shrink(1);
				world.playSound(playerEntity, pos, SoundRegistry.upgradeBeacon, SoundCategory.BLOCKS, 1, 1);
				return ActionResultType.SUCCESS;
			}else if(item == Items.DIAMOND_BLOCK){
				world.setBlockAndUpdate(pos, DSBlocks.magicDragonBeacon.defaultBlockState());
				DragonBeaconTileEntity dragonBeaconEntity = (DragonBeaconTileEntity)world.getBlockEntity(pos);
				dragonBeaconEntity.type = DragonBeaconTileEntity.Type.MAGIC;
				dragonBeaconEntity.tick = old.tick;
				itemStack.shrink(1);
				world.playSound(playerEntity, pos, SoundRegistry.upgradeBeacon, SoundCategory.BLOCKS, 1, 1);
				return ActionResultType.SUCCESS;
			}else if(item == Items.NETHERITE_INGOT){
				world.setBlockAndUpdate(pos, DSBlocks.fireDragonBeacon.defaultBlockState());
				DragonBeaconTileEntity dragonBeaconEntity = (DragonBeaconTileEntity)world.getBlockEntity(pos);
				dragonBeaconEntity.type = DragonBeaconTileEntity.Type.FIRE;
				dragonBeaconEntity.tick = old.tick;
				itemStack.shrink(1);
				world.playSound(playerEntity, pos, SoundRegistry.upgradeBeacon, SoundCategory.BLOCKS, 1, 1);
				return ActionResultType.SUCCESS;
			}
		}
		//apply temporary benefits
		if(itemStack.isEmpty()){
			LazyOptional<DragonStateHandler> dragonState = DragonStateProvider.getCap(playerEntity);
			if(dragonState.isPresent()){
				DragonStateHandler dragonStateHandler = dragonState.orElse(null);
				if(dragonStateHandler.isDragon() && ((playerEntity.totalExperience >= 60 || playerEntity.experienceLevel >= 6) || playerEntity.isCreative())){
					if(this == DSBlocks.peaceDragonBeacon){
						if(!world.isClientSide){
							ConfigHandler.COMMON.peaceBeaconEffects.get().forEach(s -> {
								Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(s));
								if(effect != null){
									playerEntity.addEffect(new EffectInstance2(effect, Functions.minutesToTicks(ConfigHandler.COMMON.secondsOfBeaconEffect.get())));
								}
							});
						}
					}else if(this == DSBlocks.magicDragonBeacon){
						if(!world.isClientSide){
							ConfigHandler.COMMON.magicBeaconEffects.get().forEach(s -> {
								Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(s));
								if(effect != null){
									playerEntity.addEffect(new EffectInstance2(effect, Functions.minutesToTicks(ConfigHandler.COMMON.secondsOfBeaconEffect.get())));
								}
							});
						}
					}else if(this == DSBlocks.fireDragonBeacon){
						if(!world.isClientSide){
							ConfigHandler.COMMON.fireBeaconEffects.get().forEach(s -> {
								Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(s));
								if(effect != null){
									playerEntity.addEffect(new EffectInstance2(effect, Functions.minutesToTicks(ConfigHandler.COMMON.secondsOfBeaconEffect.get())));
								}
							});
						}
					}

					playerEntity.giveExperiencePoints(-60);
					world.playSound(playerEntity, pos, SoundRegistry.applyEffect, SoundCategory.PLAYERS, 1, 1);
					return ActionResultType.SUCCESS;
				}
			}
		}
		playerEntity.hurt(DamageSource.GENERIC, 1);
		return ActionResultType.SUCCESS;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState p_149645_1_){
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	//methods below are required for waterlogged property to work

	public FluidState getFluidState(BlockState blockState){
		return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return this.defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_){
		super.createBlockStateDefinition(p_206840_1_);
		p_206840_1_.add(LIT, WATERLOGGED);
	}
}