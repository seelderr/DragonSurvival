package by.dragonsurvivalteam.dragonsurvival.common.items.armor;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEnchantments;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEquipment;
import by.dragonsurvivalteam.dragonsurvival.util.EnchantmentUtils;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class DragonHunterSword extends SwordItem implements PermanentEnchantmentItem {
    @Override
    public ItemEnchantments getDefaultEnchantments() {
        ItemEnchantments.Mutable temp = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        EnchantmentUtils.addEnchantment(DSEnchantments.DRAGONSBANE, temp, 3);
        return temp.toImmutable();
    }

    public DragonHunterSword(Properties pProperties) {
        super(DSEquipment.DRAGON_HUNTER, pProperties);
    }
}
