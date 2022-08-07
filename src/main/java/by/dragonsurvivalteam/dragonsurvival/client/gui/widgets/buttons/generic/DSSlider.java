package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic;

import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ForgeSlider;

public class DSSlider extends ForgeSlider{
	public DSSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString){
		super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString);
	}

	public DSSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString){
		super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, drawString);
	}

	@Override
	public void setValue(double value){
		super.setValue(value);
		applyValue();
	}
}