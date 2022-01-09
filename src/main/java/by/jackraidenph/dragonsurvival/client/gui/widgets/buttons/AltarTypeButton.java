package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.DragonAltarGUI;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class AltarTypeButton extends Button
{
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/dragon_altar_icons.png");
	
	public DragonType type;
	DragonAltarGUI gui;
	
	public AltarTypeButton(DragonAltarGUI gui, DragonType type, int x, int y)
	{
		super(x, y, 49, 147, null, null);
		this.gui = gui;
		this.type = type;
	}
	
	@Override
	public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float p_230431_4_)
	{
		Minecraft.getInstance().getTextureManager().bind(BACKGROUND_TEXTURE);
		
		if(gui.selected == type){
			fill(mStack, x-1, y-1, x+width+1, y+height+1, new Color(255, 255, 255, 255).getRGB());
		}
		
		blit(mStack, x, y, (type.ordinal() * 49), isHovered || gui.selected == type ? 0 : 147, 49, 147, 512, 512);
	}
	
	@Override
	public void onPress()
	{
		gui.selected = type;
		gui.update();
	}
}
