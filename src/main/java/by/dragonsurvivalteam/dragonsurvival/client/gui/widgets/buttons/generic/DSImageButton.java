package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic;

import by.dragonsurvivalteam.dragonsurvival.client.gui.utils.TooltipRender;
import by.dragonsurvivalteam.dragonsurvival.client.util.TooltipRendering;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;

public class DSImageButton extends ImageButton implements TooltipRender{

	public Component[] tooltips;

	public DSImageButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, ResourceLocation pResourceLocation, Component... tooltip) {
		this(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pHeight, pResourceLocation, 256, 256, tooltip);
	}

	public DSImageButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex, ResourceLocation pResourceLocation, Component... tooltip) {
		this(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffTex, pResourceLocation, 256, 256, tooltip);
	}

	public DSImageButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, ResourceLocation pResourceLocation, Button.OnPress pOnPress, Component... tooltip) {
		this(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pHeight, pResourceLocation, 256, 256, pOnPress, tooltip);
	}

	public DSImageButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex, ResourceLocation pResourceLocation, Button.OnPress pOnPress, Component... tooltip) {
		this(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffTex, pResourceLocation, 256, 256, pOnPress, tooltip);
	}

	public DSImageButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex, ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, Component... tooltip) {
		this(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffTex, pResourceLocation, pTextureWidth, pTextureHeight, (t) -> {}, tooltip);
	}

	public DSImageButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex, ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, Button.OnPress pOnPress, Component... tooltip) {
		super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart,pYDiffTex, pResourceLocation, pTextureWidth, pTextureHeight, pOnPress);
		this.tooltips = tooltip;
	}

	@Override
	public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY){
		if(tooltips != null && tooltips.length > 0){
			TooltipRendering.drawHoveringText(pPoseStack, Arrays.asList(tooltips), pMouseX, pMouseY);
		}
	}
}