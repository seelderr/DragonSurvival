package by.dragonsurvivalteam.dragonsurvival.common.items.armor;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEnchantments;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEquipment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class EvilDragonArmorItem extends ArmorItem implements PermanentEnchantmentItem {
    public ItemEnchantments getDefaultEnchantments() {
        ItemEnchantments.Mutable temp = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.registryAccess().registry(Registries.ENCHANTMENT).isPresent()) {
            Minecraft.getInstance().level.registryAccess().registry(Registries.ENCHANTMENT).get();
            switch (this.getType()) {
                case HELMET ->
                    temp.set(Minecraft.getInstance().level.registryAccess().registry(Registries.ENCHANTMENT).get()
                            .getHolderOrThrow(DSEnchantments.BLOOD_SIPHON), 1);
                case CHESTPLATE ->
                    temp.set(Minecraft.getInstance().level.registryAccess().registry(Registries.ENCHANTMENT).get()
                            .getHolderOrThrow(DSEnchantments.MURDERERS_CUNNING), 1);
                case LEGGINGS ->
                    temp.set(Minecraft.getInstance().level.registryAccess().registry(Registries.ENCHANTMENT).get()
                            .getHolderOrThrow(DSEnchantments.OVERWHELMING_MIGHT), 1);
                case BOOTS ->
                    temp.set(Minecraft.getInstance().level.registryAccess().registry(Registries.ENCHANTMENT).get()
                            .getHolderOrThrow(DSEnchantments.DRACONIC_SUPERIORITY), 1);
            }
            temp.set(Minecraft.getInstance().level.registryAccess().registry(Registries.ENCHANTMENT).get()
                    .getHolderOrThrow(DSEnchantments.CURSE_OF_OUTLAW), 1);
        }
        return temp.toImmutable();
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return super.isBookEnchantable(stack, book);
    }

    public EvilDragonArmorItem(Type pType, Properties pProperties) {
        super(DSEquipment.EVIL_DRAGON_ARMOR_MATERIAL, pType, pProperties);
    }
}
