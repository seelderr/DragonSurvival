package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.function.Consumer;

public class ExtendedCheckbox extends CheckboxButton{
	public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	final ResourceLocation TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/checkbox.png");
	public Consumer<ExtendedCheckbox> pressable;
	private final int renderWidth;

	public ExtendedCheckbox(int pX, int pY, int pWidth, int renderWidth, int pHeight, ITextComponent pMessage, boolean pSelected, Consumer<ExtendedCheckbox> pressable){
		super(pX, pY, pWidth, pHeight, pMessage, pSelected);
		this.pressable = pressable;
		this.renderWidth = renderWidth;
	}

	@Override
	public void onPress(){
		super.onPress();
		pressable.accept(this);
	}

	@Override
	public void renderButton(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks){
		RenderSystem.pushMatrix();
		pMatrixStack.pushPose();
		pMatrixStack.translate(0, 0, 100);
		Minecraft minecraft = Minecraft.getInstance();
		FontRenderer fontrenderer = minecraft.font;

		if(height > 10){
			Minecraft.getInstance().textureManager.bind(BACKGROUND_TEXTURE);
			GuiUtils.drawContinuousTexturedBox(pMatrixStack, x, y, 0, 0, width, height, 32, 32, 10, 0);
			GuiUtils.drawContinuousTexturedBox(pMatrixStack, x, y, 0, 0, renderWidth, height, 32, 32, 10, 0);
		}


		minecraft.getTextureManager().bind(TEXTURE);

		if(height > 10){
			float widthMod = ((renderWidth - 4) / 36f);
			float heightMod = ((height - 4) / 36f);

			float u = this.isHovered() || this.isFocused() ? renderWidth - 4 : 0.0F;
			float v = this.selected() ? height - 4 : 0.0F;

			blit(pMatrixStack, this.x + 2, this.y + 2, u, v, renderWidth - 4, height - 4, (int)(72 * widthMod), (int)(72f * heightMod));
			this.renderBg(pMatrixStack, minecraft, pMouseX, pMouseY);

			ITextComponent message = active ? getMessage() : ((TranslationTextComponent)this.getMessage()).withStyle(TextFormatting.DARK_GRAY);

			drawString(pMatrixStack, fontrenderer, message, this.x + renderWidth + 2, this.y + (height - 8) / 2, 14737632);
		}else{
			float widthMod = ((renderWidth) / 36f);
			float heightMod = ((height) / 36f);

			float u = this.isHovered() || this.isFocused() ? renderWidth : 0.0F;
			float v = this.selected() ? height : 0.0F;

			blit(pMatrixStack, this.x, this.y, u, v, renderWidth, height, (int)(72 * widthMod), (int)(72f * heightMod));
			this.renderBg(pMatrixStack, minecraft, pMouseX, pMouseY);

			ITextComponent message = active ? getMessage() : ((TranslationTextComponent)this.getMessage()).withStyle(TextFormatting.DARK_GRAY);

			drawString(pMatrixStack, fontrenderer, message, this.x + renderWidth + 2, this.y + (height - 8) / 2, 14737632);
		}
		pMatrixStack.popPose();
		RenderSystem.popMatrix();
	}
}