package by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.fields.TextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DSNumberFieldOption extends Option{
	private final Function<Options, Number> getter;
	private final BiConsumer<Options, Number> setter;
	private final Function<Minecraft, List<FormattedCharSequence>> tooltipSupplier;

	private final Number min;
	private final Number max;

	public DSNumberFieldOption(String key, Number min, Number max, Function<Options, Number> getter, BiConsumer<Options, Number> setter, Function<Minecraft, List<FormattedCharSequence>> pTooltipSupplier){
		super(key);
		this.getter = getter;
		this.setter = setter;
		this.min = min;
		this.max = max;
		tooltipSupplier = pTooltipSupplier;
	}

	@Override
	public AbstractWidget createButton(Options gameSettings, int i, int i1, int i2){
		TextField widget = new TextField(null, this, i, i1, i2, 18, getCaption()){
			@Override
			public boolean charTyped(char pCodePoint, int pModifiers){
				boolean val = super.charTyped(pCodePoint, pModifiers);
				if(val){
					Number num = NumberUtils.createNumber(getValue());
					if(num.doubleValue() > max.doubleValue())
						setValue(max.toString());
					else if(num.doubleValue() < min.doubleValue())
						setValue(min.toString());
					setter.accept(gameSettings, num);
				}
				return val;
			}
		};
		widget.tooltip = tooltipSupplier.apply(Minecraft.getInstance());
		widget.setFilter(s -> NumberUtils.isCreatable(s) || s.isEmpty());
		widget.setMaxLength(10);
		widget.setValue(getter.apply(Minecraft.getInstance().options).toString());
		return widget;
	}
}