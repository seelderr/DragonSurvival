package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.settings;

import by.jackraidenph.dragonsurvival.client.gui.widgets.ItemStackField;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;

import java.util.function.Function;

public class DSItemStackFieldOption extends AbstractOption
{
	public final Function<GameSettings, String> getter;
	
	public DSItemStackFieldOption(String p_i51158_1_, Function<GameSettings, String> getter)
	{
		super(p_i51158_1_);
		this.getter = getter;
	}
	
	@Override
	public Widget createButton(GameSettings gameSettings, int i, int i1, int i2)
	{
		ItemStackField widget = new ItemStackField(this, i, i1, i2, 20, this.getCaption());
		widget.setMaxLength(128);
		widget.setValue(getter.apply(Minecraft.getInstance().options));
		return widget;
	}
}
