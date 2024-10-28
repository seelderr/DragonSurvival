package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public class MouseTooltipPositioner implements ClientTooltipPositioner {
	private final AbstractWidget widget;

	public MouseTooltipPositioner(final AbstractWidget widget) {
		this.widget = widget;
	}

	@Override
	public @NotNull Vector2ic positionTooltip(int screenWidth, int screenHeight, int mouseX, int mouseY, int tooltipWidth, int tooltipHeight) {
		int x = mouseX + 7;
		int y = mouseY - 7;

		if (x + tooltipWidth > screenWidth) {
			x -= (screenWidth - (x + tooltipWidth));
		}

		if (y + tooltipHeight > screenHeight) {
			y -= (screenHeight - (y + tooltipHeight));
		}

		return new Vector2i(x, y);
	}
}
