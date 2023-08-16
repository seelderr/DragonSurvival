package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.GuiUtils;

public class DropdownList extends AbstractSelectionList<DropdownEntry>{
	public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	public int listWidth;
	public boolean visible;

	public DropdownList(int x, int y, int xSize, int ySize, int itemHeight){
		super(Minecraft.getInstance(), 0, 0, 0, 0, itemHeight);
		listWidth = xSize;
		setRenderBackground(false);
		setRenderTopAndBottom(false);
		reposition(x, y, xSize, ySize);
	}

	public void reposition(int x, int y, int xSize, int ySize){
		x0 = x;
		y0 = y + 3;

		width = xSize;
		height = ySize;

		height = Math.min(height, Minecraft.getInstance().getWindow().getGuiScaledHeight() - (y + 6));

		x1 = x + width;
		y1 = y + 3 + height;
	}

	@Override
	public int addEntry(DropdownEntry p_230513_1_){
		return super.addEntry(p_230513_1_);
	}

	@Override
	public void updateNarration(NarrationElementOutput pNarrationElementOutput){}	@Override
	public void centerScrollOn(DropdownEntry pEntry){
		setScrollAmount(children().indexOf(pEntry) * itemHeight + itemHeight / 2 - (y1 - y0) / 2);
	}




	@Override
	public int getScrollbarPosition(){
		return x1 - 6 - 3;
	}


	@Override
	protected void renderBackground(PoseStack stack){
		GuiUtils.drawContinuousTexturedBox(stack, BACKGROUND_TEXTURE, x0, y0 - 3, 0, 0, width, height + 6, 32, 32, 10, 10, 10, 10, (float)0);

		if(children().size() > 0){
			RenderSystem.enableScissor((int)(x0 * Minecraft.getInstance().getWindow().getGuiScale()), (int)(Minecraft.getInstance().getWindow().getScreenHeight() - (y1 - 3) * Minecraft.getInstance().getWindow().getGuiScale()), (int)(width * Minecraft.getInstance().getWindow().getGuiScale()), (int)((height - 6) * Minecraft.getInstance().getWindow().getGuiScale()));
		}
	}

	@Override
	protected int getMaxPosition(){
		return getItemCount() * itemHeight + headerHeight + itemHeight / 4;
	}

	@Override
	public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTicks){
		renderBackground(pPoseStack);
		int i = getScrollbarPosition();
		int j = i + 6;
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		// FIXME :: "Insecure modifications"?
		int j1 = getRowLeft();
		int k = y0 + 4 - (int)getScrollAmount();
		renderList(pPoseStack, j1, k, pMouseX, pMouseY, pPartialTicks);
		//

		if(children().size() > 0){
			RenderSystem.disableScissor();
		}

		int k1 = getMaxScroll();
		if(k1 > 0){
			RenderSystem.disableTexture();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			int l1 = (int)((float)((y1 - y0) * (y1 - y0)) / (float)getMaxPosition());
			l1 = Mth.clamp(l1, itemHeight, y1 - y0 - 8);
			int i2 = (int)getScrollAmount() * (y1 - y0 - l1) / k1 + y0;
			if(i2 < y0)
				i2 = y0;
			double z = getBlitOffset() + 10;

			bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
			bufferbuilder.vertex(i, y1, z).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex(j, y1, z).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex(j, y0, z).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex(i, y0, z).color(0, 0, 0, 255).endVertex();
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

		renderDecorations(pPoseStack, pMouseX, pMouseY);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	@Override
	public boolean isMouseOver(double pMouseX, double pMouseY){
		return visible && pMouseY >= (double)y0 - 3 && pMouseY <= (double)y1 + 3 && pMouseX >= (double)x0 && pMouseX <= (double)x1;
	}
}