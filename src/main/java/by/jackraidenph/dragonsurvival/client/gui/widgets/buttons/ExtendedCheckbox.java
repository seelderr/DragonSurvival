package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.Consumer;

public class ExtendedCheckbox extends CheckboxButton {
	public Consumer<ExtendedCheckbox> pressable;
	private int renderWidth;
	public ExtendedCheckbox(int pX, int pY, int pWidth, int renderWidth, int pHeight, ITextComponent pMessage, boolean pSelected, Consumer<ExtendedCheckbox> pressable)
	{
		super(pX, pY, pWidth, pHeight, pMessage, pSelected);
		this.pressable = pressable;
		this.renderWidth = renderWidth;
	}
	
	@Override
	public void onPress()
	{
		super.onPress();
		pressable.accept(this);
	}
	
	final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
	
	@Override
	public void renderButton(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
	{
		RenderSystem.pushMatrix();
		pMatrixStack.pushPose();
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getTextureManager().bind(TEXTURE);
		RenderSystem.enableDepthTest();
		FontRenderer fontrenderer = minecraft.font;
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		
		float widthMod = (this.renderWidth / 20f);
		float heightMod = (this.height / 20f);
		
		float u = this.isHovered() || this.isFocused() ? renderWidth : 0.0F;
		float v = this.selected() ? height : 0.0F;
		
		blit(pMatrixStack, this.x, this.y, u, v, this.renderWidth, this.height, (int)(64f*widthMod), (int)(64f*heightMod));
		this.renderBg(pMatrixStack, minecraft, pMouseX, pMouseY);
		
		ITextComponent message = active ? getMessage() : ((TranslationTextComponent)this.getMessage()).withStyle(TextFormatting.DARK_GRAY);
		
		drawString(pMatrixStack, fontrenderer, message, this.x + renderWidth + 4, this.y + (this.height - 8) / 2, 14737632 | MathHelper.ceil(this.alpha * 255.0F) << 24);
		pMatrixStack.popPose();
		RenderSystem.popMatrix();
	}
}
