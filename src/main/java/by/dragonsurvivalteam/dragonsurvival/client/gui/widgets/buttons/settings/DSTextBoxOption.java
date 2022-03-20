package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.settings;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.fields.TextField;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

import java.util.function.Function;

public class DSTextBoxOption extends AbstractOption{
	private final Function<GameSettings, String> getter;
	private final ValueSpec spec;

	public DSTextBoxOption(ValueSpec spec, String p_i51158_1_, Function<GameSettings, String> getter){
		super(p_i51158_1_);
		this.getter = getter;
		this.spec = spec;
	}

	@Override
	public Widget createButton(GameSettings gameSettings, int i, int i1, int i2){
		TextField widget = new TextField(spec, this, i, i1, i2, 20, this.getCaption());
		widget.setMaxLength(128);
		widget.setValue(getter.apply(Minecraft.getInstance().options));
		return widget;
	}
}