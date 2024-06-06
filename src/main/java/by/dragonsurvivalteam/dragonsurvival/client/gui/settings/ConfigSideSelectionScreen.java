package by.dragonsurvivalteam.dragonsurvival.client.gui.settings;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionsList;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/** Main Config Screen to select between Client or Server config */
public class ConfigSideSelectionScreen extends OptionsSubScreen {
	private OptionsList list;

	public ConfigSideSelectionScreen(final Screen screen, final Options options, final Component component) {
		super(screen, options, component);
		title = component;
	}

	@Override
	protected void init() {
		list = new OptionsList(width, height, 32, height - 32);

		// Button for client config
		addRenderableWidget(Button.builder(Component.translatable("ds.gui.settings.client"), button -> {
			Minecraft.getInstance().setScreen(new ClientConfigScreen(this, Minecraft.getInstance().options, Component.translatable("ds.gui.settings.client")));
		}).bounds(width / 2 - 100, 38, 200, 20).build());

		// Button for server config
		addRenderableWidget(new Button(width / 2 - 100, 38 + 27, 200, 20, Component.translatable("ds.gui.settings.server"), button -> {
			Minecraft.getInstance().setScreen(new ServerConfigScreen(this, Minecraft.getInstance().options, Component.translatable("ds.gui.settings.server")));
		}, Supplier::get) {
			@Override
			public void render(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				active = Minecraft.getInstance().player.hasPermissions(2);
				super.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
		});

		children.add(list);

		// Button to go back
		addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, button -> minecraft.setScreen(lastScreen)).bounds(width / 2 - 100, height - 27, 200, 20).build());
	}

	@Override
	public void render(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		renderBackground(guiGraphics);
		list.render(guiGraphics, mouseX, mouseY, partialTicks);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
	}
}