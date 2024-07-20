package by.dragonsurvivalteam.dragonsurvival.common.items.armor;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEnchantments;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEquipment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class DragonHunterSword extends SwordItem implements PermanentEnchantmentItem {

    public DragonHunterSword(Properties pProperties) {
        super(DSEquipment.DRAGON_HUNTER, pProperties);
    }

    @Override
    public ItemEnchantments getDefaultEnchantments() {
        ItemEnchantments.Mutable temp = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.registryAccess().registry(Registries.ENCHANTMENT).isPresent()) {
            temp.set(Minecraft.getInstance().level.registryAccess().registry(Registries.ENCHANTMENT).get()
                    .getHolderOrThrow(DSEnchantments.DRAGONSBANE), 3);
        }
        return temp.toImmutable();
    }
}
