package by.dragonsurvivalteam.dragonsurvival.common.blocks;


import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.container.AllowOpenDragonAltar;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;


public class DragonAltarBlock extends Block{
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	private final VoxelShape SHAPE = Shapes.block();


	public DragonAltarBlock(Properties properties){
		super(properties);
		registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(FACING);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack pStack, Item.@NotNull TooltipContext pContext, @NotNull List<Component> pTootipComponents, @NotNull TooltipFlag pTooltipFlag){
		super.appendHoverText(pStack, pContext, pTootipComponents, pTooltipFlag);
		pTootipComponents.add(Component.translatable("ds.description.dragonAltar"));
	}

	@Override
	public InteractionResult useWithoutItem(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull BlockHitResult pHitResult){
		DragonStateHandler handler = DragonStateProvider.getData(pPlayer);

		if(!pLevel.isClientSide()) {
			if(handler.altarCooldown > 0){
				//Show the current cooldown in minutes and seconds in cases where the cooldown is set high in the config
				int mins = (int) (Functions.ticksToMinutes(handler.altarCooldown));
				int secs = (int) (Functions.ticksToSeconds(handler.altarCooldown - Functions.minutesToTicks(mins)));
				pPlayer.sendSystemMessage(Component.translatable("ds.cooldown.active", (mins > 0 ? mins + "m" : "") + secs + (mins > 0 ? "s" : "")));
				return InteractionResult.FAIL;
			} else {
				PacketDistributor.sendToPlayer((ServerPlayer)pPlayer, new AllowOpenDragonAltar.Data());
				handler.altarCooldown = Functions.secondsToTicks(ServerConfig.altarUsageCooldown);
				handler.hasUsedAltar = true;
				handler.isInAltar = true;
				return InteractionResult.CONSUME;
			}
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPE;
	}
}