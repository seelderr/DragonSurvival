package by.jackraidenph.dragonsurvival.client.gui;

import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationLayer;
import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationObject.Texture;
import by.jackraidenph.dragonsurvival.client.SkinCustomization.DragonCustomizationHandler;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.ColorPickerButton;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownList;
import by.jackraidenph.dragonsurvival.client.util.FakeClientPlayerUtils;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;

public class ColorSelectorComponent extends FocusableGui implements IRenderable
{
	public boolean visible;
	
	private ExtendedButton colorPicker;
	
	private DragonCustomizationScreen screen;
	private int x, y, xSize, ySize;
	
	public ColorSelectorComponent(DragonCustomizationScreen screen, int x, int y, int xSize, int ySize, CustomizationLayer layer)
	{
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.xSize = xSize;
		this.ySize = ySize;
		Texture text = DragonCustomizationHandler.getSkin(FakeClientPlayerUtils.getFakePlayer(0, screen.handler), layer, screen.map.getOrDefault(screen.handler.getLevel(), new HashMap<>()).getOrDefault(layer, null), screen.handler.getType());
		
		Color defaultC = Color.decode(text.defaultColor);
		
		if(screen.hueMap.containsKey(screen.level)){
			if(screen.hueMap.get(screen.level).containsKey(layer)){
				defaultC = new Color(screen.hueMap.get(screen.level).get(layer));
			}
		}
		
		colorPicker = new ColorPickerButton(x + 3, y, xSize - 5, ySize, defaultC, (c) -> {
			screen.hueMap.computeIfAbsent(screen.level, (s) -> new HashMap<>());
			screen.hueMap.get(screen.level).put(layer, c.getRGB());
			this.screen.handler.getSkin().hueChanged.add(layer);
			screen.update();
		});
	}
	
	@Override
	public boolean isMouseOver(double pMouseX, double pMouseY)
	{
		return visible && pMouseY >= (double)this.y-3 && pMouseY <= (double)this.y + ySize+3 && pMouseX >= (double)this.x && pMouseX <= (double)this.x + xSize;
	}
	
	@Override
	public List<? extends IGuiEventListener> children()
	{
		return ImmutableList.of(colorPicker);
	}
	
	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
	{
		Minecraft.getInstance().textureManager.bind(DropdownList.BACKGROUND_TEXTURE);
		GuiUtils.drawContinuousTexturedBox(pMatrixStack, x, y - 3, 0, 0, xSize, ySize + 6, 32, 32, 10, 10);
		colorPicker.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
	}
}
