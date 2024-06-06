package by.dragonsurvivalteam.dragonsurvival.client.gui.settings;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets.DSDropDownOption;
import by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets.Option;
import by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets.ProgressOption;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.fields.TextField;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionsList;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncBooleanConfig;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncEnumConfig;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncListConfig;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncNumberConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import org.jetbrains.annotations.NotNull;

public class ResetSettingsButton extends Button {
	public static final ResourceLocation texture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/reset_icon.png");

	private final Option option;

	public ResetSettingsButton(int x, int y, final Option option) {
		super(x, y, 20, 20, Component.empty(), button -> {
			if (button.isActive()) {
				if (OptionsList.configMap.containsKey(option)) {
					String key = OptionsList.configMap.get(option);

					ConfigOption configOption = ConfigHandler.configObjects.get(key);
					ConfigValue<?> value = ConfigHandler.configValues.get(key);

					if (Minecraft.getInstance().screen instanceof ConfigScreen configScreen) {
						ConfigHandler.updateConfigValue(value, ConfigHandler.defaultConfigValues.get(key));

						String configKey = OptionsList.configMap.get(option);
						AbstractWidget widget = configScreen.list.findOption(option);

						Object defaultValues = ConfigHandler.defaultConfigValues.get(key);

						if (defaultValues instanceof Boolean) {
							handleBooleanValue(widget, defaultValues, configOption, configKey);
						} else if (defaultValues instanceof Integer) {
							handleIntegerValue(option, widget, defaultValues, configOption);
						} else if (defaultValues instanceof Double) {
							handleDoubleValue(option, widget, defaultValues, configOption);
						} else if (defaultValues instanceof Long) {
							handleLongValue(option, widget, defaultValues, configOption);
						}else if (defaultValues instanceof Enum) {
							((DSDropDownOption) option).btn.current = ((Enum<?>) defaultValues).name();
							((DSDropDownOption) option).btn.updateMessage();

							if (configOption.side() == ConfigSide.SERVER) {
								NetworkHandler.CHANNEL.sendToServer(new SyncEnumConfig(ConfigHandler.createConfigPath(configOption), (Enum<?>) defaultValues));
							}
						} else if (defaultValues instanceof List<?> list) {
							if (configOption.side() == ConfigSide.SERVER) {
								NetworkHandler.CHANNEL.sendToServer(new SyncListConfig(ConfigHandler.createConfigPath(configOption), list));
							}
						}
					}
				}
			}
		}, Supplier::get);

		this.option = option;
		setTooltip(Tooltip.create(Component.translatable("dragonsurvival.config.reset_button")));
	}

	// TODO :: Generalize numeric values (one method for all types)
	private static void handleLongValue(final Option option, final AbstractWidget widget, final Object defaultValues, final ConfigOption configOption) {
		if (widget != null) {
			if (widget instanceof AbstractOptionSliderButton abstractOptionSliderButton && option instanceof ProgressOption progressOption) {
				widget.setMessage(progressOption.getMessage(Minecraft.getInstance().options));
				abstractOptionSliderButton.value = progressOption.toPct((Long) defaultValues);
			} else if (widget instanceof TextField textField) {
				textField.setValue(defaultValues.toString());
			}
		}

		if (configOption.side() == ConfigSide.SERVER) {
			NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(ConfigHandler.createConfigPath(configOption), (Long) defaultValues));
		}
	}

	private static void handleDoubleValue(final Option option, final AbstractWidget widget, final Object defaultValues, final ConfigOption configOption) {
		if (widget != null) {
			if (widget instanceof AbstractOptionSliderButton abstractOptionSliderButton && option instanceof ProgressOption progressOption) {
				widget.setMessage(progressOption.getMessage(Minecraft.getInstance().options));
				abstractOptionSliderButton.value = progressOption.toPct((Double) defaultValues);
			} else if (widget instanceof TextField textField) {
				textField.setValue(defaultValues.toString());
			}
		}

		if (configOption.side() == ConfigSide.SERVER) {
			NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(ConfigHandler.createConfigPath(configOption), (Double) defaultValues));
		}
	}

	private static void handleIntegerValue(final Option option, final AbstractWidget widget, final Object defaultValues, final ConfigOption configOption) {
		if (widget != null) {
			if (widget instanceof AbstractOptionSliderButton abstractOptionSliderButton && option instanceof ProgressOption progressOption) {
				widget.setMessage(progressOption.getMessage(Minecraft.getInstance().options));
				abstractOptionSliderButton.value = progressOption.toPct((Integer) defaultValues);
			} else if (widget instanceof TextField textField) {
				textField.setValue(defaultValues.toString());
			}
		}

		if (configOption.side() == ConfigSide.SERVER) {
			NetworkHandler.CHANNEL.sendToServer(new SyncNumberConfig(ConfigHandler.createConfigPath(configOption), (Integer) defaultValues));
		}
	}

	private static void handleBooleanValue(final AbstractWidget widget, final Object defaultValues, final ConfigOption configOption, final String configKey) {
		if (widget instanceof CycleButton cycleButton) {
			cycleButton.setValue(defaultValues);
		}

		if (configOption.side() == ConfigSide.SERVER) {
			NetworkHandler.CHANNEL.sendToServer(new SyncBooleanConfig(ConfigHandler.createConfigPath(configOption), (Boolean) defaultValues));
		}
	}

	@Override
	public void render(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			active = false;
			isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + getWidth() && mouseY < getY() + getHeight();

			if (OptionsList.configMap.containsKey(option)) {
				String key = OptionsList.configMap.get(option);
				ConfigValue<?> value = ConfigHandler.configValues.get(key);
				active = !value.get().equals(ConfigHandler.defaultConfigValues.get(key));
			}

			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
			int i = /*getYImage(isHoveredOrFocused())*/ !isActive() ? 0 : isHoveredOrFocused() ? 2 : 1;
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			guiGraphics.blit(WIDGETS_LOCATION, getX(), getY(), 0, 46 + i * 20, getWidth() / 2, getHeight());
			guiGraphics.blit(WIDGETS_LOCATION, getX() + getWidth() / 2, getY(), 200 - getWidth() / 2, 46 + i * 20, getWidth() / 2, getHeight());

			guiGraphics.blit(texture, getX() + 2, getY() + 2, 0, 0, 16, 16, 16, 16);
		}
	}
}