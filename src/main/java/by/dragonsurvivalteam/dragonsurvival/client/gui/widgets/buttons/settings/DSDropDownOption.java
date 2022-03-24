package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.settings;

import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.DropDownButton;
import com.electronwill.nightconfig.core.EnumGetMethod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DSDropDownOption extends Option
{
	public Enum value;
	private final Consumer<Enum> setter;
	public DropDownButton btn;
	private final Function<Minecraft, List<FormattedCharSequence>> tooltipSupplier;
	
	public DSDropDownOption(String pCaptionKey, Enum value, Consumer<Enum> setter, Function<Minecraft, List<FormattedCharSequence>> pTooltipSupplier)
	{
		super(pCaptionKey);
		this.value = value;
		this.setter = setter;
		this.tooltipSupplier = pTooltipSupplier;
	}
	
	@Override
	public AbstractWidget createButton(Options pOptions, int pX, int pY, int pWidth)
	{
		this.btn = new DropDownButton(pX, pY, pWidth, 20, value.name(), Arrays.stream(value.getDeclaringClass().getEnumConstants()).map((s) ->((Enum)s).name()).collect(Collectors.toList()).toArray(new String[0]), (s) -> {
			setter.accept(EnumGetMethod.ORDINAL_OR_NAME_IGNORECASE.get(s, (Class<? extends Enum>)value.getDeclaringClass()));
		});
		btn.tooltip = tooltipSupplier.apply(Minecraft.getInstance());
		return btn;
	}
	
	public TextComponent getMessage(Options p_238334_1_) {
		return btn.getMessage();
	}
}
