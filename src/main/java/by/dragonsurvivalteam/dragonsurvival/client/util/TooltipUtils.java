package by.dragonsurvivalteam.dragonsurvival.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.List;
import java.util.Objects;

public class TooltipUtils {
    public static Tooltip createTooltip(final Component tooltip, int maxWidth) {
        List<FormattedText> formattedTexts = Minecraft.getInstance().font.getSplitter().splitLines(tooltip, maxWidth, Style.EMPTY);
        MutableComponent base = Component.empty();

        for (FormattedText formattedText : formattedTexts) {
            base.append(formattedText.getString());
        }

        return Tooltip.create(base);
    }

    public static boolean needsTooltip(final AbstractWidget widget) {
        Tooltip tooltip = widget.getTooltip();

        if (tooltip == null) {
            return true;
        }

        Component message = tooltip.message;
        return message == null || Objects.equals(message, Component.empty());
    }
}