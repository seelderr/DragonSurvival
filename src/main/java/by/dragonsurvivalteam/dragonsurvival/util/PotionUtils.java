package by.dragonsurvivalteam.dragonsurvival.util;

import static net.minecraft.core.component.DataComponents.POTION_CONTENTS;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;

public class PotionUtils {
    public static Potion getPotion(ItemStack item) {
        return item.get(POTION_CONTENTS).potion().get().value();
    }
}
