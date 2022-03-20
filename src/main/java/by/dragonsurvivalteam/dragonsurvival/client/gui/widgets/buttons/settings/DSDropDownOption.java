package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.settings;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.DropDownButton;
import com.electronwill.nightconfig.core.EnumGetMethod;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DSDropDownOption extends AbstractOption{
	private final Consumer<Enum> setter;
	public Enum value;
	public DropDownButton btn;

	public DSDropDownOption(String pCaptionKey, Enum value, Consumer<Enum> setter){
		super(pCaptionKey);
		this.value = value;
		this.setter = setter;
	}

	@Override
	public Widget createButton(GameSettings pOptions, int pX, int pY, int pWidth){
		this.btn = new DropDownButton(pX, pY, pWidth, 20, value.name(), Arrays.stream(value.getDeclaringClass().getEnumConstants()).map((s) -> ((Enum)s).name()).collect(Collectors.toList()).toArray(new String[0]), (s) -> {
			setter.accept(EnumGetMethod.ORDINAL_OR_NAME_IGNORECASE.get(s, (Class<? extends Enum>)value.getDeclaringClass()));
		});
		return btn;
	}

	public ITextComponent getMessage(GameSettings p_238334_1_){
		return btn.getMessage();
	}
}