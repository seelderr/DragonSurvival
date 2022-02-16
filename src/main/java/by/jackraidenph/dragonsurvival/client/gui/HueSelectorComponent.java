package by.jackraidenph.dragonsurvival.client.gui;

import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationLayer;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.ResetSettingsButton;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownList;
import by.jackraidenph.dragonsurvival.client.util.RenderingUtils;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.minecraftforge.fml.client.gui.widget.Slider;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;

public class HueSelectorComponent extends FocusableGui implements IRenderable
{
	public boolean visible;
	
	private Slider hueSlider;
	private Slider saturationSlider;
	private ExtendedButton hueReset, saturationReset;
	
	private DragonCustomizationScreen screen;
	private int x, y, xSize, ySize;
	
	public HueSelectorComponent(DragonCustomizationScreen screen, int x, int y, int xSize, int ySize, CustomizationLayer layer)
	{
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.xSize = xSize;
		this.ySize = ySize;
		
		hueReset = new ExtendedButton(x + 3 + xSize - 26, y, 20, 20, StringTextComponent.EMPTY, (s) ->{
			hueSlider.setValue(0.0);
			hueSlider.updateSlider();
		}){
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
			{
				super.renderButton(mStack, mouseX, mouseY, partial);
				Minecraft.getInstance().getTextureManager().bind(ResetSettingsButton.texture);
				blit(mStack, x + 2, y + 2, 0, 0, 16, 16, 16, 16);
			}
		};
		
		Color c = new Color(screen.hueMap.getOrDefault(screen.level, new HashMap<>()).getOrDefault(layer, 0));
		float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		
		hueSlider = new Slider(x + 3, y, xSize - 26, 20, StringTextComponent.EMPTY, StringTextComponent.EMPTY, -180, 180, Math.round(hsb[0] * 360 - 180), true, true, (val) -> {}, (val) -> {
			screen.hueMap.computeIfAbsent(screen.level, (s) -> new HashMap<>());
			float value = (hueSlider.getValueInt() + 180) / 360f;
			screen.hueMap.get(screen.level).put(layer, Color.HSBtoRGB(value, 1f, 1f));
			this.screen.handler.getSkin().hueChanged.add(layer);
			screen.update();
		}){
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
			{
				if (this.visible) {
					this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
					mStack.pushPose();
					RenderingUtils.renderPureColorSquare(mStack, x, y, width, height);
					mStack.popPose();
					
					if (!isMouseOver(mouseX, mouseY) && isDragging()) {
						mouseReleased(mouseX, mouseY, 0);
					}
					
					this.renderBg(mStack, Minecraft.getInstance(), mouseX, mouseY);
				}
			}
		};
		
		saturationSlider = new Slider(x + 3, y + 22, xSize - 26, 20, StringTextComponent.EMPTY, StringTextComponent.EMPTY, -179, 180, Math.round(hsb[1] * 360 - 180), true, true, (val) -> {}, (val) -> {
			screen.hueMap.computeIfAbsent(screen.level, (s) -> new HashMap<>());
			float value1 = (hueSlider.getValueInt() + 180) / 360f;
			float value = (saturationSlider.getValueInt() + 180) / 360f;
			screen.hueMap.get(screen.level).put(layer, Color.HSBtoRGB(value1, value, 1f));
			this.screen.handler.getSkin().hueChanged.add(layer);
			screen.update();
		}){
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
			{
				if (this.visible) {
					this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
					float value1 = (hueSlider.getValueInt() + 180) / 360f;
					
					mStack.pushPose();
					int col1 = Color.getHSBColor(value1, 0f, 1f).getRGB();
					int col2 = Color.getHSBColor(value1, 1f, 1f).getRGB();
					
					RenderingUtils.drawGradientRect(mStack.last().pose(), 100, x, y, x + width, y + height, new int[]{col2, col1, col1, col2});
					mStack.translate(0, 0, 100);
					this.renderBg(mStack, Minecraft.getInstance(), mouseX, mouseY);
					mStack.popPose();
					
					if (!isMouseOver(mouseX, mouseY) && isDragging()) {
						mouseReleased(mouseX, mouseY, 0);
					}
				}
			}
		};
		
		saturationReset = new ExtendedButton(x + 3 + xSize - 26, y + 22, 20, 20, StringTextComponent.EMPTY, (s) ->{
			saturationSlider.setValue(180.0);
			saturationSlider.updateSlider();
		}){
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
			{
				mStack.pushPose();
				super.renderButton(mStack, mouseX, mouseY, partial);
				Minecraft.getInstance().getTextureManager().bind(ResetSettingsButton.texture);
				blit(mStack, x + 2, y + 2, 0, 0, 16, 16, 16, 16);
				mStack.popPose();
			}
		};
	}
	
	@Override
	public boolean isMouseOver(double pMouseX, double pMouseY)
	{
		return visible && pMouseY >= (double)this.y-3 && pMouseY <= (double)this.y + ySize+3 && pMouseX >= (double)this.x && pMouseX <= (double)this.x + xSize;
	}
	
	@Override
	public List<? extends IGuiEventListener> children()
	{
		return ImmutableList.of(hueSlider, saturationSlider, hueReset, saturationReset);
	}
	
	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
	{
		Minecraft.getInstance().textureManager.bind(DropdownList.BACKGROUND_TEXTURE);
		GuiUtils.drawContinuousTexturedBox(pMatrixStack, x, y - 3, 0, 0, xSize, ySize + 6, 32, 32, 10, 10);
		
		hueReset.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		saturationReset.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		
		hueSlider.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		saturationSlider.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
	}
}
