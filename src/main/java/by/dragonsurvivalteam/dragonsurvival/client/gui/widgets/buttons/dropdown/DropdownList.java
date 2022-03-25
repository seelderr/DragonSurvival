package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.GuiUtils;

public class DropdownList extends AbstractSelectionList<by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownEntry>{
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	public int listWidth;
	public boolean visible;

	public DropdownList(int x, int y, int xSize, int ySize, int itemHeight){
		super(Minecraft.getInstance(), 0, 0, 0, 0, itemHeight);
		this.listWidth = xSize;
		this.setRenderBackground(false);
		this.setRenderTopAndBottom(false);
		reposition(x, y, xSize, ySize);
	}

	public void reposition(int x, int y, int xSize, int ySize){
		this.x0 = x;
		this.y0 = y + 3;

		this.width = xSize;
		this.height = ySize;

		height = Math.min(height, Minecraft.getInstance().getWindow().getGuiScaledHeight() - (y + 6));

		this.x1 = x + width;
		this.y1 = y + 3 + height;
	}

	@Override
	public int addEntry(by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownEntry p_230513_1_){
		return super.addEntry(p_230513_1_);
	}

	@Override
	public void updateNarration(NarrationElementOutput pNarrationElementOutput){}

	@Override
	public int getScrollbarPosition(){
		return x1 - 6 - 3;
	}


	@Override
	protected void renderBackground(PoseStack stack){
		GuiUtils.drawContinuousTexturedBox(stack, BACKGROUND_TEXTURE, x0, y0 - 3, 0, 0, width, height + 6, 32, 32, 10, 10);
	}

	@Override
	public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTicks){
		this.renderBackground(pPoseStack);
		int i = this.getScrollbarPosition();
		int j = i + 6;
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		int j1 = this.getRowLeft();
		int k = this.y0 + 4 - (int)this.getScrollAmount();
		this.renderList(pPoseStack, j1, k, pMouseX, pMouseY, pPartialTicks);

		if(children().size() > 0){
			RenderSystem.disableScissor();
		}

		int k1 = this.getMaxScroll();
		if(k1 > 0){
			RenderSystem.disableTexture();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			int l1 = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
			l1 = Mth.clamp(l1, itemHeight, this.y1 - this.y0 - 8);
			int i2 = (int)this.getScrollAmount() * (this.y1 - this.y0 - l1) / k1 + this.y0;
			if(i2 < this.y0){
				i2 = this.y0;
			}
			double z = getBlitOffset() + 10;

			bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
			bufferbuilder.vertex(i, this.y1, z).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex(j, this.y1, z).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex(j, this.y0, z).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex(i, this.y0, z).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex(i, i2 + l1, z).color(128, 128, 128, 255).endVertex();
			bufferbuilder.vertex(j, i2 + l1, 0.0D).color(128, 128, 128, 255).endVertex();
			bufferbuilder.vertex(j, i2, z).color(128, 128, 128, 255).endVertex();
			bufferbuilder.vertex(i, i2, z).color(128, 128, 128, 255).endVertex();
			bufferbuilder.vertex(i, i2 + l1 - 1, z).color(192, 192, 192, 255).endVertex();
			bufferbuilder.vertex(j - 1, i2 + l1 - 1, z).color(192, 192, 192, 255).endVertex();
			bufferbuilder.vertex(j - 1, i2, z).color(192, 192, 192, 255).endVertex();
			bufferbuilder.vertex(i, i2, z).color(192, 192, 192, 255).endVertex();
			tesselator.end();
		}

		this.renderDecorations(pPoseStack, pMouseX, pMouseY);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	@Override
	public boolean isMouseOver(double pMouseX, double pMouseY){
		return visible && pMouseY >= (double)this.y0 - 3 && pMouseY <= (double)this.y1 + 3 && pMouseX >= (double)this.x0 && pMouseX <= (double)this.x1;
	}
}