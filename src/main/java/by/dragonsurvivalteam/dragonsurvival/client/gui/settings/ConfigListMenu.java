package by.dragonsurvivalteam.dragonsurvival.client.gui.settings;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets.DSTextBoxOption;
import by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets.Option;
import by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets.ResourceTextFieldOption;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionListEntry;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionsList;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.TextBoxEntry;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigType;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncListConfig;
import com.google.common.primitives.Primitives;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/** Handles the config options / entries */
public class ConfigListMenu extends OptionsSubScreen {
	private final ConfigValue value;
	private final ConfigSide side;
	private final String configKey;
	private final String valueSpec;

	private OptionsList list;

	private List<OptionListEntry> oldVals;

	private boolean isResouce = false;

	// FIXME :: valueSpec and configKey seem to get the same value - why are there two fields for it?
	public ConfigListMenu(final Screen screen, final Options options, final Component component, final String valueSpec, final ConfigValue<?> configValue, final ConfigSide side, final String configKey) {
		super(screen, options, component);
		this.value = configValue;
		this.side = side;
		this.configKey = configKey;
		this.valueSpec = valueSpec;
		title = component;
	}

	@Override
	protected void init() {
		double scroll = 0;

		if (list != null) {
			oldVals = list.children();
			scroll = list.getScrollAmount();
		}

		list = new OptionsList(width, height, 32, height - 32) {
			@Override
			protected int getMaxPosition() {
				return super.getMaxPosition() + 120;
			}
		};

		// Entries that support suggestions (and rendering ItemStacks next to their name)
		if (!isResouce) {
			Field field = ConfigHandler.configFields.get(valueSpec);
			Class<?> checkType = Primitives.unwrap(field.getType());

			if (field.isAnnotationPresent(ConfigType.class)) {
				ConfigType type = field.getAnnotation(ConfigType.class);
				checkType = Primitives.unwrap(type.value());
			}

			if (ItemLike.class.isAssignableFrom(checkType) || checkType == Block.class || checkType == EntityType.class || checkType == MobEffect.class || checkType == Biome.class) {
				isResouce = true;
			}
		}

		if (oldVals == null || oldVals.isEmpty()) {
			if (value.get() instanceof List<?> listConfig && !listConfig.isEmpty()) {
				listConfig.forEach(element -> {
					if (element instanceof String string) {
						createOption(string);
					} else if (element instanceof Number number) {
						createOption(number.toString());
					} else {
						DragonSurvivalMod.LOGGER.warn("Invalid configuration element in [" + configKey + "]");
					}
				});
			}
		} else {
			// TODO :: Not sure when this is ever reached
			for (OptionListEntry oldVal : oldVals) {
				if (oldVal instanceof TextBoxEntry textBoxEntry) {
					createOption(((EditBox) textBoxEntry.widget).getValue());
				}
			}

			oldVals = null;
		}

		addWidget(list);

		// Button to add new entries
		addRenderableWidget(new Button(width / 2 + 20, height - 27, 100, 20, Component.literal("Add new"), button -> {
			createOption("");
			list.setScrollAmount(list.getMaxScroll());
		}));

		// Button to save the input
		addRenderableWidget(new Button(width / 2 - 120, height - 27, 100, 20, CommonComponents.GUI_DONE, button -> {
			ArrayList<String> output = new ArrayList<>();

			list.children().forEach(entry -> entry.children().forEach(child -> {
				if (child instanceof EditBox editBox) {
					String value = editBox.getValue();

					if (!value.isEmpty()) {
						output.add(value);
					}
				}
			}));

			boolean isValid = true;

			for (String configValue : output) {
				if (!ConfigHandler.checkConfig(configKey, configValue)) {
					DragonSurvivalMod.LOGGER.warn("Config entry [" + configValue + "] is invalid for [" + configKey + "]");
					isValid = false;
					break;
				}
			}

			if (isValid) {
				value.set(output);

				if (side == ConfigSide.SERVER) {
					NetworkHandler.CHANNEL.sendToServer(new SyncListConfig(configKey, output));
				}

				minecraft.setScreen(lastScreen);
			}
		}));

		list.setScrollAmount(scroll);
	}

	@Override
	public void render(@NotNull final PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		// Renders a black background - without it you would see the players viewpoint but in cinema-format (top and bottom covered)
		renderBackground(poseStack);
		// Render the actual config entries (within a config category)
		list.render(poseStack, mouseX, mouseY, partialTicks);
		// Renders default buttons when going into config list entries (e.g. `Done` or `Add new`)
		super.render(poseStack, mouseX, mouseY, partialTicks);
	}

	private void createOption(final String text) {
		Option option;

		if (isResouce) {
			option = new ResourceTextFieldOption(valueSpec, text, settings -> text);
		} else {
			option = new DSTextBoxOption(valueSpec, text, settings -> text);
		}

		AbstractWidget widget = option.createButton(minecraft.options, 32, 0, list.getScrollbarPosition() - 32 - 60);
		list.addEntry(new TextBoxEntry(option, list, widget, null));
	}
}