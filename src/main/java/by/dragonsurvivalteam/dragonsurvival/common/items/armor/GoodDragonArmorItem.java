package by.dragonsurvivalteam.dragonsurvival.common.items.armor;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEnchantments;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEquipment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class GoodDragonArmorItem extends ArmorItem implements PermanentEnchantmentItem {
    public ItemEnchantments getDefaultEnchantments() {
        ItemEnchantments.Mutable temp = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.registryAccess().registry(Registries.ENCHANTMENT).isPresent()) {
            Minecraft.getInstance().level.registryAccess().registry(Registries.ENCHANTMENT).get();
            switch (this.getType()) {
                case HELMET ->
                        temp.set(Minecraft.getInstance().level.registryAccess().registry(Registries.ENCHANTMENT).get()
                                .getHolderOrThrow(DSEnchantments.COMBAT_RECOVERY), 1);
                case CHESTPLATE ->
                        temp.set(Minecraft.getInstance().level.registryAccess().registry(Registries.ENCHANTMENT).get()
                            .getHolderOrThrow(DSEnchantments.UNBREAKABLE_SPIRIT), 1);
                case LEGGINGS ->
                        temp.set(Minecraft.getInstance().level.registryAccess().registry(Registries.ENCHANTMENT).get()
                                .getHolderOrThrow(DSEnchantments.AERODYNAMIC_MASTERY), 1);
                case BOOTS ->
                        temp.set(Minecraft.getInstance().level.registryAccess().registry(Registries.ENCHANTMENT).get()
                                .getHolderOrThrow(DSEnchantments.SACRED_SCALES), 1);
            }
            temp.set(Minecraft.getInstance().level.registryAccess().registry(Registries.ENCHANTMENT).get()
                    .getHolderOrThrow(DSEnchantments.CURSE_OF_KINDNESS), 1);
        }
        return temp.toImmutable();
    }

    public GoodDragonArmorItem(Type pType, Properties pProperties) {
        super(DSEquipment.GOOD_DRAGON_ARMOR_MATERIAL, pType, pProperties);
    }
}
