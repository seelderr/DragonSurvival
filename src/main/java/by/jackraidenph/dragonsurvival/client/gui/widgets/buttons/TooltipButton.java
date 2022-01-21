package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.client.gui.utils.TooltipProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import java.util.List;

public class TooltipButton extends ExtendedButton implements TooltipProvider
{
	private List<FormattedCharSequence> tooltip;
	public TooltipButton(int xPos, int yPos, int width, int height, Component displayString, OnPress handler, List<FormattedCharSequence> tooltip)
	{
		super(xPos, yPos, width, height, displayString, handler);
		this.tooltip = tooltip;
	}
	
	@Override
	public List<FormattedCharSequence> getTooltip()
	{
		return tooltip;
	}
}
