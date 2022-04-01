package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.GuiUtils;

import java.util.function.Consumer;

public class ExtendedCheckbox extends Checkbox{
	public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	final ResourceLocation TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/checkbox.png");
	private final int renderWidth;
	public Consumer<ExtendedCheckbox> pressable;

	public ExtendedCheckbox(int pX, int pY, int pWidth, int renderWidth, int pHeight, Component pMessage, boolean pSelected, Consumer<ExtendedCheckbox> pressable){
		super(pX, pY, pWidth, pHeight, pMessage, pSelected);
		this.pressable = pressable;
		this.renderWidth = renderWidth;
		setBlitOffset(300);
	}

	@Override
	public void onPress(){
		super.onPress();
		pressable.accept(this);
	}

	@Override
	public void renderButton(PoseStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks){
		pMatrixStack.pushPose();
		pMatrixStack.translate(0, 0, 100);
		Minecraft minecraft = Minecraft.getInstance();
		Font fontrenderer = minecraft.font;

		if(height > 10){
			GuiUtils.drawContinuousTexturedBox(pMatrixStack, BACKGROUND_TEXTURE, x, y, 0, 0, width, height, 32, 32, 10, 0);
			GuiUtils.drawContinuousTexturedBox(pMatrixStack, BACKGROUND_TEXTURE, x, y, 0, 0, renderWidth, height, 32, 32, 10, 0);
		}


		RenderSystem.setShaderTexture(0, TEXTURE);

		if(height > 10){
			float widthMod = ((renderWidth - 4) / 36f);
			float heightMod = ((height - 4) / 36f);

			float u = this.isHoveredOrFocused() || this.isFocused() ? renderWidth - 4 : 0.0F;
			float v = this.selected() ? height - 4 : 0.0F;

			blit(pMatrixStack, this.x + 2, this.y + 2, u, v, renderWidth - 4, height - 4, (int)(72 * widthMod), (int)(72f * heightMod));
			this.renderBg(pMatrixStack, minecraft, pMouseX, pMouseY);

			Component message = active ? getMessage() : ((TranslatableComponent)this.getMessage()).withStyle(ChatFormatting.DARK_GRAY);

			pMatrixStack.pushPose();
			pMatrixStack.translate(0, 0, getBlitOffset());
			drawString(pMatrixStack, fontrenderer, message, this.x + renderWidth + 2, this.y + (height - 8) / 2, 14737632);
			pMatrixStack.popPose();
		}else{
			float widthMod = ((renderWidth) / 36f);
			float heightMod = ((height) / 36f);

			float u = this.isHoveredOrFocused() || this.isFocused() ? renderWidth : 0.0F;
			float v = this.selected() ? height : 0.0F;

			blit(pMatrixStack, this.x, this.y, u, v, renderWidth, height, (int)(72 * widthMod), (int)(72f * heightMod));
			this.renderBg(pMatrixStack, minecraft, pMouseX, pMouseY);

			Component message = active ? getMessage() : ((TranslatableComponent)this.getMessage()).withStyle(ChatFormatting.DARK_GRAY);

			pMatrixStack.pushPose();
			pMatrixStack.translate(0, 0, getBlitOffset());
			drawString(pMatrixStack, fontrenderer, message, this.x + renderWidth + 2, this.y + (height - 8) / 2, 14737632);
			pMatrixStack.popPose();
		}
		pMatrixStack.popPose();
	}
}