package by.dragonsurvivalteam.dragonsurvival.common.items.armor;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEnchantments;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEquipment;
import by.dragonsurvivalteam.dragonsurvival.util.EnchantmentUtils;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class GoodDragonArmorItem extends ArmorItem implements PermanentEnchantmentItem {
    public ItemEnchantments getDefaultEnchantments() {
        ItemEnchantments.Mutable temp = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        switch (this.getType()) {
            case HELMET -> EnchantmentUtils.addEnchantment(DSEnchantments.COMBAT_RECOVERY, temp, 1);
            case CHESTPLATE -> EnchantmentUtils.addEnchantment(DSEnchantments.UNBREAKABLE_SPIRIT, temp, 1);
            case LEGGINGS -> EnchantmentUtils.addEnchantment(DSEnchantments.AERODYNAMIC_MASTERY, temp, 1);
            case BOOTS -> EnchantmentUtils.addEnchantment(DSEnchantments.SACRED_SCALES, temp, 1);
        }
        EnchantmentUtils.addEnchantment(DSEnchantments.CURSE_OF_KINDNESS, temp, 1);
        return temp.toImmutable();
    }

    public GoodDragonArmorItem(Type pType, Properties pProperties) {
        super(DSEquipment.GOOD_DRAGON_ARMOR_MATERIAL, pType, pProperties);
    }
}
