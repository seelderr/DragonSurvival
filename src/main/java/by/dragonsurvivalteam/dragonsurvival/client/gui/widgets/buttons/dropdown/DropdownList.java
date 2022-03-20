package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.Objects;

public class DropdownList extends AbstractOptionList<DropdownEntry>{
	public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
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
	public int addEntry(DropdownEntry p_230513_1_){
		return super.addEntry(p_230513_1_);
	}	@Override
	public int getScrollbarPosition(){
		return x1 - 6 - 3;
	}




	@Override
	protected void renderBackground(MatrixStack stack){
		Minecraft.getInstance().textureManager.bind(BACKGROUND_TEXTURE);
		GuiUtils.drawContinuousTexturedBox(stack, x0, y0 - 3, 0, 0, width, height + 6, 32, 32, 10, 10);
		if(children().size() > 0){
			RenderSystem.enableScissor((int)(x0 * Minecraft.getInstance().getWindow().getGuiScale()), (int)(Minecraft.getInstance().getWindow().getScreenHeight() - ((y1 - 3) * Minecraft.getInstance().getWindow().getGuiScale())), (int)(width * Minecraft.getInstance().getWindow().getGuiScale()), (int)((height - 6) * Minecraft.getInstance().getWindow().getGuiScale()));

		}
	}

	public void centerScrollOn(DropdownEntry pEntry){
		this.setScrollAmount(this.children().indexOf(pEntry) * this.itemHeight + this.itemHeight / 2 - (this.y1 - this.y0) / 2);
	}


	protected int getMaxPosition(){
		return this.getItemCount() * this.itemHeight + this.headerHeight + (this.itemHeight / 4);
	}

	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks){
		this.renderBackground(pMatrixStack);
		{
			int i = this.getScrollbarPosition();
			int j = i + 6;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuilder();
			int j1 = this.getRowLeft();
			int k = this.y0 + 4 - (int)this.getScrollAmount();
			this.renderList(pMatrixStack, j1, k, pMouseX, pMouseY, pPartialTicks);
			int k1 = this.getMaxScroll();

			if(k1 > 0){
				RenderSystem.disableTexture();
				int l1 = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
				l1 = MathHelper.clamp(l1, itemHeight, this.y1 - this.y0 - 8);
				int i2 = (int)this.getScrollAmount() * (this.y1 - this.y0 - l1) / k1 + this.y0;
				if(i2 < this.y0){
					i2 = this.y0;
				}

				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				bufferbuilder.vertex(i, this.y1, 0.0D).uv(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
				bufferbuilder.vertex(j, this.y1, 0.0D).uv(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
				bufferbuilder.vertex(j, this.y0, 0.0D).uv(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
				bufferbuilder.vertex(i, this.y0, 0.0D).uv(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
				bufferbuilder.vertex(i, i2 + l1, 0.0D).uv(0.0F, 1.0F).color(128, 128, 128, 255).endVertex();
				bufferbuilder.vertex(j, i2 + l1, 0.0D).uv(1.0F, 1.0F).color(128, 128, 128, 255).endVertex();
				bufferbuilder.vertex(j, i2, 0.0D).uv(1.0F, 0.0F).color(128, 128, 128, 255).endVertex();
				bufferbuilder.vertex(i, i2, 0.0D).uv(0.0F, 0.0F).color(128, 128, 128, 255).endVertex();
				bufferbuilder.vertex(i, i2 + l1 - 1, 0.0D).uv(0.0F, 1.0F).color(192, 192, 192, 255).endVertex();
				bufferbuilder.vertex(j - 1, i2 + l1 - 1, 0.0D).uv(1.0F, 1.0F).color(192, 192, 192, 255).endVertex();
				bufferbuilder.vertex(j - 1, i2, 0.0D).uv(1.0F, 0.0F).color(192, 192, 192, 255).endVertex();
				bufferbuilder.vertex(i, i2, 0.0D).uv(0.0F, 0.0F).color(192, 192, 192, 255).endVertex();
				tessellator.end();
			}
		}

		this.renderDecorations(pMatrixStack, pMouseX, pMouseY);
		RenderSystem.enableTexture();
		RenderSystem.shadeModel(7424);
		RenderSystem.enableAlphaTest();
		RenderSystem.disableBlend();

		if(children().size() > 0){
			RenderSystem.disableScissor();
		}
	}

	private int getRowBottom(int pIndex){
		return this.getRowTop(pIndex) + this.itemHeight;
	}

	protected void renderList(MatrixStack pMatrixStack, int pX, int pY, int pMouseX, int pMouseY, float pPartialTicks){
		int i = this.getItemCount();
		for(int j = 0; j < i; ++j){
			int k = this.getRowTop(j);
			int l = this.getRowBottom(j);
			if(l >= this.y0 && k <= this.y1){
				int j1 = this.itemHeight - 1;
				DropdownEntry e = this.getEntry(j);
				int k1 = this.getRowWidth();
				int j2 = this.getRowLeft();
				e.render(pMatrixStack, j, k, j2, k1, j1, pMouseX, pMouseY, this.isMouseOver(pMouseX, pMouseY) && Objects.equals(this.getEntryAtPosition(pMouseX, pMouseY), e), pPartialTicks);
			}
		}
	}

	@Override
	public boolean isMouseOver(double pMouseX, double pMouseY){
		return visible && pMouseY >= (double)this.y0 - 3 && pMouseY <= (double)this.y1 + 3 && pMouseX >= (double)this.x0 && pMouseX <= (double)this.x1;
	}
}