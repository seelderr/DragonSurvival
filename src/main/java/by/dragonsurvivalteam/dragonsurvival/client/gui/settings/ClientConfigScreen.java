package by.dragonsurvivalteam.dragonsurvival.client.gui.settings;

import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClientConfigScreen extends ConfigScreen {
	public ClientConfigScreen(final Screen screen, final Options options, final Component title) {
		super(screen, options, title);
	}

	@Override
	public ConfigSide screenSide() {
		return ConfigSide.CLIENT;
	}
}