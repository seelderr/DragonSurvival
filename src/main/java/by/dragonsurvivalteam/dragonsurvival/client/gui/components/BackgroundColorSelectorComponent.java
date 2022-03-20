package by.dragonsurvivalteam.dragonsurvival.client.gui.components;

import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.ColorPickerButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownList;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.awt.Color;
import java.util.List;

public class BackgroundColorSelectorComponent extends FocusableGui implements IRenderable{
	public boolean visible;

	private final ExtendedButton colorPicker;

	private final DragonEditorScreen screen;
	private final int x;
	private final int y;
	private final int xSize;
	private final int ySize;

	public BackgroundColorSelectorComponent(DragonEditorScreen screen, int x, int y, int xSize, int ySize){
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.xSize = xSize;
		this.ySize = ySize;

		Color defaultC = new Color(screen.backgroundColor);

		float f3 = (float)(screen.backgroundColor >> 24 & 255) / 255.0F;

		colorPicker = new ColorPickerButton(x + 3, y, xSize - 5, ySize, defaultC, (c) -> {
			Color c1 = new Color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, f3);
			screen.backgroundColor = c1.getRGB();
		});
	}

	@Override
	public boolean isMouseOver(double pMouseX, double pMouseY){
		return visible && pMouseY >= (double)this.y - 3 && pMouseY <= (double)this.y + ySize + 3 && pMouseX >= (double)this.x && pMouseX <= (double)this.x + xSize;
	}

	@Override
	public List<? extends IGuiEventListener> children(){
		return ImmutableList.of(colorPicker);
	}

	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks){
		Minecraft.getInstance().textureManager.bind(DropdownList.BACKGROUND_TEXTURE);
		GuiUtils.drawContinuousTexturedBox(pMatrixStack, x, y - 3, 0, 0, xSize, ySize + 6, 32, 32, 10, 10);
		colorPicker.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
	}
}