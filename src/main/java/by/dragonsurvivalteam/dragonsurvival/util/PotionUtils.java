package by.dragonsurvivalteam.dragonsurvival.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;

public class PotionUtils {
    public static Potion getPotion(ItemStack item) {
        return getPotion(item.getItem());
    }

    public static Potion getPotion(Item item) {
        return BuiltInRegistries.POTION.getHolder(PotionItem.getId(item)).get().value();
    }
}
