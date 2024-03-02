package by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.DropDownButton;
import com.electronwill.nightconfig.core.EnumGetMethod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class DSDropDownOption extends Option {
	private final Consumer<Enum<?>> setter;
	private final Function<Minecraft, List<FormattedCharSequence>> tooltipSupplier;
	public Enum<?> value;
	public DropDownButton btn;

	public DSDropDownOption(final String captionKey, final Enum<?> value, Consumer<Enum<?>> setter, final Function<Minecraft, List<FormattedCharSequence>> tooltipSupplier) {
		super(captionKey);
		this.value = value;
		this.setter = setter;
		this.tooltipSupplier = tooltipSupplier;
	}

	@Override
	public AbstractWidget createButton(final Options options, int x, int y, int width) {
		btn = new DropDownButton(x, y, width, 20, value.name(), Arrays.stream(value.getDeclaringClass().getEnumConstants()).map(Enum::name).toList().toArray(new String[0]), s -> setter.accept(EnumGetMethod.ORDINAL_OR_NAME_IGNORECASE.get(s, (Class<? extends Enum>) value.getDeclaringClass())));
		btn.tooltip = tooltipSupplier.apply(Minecraft.getInstance());
		return btn;
	}

	public Component getMessage(final Options ignored) {
		return btn.getMessage();
	}
}