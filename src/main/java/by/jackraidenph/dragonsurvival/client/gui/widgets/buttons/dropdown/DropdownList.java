package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.dropdown;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class DropdownList extends AbstractOptionList<DropdownEntry>
{
	public int listWidth;
	public boolean visible;
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	
	public DropdownList(int x, int y, int xSize, int ySize, int itemHeight)
	{
		super(Minecraft.getInstance(), 0, 0, 0, 0, itemHeight);
		this.listWidth = xSize;
		this.setRenderBackground(false);
		this.setRenderTopAndBottom(false);
		this.width = xSize;
		this.height = ySize;
		reposition(x, y, xSize, ySize);
	}
	
	public void reposition(int x, int y, int xSize, int ySize){
		this.width = xSize;
		this.height = ySize;
		this.x0 = x;
		this.x1 = x + width;
		this.y0 = y + 3;
		this.y1 = y + 3 + height;
	}
	
	@Override
	public int getScrollbarPosition()
	{
		return x1 - 6 - 3;
	}
	
	@Override
	public int addEntry(DropdownEntry p_230513_1_)
	{
		return super.addEntry(p_230513_1_);
	}
	
	
	
	@Override
	protected void renderBackground(MatrixStack stack)
	{
		Minecraft.getInstance().textureManager.bind(BACKGROUND_TEXTURE);
		GuiUtils.drawContinuousTexturedBox(stack, x0, y0 - 3, 0, 0, width, height + 6, 32, 32, 10, 10);
	}
	
	@Override
	public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
	{
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
	}
	
	@Override
	public boolean isMouseOver(double pMouseX, double pMouseY)
	{
		return visible && pMouseY >= (double)this.y0-3 && pMouseY <= (double)this.y1+3 && pMouseX >= (double)this.x0 && pMouseX <= (double)this.x1;
	}
}
