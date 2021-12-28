package by.jackraidenph.dragonsurvival.client.gui.settings;

import by.jackraidenph.dragonsurvival.client.gui.widgets.lists.OptionsList;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import com.electronwill.nightconfig.core.AbstractConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig.Entry;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IBidiTooltip;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;

import javax.annotation.Nullable;
import java.util.*;

public class ClientSettingsScreen extends SettingsScreen
{
	private ArrayList<AbstractOption> OPTIONS = new ArrayList<>();
	private HashMap<String, ArrayList<AbstractOption>> optionMap = new HashMap<>();
	private ArrayList<String> options = new ArrayList<>();
	private OptionsList list;
	
	protected void init() {
		this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
		
		for (Entry entry : getSpec().entrySet()) {
			Object value = getSpec().get(entry.getKey());
			
			if(value instanceof AbstractConfig){
				AbstractConfig config = (AbstractConfig)value;
				for(Map.Entry<String, Object> ent : config.valueMap().entrySet()){
					if(!options.contains(ent.getKey())) {
						
						if (ent.getValue() instanceof AbstractConfig) {
							AbstractConfig config1 = (AbstractConfig)ent.getValue();
							
							for (Map.Entry<String, Object> ent1 : config1.valueMap().entrySet()) {
								if (!options.contains(ent1.getKey())) {
									
									if (ent1.getValue() instanceof AbstractConfig) {
										AbstractConfig config2 = (AbstractConfig)ent1.getValue();
										for (Map.Entry<String, Object> ent2 : config2.valueMap().entrySet()) {
											if (!options.contains(ent2.getKey())) {
	
												addValue(ent.getKey() + "." + ent1.getKey() + "." + ent2.getKey(), ent1.getKey(), ent2.getKey(), ent2.getValue());
											}
										}
									} else {
										addValue(ent.getKey() + "." + ent1.getKey(), ent.getKey(), ent1.getKey(), ent1.getValue());
									}
								}
							}
						} else {
							addValue(ent.getKey(), null, ent.getKey(), ent.getValue());
						}
					}
				}
			}else{
				addValue("", null, entry.getKey(), value);
			}
		}
		
		this.list.addSmall(OPTIONS.toArray(new AbstractOption[0]));
		
		for(Map.Entry<String, ArrayList<AbstractOption>> ent : optionMap.entrySet()){
			this.list.addCategory(ent.getKey());
			this.list.addSmall(ent.getValue().toArray(new AbstractOption[0]));
		}
		
		this.children.add(this.list);
		this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, DialogTexts.GUI_BACK, (p_213106_1_) -> {
			this.minecraft.setScreen(this.lastScreen);
		}));
	}
	
	public ForgeConfigSpec getSpec(){
		return ConfigHandler.clientSpec;
	}
	
	public String getConfigName(){
		return "client";
	}
	
	public void addValue(String key, String category, String path, Object value){
		if(options.contains(path)) return;
		
		ValueSpec spec = (ValueSpec)value;
		Object ob = getSpec().getValues().get("client." + key);
		
		if(ob instanceof BooleanValue) {
			BooleanValue booleanValue = (BooleanValue)ob;
			
			AbstractOption option = new BooleanOption(path, new StringTextComponent(spec.getComment()), (settings) -> booleanValue.get(), (settings, settingValue) -> {
				booleanValue.set(settingValue);
			});
			
			addOption(category, path, option);
		}else if(ob instanceof IntValue) {
			IntValue value1 = (IntValue)ob;
			Integer min = (Integer)spec.correct(Integer.MIN_VALUE);
			Integer max = (Integer)spec.correct(Integer.MAX_VALUE);
			
			AbstractOption option = new SliderPercentageOption(path, min, max, 0.1F, (settings) -> Double.valueOf(value1.get()), (settings, settingValue) -> {
				value1.set(settingValue.intValue());
			}, (settings, slider) -> {
				return new TranslationTextComponent("options.generic_value", new TranslationTextComponent(path), slider.get(settings));
			});
			
			addOption(category, path, option);
		}else if(ob instanceof DoubleValue) {
			DoubleValue value1 = (DoubleValue)ob;
			double min = (double)spec.correct(Double.MIN_VALUE);
			double max = (double)spec.correct(Double.MAX_VALUE);
			
			AbstractOption option = new SliderPercentageOption(path, min, max, 0.1F, (settings) -> value1.get(), (settings, settingValue) -> {
				value1.set(settingValue);
			}, (settings, slider) -> {
				return new TranslationTextComponent("options.generic_value", new TranslationTextComponent(path), slider.get(settings));
			});
			
			addOption(category, path, option);
		}else if(ob instanceof LongValue) {
			LongValue value1 = (LongValue)ob;
			Long min = (Long)spec.correct(Long.MIN_VALUE);
			Long max = (Long)spec.correct(Long.MAX_VALUE);
			
			AbstractOption option = new SliderPercentageOption(path, min, max, 0.1F, (settings) -> Double.valueOf(value1.get()), (settings, settingValue) -> {
				value1.set(settingValue.longValue());
			}, (settings, slider) -> {
				return new TranslationTextComponent("options.generic_value", new TranslationTextComponent(path), slider.get(settings));
			});
			
			addOption(category, path, option);
		}else if(ob instanceof EnumValue) {
			EnumValue value1 = (EnumValue)ob;
			
			AbstractOption option = new IteratableOption(path, (settings, val) -> value1.set(value1.get()), (settings, set) -> {
				return new TranslationTextComponent("options.generic_value", new TranslationTextComponent(path), set.getMessage(settings));
			});
			
			addOption(category, path, option);
		}
	}
	
	private void addOption(String category, String path, AbstractOption option)
	{
		if(category != null){
			if(!optionMap.containsKey(category)){
				optionMap.put(category, new ArrayList<>());
			}
			optionMap.get(category).add(option);
		}else{
			OPTIONS.add(option);
		}
		
		options.add(path);
	}
	
	
	public ClientSettingsScreen(Screen p_i225930_1_, GameSettings p_i225930_2_, ITextComponent p_i225930_3_)
	{
		super(p_i225930_1_, p_i225930_2_, p_i225930_3_);
	}
	public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
		this.renderBackground(p_230430_1_);
		this.list.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 5, 16777215);
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		List<IReorderingProcessor> list = tooltipAt(this.list, p_230430_2_, p_230430_3_);
		if (list != null) {
			this.renderTooltip(p_230430_1_, list, p_230430_2_, p_230430_3_);
		}
	}
	
	@Nullable
	public static List<IReorderingProcessor> tooltipAt(OptionsList p_243293_0_, int p_243293_1_, int p_243293_2_) {
		Optional<Widget> optional = p_243293_0_.getMouseOver((double)p_243293_1_, (double)p_243293_2_);
		if (optional.isPresent() && optional.get() instanceof IBidiTooltip) {
			Optional<List<IReorderingProcessor>> optional1 = ((IBidiTooltip)optional.get()).getTooltip();
			return optional1.orElse((List<IReorderingProcessor>)null);
		} else {
			return null;
		}
	}
}
