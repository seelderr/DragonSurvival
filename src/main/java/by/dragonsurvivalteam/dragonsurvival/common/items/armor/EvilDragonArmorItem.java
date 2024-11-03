package by.dragonsurvivalteam.dragonsurvival.common.items.armor;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEnchantments;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEquipment;
import by.dragonsurvivalteam.dragonsurvival.util.EnchantmentUtils;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class EvilDragonArmorItem extends ArmorItem implements PermanentEnchantmentItem {
    public ItemEnchantments getDefaultEnchantments() {
        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

        switch (this.getType()) {
            case HELMET -> EnchantmentUtils.set(enchantments, DSEnchantments.BLOOD_SIPHON, 1);
            case CHESTPLATE -> EnchantmentUtils.set(enchantments, DSEnchantments.MURDERERS_CUNNING, 1);
            case LEGGINGS -> EnchantmentUtils.set(enchantments, DSEnchantments.OVERWHELMING_MIGHT, 1);
            case BOOTS -> EnchantmentUtils.set(enchantments, DSEnchantments.DRACONIC_SUPERIORITY, 1);
        }

        EnchantmentUtils.set(enchantments, DSEnchantments.CURSE_OF_OUTLAW, 1);
        return enchantments.toImmutable();
    }

    public EvilDragonArmorItem(Type pType, Properties pProperties) {
        super(DSEquipment.EVIL_DRAGON_ARMOR_MATERIAL, pType, pProperties);
    }
}
