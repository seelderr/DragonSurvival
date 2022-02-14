package by.jackraidenph.dragonsurvival.client.gui;

import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationLayer;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.ResetSettingsButton;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownList;
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

import java.util.HashMap;
import java.util.List;

public class HueSelectorComponent extends FocusableGui implements IRenderable
{
	public boolean visible;
	
	private Slider slider;
	private ExtendedButton reset;
	
	private DragonCustomizationScreen screen;
	private int x, y, xSize, ySize;
	
	public HueSelectorComponent(DragonCustomizationScreen screen, int x, int y, int xSize, int ySize, CustomizationLayer layer)
	{
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.xSize = xSize;
		this.ySize = ySize;
		
		reset = new ExtendedButton(x + 3 + xSize - 26, y, 20, 20, StringTextComponent.EMPTY, (s) ->{
			slider.setValue(0.0);
			slider.updateSlider();
			screen.hueMap.get(screen.level).put(layer, 0.0);
			screen.handler.getSkin().hueChanged.add(layer);
		}){
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
			{
				super.renderButton(mStack, mouseX, mouseY, partial);
				Minecraft.getInstance().getTextureManager().bind(ResetSettingsButton.texture);
				blit(mStack, x + 2, y + 2, 0, 0, 16, 16, 16, 16);
			}
		};
		
		slider = new Slider(x + 3, y, xSize - 26, 20, new StringTextComponent("Hue: "), StringTextComponent.EMPTY, -180, 180, Math.round(screen.hueMap.getOrDefault(screen.level, new HashMap<>()).getOrDefault(layer, 0.0)), true, true, (val) -> {}, (val) -> {
			screen.hueMap.computeIfAbsent(screen.level, (s) -> new HashMap<>());
			screen.hueMap.get(screen.level).put(layer, val.getValue());
			this.screen.handler.getSkin().hueChanged.add(layer);
			screen.update();
		}){
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
			{
				super.renderButton(mStack, mouseX, mouseY, partial);

				if(!isMouseOver(mouseX, mouseY) && isDragging()){
					setDragging(false);
				}
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
		return ImmutableList.of(slider, reset);
	}
	
	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
	{
		Minecraft.getInstance().textureManager.bind(DropdownList.BACKGROUND_TEXTURE);
		GuiUtils.drawContinuousTexturedBox(pMatrixStack, x, y - 3, 0, 0, xSize, ySize + 6, 32, 32, 10, 10);
		
		slider.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		reset.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
	}
}
