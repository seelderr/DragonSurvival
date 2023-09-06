package by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.fields.TextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DSNumberFieldOption extends Option {
	private final Function<Options, Number> getter;
	private final BiConsumer<Options, Number> setter;
	private final Function<Minecraft, List<FormattedCharSequence>> tooltipSupplier;

	private final Number min;
	private final Number max;
	private final boolean hasDecimals;

	public DSNumberFieldOption(String key, Number min, Number max, Function<Options, Number> getter, BiConsumer<Options, Number> setter, Function<Minecraft, List<FormattedCharSequence>> tooltipSupplier, boolean hasDecimals) {
		super(key);
		this.getter = getter;
		this.setter = setter;
		this.min = min;
		this.max = max;
		this.hasDecimals = hasDecimals;
		this.tooltipSupplier = tooltipSupplier;
	}

	@Override
	public AbstractWidget createButton(Options gameSettings, int i, int i1, int i2){
		TextField widget = new TextField(i, i1, i2, 18, getCaption()){
			@Override
			public boolean charTyped(char codePoint, int modifiers) {
				boolean isCharAllowed = super.charTyped(codePoint, modifiers);

				if (isCharAllowed && !getValue().isBlank()) {
					Number number = hasDecimals ? Double.parseDouble(getValue()) : Long.parseLong(getValue());

					// Have to explicitly storing non-decimal values in a long field, otherwise they still keep '.0' which causes problems
					if (number.doubleValue() > max.doubleValue()) {
						if (hasDecimals) {
							double value = max.doubleValue();
							setValue(String.valueOf(value));
							number = value;
						} else {
							long value = max.longValue();
							setValue(String.valueOf(value));
							number = value;
						}
					} else if (number.doubleValue() < min.doubleValue()) {
						if (hasDecimals) {
							double value = min.doubleValue();
							setValue(String.valueOf(value));
							number = value;
						} else {
							long value = min.longValue();
							setValue(String.valueOf(value));
							number = value;
						}
					}

					setter.accept(gameSettings, number);
				}

				return isCharAllowed;
			}
		};

		widget.tooltip = tooltipSupplier.apply(Minecraft.getInstance());
		widget.setFilter(text -> {
			if (text.isEmpty()) {
				return true;
			}

			try {
				if (hasDecimals) {
					Double.parseDouble(text);
				} else {
					Long.parseLong(text);
				}
			} catch (NumberFormatException e) {
				return false;
			}

			return true;
		});
		widget.setMaxLength(10);
		widget.setValue(getter.apply(Minecraft.getInstance().options).toString());

		return widget;
	}
}