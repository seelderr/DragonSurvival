package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic;

import by.dragonsurvivalteam.dragonsurvival.client.gui.utils.TooltipRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DSButton extends ExtendedButton implements TooltipRender{

	public Component[] tooltips;

	public DSButton(int pX, int pY, int pWidth, int pHeight, OnPress pOnPress){
		super(pX, pY, pWidth, pHeight, Component.empty(), pOnPress);
	}

	public DSButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress){
		super(pX, pY, pWidth, pHeight, pMessage, pOnPress);
	}

	public DSButton(int pX, int pY, int pWidth, int pHeight){
		super(pX, pY, pWidth, pHeight, Component.empty(), t -> {});
	}

	public DSButton(int pX, int pY, int pWidth, int pHeight, OnPress pOnPress, Component... tooltip){
		super(pX, pY, pWidth, pHeight, Component.empty(), pOnPress);
		tooltips = tooltip;
	}

	public DSButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, Component... tooltip){
		super(pX, pY, pWidth, pHeight, pMessage, pOnPress);
		tooltips = tooltip;
	}

	public DSButton(int pX, int pY, int pWidth, int pHeight, Component... tooltip){
		super(pX, pY, pWidth, pHeight, Component.empty(), t -> {});
		tooltips = tooltip;
	}

	@Override
	public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

		if (isHoveredOrFocused()) {
			guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, List.of(tooltips), pMouseX, pMouseY);
		}
	}
}