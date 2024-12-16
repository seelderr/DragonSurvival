package by.dragonsurvivalteam.dragonsurvival.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class DSColors {
    public static int ORANGE = -219136;
    public static int LIGHT_GRAY = -5592406;
    /** {@link net.minecraft.ChatFormatting#BLUE} */
    public static int BLUE = 5592575;

    public static Component blue(final Object value) {
        return withColor(value, BLUE);
    }

    public static Component withColor(final Object value, int color) {
        if (value instanceof MutableComponent mutable) {
            return mutable.withColor(color);
        }

        return Component.literal(String.valueOf(value)).withColor(color);
    }
}
