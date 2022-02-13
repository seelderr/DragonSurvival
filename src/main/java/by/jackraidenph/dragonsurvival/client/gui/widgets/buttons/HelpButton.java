package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
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
	
	public static final ResourceLocation texture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/help_button.png");
	
	public HelpButton(int x, int y, int sizeX, int sizeY, String text)
	{
		this(DragonStateProvider.getDragonType(Minecraft.getInstance().player), x, y, sizeX, sizeY, text);
	}
	
	public HelpButton(DragonType type, int x, int y, int sizeX, int sizeY, String text)
	{
		super(x, y, sizeX, sizeY, 0, 0, (int)(((type.ordinal() + 1) * 18f) *((float)sizeY / 18f)), texture, (int)(256f * ((float)sizeX / 18f)), (int)(256f * ((float)sizeY / 18f)), (btn) -> {});
		this.text = text;
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
