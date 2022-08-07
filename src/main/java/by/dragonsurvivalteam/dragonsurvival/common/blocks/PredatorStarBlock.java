package by.dragonsurvivalteam.dragonsurvival.common.blocks;

import by.dragonsurvivalteam.dragonsurvival.registry.DamageSources;
import by.dragonsurvivalteam.dragonsurvival.common.entity.monsters.MagicalPredator;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.DSTileEntities;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.PredatorStarTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class PredatorStarBlock extends Block implements SimpleWaterloggedBlock, EntityBlock{

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	protected static final VoxelShape SHAPE = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 12.0D);

	public PredatorStarBlock(Properties p_i48440_1_){
		super(p_i48440_1_);
		registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false));
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState){
		return DSTileEntities.PREDATOR_STAR_TILE_ENTITY_TYPE.create(pPos, pState);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType){
		return pLevel.isClientSide ? null : BaseEntityBlock.createTickerHelper(pBlockEntityType, DSTileEntities.PREDATOR_STAR_TILE_ENTITY_TYPE, PredatorStarTileEntity::serverTick);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return this.defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_){
		p_206840_1_.add(WATERLOGGED);
	}

	@Override
	public void appendHoverText(ItemStack pStack, @org.jetbrains.annotations.Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag){
		super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
		pTooltip.add(new TranslatableComponent("ds.description.predatorStar"));
	}

	@Override
	public BlockState updateShape(BlockState state, Direction dir, BlockState state2, LevelAccessor level, BlockPos pos, BlockPos pos2){
		if(state.getValue(WATERLOGGED)){
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, dir, state2, level, pos, pos2);
	}

	public boolean triggerEvent(BlockState pState, Level pLevel, BlockPos pPos, int pId, int pParam){
		super.triggerEvent(pState, pLevel, pPos, pId, pParam);
		BlockEntity blockentity = pLevel.getBlockEntity(pPos);
		return blockentity != null && blockentity.triggerEvent(pId, pParam);
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state){
		return PushReaction.IGNORE;
	}

	@Override
	public FluidState getFluidState(BlockState state){
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Nullable
	public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos){
		BlockEntity blockentity = pLevel.getBlockEntity(pPos);
		return blockentity instanceof MenuProvider ? (MenuProvider)blockentity : null;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPE;
	}

	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player player){
		//TODO Fix tool type
		if(!ServerConfig.mineStarBlock || !(player.getMainHandItem().getItem() instanceof HoeItem && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, player.getMainHandItem()) > 0)){
			this.blockBehaviour(player, worldIn, pos);
		}
	}

	@Override
	public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn){
		super.entityInside(state, worldIn, pos, entityIn);
		if(!(entityIn instanceof MagicalPredator)){
			this.blockBehaviour(entityIn, worldIn, pos);
		}
	}

	public void blockBehaviour(Entity entity, Level worldIn, BlockPos pos){
		if(entity instanceof LivingEntity){
			LivingEntity target = (LivingEntity)entity;
			target.hurt(DamageSources.STAR_DRAIN, Float.MAX_VALUE);
			worldIn.destroyBlock(pos, false);
//			if(new Random().nextDouble() < ServerConfig.predatorStarSpawnChance && worldIn.getEntitiesOfClass(Player.class, new AABB(target.blockPosition()).inflate(50), playerEntity -> playerEntity.hasEffect(DragonEffects.PREDATOR_ANTI_SPAWN)).isEmpty()){
////				MagicalPredator beast = DSEntities.MAGICAL_BEAST.create(worldIn);
////				worldIn.addFreshEntity(beast);
////				beast.teleportTo(pos.getX(), pos.getY(), pos.getZ());
//			}
		}else if(entity instanceof ItemEntity){
			ItemEntity itemEntity = (ItemEntity)entity;

			if(itemEntity.getItem().getItem() == DSItems.elderDragonBone){
				itemEntity.setItem(new ItemStack(DSItems.starBone));
			}else if(itemEntity.getItem().getItem() == DSItems.elderDragonHeart || itemEntity.getItem().getItem() == DSItems.weakDragonHeart || itemEntity.getItem().getItem() == DSItems.dragonHeartShard){
				itemEntity.setItem(new ItemStack(DSItems.starHeart));
			}
		}
	}
}