package by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonBodies;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class DragonBodyButton extends Button {
	private DragonEditorScreen dragonEditorScreen;
	private AbstractDragonBody dragonBody;
	private ResourceLocation texture_location;
	private int pos;
	private boolean locked = false;
	
	public DragonBodyButton(DragonEditorScreen dragonEditorScreen, int x, int y, int xSize, int ySize, AbstractDragonBody dragonBody, int pos, boolean locked) {
		super(x, y, xSize, ySize, Component.literal(dragonBody.toString()), btn -> {
			if (!locked) {
				dragonEditorScreen.dragonBody = dragonBody;
				dragonEditorScreen.update();
			}
		});
		texture_location = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/bodyicons.png");
		this.dragonEditorScreen = dragonEditorScreen;
		this.dragonBody = dragonBody;
		this.pos = pos;
		this.locked = locked;
	}
	
	public void press() {
		dragonEditorScreen.dragonBody = dragonBody;
		dragonEditorScreen.update();
	}

	@Override
	public void render(PoseStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks){
		active = visible = dragonEditorScreen.showUi;
		super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
	}

	@Override
	public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		RenderSystem.setShaderTexture(0, texture_location);

		int i = 0;
		if (this.dragonBody.equals(dragonEditorScreen.dragonBody)) {
			i = 2;
		} else if (this.locked) {
			i = 3;
		} else if (this.isHoveredOrFocused()) {
			i = 1;
		}
		this.blit(pPoseStack, this.x, this.y, pos * this.width, i * this.height, this.width, this.height);
		//this.blit(pPoseStack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
		RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
	}
}
