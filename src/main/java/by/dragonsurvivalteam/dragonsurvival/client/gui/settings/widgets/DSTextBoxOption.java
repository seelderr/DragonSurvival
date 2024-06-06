package by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.fields.TextField;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;

public class DSTextBoxOption extends Option {
	private final Function<Options, String> getter;

	public DSTextBoxOption(final String text, final Function<Options, String> getter) {
		super(text);
		this.getter = getter;
	}

	@Override
	public AbstractWidget createButton(final Options options, int x, int y, int width) {
		TextField widget = new TextField(x, y, width, 20, getCaption());
		widget.setMaxLength(128);
		widget.setValue(getter.apply(Minecraft.getInstance().options));
		return widget;
	}
}