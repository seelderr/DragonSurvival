package by.dragonsurvivalteam.dragonsurvival.client.gui.settings;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets.DSTextBoxOption;
import by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets.Option;
import by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets.ResourceTextFieldOption;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownList;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionListEntry;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionsList;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.TextBoxEntry;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncListConfig;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/** Handles the config options / entries */
public class ConfigListMenu extends OptionsSubScreen {
	private final ConfigValue value;
	private final ConfigSide side;
	private final ConfigOption configOption;

	private OptionsList list;
	private List<OptionListEntry> oldVals;

	private boolean isResouce = false;

	// FIXME :: valueSpec and configKey seem to get the same value - why are there two fields for it?
	public ConfigListMenu(final Screen screen, final Options options, final Component title, final ConfigValue<?> configValue, final ConfigSide side, final ConfigOption configOption) {
		super(screen, options, title);
		this.value = configValue;
		this.side = side;
		this.configOption = configOption;
		this.title = title;
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
			isResouce = ConfigHandler.isResource(configOption);
		}

		if (oldVals == null || oldVals.isEmpty()) {
			if (value.get() instanceof List<?> listConfig && !listConfig.isEmpty()) {
				listConfig.forEach(element -> {
					if (element instanceof String string) {
						createOption(string);
					} else if (element instanceof Number number) {
						createOption(number.toString());
					} else {
						DragonSurvivalMod.LOGGER.warn("Invalid configuration element in [" + configOption.key() + "]");
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
		addRenderableWidget(new Button.Builder(Component.literal("Add new"), button -> {
			createOption("");
			list.setScrollAmount(list.getMaxScroll());
		}).bounds(width / 2 + 20, height - 27, 100, 20).build());

		// Button to save the input
		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> {
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
				if (!ConfigHandler.checkConfig(configOption, configValue)) {
					DragonSurvivalMod.LOGGER.warn("Config entry [" + configValue + "] is invalid for [" + configOption.key() + "]");
					isValid = false;
					break;
				}
			}

			if (isValid) {
				value.set(output);

				if (side == ConfigSide.SERVER) {
					NetworkHandler.CHANNEL.sendToServer(new SyncListConfig(ConfigHandler.createConfigPath(configOption), output));
				}

				minecraft.setScreen(lastScreen);
			}
		}).bounds(width / 2 - 120, height - 27, 100, 20).build());

		list.setScrollAmount(scroll);
	}

	@Override
	public void render(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		// Renders a black background - without it you would see the players viewpoint but in cinema-format (top and bottom covered)
		renderBackground(guiGraphics);
		// Render the actual config entries (within a config category)
		list.render(guiGraphics, mouseX, mouseY, partialTicks);
		// Renders default buttons when going into config list entries (e.g. `Done` or `Add new`)
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
	}

	private void createOption(final String text) {
		Option option;

		if (isResouce) {
			option = new ResourceTextFieldOption(configOption.key(), text, settings -> text);
		} else {
			option = new DSTextBoxOption(text, settings -> text);
		}

		AbstractWidget widget = option.createButton(minecraft.options, 32, 0, list.getScrollbarPosition() - 32 - 60);
		list.addEntry(new TextBoxEntry(option, list, widget, null));
	}

	@Override
	public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
		for (GuiEventListener child : children()) {
			if (child instanceof DropdownList dropdownList) {
				if (dropdownList.visible && dropdownList.isMouseOver(pMouseX, pMouseY)) {
					dropdownList.mouseScrolled(pMouseX, pMouseY, pDelta);
					return false;
				}
			}
		}

		return super.mouseScrolled(pMouseX, pMouseY, pDelta);
	}
}