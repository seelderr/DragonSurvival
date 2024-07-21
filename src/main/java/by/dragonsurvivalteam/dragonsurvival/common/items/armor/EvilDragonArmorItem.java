package by.dragonsurvivalteam.dragonsurvival.common.items.armor;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEnchantments;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEquipment;
import by.dragonsurvivalteam.dragonsurvival.util.EnchantmentUtils;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class EvilDragonArmorItem extends ArmorItem implements PermanentEnchantmentItem {
    public ItemEnchantments getDefaultEnchantments() {
        ItemEnchantments.Mutable temp = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        switch (this.getType()) {
            case HELMET -> EnchantmentUtils.addEnchantment(DSEnchantments.BLOOD_SIPHON, temp, 1);
            case CHESTPLATE -> EnchantmentUtils.addEnchantment(DSEnchantments.MURDERERS_CUNNING, temp, 1);
            case LEGGINGS -> EnchantmentUtils.addEnchantment(DSEnchantments.OVERWHELMING_MIGHT, temp, 1);
            case BOOTS -> EnchantmentUtils.addEnchantment(DSEnchantments.DRACONIC_SUPERIORITY, temp, 1);
        }
        EnchantmentUtils.addEnchantment(DSEnchantments.CURSE_OF_OUTLAW, temp, 1);
        return temp.toImmutable();
    }

    public EvilDragonArmorItem(Type pType, Properties pProperties) {
        super(DSEquipment.EVIL_DRAGON_ARMOR_MATERIAL, pType, pProperties);
    }
}
