package by.dragonsurvivalteam.dragonsurvival.client.gui.settings;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets.*;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.fields.TextField;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.CategoryEntry;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionEntry;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionListEntry;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionsList;
import by.dragonsurvivalteam.dragonsurvival.client.util.TooltipUtils;
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
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.CycleButton.Builder;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import org.jetbrains.annotations.NotNull;

/** Handles the config categories */
public abstract class ConfigScreen extends OptionsSubScreen{
	private static final Float sliderPercentage = 0.1F;
	private final ArrayList<Option> OPTIONS = new ArrayList<>();
	/** Contains all categories (with their full path) */
	private final TreeMap<String, List<Option>> categories = new TreeMap<>();
	/** The individual config options / entries */
	private final ArrayList<String> optionKeys = new ArrayList<>();
	public OptionsList list;
	private double scroll;

	public ConfigScreen(final Screen screen, final Options options, final Component component) {
		super(screen, options, component);
		OptionsList.activeCats.clear();
		title = component;
	}

	public abstract ConfigSide screenSide();

	@Override
	protected void init(){
		if (list != null) {
			scroll = list.getScrollAmount();
		}

		OPTIONS.clear();
		categories.clear();
		optionKeys.clear();
		OptionsList.configMap.clear();

		list = new OptionsList(width, height, 32, height - 32);
		addConfigs();
		list.add(OPTIONS.toArray(new Option[0]), null);

		int categoryNumber = 0;

		for (Map.Entry<String, List<Option>> entryList : categories.entrySet()) {
			CategoryEntry entry = null;

			if (!entryList.getKey().isEmpty()) {
				String path = entryList.getKey();
				String lastPath = path;

				for (String pathElement : path.split("\\.")) {
					if (list.findCategory(pathElement, lastPath) == null || Objects.requireNonNull(list.findCategory(pathElement, lastPath)).parent != null && !Objects.requireNonNull(list.findCategory(pathElement, lastPath)).parent.origName.equals(lastPath)) {
						entry = list.addCategory(pathElement, entry, categoryNumber);
						categoryNumber++;
					} else {
						entry = list.findCategory(pathElement, lastPath);
					}

					lastPath = pathElement;
				}
			}

			list.add(entryList.getValue().toArray(new Option[0]), entry);
		}

		children.add(list);

		// Button to go back
		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> minecraft.setScreen(lastScreen)).bounds(32, height - 27, 120 + 32, 20).build());

		// Search field
		addRenderableWidget(new TextField(list.getScrollbarPosition() - 150 - 32, height - 27, 150 + 32, 20, Component.literal("Search")) {
			final ArrayList<CategoryEntry> categories = new ArrayList<>();

			@Override
			public boolean charTyped(char codePoint, int modifiers) {
				categories.forEach(entry -> entry.enabled = false);
				categories.clear();

				if (!getValue().isEmpty()) {
					OptionEntry entry = list.findClosest(getValue());

					if (entry != null) {
						CategoryEntry category = entry.category;

						while (category != null) {
							category.enabled = true;
							categories.add(category);
							category = category.parent;
						}

						list.centerScrollOn(entry);
					}
				}

				return super.charTyped(codePoint, modifiers);
			}
		});

		list.setScrollAmount(scroll);
	}

	private void addConfigs(){
		List<ConfigOption> configOptions = new ArrayList<>();

		for (String s : ConfigHandler.configs.getOrDefault(screenSide(), Collections.emptyList())){
			if (ConfigHandler.configObjects.containsKey(s)) {
				ConfigOption option = ConfigHandler.configObjects.get(s);
				configOptions.add(option);
			}
		}

		configOptions.sort((configOptionOne, configOptionTwo) -> Arrays.compare(configOptionOne.category(), configOptionTwo.category()));

		for (ConfigOption configOption : configOptions) {
			String key = configOption.key();
			Field field = ConfigHandler.configFields.get(configOption.key());
			ConfigValue<?> configValue = ConfigHandler.configValues.get(configOption.key());
			String category = String.join(".", configOption.category());

			if (optionKeys.contains(key) || configValue == null) {
				continue;
			}

			String fullPath = String.join(".", List.of(category, key));

			String translatedName = Component.translatable("ds." + fullPath).getString();
			String translateTooltip = Component.translatable("ds." + fullPath + ".tooltip").getString();

			String name = !translatedName.equalsIgnoreCase("ds." + fullPath) ? translatedName : key;
			String tooltip0 = !translateTooltip.equalsIgnoreCase("ds." + fullPath + ".tooltip") ? translateTooltip : configOption.comment() != null ? String.join("\n", configOption.comment()) : "";

			if (configOption.restart()) {
				tooltip0 += "\n" + I18n.get("ds.config.server_restart"); // FIXME :: No translation exists?
			}

			ConfigRange range = field.isAnnotationPresent(ConfigRange.class) ? field.getAnnotation(ConfigRange.class) : null;
			boolean hasDecimals = field.getType() == Double.class || field.getType() == Float.class;

			Number min = range != null ? range.min() : Integer.MIN_VALUE;
			Number max = range != null ? range.max() : Integer.MAX_VALUE;

			Component tooltip = Component.literal(tooltip0);
			Class<?> checkType = Primitives.unwrap(field.getType());

			if (Number.class.isAssignableFrom(field.getType())) {
				// Handle numbers
				BiFunction<Class<?>, Number, Object> numberFunction = (type, val) -> {
					BigDecimal outVal = BigDecimal.valueOf(val.doubleValue()).setScale(3, RoundingMode.FLOOR);

					if (hasDecimals) {
						return outVal.doubleValue();
					} else {
						return outVal.intValue();
					}
				};

				Function<Options, Number> getter = options -> ((ConfigValue<Number>) configValue).get();

                BiConsumer<Options, Number> setter = (settings, settingValue) -> {
					ConfigHandler.updateConfigValue(configValue, numberFunction.apply(checkType, settingValue));

					if (screenSide() == ConfigSide.SERVER) {
						if (field.getType() == Double.class || field.getType() == Float.class) {
							NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(ConfigHandler.createConfigPath(configOption), settingValue.doubleValue()));
                        } else if (field.getType() == Integer.class || field.getType() == Long.class) {
							NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(ConfigHandler.createConfigPath(configOption), settingValue.longValue()));
						}
					}
				};

                Option option;

				// Use slider if the difference between min and max value is small enough, otherwise use text field
				if (false /*max.subtract(min).intValue() <= 10*/) { // FIXME :: Calling the setter every time a value changes causes problems - only set value when 'Done' is clicked? Also has problems with certain decimals (it moves in 0.1 range but some config are in 0.01 range)
					option = new ProgressOption(
						    name,
						    hasDecimals ? min.doubleValue() : min.intValue(),
						    hasDecimals ? max.doubleValue() : max.intValue(),
							sliderPercentage,
						    options -> hasDecimals ? getter.apply(options).doubleValue() : getter.apply(options).intValue(),
						    setter::accept,
						    (settings, slider) -> Component.literal(numberFunction.apply(checkType, slider.get(settings)) + ""),
						    TooltipUtils.createTooltip(tooltip, 200)
					);
				} else {
					option = new DSNumberFieldOption(name, min, max, getter, setter, TooltipUtils.createTooltip(tooltip, 200), hasDecimals);
				}

				OptionsList.configMap.put(option, key);
				addOption(category, name, option);
			} else if (checkType.equals(boolean.class)) {
                // Handle booleans
				CycleOption<Boolean> option = new CycleOption<>(name, val -> ((ConfigValue<Boolean>) configValue).get(), (settings, optionO, settingValue) -> {
					ConfigHandler.updateConfigValue(configValue, settingValue);

					if (screenSide() == ConfigSide.SERVER) {
						NetworkHandler.CHANNEL.sendToServer(new SyncBooleanConfig(ConfigHandler.createConfigPath(configOption), settingValue));
					}
				}, () -> CycleButton.booleanBuilder(((MutableComponent) CommonComponents.OPTION_ON)
                                .withStyle(ChatFormatting.GREEN), ((MutableComponent) CommonComponents.OPTION_OFF)
                                .withStyle(ChatFormatting.RED))
                        .displayOnlyValue())
                        .setTooltip(minecraft -> ignored -> TooltipUtils.createTooltip(tooltip, 200));
				OptionsList.configMap.put(option, key);
				addOption(category, name, option);
			} else if (checkType.isEnum()) {
				Class<? extends Enum> enumClass = (Class<? extends Enum>) configValue.get().getClass();
				Enum<?> value = null;

				if (configValue.get() instanceof String stringValue) {
					value = EnumGetMethod.ORDINAL_OR_NAME.get(stringValue, enumClass);
				} else if (configValue.get() instanceof Enum<?> enumValue) {
					value = EnumGetMethod.ORDINAL_OR_NAME.get(enumValue.ordinal(), enumClass);
				}

				if (value != null) {
					Option option = new DSDropDownOption(name, value, enumValue -> {
						ConfigHandler.updateConfigValue(configValue, enumValue);

						if (screenSide() == ConfigSide.SERVER) {
							NetworkHandler.CHANNEL.sendToServer(new SyncEnumConfig(ConfigHandler.createConfigPath(configOption), enumValue));
						}
					}, TooltipUtils.createTooltip(tooltip, 200));

					OptionsList.configMap.put(option, key);
					addOption(category, name, option);
				} else {
					DragonSurvivalMod.LOGGER.error("An enum configuration seems to be broken for [" + ConfigHandler.createConfigPath(configOption) + "] - value: [" + configValue.get() + "], class: [" + configValue.get().getClass() + "]");
				}
			} else if (checkType.isAssignableFrom(List.class)) {
                // Handle list values
				if (configValue.get() instanceof List<?> listConfig && (listConfig.isEmpty() || listConfig.get(0) instanceof String || listConfig.get(0) instanceof Number)) {
					StringJoiner joiner = new StringJoiner(",");

					if (!listConfig.isEmpty()) {
                        for (Object listElement : listConfig) {
                            joiner.add("[" + listElement.toString() + "]");
                        }
                    } else {
						joiner.add("[]");
					}

					String text = Minecraft.getInstance().font.substrByWidth(Component.literal(joiner.toString()), 120).getString();
					CycleOption<String> option = new CycleOption<>(
							name,
                            options -> text,
                            (val1, val2, val3) -> minecraft.setScreen(new ConfigListMenu(this, minecraft.options, Component.literal(name), configValue, screenSide(), configOption)),
                            () -> new Builder<String>(ignored -> Component.literal(text)).displayOnlyValue().withValues(text).withInitialValue(text)).setTooltip(minecraft -> ignored -> TooltipUtils.createTooltip(tooltip, 200));

					OptionsList.configMap.put(option, key);
					addOption(category, name, option);
				} else {
					DragonSurvivalMod.LOGGER.warn("Invalid configuration: [" + key + "]");
				}
			} else if (checkType.isAssignableFrom(String.class)) {
				// Handle String values
				if (configValue.get() instanceof String stringValue) {
					Option option;

					if (ConfigHandler.isResource(configOption)) {
						option = new ResourceTextFieldOption(configOption.key(), stringValue, settings -> stringValue);
					} else {
						option = new DSTextBoxOption(stringValue, settings -> stringValue);
					}

					OptionsList.configMap.put(option, key);
					addOption(category, name, option);
				}
			}
		}
	}

	private void addOption(final String category, final String path, final Option option) {
		if (category != null) {
			categories.computeIfAbsent(category, key -> new ArrayList<>()).add(option);
		} else {
            OPTIONS.add(option);
        }

		optionKeys.add(path);
	}

	@Override
	public void render(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		// Renders a black transparent overlay behind the config categories
		renderBackground(guiGraphics);
		// Render the actual config categories
		list.render(guiGraphics, mouseX, mouseY, partialTicks);
		// Renders default buttons (e.g. `Done`)
		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		setTooltipForNextRenderPass(tooltipAt(this.list, mouseX, mouseY));
	}

	public static List<FormattedCharSequence> tooltipAt(final OptionsList options, int mouseX, int mouseY) {
		Optional<AbstractWidget> optional = options.getMouseOver(mouseX, mouseY);
		OptionListEntry entry = options.getEntryAtPos(mouseX, mouseY);

		if (optional.isEmpty() && entry instanceof OptionEntry optionEntry) {
			if (optionEntry.resetButton.isHovered()) {
				Tooltip tooltip = optionEntry.resetButton.getTooltip();
				return tooltip != null ? tooltip.toCharSequence(Minecraft.getInstance()) : List.of();
			}

			optional = Optional.of(optionEntry.widget);
		}

		if (optional.isPresent() && optional.get().visible && !optional.get().isHoveredOrFocused()) {
			Tooltip tooltip = optional.get().getTooltip();
			return tooltip != null ? tooltip.toCharSequence(Minecraft.getInstance()) : List.of();
		}

		return ImmutableList.of();
	}
}