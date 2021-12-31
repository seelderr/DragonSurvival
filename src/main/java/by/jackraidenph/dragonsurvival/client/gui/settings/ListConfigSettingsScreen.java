package by.jackraidenph.dragonsurvival.client.gui.settings;

import by.jackraidenph.dragonsurvival.client.gui.widgets.lists.OptionsList;
import by.jackraidenph.dragonsurvival.client.gui.widgets.lists.TextBoxOption;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.config.SyncListConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

import java.util.ArrayList;
import java.util.List;

public class ListConfigSettingsScreen extends SettingsScreen
{
	private ConfigValue value;
	private ForgeConfigSpec spec;
	private String configKey;
	
	private OptionsList list;
	
	public ListConfigSettingsScreen(Screen p_i225930_1_, GameSettings p_i225930_2_, ITextComponent p_i225930_3_, ConfigValue value, ForgeConfigSpec spec, String configKey)
	{
		super(p_i225930_1_, p_i225930_2_, p_i225930_3_);
		this.value = value;
		this.spec = spec;
		this.configKey = configKey;
	}
	
	
	protected void init() {
		this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
		
		List<String> list = (List<String>)value.get();
		
		for(String t : list){
			AbstractOption option = new TextBoxOption(t, (settings) -> { return t; });
			this.list.textBox(option);
		}
		
		AbstractOption option = new IteratableOption("Add new", (settings, val) -> {
			OptionsList.Entry ent = this.list.findEntry("Add new");
			this.list.removeEntry(ent);
			
			AbstractOption newOpt = new TextBoxOption("", (settings1) -> { return ""; });
			this.list.textBox(newOpt);
			this.list.addEntry(ent);
			this.list.scroll(ent.getHeight());
			
		}, (settings, set) -> {
			return new StringTextComponent("Add new");
		});
		this.list.addBig(option, null);
		
		this.children.add(this.list);
		this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, DialogTexts.GUI_DONE, (p_213106_1_) -> {
			ArrayList<String> output = new ArrayList<>();
			
			this.list.children().forEach((ent) -> {
				ent.children().forEach(child -> {
					if(child instanceof TextFieldWidget) {
						String value = ((TextFieldWidget)child).getValue();
						
						if(!value.isEmpty()){
							output.add(value);
						}
					}
				});
			});
			
			value.set(output);
			
			if(spec != ConfigHandler.clientSpec){
				NetworkHandler.CHANNEL.sendToServer(new SyncListConfig(configKey, output, spec == ConfigHandler.serverSpec ? 0 : 1));
			}
			
			this.minecraft.setScreen(this.lastScreen);
		}));
	}
	
	@Override
	public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_)
	{
		list.children().forEach((ent) -> {
			ent.children().forEach(child -> {
				if(child instanceof TextFieldWidget) {
					child.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
				}
			});
		});
		return super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
	}
	
	public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
		this.renderBackground(p_230430_1_);
		this.list.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 5, 16777215);
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
	}
	
}
