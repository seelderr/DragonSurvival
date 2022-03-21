package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.Arrays;

public class HelpButton extends ExtendedButton{
	public static final ResourceLocation texture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/help_button.png");
	public String text;
	public int variation;
	public DragonType type;

	public HelpButton(int x, int y, int sizeX, int sizeY, String text, int variation){
		this(DragonUtils.getDragonType(Minecraft.getInstance().player), x, y, sizeX, sizeY, text, variation);
	}

	public HelpButton(DragonType type, int x, int y, int sizeX, int sizeY, String text, int variation){
		super(x, y, sizeX, sizeY, StringTextComponent.EMPTY, (s) -> {});
		this.text = text;
		this.variation = variation;
		this.type = type;
	}

	@Override
	public void renderButton(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks){
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getTextureManager().bind(texture);

		float size = variation == 0 ? 18f : 22f;
		float xSize = (float)(width + (variation == 0 ? 0 : 2)) / size;
		float ySize = (float)(height + (variation == 0 ? 0 : 2)) / size;

		int i = 0;
		if(this.isHovered()){
			i += (int)(((type.ordinal() + 1) * size));
		}

		pMatrixStack.pushPose();
		pMatrixStack.translate(0, 0, 200);

		pMatrixStack.translate(x - x * xSize, y - y * ySize, 0);
		pMatrixStack.scale(xSize, ySize, 0);

		if(variation == 0){
			blit(pMatrixStack, this.x, this.y, 0, (float)i, 18, 18, 256, 256);
		}else{
			blit(pMatrixStack, this.x - 1, this.y - 1, 18, (float)i, 22, 22, 256, 256);
		}

		pMatrixStack.popPose();
	}

	@Override
	public void renderToolTip(MatrixStack pPoseStack, int pMouseX, int pMouseY){
		GuiUtils.drawHoveringText(pPoseStack, Arrays.asList(new TranslationTextComponent(text)), pMouseX, pMouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);

	}
	@Override
	public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_){
		return false;
	}
}