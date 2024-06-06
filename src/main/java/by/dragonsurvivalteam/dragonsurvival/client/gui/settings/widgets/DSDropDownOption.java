package by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.DropDownButton;
import com.electronwill.nightconfig.core.EnumGetMethod;
import java.util.Arrays;
import java.util.function.Consumer;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class DSDropDownOption extends Option {
	private final Consumer<Enum<?>> setter;
	public Enum<?> value;
	public DropDownButton btn;
	private final Tooltip tooltip;

	public DSDropDownOption(final String captionKey, final Enum<?> value, Consumer<Enum<?>> setter, final Tooltip tooltip) {
		super(captionKey);
		this.value = value;
		this.setter = setter;
		this.tooltip = tooltip;
	}

	@Override
	public AbstractWidget createButton(final Options options, int x, int y, int width) {
		btn = new DropDownButton(x, y, width, 20, value.name(), Arrays.stream(value.getDeclaringClass().getEnumConstants()).map(Enum::name).toList().toArray(new String[0]), s -> setter.accept(EnumGetMethod.ORDINAL_OR_NAME_IGNORECASE.get(s, (Class<? extends Enum>) value.getDeclaringClass())));
		btn.setTooltip(tooltip);
		return btn;
	}

	public Component getMessage(final Options ignored) {
		return btn.getMessage();
	}
}