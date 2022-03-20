package by.dragonsurvivalteam.dragonsurvival.client.gui.components;

import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.ColorPickerButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.ExtendedCheckbox;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownList;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.DragonEditorHandler;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.objects.DragonEditorObject.Texture;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.objects.LayerSettings;
import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayerUtils;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.awt.Color;
import java.util.List;
import java.util.function.Supplier;

public class ColorSelectorComponent extends FocusableGui implements IRenderable{
	public boolean visible;

	private final ExtendedButton colorPicker;
	private final CheckboxButton glowing;

	private final DragonEditorScreen screen;
	private final int x;
	private final int y;
	private final int xSize;
	private final int ySize;
	private final Supplier<LayerSettings> settings;

	public ColorSelectorComponent(DragonEditorScreen screen, int x, int y, int xSize, int ySize, EnumSkinLayer layer){
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.xSize = xSize;
		this.ySize = ySize;

		settings = () -> screen.preset.skinAges.get(screen.level).layerSettings.get(layer);

		LayerSettings set = settings.get();
		Texture text = DragonEditorHandler.getSkin(FakeClientPlayerUtils.getFakePlayer(0, screen.handler), layer, set.selectedSkin, screen.handler.getType());

		glowing = new ExtendedCheckbox(x + 3, y, xSize - 5, 10, 10, new TranslationTextComponent("ds.gui.dragon_editor.glowing"), set.glowing, (s) -> {
			settings.get().glowing = s.selected();
			screen.handler.getSkin().updateLayers.add(layer);
		});

		Color defaultC = Color.decode(text.defaultColor);

		if(set.modifiedColor){
			defaultC = Color.getHSBColor(set.hue, set.saturation, set.brightness);
		}

		colorPicker = new ColorPickerButton(x + 3, y + 11, xSize - 5, ySize - 11, defaultC, (c) -> {
			float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);

			settings.get().hue = hsb[0];
			settings.get().saturation = hsb[1];
			settings.get().brightness = hsb[2];
			settings.get().modifiedColor = true;

			screen.handler.getSkin().updateLayers.add(layer);
			screen.update();
		});
	}

	@Override
	public boolean isMouseOver(double pMouseX, double pMouseY){
		return visible && pMouseY >= (double)this.y - 3 && pMouseY <= (double)this.y + ySize + 3 && pMouseX >= (double)this.x && pMouseX <= (double)this.x + xSize;
	}

	@Override
	public List<? extends IGuiEventListener> children(){
		return ImmutableList.of(colorPicker, glowing);
	}

	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks){
		Minecraft.getInstance().textureManager.bind(DropdownList.BACKGROUND_TEXTURE);
		GuiUtils.drawContinuousTexturedBox(pMatrixStack, x, y - 3, 0, 0, xSize, ySize + 6, 32, 32, 10, 10);
		colorPicker.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		glowing.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
	}
}