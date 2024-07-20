package by.dragonsurvivalteam.dragonsurvival.common.items.armor;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public interface PermanentEnchantmentItem {
    static boolean isDefaultEnchanted(ItemStack pStack) {
        ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(pStack);
        if (pStack.getItem() instanceof PermanentEnchantmentItem armorItem) {
            return enchantments.equals(armorItem.getDefaultEnchantments());
        } else { return false; }
    }

    static boolean isEnchantable(ItemStack pStack) {
        return isDefaultEnchanted(pStack);
    }

    static boolean canGrindstoneRepair(ItemStack stack) {
        return !isDefaultEnchanted(stack);
    }

    ItemEnchantments getDefaultEnchantments();
}
