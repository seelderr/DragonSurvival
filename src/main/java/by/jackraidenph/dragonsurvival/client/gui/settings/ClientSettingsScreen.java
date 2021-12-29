package by.jackraidenph.dragonsurvival.client.gui.settings;

import by.jackraidenph.dragonsurvival.client.gui.widgets.lists.OptionsList;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.config.SyncBooleanConfig;
import by.jackraidenph.dragonsurvival.network.config.SyncEnumConfig;
import by.jackraidenph.dragonsurvival.network.config.SyncNumberConfig;
import com.electronwill.nightconfig.core.AbstractConfig;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.electronwill.nightconfig.core.UnmodifiableConfig.Entry;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
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
import java.util.concurrent.CopyOnWriteArrayList;

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
				
				CopyOnWriteArrayList<Pair<String, Set<Map.Entry<String, Object>>>> list = new CopyOnWriteArrayList<>();
				list.add(Pair.of("", config.valueMap().entrySet()));
				
				while(list.size() > 0){
					Pair<String, Set<Map.Entry<String, Object>>> pair = list.get(0);
					
					for(Map.Entry<String, Object> ent : pair.getSecond()){
						if (ent.getValue() instanceof AbstractConfig) {
							AbstractConfig config1 = (AbstractConfig)ent.getValue();
							list.add(Pair.of((!pair.getFirst().isEmpty() ? pair.getFirst() + "." : "") + ent.getKey(), config1.valueMap().entrySet()));
						}else{
							addValue((!pair.getFirst().isEmpty() ? pair.getFirst() + "." : "") + ent.getKey(), pair.getFirst(), ent.getKey(), ent.getValue());
						}
					}
					
					list.remove(0);
				}

			}else{
				addValue("", null, entry.getKey(), value);
			}
		}
		
		this.list.addSmall(OPTIONS.toArray(new AbstractOption[0]));
		
		for(Map.Entry<String, ArrayList<AbstractOption>> ent : optionMap.entrySet()){
			if(!ent.getKey().isEmpty()) {
				this.list.addCategory(ent.getKey());
			}
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
		
		int origLength = path.length();
		path = path.substring(0, Math.min(path.length(), 20));
		
		if(path.length() < origLength){
			path += "...";
		}
		
		String finalPath = path;
		
		if(value instanceof ValueSpec) {
			ValueSpec spec = (ValueSpec)value;
			Object ob = getSpec().getValues().get(getConfigName() + "." + key);
			
			if(spec.needsWorldRestart()){
				return;
			}
			
			if (ob instanceof BooleanValue) {
				BooleanValue booleanValue = (BooleanValue)ob;
				
				AbstractOption option = new BooleanOption(path, new StringTextComponent(spec.getComment()), (settings) -> booleanValue.get(), (settings, settingValue) -> {
					booleanValue.set(settingValue);
					
					if(getConfigName() != "client") {
						NetworkHandler.CHANNEL.sendToServer(new SyncBooleanConfig(key, settingValue, getConfigName() == "server" ? 0 : 1));
					}
				});
				
				addOption(category, path, option);
			} else if (ob instanceof IntValue) {
				IntValue value1 = (IntValue)ob;
				Integer min = (Integer)spec.correct(Integer.MIN_VALUE);
				Integer max = (Integer)spec.correct(Integer.MAX_VALUE);
				
				AbstractOption option = new SliderPercentageOption(path, min, max, 0.01F, (settings) -> Double.valueOf(value1.get()), (settings, settingValue) -> {
					value1.set(settingValue.intValue());
					
					if(getConfigName() != "client") {
						NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(key, settingValue, getConfigName() == "server" ? 0 : 1));
					}
				}, (settings, slider) -> {
					return new TranslationTextComponent("options.generic_value", new StringTextComponent(finalPath), slider.get(settings));
				});
				
				addOption(category, path, option);
			} else if (ob instanceof DoubleValue) {
				DoubleValue value1 = (DoubleValue)ob;
				double min = (double)spec.correct(Double.MIN_VALUE);
				double max = (double)spec.correct(Double.MAX_VALUE);
				
				AbstractOption option = new SliderPercentageOption(path, min, max, 0.01F, (settings) -> value1.get(), (settings, settingValue) -> {
					value1.set(settingValue);
					
					if(getConfigName() != "client") {
						NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(key, settingValue, getConfigName() == "server" ? 0 : 1));
					}
				}, (settings, slider) -> {
					return new TranslationTextComponent("options.generic_value", new StringTextComponent(finalPath), slider.get(settings));
				});
				
				addOption(category, path, option);
			} else if (ob instanceof LongValue) {
				LongValue value1 = (LongValue)ob;
				Long min = (Long)spec.correct(Long.MIN_VALUE);
				Long max = (Long)spec.correct(Long.MAX_VALUE);
				
				AbstractOption option = new SliderPercentageOption(path, min, max, 0.01F, (settings) -> Double.valueOf(value1.get()), (settings, settingValue) -> {
					value1.set(settingValue.longValue());
					
					if(getConfigName() != "client") {
						NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(key, settingValue, getConfigName() == "server" ? 0 : 1));
					}
				}, (settings, slider) -> {
					return new TranslationTextComponent("options.generic_value", new StringTextComponent(finalPath), slider.get(settings));
				});
				
				addOption(category, path, option);
			} else if (ob instanceof EnumValue) {
				EnumValue value1 = (EnumValue)ob;
				AbstractOption option = new IteratableOption(path, (settings, val) -> {
					Class<? extends Enum> cs = (Class<? extends Enum>)value1.get().getClass();
					int max = cs.getEnumConstants().length;
					int curVal = ((Enum)value1.get()).ordinal();
					
					if(curVal == max - 1){
						curVal = 0;
					}else{
						curVal += 1;
					}
					Enum en = EnumGetMethod.ORDINAL_OR_NAME.get(curVal, cs);
					value1.set(en);
					
					if(getConfigName() != "client") {
						NetworkHandler.CHANNEL.sendToServer(new SyncEnumConfig(key, en, getConfigName() == "server" ? 0 : 1));
					}
				}, (settings, set) -> {
					return new TranslationTextComponent("options.generic_value", new StringTextComponent(finalPath), ((Enum)value1.get()).name());
				});
				
				addOption(category, path, option);
			}
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
