package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.settings;

import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.DropDownButton;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

import java.util.function.Consumer;

public class DSDropDownOption extends AbstractOption
{
	public Enum value;
	private final Consumer<Enum> setter;
	public DropDownButton btn;
	
	public DSDropDownOption(String pCaptionKey, Enum value, Consumer<Enum> setter)
	{
		super(pCaptionKey);
		this.value = value;
		this.setter = setter;
	}
	
	@Override
	public Widget createButton(GameSettings pOptions, int pX, int pY, int pWidth)
	{
		this.btn = new DropDownButton(value, pX, pY, pWidth, 20, setter);
		return btn;
	}
	
	public ITextComponent getMessage(GameSettings p_238334_1_) {
		return btn.getMessage();
	}
}
