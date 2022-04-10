package by.dragonsurvivalteam.dragonsurvival.common.blocks;


import by.dragonsurvivalteam.dragonsurvival.client.gui.DragonAltarGUI;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;


public class DragonAltarBlock extends Block{
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	private final VoxelShape SHAPE = Shapes.block();


	public DragonAltarBlock(Properties properties){
		super(properties);
		registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(FACING);
	}

	@Override
	public void appendHoverText(ItemStack p_190948_1_,
		@Nullable
			BlockGetter p_190948_2_, List<Component> p_190948_3_, TooltipFlag p_190948_4_){
		super.appendHoverText(p_190948_1_, p_190948_2_, p_190948_3_, p_190948_4_);
		p_190948_3_.add(new TranslatableComponent("ds.description.dragonAltar"));
	}

	@Override
	public InteractionResult use(BlockState blockState, Level worldIn, BlockPos blockPos, Player player, InteractionHand handIn, BlockHitResult p_225533_6_){
		DragonStateHandler handler = DragonUtils.getHandler(player);
		int cooldown = handler.altarCooldown;
		if(cooldown > 0){
			if(worldIn.isClientSide){
				//Show the current cooldown in minutes and seconds in cases where the cooldown is set high in the config
				int mins = Functions.ticksToMinutes(cooldown);
				int secs = Functions.ticksToSeconds(cooldown - Functions.minutesToTicks(mins));
				player.sendMessage(new TranslatableComponent("ds.cooldown.active", (mins > 0 ? mins + "m" : "") + secs + (mins > 0 ? "s" : "")), player.getUUID());
			}
			return InteractionResult.CONSUME;
		}else{
			if(worldIn.isClientSide){
				openGUi();
			}
		}
		return InteractionResult.SUCCESS;
	}

	@OnlyIn( Dist.CLIENT )
	private void openGUi(){
		Minecraft.getInstance().setScreen(new DragonAltarGUI());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPE;
	}
}