package by.dragonsurvivalteam.dragonsurvival.common.items.armor;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class DragonHunterWeapon extends SwordItem implements PermanentEnchantmentItem {
    private final ItemEnchantments defaultEnchantments;

    @Override
    public ItemEnchantments getDefaultEnchantments() {
        return defaultEnchantments;
    }

    public DragonHunterWeapon(Tier pTier, ItemEnchantments defaultEnchantments, Properties pProperties) {
        super(pTier, pProperties);
        this.defaultEnchantments = defaultEnchantments;
    }
}
