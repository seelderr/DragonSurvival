package by.dragonsurvivalteam.dragonsurvival.util;

import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolActions;

public class ToolUtils {
    public static boolean shouldUseDragonTools(final ItemStack itemStack) {
        return !(itemStack.getItem() instanceof TieredItem) && !isHarvestTool(itemStack) && !isWeapon(itemStack);
    }

    public static boolean isHarvestTool(final ItemStack itemStack) {
        return isPickaxe(itemStack) || isAxe(itemStack) || isShovel(itemStack) || isHoe(itemStack) || isShears(itemStack);
    }

    public static boolean isWeapon(final ItemStack itemStack) {
        return itemStack.getItem() instanceof SwordItem || itemStack.canPerformAction(ToolActions.SWORD_DIG) || itemStack.canPerformAction(ToolActions.SWORD_SWEEP) || /* TODO :: Unsure if this is the correct check to make */ itemStack.canPerformAction(ToolActions.AXE_DIG);
    }

    public static boolean isPickaxe(final ItemStack itemStack) {
        return itemStack.getItem() instanceof PickaxeItem || itemStack.canPerformAction(ToolActions.PICKAXE_DIG) || itemStack.getItem().isCorrectToolForDrops(Blocks.STONE.defaultBlockState());
    }

    public static boolean isAxe(final ItemStack itemStack) {
        return itemStack.getItem() instanceof AxeItem || itemStack.canPerformAction(ToolActions.AXE_STRIP) || itemStack.canPerformAction(ToolActions.AXE_DIG) || itemStack.canPerformAction(ToolActions.AXE_SCRAPE) || itemStack.getItem().isCorrectToolForDrops(Blocks.OAK_LOG.defaultBlockState());
    }

    public static boolean isShovel(final ItemStack itemStack) {
        return itemStack.getItem() instanceof ShovelItem || itemStack.canPerformAction(ToolActions.SHOVEL_FLATTEN) || itemStack.canPerformAction(ToolActions.SHOVEL_DIG) || itemStack.getItem().isCorrectToolForDrops(Blocks.DIRT.defaultBlockState());
    }

    public static boolean isHoe(final ItemStack itemStack) {
        return itemStack.canPerformAction(ToolActions.HOE_DIG) || itemStack.canPerformAction(ToolActions.HOE_TILL);
    }

    public static boolean isShears(final ItemStack itemStack) {
        return itemStack.canPerformAction(ToolActions.SHEARS_CARVE) || itemStack.canPerformAction(ToolActions.SHEARS_DIG) || itemStack.canPerformAction(ToolActions.SHEARS_DISARM) || itemStack.canPerformAction(ToolActions.SHEARS_HARVEST) || itemStack.is(Tags.Items.SHEARS);
    }
}
