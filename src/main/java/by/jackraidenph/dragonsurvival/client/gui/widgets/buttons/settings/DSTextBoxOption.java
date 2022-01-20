package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.settings;

import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.fields.TextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

import java.util.function.Function;

public class DSTextBoxOption extends Option
{
	private final Function<Options, String> getter;
	private ValueSpec spec;
	
	public DSTextBoxOption(ValueSpec spec, String p_i51158_1_, Function<Options, String> getter)
	{
		super(p_i51158_1_);
		this.getter = getter;
		this.spec = spec;
	}
	
	@Override
	public AbstractWidget createButton(Options gameSettings, int i, int i1, int i2)
	{
		TextField widget = new TextField(spec, this,  i, i1, i2, 20, this.getCaption());
		widget.setMaxLength(128);
		widget.setValue(getter.apply(Minecraft.getInstance().options));
		return widget;
	}
}
