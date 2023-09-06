package by.dragonsurvivalteam.dragonsurvival.client.gui.settings;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionsList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
		addRenderableWidget(new Button(width / 2 - 100, 38, 200, 20, new TranslatableComponent("ds.gui.settings.client"), button -> {
			Minecraft.getInstance().setScreen(new ClientConfigScreen(this, Minecraft.getInstance().options, new TranslatableComponent("ds.gui.settings.client")));
		}));

		// Button for server config
		addRenderableWidget(new Button(width / 2 - 100, 38 + 27, 200, 20, new TranslatableComponent("ds.gui.settings.server"), button -> {
			Minecraft.getInstance().setScreen(new ServerConfigScreen(this, Minecraft.getInstance().options, new TranslatableComponent("ds.gui.settings.server")));
		}){
			@Override
			public void render(@NotNull final PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
				active = Minecraft.getInstance().player.hasPermissions(2);
				super.render(poseStack, mouseX, mouseY, partialTicks);
			}
		});

		children.add(list);

		// Button to go back
		addRenderableWidget(new Button(width / 2 - 100, height - 27, 200, 20, CommonComponents.GUI_BACK, p_213106_1_ -> minecraft.setScreen(lastScreen)));
	}

	@Override
	public void render(@NotNull final PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(poseStack);
		list.render(poseStack, mouseX, mouseY, partialTicks);
		super.render(poseStack, mouseX, mouseY, partialTicks);
	}
}