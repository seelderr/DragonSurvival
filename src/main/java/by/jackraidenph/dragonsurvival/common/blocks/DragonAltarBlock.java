package by.jackraidenph.dragonsurvival.common.blocks;


import by.jackraidenph.dragonsurvival.client.gui.DragonAltarGUI;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.util.Functions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;


public class DragonAltarBlock extends Block {
    private final VoxelShape SHAPE = VoxelShapes.block();
    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    
    
    public DragonAltarBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any()
        		.setValue(FACING, Direction.NORTH));
    }
    
    @Override
    public void appendHoverText(ItemStack p_190948_1_,
            @Nullable
                    IBlockReader p_190948_2_, List<ITextComponent> p_190948_3_, ITooltipFlag p_190948_4_)
    {
        super.appendHoverText(p_190948_1_, p_190948_2_, p_190948_3_, p_190948_4_);
        p_190948_3_.add(new TranslationTextComponent("ds.description.dragonAltar"));
    }
    
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }
    
    @Override
    public ActionResultType use(BlockState blockState, World worldIn, BlockPos blockPos, PlayerEntity player, Hand handIn, BlockRayTraceResult p_225533_6_) {
        DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
        if (handler != null) {
            int cooldown = handler.altarCooldown;
            if (cooldown > 0) {
                if (worldIn.isClientSide){
                    //Show the current cooldown in minutes and seconds in cases where the cooldown is set high in the config
                    int mins = Functions.ticksToMinutes(cooldown);
                    int secs = Functions.ticksToSeconds(cooldown - Functions.minutesToTicks(mins));
                    player.sendMessage(new TranslationTextComponent("ds.cooldown.active", (mins > 0 ? mins + "m" : "") + secs + (mins > 0 ? "s" : "")), player.getUUID());
                }
                return ActionResultType.CONSUME;
            } else {
                if (worldIn.isClientSide) {
                    openGUi();
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    @OnlyIn(Dist.CLIENT)
    private void openGUi() {
        Minecraft.getInstance().setScreen(new DragonAltarGUI());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }
    

}
