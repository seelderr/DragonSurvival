package by.dragonsurvivalteam.dragonsurvival.common.blocks;

import by.dragonsurvivalteam.dragonsurvival.common.DamageSources;
import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.common.entity.monsters.MagicalPredator;
import by.dragonsurvivalteam.dragonsurvival.common.items.DSItems;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
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
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class PredatorStarBlock extends Block implements SimpleWaterloggedBlock{

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	protected static final VoxelShape SHAPE = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 12.0D);

	public PredatorStarBlock(Properties p_i48440_1_){
		super(p_i48440_1_);
		registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false));
	}

	@Override
	public boolean hasBlock(BlockState state){
		return true;
	}

	@Override
	public Block createBlock(BlockState state, BlockGetter world){

		return new PredatorStarBlock();
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
	public void appendHoverText(ItemStack p_190948_1_,
		@Nullable
			BlockGetter p_190948_2_, List<Component> p_190948_3_, TooltipFlag p_190948_4_){
		super.appendHoverText(p_190948_1_, p_190948_2_, p_190948_3_, p_190948_4_);
		p_190948_3_.add(new TranslatableComponent("ds.description.predatorStar"));
	}

	@Override
	public BlockState updateShape(BlockState state, Direction dir, BlockState state2, LevelAccessor level, BlockPos pos, BlockPos pos2){
		if(state.getValue(WATERLOGGED)){
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, dir, state2, level, pos, pos2);
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

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPE;
	}

	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player player){
		// TODO Should be able to do "player.getMainHandItem().isCorrectToolForDrops(state)" but always returns false for some reason
		if(!ConfigHandler.SERVER.mineStarBlock.get() || !(player.getMainHandItem().getToolTypes().contains(ToolType.HOE) && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, player.getMainHandItem()) > 0)){
			this.blockBehaviour(player, worldIn, pos);
		}
	}

	public void blockBehaviour(Entity entity, Level worldIn, BlockPos pos){
		if(entity instanceof LivingEntity){
			LivingEntity target = (LivingEntity)entity;
			target.hurt(DamageSources.STAR_DRAIN, Float.MAX_VALUE);
			worldIn.destroyBlock(pos, false);
			if(new Random().nextDouble() < ConfigHandler.COMMON.predatorStarSpawnChance.get() && worldIn.getEntitiesOfClass(Player.class, new AABB(target.blockPosition()).inflate(50), player -> player.hasEffect(DragonEffects.PREDATOR_ANTI_SPAWN)).isEmpty()){
				MagicalPredator beast = DSEntities.MAGICAL_BEAST.create(worldIn);
				worldIn.addFreshEntity(beast);
				beast.teleportTo(pos.getX(), pos.getY(), pos.getZ());
			}
		}else if(entity instanceof ItemEntity){
			ItemEntity item = (ItemEntity)entity;

			if(item.getItem().getItem() == DSItems.elderDragonBone){
				item.setItem(new ItemStack(DSItems.starBone));
			}else if(item.getItem().getItem() == DSItems.elderDragonHeart || item.getItem().getItem() == DSItems.weakDragonHeart || item.getItem().getItem() == DSItems.dragonHeartShard){
				item.setItem(new ItemStack(DSItems.starHeart));
			}
		}
	}

	@Override
	public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn){
		super.entityInside(state, worldIn, pos, entityIn);
		if(!(entityIn instanceof MagicalPredator)){
			this.blockBehaviour(entityIn, worldIn, pos);
		}
	}
}