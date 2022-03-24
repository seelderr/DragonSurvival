package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;

public class ArrowButton extends Button
{
	public boolean next;
	
	public ArrowButton(int x, int y, int xSize, int ySize, boolean next, Button.OnPress pressable)
	{
		super(x, y, xSize, ySize, null, pressable);
		this.next = next;
	}
	
	@Override
	public void renderButton(PoseStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
	{
		stack.pushPose();
		stack.translate(0, 0, 200);
		RenderSystem.setShaderTexture(0, ClientMagicHUDHandler.widgetTextures);
		float xSize = ((float)width / 15f) / 2;
		float ySize = ((float)height / 17f) / 2;
		
		if(next) {
			if (isHovered) {
				blit(stack, x, y, 66 * xSize, 222 * ySize, (int)(xSize*2 * 11), (int)(ySize*2  * 17), (int)(256f * xSize), (int)(256f * ySize));
			} else {
				blit(stack, x, y, 44 * xSize, 222 * ySize, (int)(xSize*2 * 11), (int)(ySize*2  * 17), (int)(256f * xSize), (int)(256f * ySize));
			}
		}else{
			if(isHovered){
				blit(stack, x, y, 22 * xSize, 222 * ySize, (int)(xSize*2 * 11), (int)(ySize*2  * 17),(int)(256f * xSize), (int)(256f * ySize));
			}else{
				blit(stack, x, y, 0, 222 * ySize, (int)(xSize*2 * 11), (int)(ySize*2 * 17), (int)(256f * xSize), (int)(256f * ySize));
			}
		}
		stack.popPose();
	}
}
