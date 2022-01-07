package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.settings.ClientSettingsScreen;
import by.jackraidenph.dragonsurvival.client.gui.widgets.lists.OptionsList;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.config.SyncBooleanConfig;
import by.jackraidenph.dragonsurvival.network.config.SyncEnumConfig;
import by.jackraidenph.dragonsurvival.network.config.SyncListConfig;
import by.jackraidenph.dragonsurvival.network.config.SyncNumberConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

import java.util.List;

public class ResetSettingsButton extends Button
{
	public static final ResourceLocation texture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/reset_icon.png");
	
	private AbstractOption option;
	public ResetSettingsButton(int x, int y, AbstractOption option)
	{
		super(x, y, 20, 20, null, (btn) -> {
			if(btn.active) {
				if (OptionsList.config.containsKey(option)) {
					Pair<ValueSpec, ConfigValue> pair = OptionsList.config.get(option);
					
					if (Minecraft.getInstance().screen instanceof ClientSettingsScreen) {
						ClientSettingsScreen screen = (ClientSettingsScreen)Minecraft.getInstance().screen;
						
						pair.getSecond().set(pair.getFirst().getDefault());
						
						String configKey = OptionsList.configMap.get(option);
						Widget widget = screen.list.findOption(option);
						
						Object ob = pair.getSecond().get();
						if (ob instanceof Boolean) {
							widget.setMessage(((BooleanOption)option).getMessage(Minecraft.getInstance().options));
							if(screen.getConfigName() != "client") {
								NetworkHandler.CHANNEL.sendToServer(new SyncBooleanConfig(configKey, (Boolean)ob, screen.getConfigName() == "server" ? 0 : 1));
							}
						}else if(ob instanceof Integer){
							widget.setMessage(((SliderPercentageOption)option).getMessage(Minecraft.getInstance().options));
							((AbstractSlider)widget).value = ((SliderPercentageOption)option).toPct((Integer)ob);
							if(screen.getConfigName() != "client") {
								NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(configKey, (Integer)ob, screen.getConfigName() == "server" ? 0 : 1));
							}
						}else if(ob instanceof Double){
							widget.setMessage(((SliderPercentageOption)option).getMessage(Minecraft.getInstance().options));
							((AbstractSlider)widget).value = ((SliderPercentageOption)option).toPct((Double)ob);
							if(screen.getConfigName() != "client") {
								NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(configKey, (Double)ob, screen.getConfigName() == "server" ? 0 : 1));
							}
						}else if(ob instanceof Long){
							widget.setMessage(((SliderPercentageOption)option).getMessage(Minecraft.getInstance().options));
							((AbstractSlider)widget).value = ((SliderPercentageOption)option).toPct((Long)ob);
							if(screen.getConfigName() != "client") {
								NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(configKey, (Long)ob, screen.getConfigName() == "server" ? 0 : 1));
							}
						}else if(ob instanceof Enum){
							widget.setMessage(((IteratableOption)option).getMessage(Minecraft.getInstance().options));
							if(screen.getConfigName() != "client") {
								NetworkHandler.CHANNEL.sendToServer(new SyncEnumConfig(configKey, (Enum)ob, screen.getConfigName() == "server" ? 0 : 1));
							}
						}else if(ob instanceof List){
							if(screen.getConfigName() != "client") {
								NetworkHandler.CHANNEL.sendToServer(new SyncListConfig(configKey, (List<String>)ob, screen.getConfigName() == "server" ? 0 : 1));
							}
						}
					}
				}
			}
		});
		
		this.option = option;
	}
	
	@Override
	public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
	{
		if(this.visible) {
			this.active = false;
			this.isHovered = p_230430_2_ >= this.x && p_230430_3_ >= this.y && p_230430_2_ < this.x + this.width && p_230430_3_ < this.y + this.height;
			
			if (OptionsList.config.containsKey(option)) {
				Pair<ValueSpec, ConfigValue> pair = OptionsList.config.get(option);
				this.active = !pair.getSecond().get().equals(pair.getFirst().getDefault());
			}
			
			Minecraft minecraft = Minecraft.getInstance();
			minecraft.getTextureManager().bind(WIDGETS_LOCATION);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
			int i = this.getYImage(this.isHovered());
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			this.blit(p_230430_1_, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
			this.blit(p_230430_1_, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
			this.renderBg(p_230430_1_, minecraft, p_230430_2_, p_230430_3_);
			
			Minecraft.getInstance().getTextureManager().bind(texture);
			blit(p_230430_1_, x + 2, y + 2, 0, 0, 16, 16, 16, 16);
		}
	}
}
