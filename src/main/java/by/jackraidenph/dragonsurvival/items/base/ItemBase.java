package by.jackraidenph.dragonsurvival.items.base;

import by.jackraidenph.dragonsurvival.registration.ItemsInit;
import net.minecraft.item.Item;

public class ItemBase extends Item {
    public ItemBase() {
        super(new Item.Properties().tab(ItemsInit.items));
    }
}