package by.jackraidenph.dragonsurvival.client.gui.settings;

import by.jackraidenph.dragonsurvival.client.gui.widgets.lists.OptionsList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class SettingsSideScreen extends OptionsSubScreen
{
	public SettingsSideScreen(Screen p_i225930_1_, Options p_i225930_2_, Component p_i225930_3_)
	{
		super(p_i225930_1_, p_i225930_2_, p_i225930_3_);
	}
	private OptionsList list;
	
	protected void init() {
		this.list = new OptionsList(this.width, this.height, 32, this.height - 32);
		
		this.addRenderableWidget(new Button(this.width / 2 - 100, 38, 200, 20, new TranslatableComponent("ds.gui.settings.client"), (p_213106_1_) -> {
           Minecraft.getInstance().setScreen(new ClientSettingsScreen(this, Minecraft.getInstance().options, new TranslatableComponent("ds.gui.settings.client")));
		})
		);
		
		this.addRenderableWidget(new Button(this.width / 2 - 100, 38 + 27, 200, 20, new TranslatableComponent("ds.gui.settings.common"), (p_213106_1_) -> {
			Minecraft.getInstance().setScreen(new CommonSettingsScreen(this, Minecraft.getInstance().options, new TranslatableComponent("ds.gui.settings.common")));
			
		}){
			@Override
			public void render(PoseStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
			{
				this.active = Minecraft.getInstance().player.hasPermissions(2);
				super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
			}
		});
		
		this.addRenderableWidget(new Button(this.width / 2 - 100, 38 + 27 * 2, 200, 20, new TranslatableComponent("ds.gui.settings.server"), (p_213106_1_) -> {
			Minecraft.getInstance().setScreen(new ServerSettingsScreen(this, Minecraft.getInstance().options, new TranslatableComponent("ds.gui.settings.server")));
			
		}){
			@Override
			public void render(PoseStack  p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
			{
				this.active = Minecraft.getInstance().player.hasPermissions(2);
				super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
			}
		});
		
		/*
		this.addRenderableWidget(new Button(this.width / 2 - 100 + 205, 38, 20, 20, null, (p_213106_1_) -> {
		
		}){
			@Override
			public void render(PoseStack  p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
			{
				this.active = Minecraft.getInstance().player.hasPermissions(2);
				this.isHovered = p_230430_2_ >= this.x && p_230430_3_ >= this.y && p_230430_2_ < this.x + this.width && p_230430_3_ < this.y + this.height;
				
				Minecraft minecraft = Minecraft.getInstance();
				minecraft.getTextureManager().bindForSetup(WIDGETS_LOCATION);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
				int i = this.getYImage(this.isHovered);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.enableDepthTest();
				this.blit(p_230430_1_, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
				this.blit(p_230430_1_, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
				this.renderBg(p_230430_1_, minecraft, p_230430_2_, p_230430_3_);
				
				RenderSystem.setShaderTexture(0, ResetSettingsButton.texture);
				blit(p_230430_1_, x + 2, y + 2, 0, 0, 16, 16, 16, 16);
			}
		});
		
		this.addRenderableWidget(new Button(this.width / 2 - 100 + 205, 38 + 27, 20, 20, null, (p_213106_1_) -> {
		
		}){
			@Override
			public void render(PoseStack  p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
			{
				this.active = Minecraft.getInstance().player.hasPermissions(2);
				this.isHovered = p_230430_2_ >= this.x && p_230430_3_ >= this.y && p_230430_2_ < this.x + this.width && p_230430_3_ < this.y + this.height;
				
				Minecraft minecraft = Minecraft.getInstance();
				minecraft.getTextureManager().bindForSetup(WIDGETS_LOCATION);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
				int i = this.getYImage(this.isHovered);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.enableDepthTest();
				this.blit(p_230430_1_, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
				this.blit(p_230430_1_, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
				this.renderBg(p_230430_1_, minecraft, p_230430_2_, p_230430_3_);
				
				RenderSystem.setShaderTexture(0, ResetSettingsButton.texture);
				blit(p_230430_1_, x + 2, y + 2, 0, 0, 16, 16, 16, 16);
			}
		});
		
		this.addRenderableWidget(new Button(this.width / 2 - 100 + 205, 38 + 27 * 2, 20, 20, null, (p_213106_1_) -> {
		
		}){
			@Override
			public void render(PoseStack  p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
			{
				this.active = Minecraft.getInstance().player.hasPermissions(2);
				this.isHovered = p_230430_2_ >= this.x && p_230430_3_ >= this.y && p_230430_2_ < this.x + this.width && p_230430_3_ < this.y + this.height;
				
				Minecraft minecraft = Minecraft.getInstance();
				minecraft.getTextureManager().bindForSetup(WIDGETS_LOCATION);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
				int i = this.getYImage(this.isHovered);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.enableDepthTest();
				this.blit(p_230430_1_, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
				this.blit(p_230430_1_, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
				this.renderBg(p_230430_1_, minecraft, p_230430_2_, p_230430_3_);
				
				RenderSystem.setShaderTexture(0, ResetSettingsButton.texture);
				blit(p_230430_1_, x + 2, y + 2, 0, 0, 16, 16, 16, 16);
			}
		});
		 */
		
		this.children.add(this.list);
		
		this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 27, 200, 20, CommonComponents.GUI_BACK, (p_213106_1_) -> {
			this.minecraft.setScreen(this.lastScreen);
		}));
	}
	public void render(PoseStack  p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
		this.renderBackground(p_230430_1_);
		this.list.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 5, 16777215);
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
	}
}
