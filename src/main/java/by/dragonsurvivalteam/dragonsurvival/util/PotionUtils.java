package by.dragonsurvivalteam.dragonsurvival.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;

import static net.minecraft.core.component.DataComponents.POTION_CONTENTS;

public class PotionUtils {
    public static Potion getPotion(ItemStack item) {
        return item.get(POTION_CONTENTS).potion().get().value();
    }
}
