package by.jackraidenph.dragonsurvival.client.gui.settings;

import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.fields.TextField;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.settings.DSDropDownOption;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.settings.DSNumberFieldOption;
import by.jackraidenph.dragonsurvival.client.gui.widgets.lists.CategoryEntry;
import by.jackraidenph.dragonsurvival.client.gui.widgets.lists.OptionEntry;
import by.jackraidenph.dragonsurvival.client.gui.widgets.lists.OptionListEntry;
import by.jackraidenph.dragonsurvival.client.gui.widgets.lists.OptionsList;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.config.SyncBooleanConfig;
import by.jackraidenph.dragonsurvival.network.config.SyncEnumConfig;
import by.jackraidenph.dragonsurvival.network.config.SyncNumberConfig;
import com.electronwill.nightconfig.core.AbstractConfig;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.electronwill.nightconfig.core.UnmodifiableConfig.Entry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.*;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientSettingsScreen extends OptionsSubScreen
{
	private final ArrayList<Option> OPTIONS = new ArrayList<>();
	private final TreeMap<String, ArrayList<Option>> optionMap = new TreeMap<>();
	private final ArrayList<String> options = new ArrayList<>();
	public OptionsList list;
	private double scroll;
	
	private AbstractConfig config;
	
	protected void init() {
		if(list != null){
			scroll = list.getScrollAmount();
		}
		
		OPTIONS.clear();
		optionMap.clear();
		options.clear();
		
		OptionsList.config.clear();
		OptionsList.configMap.clear();
		
		config = (AbstractConfig)getSpec().getValues();
		
		this.list = new OptionsList(this.width, this.height, 32, this.height - 32);
		
		addConfigs();
		
		this.list.add(OPTIONS.toArray(new Option[0]), null);
		
		int catNum = 0;
		for(Map.Entry<String, ArrayList<Option>> ent : optionMap.entrySet()){
			CategoryEntry entry = null;
			if(!ent.getKey().isEmpty()) {
				String key = ent.getKey();
				String lastKey = key;
				for (String s : key.split("\\.")) {
					if(this.list.findCategory(s, lastKey) == null
					   || (Objects.requireNonNull(this.list.findCategory(s, lastKey)).parent != null && !Objects.requireNonNull(this.list.findCategory(s, lastKey)).parent.origName.equals(lastKey))) {
						entry = this.list.addCategory(s, entry, catNum);
						catNum++;

					}else{
						entry = this.list.findCategory(s, lastKey);
					}
					lastKey = s;
				}
			}
			this.list.add(ent.getValue().toArray(new Option[0]), entry);
		}
		
		this.children.add(this.list);
		this.addRenderableWidget(new Button(32, this.height - 27, 120 + 32, 20, CommonComponents.GUI_DONE, (p_213106_1_) -> {
			getSpec().save();
			this.minecraft.setScreen(this.lastScreen);
		}));
		
		this.addRenderableWidget(new TextField(this.list.getScrollbarPosition() - 150 - 32, this.height - 27, 150 + 32, 20, new TextComponent("Search")){
		
			ArrayList<CategoryEntry> cats = new ArrayList<>();
			
			@Override
			public boolean charTyped(char pCodePoint, int pModifiers)
			{
				cats.forEach((c) -> c.enabled = false);
				cats.clear();
				
				if(!getValue().isEmpty()){
					OptionEntry entry = list.findClosest(getValue());
					
					if(entry != null){
						CategoryEntry cat = entry.category;
						
						while(cat != null){
							cat.enabled = true;
							cats.add(cat);
							cat = cat.parent;
						}
						list.centerScrollOn(entry);
					}
				}
				return super.charTyped(pCodePoint, pModifiers);
			}
		});
		
		this.list.setScrollAmount(scroll);
	}
	
	private void addConfigs()
	{
		for (Entry entry : config.entrySet()) {
			Object value = config.get(entry.getKey());
			
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
	}
	
	public ForgeConfigSpec getSpec(){
		return ConfigHandler.clientSpec;
	}
	
	public String getConfigName(){
		return "client";
	}
	
	private static final Float sliderPerc = 0.1F;
	
	public void addValue(String key, String category, String pName, Object value){
		if(options.contains(pName)) return;
		
		String fullpath = getConfigName() + "." + key;
		ValueSpec spec = getSpec().getSpec().get(fullpath);
		
		String translatedName = new TranslatableComponent(spec.getTranslationKey() != null ? spec.getTranslationKey() : "").getString();
		String translateTooltip = new TranslatableComponent((spec.getTranslationKey() != null ? spec.getTranslationKey() : "") + ".tooltip").getString();
		
		String name = translatedName != null && spec.getTranslationKey() != null && !Objects.equals(spec.getTranslationKey(), translatedName)
		? translatedName : pName;
		
		String tooltip0 = translateTooltip != null && spec.getTranslationKey() != null
		                 && !Objects.equals(spec.getTranslationKey() + ".tooltip", translateTooltip)
				? translateTooltip : (spec.getComment() != null ? spec.getComment() : "");
		
		if(spec.needsWorldRestart()){
			tooltip0+= ("\n§4This setting requires a server restart!§r");
		}
		TextComponent tooltip = new TextComponent(tooltip0);
		
		if (value instanceof BooleanValue) {
			BooleanValue booleanValue = (BooleanValue)value;
			CycleOption option = CycleOption.createOnOff(name, tooltip, (settings) -> booleanValue.get(), (settings, optionO, settingValue) -> {
				try {
					booleanValue.set(settingValue);
					booleanValue.save();
					
				}catch (Exception ignored){}
				
				if(!Objects.equals(getConfigName(), "client")) {
					NetworkHandler.CHANNEL.sendToServer(new SyncBooleanConfig(key, settingValue, getConfigName()));
				}
			}).setTooltip((p_167791_) -> {
				List<FormattedCharSequence> list = p_167791_.font.split(tooltip, 200);
				return (p_167772_) -> {
					return list;
				};
			});
			
			OptionsList.configMap.put(option, key);
			OptionsList.config.put(option, Pair.of(spec, booleanValue));
			addOption(category, name, option);
		} else if (value instanceof IntValue) {
			IntValue value1 = (IntValue)value;
			Integer min = (Integer)spec.correct(Integer.MIN_VALUE);
			Integer max = (Integer)spec.correct(Integer.MAX_VALUE);
			int dif = max - min;
			Option option = null;
			if(dif <= 10) {
				option = new ProgressOption(name, min, max, sliderPerc, (settings) -> Double.valueOf(value1.get()), (settings, settingValue) -> {
					try {
						value1.set(settingValue.intValue());
						value1.save();
					} catch (Exception ignored) {}
					
					if (!Objects.equals(getConfigName(), "client")) {
						NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(key, settingValue, getConfigName()));
					}
				}, (settings, slider) -> {
					return new TextComponent(slider.get(settings) + "");
				}, (m) -> Minecraft.getInstance().font.split(tooltip, 200));
			}else{
				option = new DSNumberFieldOption(name, min, max, (settings) -> value1.get(), (settings, settingValue) -> {
					try {
						value1.set(settingValue.intValue());
						value1.save();
					} catch (Exception ignored) {}
					
					if (!Objects.equals(getConfigName(), "client")) {
						NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(key, settingValue.intValue(), getConfigName()));
					}
				}, (m) -> Minecraft.getInstance().font.split(tooltip, 200));
			}
			
		
			OptionsList.configMap.put(option, key);
			OptionsList.config.put(option, Pair.of(spec, value1));
			addOption(category, name, option);
		} else if (value instanceof DoubleValue) {
			DoubleValue value1 = (DoubleValue)value;
			BigDecimal min = new BigDecimal((double)spec.correct(Double.MIN_VALUE)).setScale(5, RoundingMode.FLOOR);
			BigDecimal max = new BigDecimal((double)spec.correct(Double.MAX_VALUE)).setScale(5, RoundingMode.FLOOR);
			double dif = max.subtract(min).doubleValue();
			Option option = null;
			if(dif <= 10) {
				 option = new ProgressOption(name, min.doubleValue(), max.doubleValue(), sliderPerc, (settings) -> value1.get(), (settings, settingValue) -> {
					try {
						value1.set(Math.round(settingValue * 100.0) / 100.0);
						value1.save();
					} catch (Exception ignored) {}
					
					if (!Objects.equals(getConfigName(), "client")) {
						NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(key, settingValue, getConfigName()));
					}
				}, (settings, slider) -> {
					return new TextComponent(Math.round(slider.get(settings) * 100.0) / 100.0 + "");
				}, (m) -> Minecraft.getInstance().font.split(tooltip, 200));
			}else{
				option = new DSNumberFieldOption(name, min, max, (settings) -> value1.get(), (settings, settingValue) -> {
					try {
						value1.set(Math.round(settingValue.doubleValue() * 100.0) / 100.0);
						value1.save();
					} catch (Exception ignored) {}
					
					if (!Objects.equals(getConfigName(), "client")) {
						NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(key, settingValue.doubleValue(), getConfigName()));
					}
				}, (m) -> Minecraft.getInstance().font.split(tooltip, 200));
			}
			System.out.println(option);
			OptionsList.configMap.put(option, key);
			OptionsList.config.put(option, Pair.of(spec, value1));
			addOption(category, name, option);
		} else if (value instanceof LongValue) {
			LongValue value1 = (LongValue)value;
			Long min = (Long)spec.correct(Long.MIN_VALUE);
			Long max = (Long)spec.correct(Long.MAX_VALUE);
			long dif = max - min;
			Option option = null;
			if(dif <= 10) {
				option = new ProgressOption(name, min, max, sliderPerc, (settings) -> Double.valueOf(value1.get()), (settings, settingValue) -> {
					try {
						value1.set(settingValue.longValue());
						value1.save();
					} catch (Exception ignored) {}
					
					if (!Objects.equals(getConfigName(), "client")) {
						NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(key, settingValue, getConfigName()));
					}
				}, (settings, slider) -> {
					return new TextComponent(slider.get(settings) + "");
				}, (m) -> Minecraft.getInstance().font.split(tooltip, 200));
			}else{
				option = new DSNumberFieldOption(name, min, max, (settings) -> value1.get(), (settings, settingValue) -> {
					try {
						value1.set(settingValue.longValue());
						value1.save();
					} catch (Exception ignored) {}
					
					if (!Objects.equals(getConfigName(), "client")) {
						NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(key, settingValue.longValue(), getConfigName()));
					}
				}, (m) -> Minecraft.getInstance().font.split(tooltip, 200));
			}
			
			OptionsList.configMap.put(option, key);
			OptionsList.config.put(option, Pair.of(spec, value1));
			addOption(category, name, option);
		} else if (value instanceof EnumValue) {
			EnumValue value1 = (EnumValue)value;
			Class<? extends Enum> cs = (Class<? extends Enum>)value1.get().getClass();
			Enum vale = EnumGetMethod.ORDINAL_OR_NAME.get(((Enum)value1.get()).ordinal(), cs);
			
			Option option = new DSDropDownOption(name, vale, (val) -> {
				try {
					value1.set(val);
					value1.save();
				}catch (Exception ignored){}
				
				if(!Objects.equals(getConfigName(), "client")) {
					NetworkHandler.CHANNEL.sendToServer(new SyncEnumConfig(key, val, getConfigName()));
				}
			}, (m) -> Minecraft.getInstance().font.split(tooltip, 200));
			
			OptionsList.configMap.put(option, key);
			OptionsList.config.put(option, Pair.of(spec, value1));
			addOption(category, name, option);
		}else if (value instanceof ConfigValue) {
			ConfigValue value1 = (ConfigValue)value;
			if(value1.get() instanceof List && (((List)value1.get()).isEmpty() || ((List)value1.get()).get(0) instanceof String)) {
				String finalPath1 = name;
				StringJoiner joiner = new StringJoiner(",");
				
				if(((List<?>)value1.get()).size() > 0) {
					for (Object ob1 : (List)value1.get()) {
						joiner.add("[" + ob1.toString() + "]");
					}
				}else{
					joiner.add("[]");
				}
				CycleOption option = CycleOption.createBinaryOption(name, new TextComponent(Minecraft.getInstance().font.substrByWidth(new TextComponent(joiner.toString()), 120).getString()), TextComponent.EMPTY, (val) -> true, (val1, val2, val3) -> {
					this.minecraft.setScreen(new ListConfigSettingsScreen(this, minecraft.options, new TextComponent(finalPath1), spec, value1, getSpec(), getConfigName() + "." + key));
				}).setTooltip((p_167791_) -> {
					List<FormattedCharSequence> list = p_167791_.font.split(tooltip, 200);
					return (p_167772_) -> {
						return list;
					};
				});
				
				
				OptionsList.configMap.put(option, key);
				OptionsList.config.put(option, Pair.of(spec, value1));
				addOption(category, name, option);
			}
		}
	}
	
	private void addOption(String category, String path, Option option)
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
	
	public ClientSettingsScreen(Screen p_i225930_1_, Options p_i225930_2_, BaseComponent p_i225930_3_)
	{
		super(p_i225930_1_, p_i225930_2_, p_i225930_3_);
		OptionsList.activeCats.clear();
	}
	
	
	public void render(PoseStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
		this.renderBackground(p_230430_1_);
		this.list.render(p_230430_1_,p_230430_2_, p_230430_3_, p_230430_4_);
		drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 5, 16777215);
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		
		List<FormattedCharSequence> list = tooltipAt(this.list, p_230430_2_, p_230430_3_);
		if (list != null) {
			this.renderTooltip(p_230430_1_, list, p_230430_2_, p_230430_3_);
		}
	}
	
	@Nullable
	public static List<FormattedCharSequence> tooltipAt(OptionsList p_243293_0_, int p_243293_1_, int p_243293_2_) {
		Optional<AbstractWidget> optional = p_243293_0_.getMouseOver(p_243293_1_, p_243293_2_);
		OptionListEntry optional2 = p_243293_0_.getEntryAtPos(p_243293_1_, p_243293_2_);
		
		if(!optional.isPresent() || !(optional.get() instanceof TooltipAccessor)){
			if(optional2 instanceof OptionEntry){
				optional = Optional.of(((OptionEntry)optional2).widget);
			}
		}
		
		if (optional.isPresent() && optional.get() instanceof TooltipAccessor && optional.get().visible && !optional.get().isHoveredOrFocused()) {
			Optional<List<FormattedCharSequence>> optional1 = Optional.of(((TooltipAccessor)optional.get()).getTooltip());
			return optional1.orElse(null);
		}else {
			return null;
		}
	}
}
