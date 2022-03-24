package by.dragonsurvivalteam.dragonsurvival.client.gui.settings;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.fields.TextField;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.settings.DSBooleanOption;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.settings.DSDropDownOption;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.settings.DSIteratableOption;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.settings.DSNumberFieldOption;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.CategoryEntry;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionEntry;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionListEntry;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionsList;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncBooleanConfig;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncEnumConfig;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncNumberConfig;
import com.electronwill.nightconfig.core.AbstractConfig;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.electronwill.nightconfig.core.UnmodifiableConfig.Entry;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IBidiTooltip;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientSettingsScreen extends SettingsScreen{
	private static final Float sliderPerc = 0.1F;
	private final ArrayList<AbstractOption> OPTIONS = new ArrayList<>();
	private final TreeMap<String, ArrayList<AbstractOption>> optionMap = new TreeMap<>();
	private final ArrayList<String> options = new ArrayList<>();
	public OptionsList list;
	private double scroll;
	private AbstractConfig config;

	public ClientSettingsScreen(Screen p_i225930_1_, GameSettings p_i225930_2_, ITextComponent p_i225930_3_){
		super(p_i225930_1_, p_i225930_2_, p_i225930_3_);
		OptionsList.activeCats.clear();
	}

	public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_){
		this.renderBackground(p_230430_1_);
		this.list.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 5, 16777215);
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);

		List<IReorderingProcessor> list = tooltipAt(this.list, p_230430_2_, p_230430_3_);
		if(list != null){
			this.renderTooltip(p_230430_1_, list, p_230430_2_, p_230430_3_);
		}
	}

	protected void init(){
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

		this.list.add(OPTIONS.toArray(new AbstractOption[0]), null);

		int catNum = 0;
		for(Map.Entry<String, ArrayList<AbstractOption>> ent : optionMap.entrySet()){
			CategoryEntry entry = null;
			if(!ent.getKey().isEmpty()){
				String key = ent.getKey();
				String lastKey = key;
				for(String s : key.split("\\.")){
					if(this.list.findCategory(s, lastKey) == null || (Objects.requireNonNull(this.list.findCategory(s, lastKey)).parent != null && !Objects.requireNonNull(this.list.findCategory(s, lastKey)).parent.origName.equals(lastKey))){
						entry = this.list.addCategory(s, entry, catNum);
						catNum++;
					}else{
						entry = this.list.findCategory(s, lastKey);
					}
					lastKey = s;
				}
			}
			this.list.add(ent.getValue().toArray(new AbstractOption[0]), entry);
		}

		this.children.add(this.list);
		this.addButton(new Button(32, this.height - 27, 120 + 32, 20, DialogTexts.GUI_DONE, (p_213106_1_) -> {
			getSpec().save();
			this.minecraft.setScreen(this.lastScreen);
		}));

		this.addButton(new TextField(this.list.getScrollbarPosition() - 150 - 32, this.height - 27, 150 + 32, 20, new StringTextComponent("Search")){

			final ArrayList<CategoryEntry> cats = new ArrayList<>();

			@Override
			public boolean charTyped(char pCodePoint, int pModifiers){
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

	private void addConfigs(){
		for(Entry entry : config.entrySet()){
			Object value = config.get(entry.getKey());

			if(value instanceof AbstractConfig){
				AbstractConfig config = (AbstractConfig)value;

				CopyOnWriteArrayList<Pair<String, Set<Map.Entry<String, Object>>>> list = new CopyOnWriteArrayList<>();
				list.add(Pair.of("", config.valueMap().entrySet()));

				while(list.size() > 0){
					Pair<String, Set<Map.Entry<String, Object>>> pair = list.get(0);

					for(Map.Entry<String, Object> ent : pair.getSecond()){
						if(ent.getValue() instanceof AbstractConfig){
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

	public void addValue(String key, String category, String pName, Object value){
		if(options.contains(pName)){
			return;
		}

		String fullpath = getConfigName() + "." + key;
		ValueSpec spec = getSpec().getSpec().get(fullpath);

		String translatedName = new TranslationTextComponent(spec.getTranslationKey() != null ? spec.getTranslationKey() : "").getString();
		String translateTooltip = new TranslationTextComponent((spec.getTranslationKey() != null ? spec.getTranslationKey() : "") + ".tooltip").getString();

		String name = translatedName != null && spec.getTranslationKey() != null && !Objects.equals(spec.getTranslationKey(), translatedName) ? translatedName : pName;

		IFormattableTextComponent tooltip = new StringTextComponent(translateTooltip != null && spec.getTranslationKey() != null && !Objects.equals(spec.getTranslationKey() + ".tooltip", translateTooltip) ? translateTooltip : (spec.getComment() != null ? spec.getComment() : ""));

		if(spec.needsWorldRestart()){
			tooltip.append("\n§4This setting requires a server restart!§r");
		}

		List<IReorderingProcessor> tooltip1 = Minecraft.getInstance().font.split(tooltip, 200);
		if(value instanceof BooleanValue){
			BooleanValue booleanValue = (BooleanValue)value;
			DSBooleanOption option = new DSBooleanOption(name, tooltip, (settings) -> booleanValue.get(), (settings, settingValue) -> {
				try{
					booleanValue.set(settingValue);
					booleanValue.save();
				}catch(Exception ignored){
				}

				if(!Objects.equals(getConfigName(), "client")){
					NetworkHandler.CHANNEL.sendToServer(new SyncBooleanConfig(key, settingValue, getConfigName()));
				}
			});

			OptionsList.configMap.put(option, key);
			OptionsList.config.put(option, Pair.of(spec, booleanValue));
			addOption(category, name, option);
		}else if(value instanceof IntValue){
			IntValue value1 = (IntValue)value;
			Integer min = (Integer)spec.correct(Integer.MIN_VALUE);
			Integer max = (Integer)spec.correct(Integer.MAX_VALUE);
			int dif = max - min;
			AbstractOption option = null;
			if(dif <= 10){
				option = new SliderPercentageOption(name, min, max, sliderPerc, (settings) -> Double.valueOf(value1.get()), (settings, settingValue) -> {
					try{
						value1.set(settingValue.intValue());
						value1.save();
					}catch(Exception ignored){
					}

					if(!Objects.equals(getConfigName(), "client")){
						NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(key, settingValue, getConfigName()));
					}
				}, (settings, slider) -> {
					return new StringTextComponent(slider.get(settings) + "");
				});
			}else{
				option = new DSNumberFieldOption(name, min, max, (settings) -> value1.get(), (settings, settingValue) -> {
					try{
						value1.set(settingValue.intValue());
						value1.save();
					}catch(Exception ignored){
					}

					if(!Objects.equals(getConfigName(), "client")){
						NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(key, settingValue.intValue(), getConfigName()));
					}
				});
			}

			option.setTooltip(tooltip1);

			OptionsList.configMap.put(option, key);
			OptionsList.config.put(option, Pair.of(spec, value1));
			addOption(category, name, option);
		}else if(value instanceof DoubleValue){
			DoubleValue value1 = (DoubleValue)value;
			double min = (double)spec.correct(-Double.MAX_VALUE);
			double max = (double)spec.correct(Double.MAX_VALUE);
			double dif = max - min;
			AbstractOption option = null;
			if(dif <= 10){
				option = new SliderPercentageOption(name, min, max, sliderPerc, (settings) -> value1.get(), (settings, settingValue) -> {
					try{
						value1.set(Math.round(settingValue * 100.0) / 100.0);
						value1.save();
					}catch(Exception ignored){
					}

					if(!Objects.equals(getConfigName(), "client")){
						NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(key, settingValue, getConfigName()));
					}
				}, (settings, slider) -> {
					return new StringTextComponent(Math.round(slider.get(settings) * 100.0) / 100.0 + "");
				});
			}else{
				option = new DSNumberFieldOption(name, min, max, (settings) -> value1.get(), (settings, settingValue) -> {
					try{
						value1.set(Math.round(settingValue.doubleValue() * 100.0) / 100.0);
						value1.save();
					}catch(Exception ignored){
					}

					if(!Objects.equals(getConfigName(), "client")){
						NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(key, settingValue.doubleValue(), getConfigName()));
					}
				});
			}

			option.setTooltip(tooltip1);

			OptionsList.configMap.put(option, key);
			OptionsList.config.put(option, Pair.of(spec, value1));
			addOption(category, name, option);
		}else if(value instanceof LongValue){
			LongValue value1 = (LongValue)value;
			Long min = (Long)spec.correct(Long.MIN_VALUE);
			Long max = (Long)spec.correct(Long.MAX_VALUE);
			long dif = max - min;
			AbstractOption option = null;
			if(dif <= 10){
				option = new SliderPercentageOption(name, min, max, sliderPerc, (settings) -> Double.valueOf(value1.get()), (settings, settingValue) -> {
					try{
						value1.set(settingValue.longValue());
						value1.save();
					}catch(Exception ignored){
					}

					if(!Objects.equals(getConfigName(), "client")){
						NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(key, settingValue, getConfigName()));
					}
				}, (settings, slider) -> {
					return new StringTextComponent(slider.get(settings) + "");
				});
			}else{
				option = new DSNumberFieldOption(name, min, max, (settings) -> value1.get(), (settings, settingValue) -> {
					try{
						value1.set(settingValue.longValue());
						value1.save();
					}catch(Exception ignored){
					}

					if(!Objects.equals(getConfigName(), "client")){
						NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(key, settingValue.longValue(), getConfigName()));
					}
				});
			}

			option.setTooltip(tooltip1);

			OptionsList.configMap.put(option, key);
			OptionsList.config.put(option, Pair.of(spec, value1));
			addOption(category, name, option);
		}else if(value instanceof EnumValue){
			EnumValue value1 = (EnumValue)value;
			Class<? extends Enum> cs = (Class<? extends Enum>)value1.get().getClass();
			Enum vale = EnumGetMethod.ORDINAL_OR_NAME.get(((Enum)value1.get()).ordinal(), cs);

			AbstractOption option = new DSDropDownOption(name, vale, (val) -> {
				try{
					value1.set(val);
					value1.save();
				}catch(Exception ignored){
				}

				if(!Objects.equals(getConfigName(), "client")){
					NetworkHandler.CHANNEL.sendToServer(new SyncEnumConfig(key, val, getConfigName()));
				}
			});

			option.setTooltip(tooltip1);

			OptionsList.configMap.put(option, key);
			OptionsList.config.put(option, Pair.of(spec, value1));
			addOption(category, name, option);
		}else if(value instanceof ConfigValue){
			ConfigValue value1 = (ConfigValue)value;
			if(value1.get() instanceof List && (((List)value1.get()).isEmpty() || ((List)value1.get()).get(0) instanceof String)){
				String finalPath1 = name;
				StringJoiner joiner = new StringJoiner(",");

				if(((List<?>)value1.get()).size() > 0){
					for(Object ob1 : (List)value1.get()){
						joiner.add("[" + ob1.toString() + "]");
					}
				}else{
					joiner.add("[]");
				}

				DSIteratableOption option = new DSIteratableOption(name, (settings, val) -> {
					this.minecraft.setScreen(new ListConfigSettingsScreen(this, minecraft.options, new StringTextComponent(finalPath1), spec, value1, getSpec(), getConfigName() + "." + key));
				}, (settings, set) -> {
					return new StringTextComponent(Minecraft.getInstance().font.substrByWidth(new StringTextComponent(joiner.toString()), 120).getString());
				});

				option.setTooltip(tooltip1);

				OptionsList.configMap.put(option, key);
				OptionsList.config.put(option, Pair.of(spec, value1));
				addOption(category, name, option);
			}
		}
	}

	public String getConfigName(){
		return "client";
	}

	private void addOption(String category, String path, AbstractOption option){
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

	public ForgeConfigSpec getSpec(){
		return ConfigHandler.clientSpec;
	}

	@Nullable
	public static List<IReorderingProcessor> tooltipAt(OptionsList p_243293_0_, int p_243293_1_, int p_243293_2_){
		Optional<Widget> optional = p_243293_0_.getMouseOver(p_243293_1_, p_243293_2_);
		OptionListEntry optional2 = p_243293_0_.getEntryAtPos(p_243293_1_, p_243293_2_);

		if(!optional.isPresent() || !(optional.get() instanceof IBidiTooltip)){
			if(optional2 instanceof OptionEntry){
				optional = Optional.of(((OptionEntry)optional2).widget);
			}
		}

		if(optional.isPresent() && optional.get() instanceof IBidiTooltip && optional.get().visible && !optional.get().isHovered()){
			Optional<List<IReorderingProcessor>> optional1 = ((IBidiTooltip)optional.get()).getTooltip();
			return optional1.orElse(null);
		}else{
			return null;
		}
	}
}