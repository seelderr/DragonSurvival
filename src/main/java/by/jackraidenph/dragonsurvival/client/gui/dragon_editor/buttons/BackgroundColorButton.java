package by.jackraidenph.dragonsurvival.client.gui.dragon_editor.buttons;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.Arrays;

public class BackgroundColorButton extends ExtendedButton
{
	private final DragonEditorScreen dragonEditorScreen;
	
	public BackgroundColorButton(int xPos, int yPos, int width, int height, ITextComponent displayString, IPressable handler, DragonEditorScreen dragonEditorScreen)
	{
		super(xPos, yPos, width, height, displayString, handler);
		this.dragonEditorScreen = dragonEditorScreen;
	}
	
	@Override
	public void onPress()
	{
		super.onPress();

	}
	
	@Override
	public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
	{
		Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/background_color_button.png"));
		blit(mStack, x, y, 0, 0, width, height, width, height);
		
		if (this.isHovered()) {
			this.renderToolTip(mStack, mouseX, mouseY);
		}
	}
	
	@Override
	public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_)
	{
		GuiUtils.drawHoveringText(p_230443_1_, Arrays.asList(new TranslationTextComponent("ds.gui.dragon_editor.background_color")), p_230443_2_, p_230443_3_, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
	}
}
