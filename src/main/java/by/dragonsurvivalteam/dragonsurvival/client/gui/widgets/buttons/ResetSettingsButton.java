package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.settings.ClientSettingsScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.fields.TextField;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.settings.DSDropDownOption;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionsList;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncBooleanConfig;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncEnumConfig;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncListConfig;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncNumberConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

import java.util.List;
import java.util.Objects;

public class ResetSettingsButton extends Button{
	public static final ResourceLocation texture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/reset_icon.png");

	private final AbstractOption option;

	public ResetSettingsButton(int x, int y, AbstractOption option){
		super(x, y, 20, 20, null, (btn) -> {
			if(btn.active){
				if(OptionsList.config.containsKey(option)){
					Pair<ValueSpec, ConfigValue> pair = OptionsList.config.get(option);

					if(Minecraft.getInstance().screen instanceof ClientSettingsScreen){
						ClientSettingsScreen screen = (ClientSettingsScreen)Minecraft.getInstance().screen;

						pair.getSecond().set(pair.getFirst().getDefault());

						String configKey = OptionsList.configMap.get(option);
						Widget widget = screen.list.findOption(option);

						Object ob = pair.getSecond().get();
						if(ob instanceof Boolean){
							if(widget != null){
								widget.setMessage(((BooleanOption)option).getMessage(Minecraft.getInstance().options));
							}
							if(!Objects.equals(screen.getConfigName(), "client")){
								NetworkHandler.CHANNEL.sendToServer(new SyncBooleanConfig(configKey, (Boolean)ob, screen.getConfigName()));
							}
						}else if(ob instanceof Integer){
							if(widget != null){
								if(widget instanceof AbstractSlider){
									widget.setMessage(((SliderPercentageOption)option).getMessage(Minecraft.getInstance().options));
									((AbstractSlider)widget).value = ((SliderPercentageOption)option).toPct((Integer)ob);
								}else if(widget instanceof TextField){
									((TextField)widget).setValue(ob.toString());
								}
							}
							if(!Objects.equals(screen.getConfigName(), "client")){
								NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(configKey, (Integer)ob, screen.getConfigName()));
							}
						}else if(ob instanceof Double){
							if(widget != null){
								if(widget instanceof AbstractSlider){
									widget.setMessage(((SliderPercentageOption)option).getMessage(Minecraft.getInstance().options));
									((AbstractSlider)widget).value = ((SliderPercentageOption)option).toPct((Double)ob);
								}else if(widget instanceof TextField){
									((TextField)widget).setValue(ob.toString());
								}
							}
							if(!Objects.equals(screen.getConfigName(), "client")){
								NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(configKey, (Double)ob, screen.getConfigName()));
							}
						}else if(ob instanceof Long){
							if(widget != null){
								if(widget instanceof AbstractSlider){
									widget.setMessage(((SliderPercentageOption)option).getMessage(Minecraft.getInstance().options));
									((AbstractSlider)widget).value = ((SliderPercentageOption)option).toPct((Long)ob);
								}else if(widget instanceof TextField){
									((TextField)widget).setValue(ob.toString());
								}
							}
							if(!Objects.equals(screen.getConfigName(), "client")){
								NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(configKey, (Long)ob, screen.getConfigName()));
							}
						}else if(ob instanceof Enum){
							((DSDropDownOption)option).btn.current = ((Enum<?>)ob).name();
							((DSDropDownOption)option).btn.updateMessage();

							if(!Objects.equals(screen.getConfigName(), "client")){
								NetworkHandler.CHANNEL.sendToServer(new SyncEnumConfig(configKey, (Enum)ob, screen.getConfigName()));
							}
						}else if(ob instanceof List){
							if(!Objects.equals(screen.getConfigName(), "client")){
								NetworkHandler.CHANNEL.sendToServer(new SyncListConfig(configKey, (List<String>)ob, screen.getConfigName()));
							}
						}
					}
				}
			}
		});

		this.option = option;
	}

	@Override
	public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_){
		if(this.visible){
			this.active = false;
			this.isHovered = p_230430_2_ >= this.x && p_230430_3_ >= this.y && p_230430_2_ < this.x + this.width && p_230430_3_ < this.y + this.height;

			if(OptionsList.config.containsKey(option)){
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