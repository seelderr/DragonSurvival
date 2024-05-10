package by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.SkinsScreen;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class DragonSkinBodyButton extends Button {
	private SkinsScreen screen;
	private AbstractDragonBody dragonBody;
	private ResourceLocation texture_location;
	private int pos;
	
	public DragonSkinBodyButton(SkinsScreen screen, int x, int y, int xSize, int ySize, AbstractDragonBody body, int pos) {
		super(x, y, xSize, ySize, Component.literal(body.toString()), btn -> {
			screen.dragonBody = body;
		});
		texture_location = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/body_type_icon_skintab.png");
		this.screen = screen;
		this.dragonBody = body;
		this.pos = pos;
	}

	@Override
	public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		RenderSystem.setShaderTexture(0, texture_location);

		int i = 0;
		if (this.dragonBody.equals(screen.handler.getBody())) {
			i = 2;
		} else if (false) {
			i = 3;
		} else if (this.isHoveredOrFocused()) {
			i = 1;
		}else {
			//this.alpha = 0.5f;
		}
		//RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
		this.blit(pPoseStack, this.x, this.y, pos * this.width, i * this.height, this.width, this.height);
		RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
		//this.alpha = 1.0f;
	}
}
