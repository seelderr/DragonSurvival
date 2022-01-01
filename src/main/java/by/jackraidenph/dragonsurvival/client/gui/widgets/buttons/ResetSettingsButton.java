package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.settings.ClientSettingsScreen;
import by.jackraidenph.dragonsurvival.client.gui.widgets.lists.OptionsList;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.config.SyncBooleanConfig;
import by.jackraidenph.dragonsurvival.network.config.SyncEnumConfig;
import by.jackraidenph.dragonsurvival.network.config.SyncListConfig;
import by.jackraidenph.dragonsurvival.network.config.SyncNumberConfig;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;

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
						
						ForgeConfigSpec spec = screen.getSpec();
						pair.getSecond().set(pair.getFirst().getDefault());
						
						String configKey = OptionsList.configMap.get(option);
						
						if(spec != ConfigHandler.clientSpec) {
							Object ob = pair.getSecond().get();
							if (ob instanceof BooleanValue) {
								NetworkHandler.CHANNEL.sendToServer(new SyncBooleanConfig(configKey, ((BooleanValue)ob).get(), spec == ConfigHandler.serverSpec ? 0 : 1));
								
							}else if(ob instanceof IntValue){
								NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(configKey, ((IntValue)ob).get(), spec == ConfigHandler.serverSpec ? 0 : 1));
								
							}else if(ob instanceof DoubleValue){
								NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(configKey, ((DoubleValue)ob).get(), spec == ConfigHandler.serverSpec ? 0 : 1));
								
							}else if(ob instanceof LongValue){
								NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(configKey, ((LongValue)ob).get(), spec == ConfigHandler.serverSpec ? 0 : 1));
								
							}else if(ob instanceof EnumValue){
								Class<? extends Enum> cs = (Class<? extends Enum>)((EnumValue)ob).get().getClass();
								int curVal = ((Enum)((EnumValue)ob).get()).ordinal();
								Enum en = EnumGetMethod.ORDINAL_OR_NAME.get(curVal, cs);
								NetworkHandler.CHANNEL.sendToServer(new SyncEnumConfig(configKey, en, spec == ConfigHandler.serverSpec ? 0 : 1));
								
							}else if(ob instanceof ConfigValue){
								NetworkHandler.CHANNEL.sendToServer(new SyncListConfig(configKey, (List<String>)((ConfigValue)ob).get(), spec == ConfigHandler.serverSpec ? 0 : 1));

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
			
			Minecraft.getInstance().getTextureManager().bind(texture);
			blit(p_230430_1_, x, y, 0, 0, 16, 16, 16, 16);
			
			
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
		}
	}
}
