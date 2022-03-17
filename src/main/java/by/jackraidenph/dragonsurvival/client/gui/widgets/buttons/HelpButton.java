package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.common.util.DragonUtils;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.Arrays;

public class HelpButton extends ExtendedButton
{
	public String text;
	public int variation;
	public DragonType type;
	public static final ResourceLocation texture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/help_button.png");
	
	public HelpButton(int x, int y, int sizeX, int sizeY, String text, int variation)
	{
		this(DragonUtils.getDragonType(Minecraft.getInstance().player), x, y, sizeX, sizeY, text, variation);
	}
	
	public HelpButton(DragonType type, int x, int y, int sizeX, int sizeY, String text, int variation)
	{
		super(x, y, sizeX, sizeY, StringTextComponent.EMPTY, (s) -> {});
		this.text = text;
		this.variation = variation;
		this.type = type;
	}
	
	@Override
	public void renderButton(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
	{
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getTextureManager().bind(texture);
		int i = 0;
		if (this.isHovered()) {
			i += (int)(((type.ordinal() + 1) * 18f));
		}
		
		pMatrixStack.pushPose();
		pMatrixStack.translate(0, 0, 200);
		float xSize = (float)width / 18F;
		float ySize = (float)height / 18F;
		
		pMatrixStack.translate(x - x * xSize, y - y * ySize, 0);
		pMatrixStack.scale(xSize, ySize, 0);
		
		blit(pMatrixStack, this.x, this.y, 0, (float)i, 18, 18, 256, 256);
		
		if(variation == 1){
			blit(pMatrixStack, this.x, this.y, 18, 0, 18, 18, 256, 256);
		}
		
		pMatrixStack.popPose();
		
		if (this.isHovered()) {
			this.renderToolTip(pMatrixStack, pMouseX, pMouseY);
		}
	}
	
	@Override
	public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
	{
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		if(isHovered() && this.visible){
			GuiUtils.drawHoveringText(p_230430_1_, Arrays.asList(new TranslationTextComponent(text)), p_230430_2_, p_230430_3_, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
		}
	}
	
	@Override
	public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_)
	{
		return false;
	}
}
