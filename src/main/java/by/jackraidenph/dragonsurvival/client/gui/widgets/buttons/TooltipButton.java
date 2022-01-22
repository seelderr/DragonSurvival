package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.client.gui.utils.TooltipProvider;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import java.util.List;

public class TooltipButton extends ExtendedButton implements TooltipProvider
{
	private List<Component> tooltip;
	public TooltipButton(int xPos, int yPos, int width, int height, Component displayString, OnPress handler, List<Component> tooltip)
	{
		super(xPos, yPos, width, height, displayString, handler);
		this.tooltip = tooltip;
	}
	
	@Override
	public List<Component> getTooltip()
	{
		return tooltip;
	}
}
