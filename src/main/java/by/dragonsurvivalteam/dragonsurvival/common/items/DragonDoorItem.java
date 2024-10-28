package by.dragonsurvivalteam.dragonsurvival.common.items;


import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DragonDoorItem extends BlockItem {
    public DragonDoorItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        // 27: Block.UPDATE_NEIGHBORS + Block.UPDATE_CLIENTS + Block.UPDATE_IMMEDIATE + Block.UPDATE_KNOWN_SHAPE
        context.getLevel().setBlock(context.getClickedPos().above(), Blocks.AIR.defaultBlockState(), 27);
        context.getLevel().setBlock(context.getClickedPos().above(2), Blocks.AIR.defaultBlockState(), 27);
        return super.placeBlock(context, state);
    }
}