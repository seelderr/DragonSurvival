package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic;

import by.dragonsurvivalteam.dragonsurvival.client.gui.utils.TooltipRender;
import by.dragonsurvivalteam.dragonsurvival.client.util.TooltipRendering;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import java.util.Arrays;

public class DSButton extends ExtendedButton implements TooltipRender{

	public Component[] tooltips;

	public DSButton(int pX, int pY, int pWidth, int pHeight, OnPress pOnPress){
		super(pX, pY, pWidth, pHeight, TextComponent.EMPTY, pOnPress);
	}

	public DSButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress){
		super(pX, pY, pWidth, pHeight, pMessage, pOnPress);
	}

	public DSButton(int pX, int pY, int pWidth, int pHeight){
		super(pX, pY, pWidth, pHeight, TextComponent.EMPTY, t -> {});
	}

	public DSButton(int pX, int pY, int pWidth, int pHeight, OnPress pOnPress, Component... tooltip){
		super(pX, pY, pWidth, pHeight, TextComponent.EMPTY, pOnPress);
		tooltips = tooltip;
	}

	public DSButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, Component... tooltip){
		super(pX, pY, pWidth, pHeight, pMessage, pOnPress);
		tooltips = tooltip;
	}

	public DSButton(int pX, int pY, int pWidth, int pHeight, Component... tooltip){
		super(pX, pY, pWidth, pHeight, TextComponent.EMPTY, t -> {});
		tooltips = tooltip;
	}

	@Override
	public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY){
		if(tooltips != null && tooltips.length > 0)
			TooltipRendering.drawHoveringText(pPoseStack, Arrays.asList(tooltips), pMouseX, pMouseY);
	}
}