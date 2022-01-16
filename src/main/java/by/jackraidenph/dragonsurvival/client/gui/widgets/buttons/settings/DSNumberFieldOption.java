package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.settings;

import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.fields.TextField;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DSNumberFieldOption extends AbstractOption
{
	private final Function<GameSettings, Number> getter;
	private final BiConsumer<GameSettings, Number> setter;
	
	private Number min;
	private Number max;
	
	public DSNumberFieldOption(String key, Number min, Number max, Function<GameSettings, Number> getter, BiConsumer<GameSettings, Number> setter)
	{
		super(key);
		this.getter = getter;
		this.setter = setter;
		this.min = min;
		this.max = max;
	}
	
	@Override
	public Widget createButton(GameSettings gameSettings, int i, int i1, int i2)
	{
		TextField widget = new TextField(null, this, i, i1, i2, 18, this.getCaption()){
			@Override
			public boolean charTyped(char pCodePoint, int pModifiers)
			{
				boolean val = super.charTyped(pCodePoint, pModifiers);
				if(val){
					Number num = NumberUtils.createNumber(getValue());
					if(num.doubleValue() > max.doubleValue()){
						setValue(max.toString());
					}else if(num.doubleValue() < min.doubleValue()){
						setValue(min.toString());
					}
					setter.accept(gameSettings, num);
				}
				return val;
			}
		};
		widget.setFilter((s) -> NumberUtils.isCreatable(s) || s.isEmpty());
		widget.setMaxLength(10);
		widget.setValue(getter.apply(Minecraft.getInstance().options).toString());
		return widget;
	}
}
