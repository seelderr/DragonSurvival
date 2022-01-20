package by.jackraidenph.dragonsurvival.common.blocks;


import by.jackraidenph.dragonsurvival.client.gui.DragonAltarGUI;
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.util.Functions;
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
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;


public class DragonAltarBlock extends Block
{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    
    
    public DragonAltarBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any()
        		.setValue(FACING, Direction.NORTH));
    }
    
    @Override
    public void appendHoverText(ItemStack pStack, @org.jetbrains.annotations.Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag)
    {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        pTooltip.add(new TranslatableComponent("ds.description.dragonAltar"));
    }
    
    
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }
    
    
    @Override
    public InteractionResult use(BlockState blockState, Level worldIn, BlockPos blockPos, Player player, InteractionHand handIn, BlockHitResult p_225533_6_) {
        DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
        if (handler != null) {
            int cooldown = handler.altarCooldown;
            if (cooldown > 0) {
                if (worldIn.isClientSide){
                    //Show the current cooldown in minutes and seconds in cases where the cooldown is set high in the config
                    int mins = Functions.ticksToMinutes(cooldown);
                    int secs = Functions.ticksToSeconds(cooldown - Functions.minutesToTicks(mins));
                    player.sendMessage(new TranslatableComponent("ds.cooldown.active", (mins > 0 ? mins + "m" : "") + secs + (mins > 0 ? "s" : "")), player.getUUID());
                }
                return InteractionResult.CONSUME;
            } else {
                if (worldIn.isClientSide) {
                    openGUi();
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @OnlyIn(Dist.CLIENT)
    private void openGUi() {
        Minecraft.getInstance().setScreen(new DragonAltarGUI());
    }
}
