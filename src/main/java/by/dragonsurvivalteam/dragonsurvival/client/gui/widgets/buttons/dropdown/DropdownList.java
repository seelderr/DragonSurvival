package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class DropdownList extends AbstractSelectionList<DropdownEntry> {
	public static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/textbox.png");
	public int listWidth;
	public boolean visible;

	public DropdownList(int x, int y, int xSize, int ySize, int itemHeight) {
		super(Minecraft.getInstance(), 0, 0, 0,  itemHeight);
		listWidth = xSize;
		reposition(x, y, xSize, ySize);
	}

	public void reposition(int x, int y, int xSize, int ySize){
		setX(x);
		setY(y);

		width = xSize;
		height = ySize;

		height = Math.min(height, Minecraft.getInstance().getWindow().getGuiScaledHeight() - (y + 6));

		setWidth(width);
		setHeight(height);
	}

	@Override
	public int addEntry(@NotNull final DropdownEntry entry) {
		return super.addEntry(entry);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

	}

	@Override
	public void centerScrollOn(@NotNull final DropdownEntry entry) {
		setScrollAmount(children().indexOf(entry) * itemHeight + (double) itemHeight / 2 - (double) (getHeight()) / 2);
	}

	@Override
	public int getScrollbarPosition(){
		return getWidth() - getX() - 6 - 3;
	}

	@Override
	protected void renderListBackground(@NotNull final GuiGraphics guiGraphics) {
		// Renders the black background
		guiGraphics.blitWithBorder(BACKGROUND_TEXTURE, getX(), getY() - 3, 0, 0, width, height + 6, 32, 32, 10, 10, 10, 10);

		if (!children().isEmpty()) {
			RenderSystem.enableScissor((int)(getX() * Minecraft.getInstance().getWindow().getGuiScale()), (int)(Minecraft.getInstance().getWindow().getScreenHeight() - (getHeight() + getY() - 3) * Minecraft.getInstance().getWindow().getGuiScale()), (int)(width * Minecraft.getInstance().getWindow().getGuiScale()), (int)((height - 6) * Minecraft.getInstance().getWindow().getGuiScale()));
		}
	}

	@Override
	protected int getMaxPosition() {
		return getItemCount() * itemHeight + headerHeight + itemHeight / 4;
	}

	// For some reason the parent function for this returns a constant of 220 instead of width
	@Override
	public int getRowWidth() {
		return width;
	}

	@Override
	public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		if (!visible) {
			return;
		}

		int zTranslation = 150;

		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(0, 0, zTranslation);
		// Background square which contains all dragon editor part buttons
		renderListBackground(guiGraphics);
		int i = getScrollbarPosition();
		int j = i + 6;
		Tesselator tesselator = Tesselator.getInstance();
		renderListItems(guiGraphics, mouseX, mouseY, partialTicks);

		if (!children().isEmpty()) {
			RenderSystem.disableScissor();
		}

		int k1 = getMaxScroll();

		// Render the scroll bar
		if (k1 > 0) {
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			int l1 = (int) ((float) ((getWidth()) * (getHeight())) / (float) getMaxPosition());
			l1 = Mth.clamp(l1, itemHeight, getHeight() - 8);
			int i2 = Math.max(getY(), (int) getScrollAmount() * (getHeight()- l1) / k1 + getX());
			float z = zTranslation + 10;

			BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
			bufferbuilder.addVertex(i, getHeight() - getY(), z).setColor(0, 0, 0, 255);
			bufferbuilder.addVertex(j, getHeight() - getY(), z).setColor(0, 0, 0, 255);
			bufferbuilder.addVertex(j, getY(), z).setColor(0, 0, 0, 255);
			bufferbuilder.addVertex(i, getY(), z).setColor(0, 0, 0, 255);
			bufferbuilder.addVertex(i, i2 + l1, z).setColor(128, 128, 128, 255);
			bufferbuilder.addVertex(j, i2 + l1, 0.0F).setColor(128, 128, 128, 255);
			bufferbuilder.addVertex(j, i2, z).setColor(128, 128, 128, 255);
			bufferbuilder.addVertex(i, i2, z).setColor(128, 128, 128, 255);
			bufferbuilder.addVertex(i, i2 + l1 - 1, z).setColor(192, 192, 192, 255);
			bufferbuilder.addVertex(j - 1, i2 + l1 - 1, z).setColor(192, 192, 192, 255);
			bufferbuilder.addVertex(j - 1, i2, z).setColor(192, 192, 192, 255);
			bufferbuilder.addVertex(i, i2, z).setColor(192, 192, 192, 255);
			BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
		}

		renderDecorations(guiGraphics, mouseX, mouseY);
		RenderSystem.disableBlend();
		guiGraphics.pose().popPose();
	}
}