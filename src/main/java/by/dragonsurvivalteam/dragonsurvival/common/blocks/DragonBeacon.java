package by.dragonsurvivalteam.dragonsurvival.common.blocks;

import by.dragonsurvivalteam.dragonsurvival.client.sounds.SoundRegistry;
import by.dragonsurvivalteam.dragonsurvival.common.EffectInstance2;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.DSTileEntities;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.DragonBeaconBlockEntity;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class DragonBeacon extends Block implements SimpleWaterloggedBlock{
	public static BooleanProperty LIT = BlockStateProperties.LIT;
	public static BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public DragonBeacon(Properties p_i48440_1_){
		super(p_i48440_1_);
		registerDefaultState(getStateDefinition().any().setValue(LIT, false).setValue(WATERLOGGED, false));
	}

	@Override
	public boolean hasBlock(BlockState state){
		return true;
	}

	@Nullable
	@Override
	public Block createBlock(BlockState state, BlockGetter world){
		return DSTileEntities.dragonBeacon.create();
	}

	public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState1, LevelAccessor world, BlockPos blockPos, BlockPos blockPos1){
		if(blockState.getValue(WATERLOGGED)){
			world.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		return super.updateShape(blockState, direction, blockState1, world, blockPos, blockPos1);
	}

	@Override
	public InteractionResult use(BlockState blockState, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult p_225533_6_){
		ItemStack itemStack = player.getItemInHand(hand);
		Item item = itemStack.getItem();
		//upgrading
		if(this == DSBlocks.dragonBeacon){
			DragonBeaconBlockEntity old = (DragonBeaconBlockEntity)world.getBlockEntity(pos);
			if(item == Items.GOLD_BLOCK){
				world.setBlockAndUpdate(pos, DSBlocks.peaceDragonBeacon.defaultBlockState());
				DragonBeaconBlockEntity dragonBeacon = (DragonBeaconBlockEntity)world.getBlockEntity(pos);
				dragonBeacon.type = DragonBeaconBlockEntity.Type.PEACE;
				dragonBeacon.tick = old.tick;
				itemStack.shrink(1);
				world.playSound(player, pos, SoundRegistry.upgradeBeacon, SoundSource.BLOCKS, 1, 1);
				return InteractionResult.SUCCESS;
			}else if(item == Items.DIAMOND_BLOCK){
				world.setBlockAndUpdate(pos, DSBlocks.magicDragonBeacon.defaultBlockState());
				DragonBeaconBlockEntity dragonBeacon = (DragonBeaconBlockEntity)world.getBlockEntity(pos);
				dragonBeacon.type = DragonBeaconBlockEntity.Type.MAGIC;
				dragonBeacon.tick = old.tick;
				itemStack.shrink(1);
				world.playSound(player, pos, SoundRegistry.upgradeBeacon, SoundSource.BLOCKS, 1, 1);
				return InteractionResult.SUCCESS;
			}else if(item == Items.NETHERITE_INGOT){
				world.setBlockAndUpdate(pos, DSBlocks.fireDragonBeacon.defaultBlockState());
				DragonBeaconBlockEntity dragonBeacon = (DragonBeaconBlockEntity)world.getBlockEntity(pos);
				dragonBeacon.type = DragonBeaconBlockEntity.Type.FIRE;
				dragonBeacon.tick = old.tick;
				itemStack.shrink(1);
				world.playSound(player, pos, SoundRegistry.upgradeBeacon, SoundSource.BLOCKS, 1, 1);
				return InteractionResult.SUCCESS;
			}
		}
		//apply temporary benefits
		if(itemStack.isEmpty()){
			LazyOptional<DragonStateHandler> dragonState = DragonStateProvider.getCap(player);
			if(dragonState.isPresent()){
				DragonStateHandler dragonStateHandler = dragonState.orElse(null);
				if(dragonStateHandler.isDragon() && ((player.totalExperience >= 60 || player.experienceLevel >= 6) || player.isCreative())){
					if(this == DSBlocks.peaceDragonBeacon){
						if(!world.isClientSide){
							ConfigHandler.COMMON.peaceBeaconEffects.get().forEach(s -> {
								MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(s));
								if(effect != null){
									player.addEffect(new EffectInstance2(effect, Functions.minutesToTicks(ConfigHandler.COMMON.secondsOfBeaconEffect.get())));
								}
							});
						}
					}else if(this == DSBlocks.magicDragonBeacon){
						if(!world.isClientSide){
							ConfigHandler.COMMON.magicBeaconEffects.get().forEach(s -> {
								MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(s));
								if(effect != null){
									player.addEffect(new EffectInstance2(effect, Functions.minutesToTicks(ConfigHandler.COMMON.secondsOfBeaconEffect.get())));
								}
							});
						}
					}else if(this == DSBlocks.fireDragonBeacon){
						if(!world.isClientSide){
							ConfigHandler.COMMON.fireBeaconEffects.get().forEach(s -> {
								MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(s));
								if(effect != null){
									player.addEffect(new EffectInstance2(effect, Functions.minutesToTicks(ConfigHandler.COMMON.secondsOfBeaconEffect.get())));
								}
							});
						}
					}

					player.giveExperiencePoints(-60);
					world.playSound(player, pos, SoundRegistry.applyEffect, SoundSource.PLAYERS, 1, 1);
					return InteractionResult.SUCCESS;
				}
			}
		}
		player.hurt(DamageSource.GENERIC, 1);
		return InteractionResult.SUCCESS;
	}

	@Override
	public RenderShape getRenderShape(BlockState p_149645_1_){
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	//methods below are required for waterlogged property to work

	public FluidState getFluidState(BlockState blockState){
		return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return this.defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_){
		super.createBlockStateDefinition(p_206840_1_);
		p_206840_1_.add(LIT, WATERLOGGED);
	}
}