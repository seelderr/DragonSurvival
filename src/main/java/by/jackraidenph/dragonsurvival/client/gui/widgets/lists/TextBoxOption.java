package by.jackraidenph.dragonsurvival.client.gui.widgets.lists;

import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;

import java.util.function.Function;

public class TextBoxOption extends AbstractOption
{
	private final Function<GameSettings, String> getter;
	
	public TextBoxOption(String p_i51158_1_, Function<GameSettings, String> getter)
	{
		super(p_i51158_1_);
		this.getter = getter;
	}
	
	@Override
	public Widget createButton(GameSettings gameSettings, int i, int i1, int i2)
	{
		TextFieldWidget widget = new TextFieldWidget(Minecraft.getInstance().font, i, i1, i2, 18, this.getCaption());
		widget.setMaxLength(128);
		widget.setValue(getter.apply(Minecraft.getInstance().options));
		return widget;
	}
}
