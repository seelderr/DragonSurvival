package by.dragonsurvivalteam.dragonsurvival.client.gui.settings;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionsList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;


public class ConfigSideSelectionScreen extends OptionsSubScreen{
	private OptionsList list;

	public ConfigSideSelectionScreen(Screen p_i225930_1_, Options p_i225930_2_, Component p_i225930_3_){
		super(p_i225930_1_, p_i225930_2_, p_i225930_3_);
		this.title = p_i225930_3_;
	}

	@Override
	protected void init(){
		this.list = new OptionsList(this.width, this.height, 32, this.height - 32);

		this.addRenderableWidget(new Button(this.width / 2 - 100, 38, 200, 20, new TranslatableComponent("ds.gui.settings.client"), p_213106_1_ -> {
			Minecraft.getInstance().setScreen(new ClientConfigScreen(this, Minecraft.getInstance().options, new TranslatableComponent("ds.gui.settings.client")));
		}));


		this.addRenderableWidget(new Button(this.width / 2 - 100, 38 + 27, 200, 20, new TranslatableComponent("ds.gui.settings.server"), p_213106_1_ -> {
			Minecraft.getInstance().setScreen(new ServerConfigScreen(this, Minecraft.getInstance().options, new TranslatableComponent("ds.gui.settings.server")));
		}){
			@Override
			public void render(PoseStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_){
				this.active = Minecraft.getInstance().player.hasPermissions(2);
				super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
			}
		});

		this.children.add(this.list);

		this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 27, 200, 20, CommonComponents.GUI_BACK, p_213106_1_ -> {
			this.minecraft.setScreen(this.lastScreen);
		}));
	}

	public void render(PoseStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_){
		this.renderBackground(p_230430_1_);
		this.list.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
	}
}