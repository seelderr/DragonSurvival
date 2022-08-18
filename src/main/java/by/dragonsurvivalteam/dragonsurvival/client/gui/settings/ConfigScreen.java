package by.dragonsurvivalteam.dragonsurvival.client.gui.settings;

import by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets.DSDropDownOption;
import by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets.DSNumberFieldOption;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.fields.TextField;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.CategoryEntry;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionEntry;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionListEntry;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionsList;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncBooleanConfig;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncEnumConfig;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncNumberConfig;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Primitives;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.*;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.CycleButton.Builder;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.*;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class ConfigScreen extends OptionsSubScreen{
	private static final Float sliderPerc = 0.1F;
	private final ArrayList<Option> OPTIONS = new ArrayList<>();
	private final TreeMap<String, ArrayList<Option>> optionMap = new TreeMap<>();
	private final ArrayList<String> options = new ArrayList<>();
	public OptionsList list;
	private double scroll;

	public ConfigScreen(Screen p_i225930_1_, Options p_i225930_2_){
		super(p_i225930_1_, p_i225930_2_, TextComponent.EMPTY);
		OptionsList.activeCats.clear();
	}

	public ConfigScreen(Screen p_i225930_1_, Options p_i225930_2_, Component p_i225930_3_){
		super(p_i225930_1_, p_i225930_2_, p_i225930_3_);
		OptionsList.activeCats.clear();
		this.title = p_i225930_3_;
	}

	public abstract ConfigSide screenSide();

	protected void init(){
		if(list != null){
			scroll = list.getScrollAmount();
		}

		OPTIONS.clear();
		optionMap.clear();
		options.clear();

		OptionsList.configMap.clear();

		this.list = new OptionsList(this.width, this.height, 32, this.height - 32);

		addConfigs();

		this.list.add(OPTIONS.toArray(new Option[0]), null);

		int catNum = 0;
		for(Map.Entry<String, ArrayList<Option>> ent : optionMap.entrySet()){
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
			this.list.add(ent.getValue().toArray(new Option[0]), entry);
		}

		this.children.add(this.list);

		this.addRenderableWidget(new Button(32, this.height - 27, 120 + 32, 20, CommonComponents.GUI_DONE, (p_213106_1_) -> {
			this.minecraft.setScreen(this.lastScreen);
		}));

		this.addRenderableWidget(new TextField(this.list.getScrollbarPosition() - 150 - 32, this.height - 27, 150 + 32, 20, new TextComponent("Search")){
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
		List<ConfigOption> opts = new ArrayList<>();

		for(String s : ConfigHandler.configs.getOrDefault(screenSide(), Collections.emptyList())){
			if(ConfigHandler.configObjects.containsKey(s)){
				ConfigOption option = ConfigHandler.configObjects.get(s);
				opts.add(option);
			}
		}

		opts.sort((C1, c2) -> Arrays.compare(C1.category(), c2.category()));

		for(ConfigOption opt : opts){
			String key = opt.key();
			Field fe = ConfigHandler.configFields.get(opt.key());
			ConfigValue<?> value = ConfigHandler.configValues.get(opt.key());
			String category = String.join(".", opt.category());

			if(options.contains(key) || value == null){
				continue;
			}

			String fullpath = String.join(".", List.of(category, key));

			String translatedName = new TranslatableComponent("ds." + fullpath).getString();
			String translateTooltip = new TranslatableComponent("ds." + fullpath + ".tooltip").getString();

			String name = !translatedName.equalsIgnoreCase("ds." + fullpath) ? translatedName : key;
			String tooltip0 = !translateTooltip.equalsIgnoreCase("ds." + fullpath + ".tooltip") ? translateTooltip : opt.comment() != null ? String.join("\n", opt.comment()) : "";

			if(opt.restart()){
				tooltip0 += "\n" + I18n.get("ds.config.server_restart");
			}

			ConfigRange range = fe.isAnnotationPresent(ConfigRange.class) ? fe.getAnnotation(ConfigRange.class) : null;
			BigDecimal min = BigDecimal.valueOf(range != null ? range.min() : -1).setScale(5, RoundingMode.FLOOR);
			BigDecimal max = BigDecimal.valueOf(range != null ? range.max() : Integer.MAX_VALUE).setScale(5, RoundingMode.FLOOR);

			TextComponent tooltip = new TextComponent(tooltip0);
			Class<?> checkType = Primitives.unwrap(fe.getType());

			if(Number.class.isAssignableFrom(fe.getType())){
				Option option = null;

				BiFunction<Class<?>, Number, Object> numberFunction = (type, val) -> {
					BigDecimal outVal = BigDecimal.valueOf(val.doubleValue()).setScale(3, RoundingMode.FLOOR);
					return Primitives.wrap(fe.getType()).cast(outVal.doubleValue());
				};

				Function<Options, Number> getter = options -> ((ConfigValue<Number>)value).get();
				BiConsumer<Options, Number> setter = (settings, settingValue) -> {
					ConfigHandler.updateConfigValue(value, numberFunction.apply(checkType, settingValue));

					if(screenSide() == ConfigSide.SERVER){
						NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(key, settingValue.doubleValue()));
					}
				};

				if(max.subtract(min).intValue() <= 10){ //Use slider if the difference between min and max value is small enough, otherwise use text field
					option = new ProgressOption(name, min.doubleValue(), max.doubleValue(), sliderPerc, val->getter.apply(val).doubleValue(), setter::accept, (settings, slider) -> new TextComponent(numberFunction.apply(checkType, slider.get(settings)) + ""), mc -> mc.font.split(tooltip, 200));
				}else{
					option = new DSNumberFieldOption(name, min, max, getter, setter, m -> Minecraft.getInstance().font.split(tooltip, 200));
				}

				OptionsList.configMap.put(option, key);
				addOption(category, name, option);

			}else if(checkType.equals(boolean.class)){
				CycleOption<Boolean> option = new CycleOption<>(name, val -> ((ConfigValue<Boolean>)value).get(), (settings, optionO, settingValue) -> {
					ConfigHandler.updateConfigValue(value, settingValue);

					if(screenSide() == ConfigSide.SERVER){
						NetworkHandler.CHANNEL.sendToServer(new SyncBooleanConfig(key, settingValue));
					}
				}, () -> CycleButton.booleanBuilder(((BaseComponent)CommonComponents.OPTION_ON).withStyle(ChatFormatting.GREEN), ((BaseComponent)CommonComponents.OPTION_OFF).withStyle(ChatFormatting.RED)).displayOnlyValue())
					.setTooltip(mc-> s ->mc.font.split(tooltip, 200));
				OptionsList.configMap.put(option, key);
				addOption(category, name, option);
			}else if(checkType.isEnum()){
				Class<? extends Enum> cs = (Class<? extends Enum>)value.get().getClass();
				Enum vale = EnumGetMethod.ORDINAL_OR_NAME.get(((Enum)value.get()).ordinal(), cs);

				Option option = new DSDropDownOption(name, vale, val -> {
					ConfigHandler.updateConfigValue(value, val);

					if(screenSide() == ConfigSide.SERVER)
						NetworkHandler.CHANNEL.sendToServer(new SyncEnumConfig(key, val));
				}, m -> m.font.split(tooltip, 200));

				OptionsList.configMap.put(option, key);
				addOption(category, name, option);
			}else if(checkType.isAssignableFrom(List.class)){
				if(value.get() instanceof List && (((List<?>)value.get()).isEmpty() || ((List<?>)value.get()).get(0) instanceof String)){
					StringJoiner joiner = new StringJoiner(",");

					if(((List<?>)value.get()).size() > 0)
						for(Object ob1 : (List<?>)value.get()){
							joiner.add("[" + ob1.toString() + "]");
						}
					else
						joiner.add("[]");
					String text = Minecraft.getInstance().font.substrByWidth(new TextComponent(joiner.toString()), 120).getString();
					CycleOption<String> option = new CycleOption<String>(name,
	                     val -> text,
	                     (val1, val2, val3) -> this.minecraft.setScreen(new ConfigListMenu(this, minecraft.options, new TextComponent(name), key, value, screenSide(), key)),
	                     () -> {
							return new Builder<String>(t -> new TextComponent(text)).displayOnlyValue().withValues(text).withInitialValue(text);
					}).setTooltip(mc-> s ->mc.font.split(tooltip, 200));

					OptionsList.configMap.put(option, key);
					addOption(category, name, option);
				}else
					System.err.println("Invalid config \"" + key + "\"");
			}
		}
	}

	private void addOption(String category, String path, Option option){
		if(category != null){
			if(!optionMap.containsKey(category))
				optionMap.put(category, new ArrayList<>());
			optionMap.get(category).add(option);
		}else
			OPTIONS.add(option);

		options.add(path);
	}




	public void render(PoseStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_){
		this.renderBackground(p_230430_1_);
		this.list.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);

		List<FormattedCharSequence> list = tooltipAt(this.list, p_230430_2_, p_230430_3_);
		if(list != null)
			this.renderTooltip(p_230430_1_, list, p_230430_2_, p_230430_3_);
	}


	@Nullable
	public static List<FormattedCharSequence> tooltipAt(OptionsList p_243293_0_, int p_243293_1_, int p_243293_2_){
		Optional<AbstractWidget> optional = p_243293_0_.getMouseOver(p_243293_1_, p_243293_2_);
		OptionListEntry optional2 = p_243293_0_.getEntryAtPos(p_243293_1_, p_243293_2_);

		if(!optional.isPresent() || !(optional.get() instanceof TooltipAccessor))
			if(optional2 instanceof OptionEntry){
				optional = Optional.of(((OptionEntry)optional2).widget);
			}

		if(optional.isPresent() && optional.get() instanceof TooltipAccessor && optional.get().visible && !optional.get().isHoveredOrFocused())
			return ((TooltipAccessor)optional.get()).getTooltip();
		else
			return ImmutableList.of();
	}
}