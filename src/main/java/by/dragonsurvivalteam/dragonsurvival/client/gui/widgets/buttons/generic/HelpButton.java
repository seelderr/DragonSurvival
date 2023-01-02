package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.utils.TooltipRender;
import by.dragonsurvivalteam.dragonsurvival.client.util.TooltipRendering;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import java.util.Objects;

public class HelpButton extends ExtendedButton implements TooltipRender{
	public static final ResourceLocation texture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/help_button.png");
	public String text;
	public int variation;
	public AbstractDragonType type;

	public HelpButton(int x, int y, int sizeX, int sizeY, String text, int variation){
		this(DragonUtils.getDragonType(Minecraft.getInstance().player), x, y, sizeX, sizeY, text, variation);
	}

	public HelpButton(AbstractDragonType type, int x, int y, int sizeX, int sizeY, String text, int variation){
		super(x, y, sizeX, sizeY, TextComponent.EMPTY, s -> {});
		this.text = text;
		this.variation = variation;
		this.type = type;
	}

	@Override
	public void renderButton(PoseStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks){
		Minecraft minecraft = Minecraft.getInstance();
		RenderSystem.setShaderTexture(0, texture);

		float size = variation == 0 ? 18f : 22f;
		float xSize = (float)(width + (variation == 0 ? 0 : 2)) / size;
		float ySize = (float)(height + (variation == 0 ? 0 : 2)) / size;

		int i = 0;
		if(isHoveredOrFocused())
			i += (int)(type == null ? 4 : (Objects.equals(type, DragonTypes.CAVE) ? 1 :  Objects.equals(type, DragonTypes.FOREST) ? 2 :  Objects.equals(type, DragonTypes.SEA) ? 3 : 4) * size);

		pMatrixStack.pushPose();
		pMatrixStack.translate(0, 0, 200);

		pMatrixStack.translate(x - x * xSize, y - y * ySize, 0);
		pMatrixStack.scale(xSize, ySize, 0);

		if(variation == 0)
			blit(pMatrixStack, x, y, 0, (float)i, 18, 18, 256, 256);
		else
			blit(pMatrixStack, x - 1, y - 1, 18, (float)i, 22, 22, 256, 256);

		pMatrixStack.popPose();
	}

	@Override
	public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY){
		TooltipRendering.drawHoveringText(pPoseStack, new TranslatableComponent(text), pMouseX, pMouseY);
	}

	@Override
	public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_){
		return false;
	}
}