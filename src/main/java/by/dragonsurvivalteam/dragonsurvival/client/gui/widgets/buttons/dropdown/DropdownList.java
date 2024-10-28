package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class DropdownList extends AbstractSelectionList<DropdownEntry> {
	public static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/textbox.png");
	private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("widget/scroller");
	private static final ResourceLocation SCROLLER_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("widget/scroller_background");
	public int listWidth;
	public boolean visible;

	public DropdownList(int x, int y, int xSize, int ySize, int itemHeight) {
		super(Minecraft.getInstance(), 0, 0, 0, itemHeight);
		listWidth = xSize;
		reposition(x, y, xSize, ySize);
	}

	public void reposition(int x, int y, int xSize, int ySize) {
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
	public int getScrollbarPosition() {
		// Either move the scroll bar within the item list box or extend the check for 'isMouseOver' (regarding width)
		return super.getScrollbarPosition() - 18;
	}

	@Override
	protected void renderListBackground(@NotNull final GuiGraphics guiGraphics) {
		// Renders the black background
		guiGraphics.blitWithBorder(BACKGROUND_TEXTURE, getX(), getY() - 3, 0, 0, width, height + 6, 32, 32, 10, 10, 10, 10);

		if (!children().isEmpty()) {
			RenderSystem.enableScissor((int) (getX() * Minecraft.getInstance().getWindow().getGuiScale()), (int) (Minecraft.getInstance().getWindow().getScreenHeight() - (getHeight() + getY() - 3) * Minecraft.getInstance().getWindow().getGuiScale()), (int) (width * Minecraft.getInstance().getWindow().getGuiScale()), (int) ((height - 6) * Minecraft.getInstance().getWindow().getGuiScale()));
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

		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(0, 0, 150);
		// Background square which contains all dragon editor part buttons
		renderListBackground(guiGraphics);
		renderListItems(guiGraphics, mouseX, mouseY, partialTicks);

		if (!children().isEmpty()) {
			RenderSystem.disableScissor();
		}

		// Render the scroll bar (see AbstractSelectionList#renderWidget)
		if (scrollbarVisible()) {
			int position = this.getScrollbarPosition();
			int scrollBarHeight = Mth.clamp((int) ((float) (this.height * this.height) / (float) this.getMaxPosition()), 32, this.height - 8);
			scrollBarHeight = Mth.clamp(scrollBarHeight, 32, this.height - 8);
			int scrollAmount = (int) this.getScrollAmount() * (this.height - scrollBarHeight) / this.getMaxScroll() + this.getY();

			if (scrollAmount < this.getY()) {
				scrollAmount = this.getY();
			}

			RenderSystem.enableBlend();
			guiGraphics.blitSprite(SCROLLER_BACKGROUND_SPRITE, position, this.getY(), 6, this.getHeight());
			guiGraphics.blitSprite(SCROLLER_SPRITE, position, scrollAmount, 6, scrollBarHeight);
			RenderSystem.disableBlend();
		}

		renderDecorations(guiGraphics, mouseX, mouseY);
//		guiGraphics.fill(getX(), getY(), getRight(), getBottom(), 0xFFA5A5A5);
		RenderSystem.disableBlend();
		guiGraphics.pose().popPose();
	}

//	@Override
//	public boolean isMouseOver(double pMouseX, double pMouseY) {
//		return pMouseY >= (double)this.getY()
//				&& pMouseY <= (double)this.getBottom()
//				&& pMouseX >= (double)this.getX()
//				&& pMouseX <= (double) this.getX();
//	}
}