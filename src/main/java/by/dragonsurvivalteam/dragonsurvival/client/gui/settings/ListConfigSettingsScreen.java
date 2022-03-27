package by.dragonsurvivalteam.dragonsurvival.client.gui.settings;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.settings.DSTextBoxOption;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.settings.ResourceTextFieldOption;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionListEntry;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionsList;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.TextBoxEntry;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncListConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

import java.util.ArrayList;
import java.util.List;

public class ListConfigSettingsScreen extends OptionsScreen{
	private final ConfigValue value;
	private final ForgeConfigSpec spec;
	private final String configKey;
	private final ValueSpec valueSpec;

	private OptionsList list;

	private List<OptionListEntry> oldVals;

	private boolean isItems = false;

	public ListConfigSettingsScreen(Screen p_i225930_1_, Options p_i225930_2_, Component p_i225930_3_, ValueSpec valueSpec, ConfigValue value, ForgeConfigSpec spec, String configKey){
		super(p_i225930_1_, p_i225930_2_);
		this.value = value;
		this.spec = spec;
		this.configKey = configKey;
		this.valueSpec = valueSpec;
		this.title = p_i225930_3_;
	}

	protected void init(){
		double scroll = 0;
		if(list != null){
			oldVals = list.children();
			scroll = list.getScrollAmount();
		}

		this.list = new OptionsList(this.width, this.height, 32, this.height - 32){
			@Override
			protected int getMaxPosition(){
				return super.getMaxPosition() + 120;
			}
		};

		if(!isItems){
			if(ConfigHandler.isResourcePredicate((obj) -> valueSpec.test(obj))){
				isItems = true;
			}
		}

		if(oldVals == null || oldVals.isEmpty()){
			List<String> list = (List<String>)value.get();

			for(String t : list){
				createOption(t);
			}
		}else{
			for(OptionListEntry oldVal : oldVals){
				if(oldVal instanceof TextBoxEntry){
					TextBoxEntry textBoxEntry = (TextBoxEntry)oldVal;
					createOption(((EditBox)textBoxEntry.widget).getValue());
				}
			}

			oldVals = null;
		}

		this.children.add(this.list);
		this.addRenderableWidget(new Button(this.width / 2 + 20, this.height - 27, 100, 20, new TextComponent("Add new"), (p_213106_1_) -> {
			createOption("");
			list.setScrollAmount(list.getMaxScroll());
		}));

		this.addRenderableWidget(new Button(this.width / 2 - 120, this.height - 27, 100, 20, CommonComponents.GUI_DONE, (p_213106_1_) -> {
			ArrayList<String> output = new ArrayList<>();

			this.list.children().forEach((ent) -> {
				ent.children().forEach(child -> {
					if(child instanceof EditBox){
						String value = ((EditBox)child).getValue();

						if(!value.isEmpty()){
							output.add(value);
						}
					}
				});
			});

			value.set(output);

			if(spec != ConfigHandler.clientSpec){
				NetworkHandler.CHANNEL.sendToServer(new SyncListConfig(configKey, output, spec == ConfigHandler.serverSpec ? "server" : "common"));
			}

			this.minecraft.setScreen(this.lastScreen);
		}));

		this.list.setScrollAmount(scroll);
	}

	public void render(PoseStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_){
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		this.list.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
	}

	private void createOption(String t){
		Option option;

		if(isItems){
			option = new ResourceTextFieldOption(valueSpec, t, (settings) -> t);
		}else{
			option = new DSTextBoxOption(valueSpec, t, (settings) -> t);
		}
		AbstractWidget widget1 = option.createButton(this.minecraft.options, 32, 0, this.list.getScrollbarPosition() - 32 - 60);
		this.list.addEntry(new TextBoxEntry(option, this.list, widget1, null));
	}
}