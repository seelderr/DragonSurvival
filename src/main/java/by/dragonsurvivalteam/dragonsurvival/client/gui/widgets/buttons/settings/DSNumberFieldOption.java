package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.settings;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.fields.TextField;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionListEntry;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionsList;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DSNumberFieldOption extends AbstractOption{
	private final Function<GameSettings, Number> getter;
	private final BiConsumer<GameSettings, Number> setter;

	private final Number min;
	private final Number max;

	public DSNumberFieldOption(String key, Number min, Number max, Function<GameSettings, Number> getter, BiConsumer<GameSettings, Number> setter){
		super(key);
		this.getter = getter;
		this.setter = setter;
		this.min = min;
		this.max = max;
	}

	@Override
	public Widget createButton(GameSettings gameSettings, int i, int i1, int i2){
		TextField widget = new TextField(null, this, i, i1, i2, 18, this.getCaption()){
			@Override
			public boolean charTyped(char pCodePoint, int pModifiers){
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

			@Override
			public boolean mouseClicked(double pMouseX, double pMouseY, int pButton){
				Screen screen = Minecraft.getInstance().screen;

				for(IGuiEventListener child : screen.children){
					if(child instanceof OptionsList){
						for(OptionListEntry optionListEntry : ((OptionsList)child).children()){
							for(IGuiEventListener button : optionListEntry.children()){
								if(button != this && button instanceof TextField){
									((TextField)button).setFocus(false);
								}
							}
						}
					}
				}

				return super.mouseClicked(pMouseX, pMouseY, pButton);
			}
		};
		widget.setFilter((s) -> NumberUtils.isCreatable(s) || s.isEmpty());
		widget.setMaxLength(10);
		widget.setValue(getter.apply(Minecraft.getInstance().options).toString());
		return widget;
	}
}