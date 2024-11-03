package by.dragonsurvivalteam.dragonsurvival.common.items.armor;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEnchantments;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEquipment;
import by.dragonsurvivalteam.dragonsurvival.util.EnchantmentUtils;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class GoodDragonArmorItem extends ArmorItem implements PermanentEnchantmentItem {
    public ItemEnchantments getDefaultEnchantments() {
        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

        switch (this.getType()) {
            case HELMET -> EnchantmentUtils.set(enchantments, DSEnchantments.COMBAT_RECOVERY, 1);
            case CHESTPLATE -> EnchantmentUtils.set(enchantments, DSEnchantments.AERODYNAMIC_MASTERY, 1);
            case LEGGINGS -> EnchantmentUtils.set(enchantments, DSEnchantments.UNBREAKABLE_SPIRIT, 1);
            case BOOTS -> EnchantmentUtils.set(enchantments, DSEnchantments.SACRED_SCALES, 1);
        }

        EnchantmentUtils.set(enchantments, DSEnchantments.CURSE_OF_KINDNESS, 1);
        return enchantments.toImmutable();
    }

    public GoodDragonArmorItem(Type pType, Properties pProperties) {
        super(DSEquipment.GOOD_DRAGON_ARMOR_MATERIAL, pType, pProperties);
    }
}
