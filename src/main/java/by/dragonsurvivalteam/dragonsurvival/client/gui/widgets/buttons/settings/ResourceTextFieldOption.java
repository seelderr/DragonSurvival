package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.settings;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.ResourceTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

import java.util.function.Function;

public class ResourceTextFieldOption extends Option{
	public final Function<Options, String> getter;
	private final ValueSpec spec;

	public ResourceTextFieldOption(ValueSpec spec, String p_i51158_1_, Function<Options, String> getter){
		super(p_i51158_1_);
		this.getter = getter;
		this.spec = spec;
	}

	@Override
	public AbstractWidget createButton(Options gameSettings, int i, int i1, int i2){
		ResourceTextField widget = new ResourceTextField(spec, this, i, i1, i2, 20, this.getCaption());
		widget.setMaxLength(128);
		widget.setValue(getter.apply(Minecraft.getInstance().options));
		return widget;
	}
}