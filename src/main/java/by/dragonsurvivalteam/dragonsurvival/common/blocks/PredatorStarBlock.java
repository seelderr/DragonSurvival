package by.dragonsurvivalteam.dragonsurvival.common.blocks;

import by.dragonsurvivalteam.dragonsurvival.common.DamageSources;
import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.common.entity.monsters.MagicalPredatorEntity;
import by.dragonsurvivalteam.dragonsurvival.common.items.DSItems;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.PredatorStarTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class PredatorStarBlock extends Block implements IWaterLoggable{

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	protected static final VoxelShape SHAPE = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 12.0D);

	public PredatorStarBlock(Properties p_i48440_1_){
		super(p_i48440_1_);
		registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false));
	}

	@Override
	public boolean hasTileEntity(BlockState state){
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world){

		return new PredatorStarTileEntity();
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return this.defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	public void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_){
		p_206840_1_.add(WATERLOGGED);
	}

	@Override
	public void appendHoverText(ItemStack p_190948_1_,
		@Nullable
			IBlockReader p_190948_2_, List<ITextComponent> p_190948_3_, ITooltipFlag p_190948_4_){
		super.appendHoverText(p_190948_1_, p_190948_2_, p_190948_3_, p_190948_4_);
		p_190948_3_.add(new TranslationTextComponent("ds.description.predatorStar"));
	}

	@Override
	public BlockState updateShape(BlockState state, Direction dir, BlockState state2, IWorld level, BlockPos pos, BlockPos pos2){
		if(state.getValue(WATERLOGGED)){
			level.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, dir, state2, level, pos, pos2);
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
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
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPE;
	}

	@Override
	public void attack(BlockState state, World worldIn, BlockPos pos, PlayerEntity player){
		// TODO Should be able to do "player.getMainHandItem().isCorrectToolForDrops(state)" but always returns false for some reason
		if(!ConfigHandler.SERVER.mineStarBlock.get() || !(player.getMainHandItem().getToolTypes().contains(ToolType.HOE) && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, player.getMainHandItem()) > 0)){
			this.blockBehaviour(player, worldIn, pos);
		}
	}

	@Override
	public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn){
		super.entityInside(state, worldIn, pos, entityIn);
		if(!(entityIn instanceof MagicalPredatorEntity)){
			this.blockBehaviour(entityIn, worldIn, pos);
		}
	}

	public void blockBehaviour(Entity entity, World worldIn, BlockPos pos){
		if(entity instanceof LivingEntity){
			LivingEntity target = (LivingEntity)entity;
			target.hurt(DamageSources.STAR_DRAIN, Float.MAX_VALUE);
			worldIn.destroyBlock(pos, false);
			if(new Random().nextDouble() < ConfigHandler.COMMON.predatorStarSpawnChance.get() && worldIn.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(target.blockPosition()).inflate(50), playerEntity -> playerEntity.hasEffect(DragonEffects.PREDATOR_ANTI_SPAWN)).isEmpty()){
				MagicalPredatorEntity beast = DSEntities.MAGICAL_BEAST.create(worldIn);
				worldIn.addFreshEntity(beast);
				beast.teleportTo(pos.getX(), pos.getY(), pos.getZ());
			}
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