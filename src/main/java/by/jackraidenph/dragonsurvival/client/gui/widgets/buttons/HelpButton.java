package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.common.util.DragonUtils;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.Arrays;

public class HelpButton extends ImageButton
{
	public String text;
	public int variation;
	public static final ResourceLocation texture0 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/help_button.png");
	public static final ResourceLocation texture1 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/help_button_white.png");
	
	public HelpButton(int x, int y, int sizeX, int sizeY, String text, int variation)
	{
		this(DragonUtils.getDragonType(Minecraft.getInstance().player), x, y, sizeX, sizeY, text, variation);
	}
	
	public HelpButton(DragonType type, int x, int y, int sizeX, int sizeY, String text, int variation)
	{
		super(x, y, sizeX, sizeY, 0, 0, (int)(((type.ordinal() + 1) * 18f) *((float)sizeY / 18f)), variation == 0 ? texture0 : texture1, (int)(256f * ((float)sizeX / 18f)), (int)(256f * ((float)sizeY / 18f)), (btn) -> {});
		this.text = text;
		this.variation = variation;
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
