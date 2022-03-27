package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.settings;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.DropDownButton;
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
import java.util.stream.Collectors;

public class DSDropDownOption extends Option{
	private final Consumer<Enum> setter;
	private final Function<Minecraft, List<FormattedCharSequence>> tooltipSupplier;
	public Enum value;
	public DropDownButton btn;

	public DSDropDownOption(String pCaptionKey, Enum value, Consumer<Enum> setter, Function<Minecraft, List<FormattedCharSequence>> pTooltipSupplier){
		super(pCaptionKey);
		this.value = value;
		this.setter = setter;
		this.tooltipSupplier = pTooltipSupplier;
	}

	@Override
	public AbstractWidget createButton(Options pOptions, int pX, int pY, int pWidth){
		this.btn = new DropDownButton(pX, pY, pWidth, 20, value.name(), Arrays.stream(value.getDeclaringClass().getEnumConstants()).map((s) -> ((Enum)s).name()).collect(Collectors.toList()).toArray(new String[0]), (s) -> {
			setter.accept(EnumGetMethod.ORDINAL_OR_NAME_IGNORECASE.get(s, (Class<? extends Enum>)value.getDeclaringClass()));
		});
		btn.tooltip = tooltipSupplier.apply(Minecraft.getInstance());
		return btn;
	}

	public Component getMessage(Options p_238334_1_){
		return btn.getMessage();
	}
}