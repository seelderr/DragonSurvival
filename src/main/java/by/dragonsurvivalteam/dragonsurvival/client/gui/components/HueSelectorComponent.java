package by.dragonsurvivalteam.dragonsurvival.client.gui.components;

import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.ExtendedCheckbox;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.ResetSettingsButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownList;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.objects.LayerSettings;
import by.dragonsurvivalteam.dragonsurvival.client.util.RenderingUtils;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.minecraftforge.fml.client.gui.widget.Slider;

import java.awt.Color;
import java.util.List;
import java.util.function.Supplier;

public class HueSelectorComponent extends FocusableGui implements IRenderable{
	public boolean visible;

	private Slider hueSlider;
	private Slider saturationSlider;
	private Slider brightnessSlider;
	private final ExtendedButton hueReset;
	private final ExtendedButton saturationReset;
	private final ExtendedButton brightnessReset;
	private final CheckboxButton glowing;

	private final DragonEditorScreen screen;
	private final int x;
	private final int y;
	private final int xSize;
	private final int ySize;

	private final Supplier<LayerSettings> settings;

	public HueSelectorComponent(DragonEditorScreen screen, int x, int y, int xSize, int ySize, EnumSkinLayer layer){
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.xSize = xSize;
		this.ySize = ySize;

		settings = () -> screen.preset.skinAges.get(screen.level).layerSettings.get(layer);
		LayerSettings set = settings.get();

		glowing = new ExtendedCheckbox(x + 3, y, xSize - 5, 10, 10, new TranslationTextComponent("ds.gui.dragon_editor.glowing"), set.glowing, (s) -> {
			settings.get().glowing = s.selected();
			screen.handler.getSkin().updateLayers.add(layer);
		});

		hueReset = new ExtendedButton(x + 3 + xSize - 26, y + 12, 20, 20, StringTextComponent.EMPTY, (s) -> {
			hueSlider.setValue(0.0);
			hueSlider.updateSlider();
		}){
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial){
				super.renderButton(mStack, mouseX, mouseY, partial);
				Minecraft.getInstance().getTextureManager().bind(ResetSettingsButton.texture);
				blit(mStack, x + 2, y + 2, 0, 0, 16, 16, 16, 16);
			}
		};

		float[] hsb = new float[]{set.hue, set.saturation, set.brightness};

		if(!set.modifiedColor){
			hsb[0] = 0;
			hsb[1] = 0.5f;
		}

		hueSlider = new Slider(x + 3, y + 12, xSize - 26, 20, StringTextComponent.EMPTY, StringTextComponent.EMPTY, -180, 180, set.modifiedColor ? Math.round(hsb[0] * 360 - 180) : 0, true, true, (val) -> {}, (val) -> {
			float value = (hueSlider.getValueInt() + 180) / 360f;
			float value1 = (saturationSlider.getValueInt() + 180) / 360f;
			float value2 = (brightnessSlider.getValueInt() + 180) / 360f;

			settings.get().hue = value;
			settings.get().saturation = value1;
			settings.get().brightness = value2;
			settings.get().modifiedColor = true;

			screen.handler.getSkin().updateLayers.add(layer);
			screen.update();
		}){
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial){
				if(this.visible){
					this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
					mStack.pushPose();
					RenderingUtils.renderPureColorSquare(mStack, x, y, width, height);
					mStack.popPose();

					if(!isMouseOver(mouseX, mouseY) && isDragging()){
						mouseReleased(mouseX, mouseY, 0);
					}

					this.renderBg(mStack, Minecraft.getInstance(), mouseX, mouseY);
				}
			}
		};

		saturationSlider = new Slider(x + 3, y + 22 + 12, xSize - 26, 20, StringTextComponent.EMPTY, StringTextComponent.EMPTY, -180, 180, set.modifiedColor ? Math.round(hsb[1] * 360 - 180) : 0, true, true, (val) -> {}, (val) -> {
			float value = (hueSlider.getValueInt() + 180) / 360f;
			float value1 = (saturationSlider.getValueInt() + 180) / 360f;
			float value2 = (brightnessSlider.getValueInt() + 180) / 360f;

			settings.get().hue = value;
			settings.get().saturation = value1;
			settings.get().brightness = value2;
			settings.get().modifiedColor = true;

			screen.handler.getSkin().updateLayers.add(layer);
			screen.update();
		}){
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial){
				if(this.visible){
					this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
					float value1 = (hueSlider.getValueInt() + 180) / 360f;

					mStack.pushPose();
					int col1 = Color.getHSBColor(value1, 0f, 1f).getRGB();
					int col2 = Color.getHSBColor(value1, 1f, 1f).getRGB();

					RenderingUtils.drawGradientRect(mStack.last().pose(), 200, x, y, x + width, y + height, new int[]{col2, col1, col1, col2});
					mStack.translate(0, 0, 200);
					this.renderBg(mStack, Minecraft.getInstance(), mouseX, mouseY);
					mStack.popPose();

					if(!isMouseOver(mouseX, mouseY) && isDragging()){
						mouseReleased(mouseX, mouseY, 0);
					}
				}
			}
		};

		saturationReset = new ExtendedButton(x + 3 + xSize - 26, y + 22 + 12, 20, 20, StringTextComponent.EMPTY, (s) -> {
			saturationSlider.setValue(0.0);
			saturationSlider.updateSlider();
		}){
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial){
				mStack.pushPose();
				super.renderButton(mStack, mouseX, mouseY, partial);
				Minecraft.getInstance().getTextureManager().bind(ResetSettingsButton.texture);
				blit(mStack, x + 2, y + 2, 0, 0, 16, 16, 16, 16);
				mStack.popPose();
			}
		};

		brightnessReset = new ExtendedButton(x + 3 + xSize - 26, y + 44 + 12, 20, 20, StringTextComponent.EMPTY, (s) -> {
			brightnessSlider.setValue(0.0);
			brightnessSlider.updateSlider();
		}){
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial){
				mStack.pushPose();
				super.renderButton(mStack, mouseX, mouseY, partial);
				Minecraft.getInstance().getTextureManager().bind(ResetSettingsButton.texture);
				blit(mStack, x + 2, y + 2, 0, 0, 16, 16, 16, 16);
				mStack.popPose();
			}
		};

		brightnessSlider = new Slider(x + 3, y + 44 + 12, xSize - 26, 20, StringTextComponent.EMPTY, StringTextComponent.EMPTY, -180, 180, set.modifiedColor ? Math.round(hsb[2] * 360 - 180) : 0, true, true, (val) -> {}, (val) -> {
			float value = (hueSlider.getValueInt() + 180) / 360f;
			float value1 = (saturationSlider.getValueInt() + 180) / 360f;
			float value2 = (brightnessSlider.getValueInt() + 180) / 360f;

			settings.get().hue = value;
			settings.get().saturation = value1;
			settings.get().brightness = value2;
			settings.get().modifiedColor = true;

			screen.handler.getSkin().updateLayers.add(layer);
			screen.update();
		}){
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial){
				if(this.visible){
					this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
					float value1 = (hueSlider.getValueInt() + 180) / 360f;

					mStack.pushPose();
					int col1 = Color.getHSBColor(value1, 1f, 0f).getRGB();
					int col2 = Color.getHSBColor(value1, 1f, 1f).getRGB();

					RenderingUtils.drawGradientRect(mStack.last().pose(), 200, x, y, x + width, y + height, new int[]{col2, col1, col1, col2});
					mStack.translate(0, 0, 200);
					this.renderBg(mStack, Minecraft.getInstance(), mouseX, mouseY);
					mStack.popPose();

					if(!isMouseOver(mouseX, mouseY) && isDragging()){
						mouseReleased(mouseX, mouseY, 0);
					}
				}
			}
		};
	}

	@Override
	public boolean isMouseOver(double pMouseX, double pMouseY){
		return visible && pMouseY >= (double)this.y - 3 && pMouseY <= (double)this.y + ySize + 3 && pMouseX >= (double)this.x && pMouseX <= (double)this.x + xSize;
	}

	@Override
	public List<? extends IGuiEventListener> children(){
		return ImmutableList.of(hueSlider, saturationSlider, brightnessSlider, hueReset, saturationReset, brightnessReset, glowing);
	}

	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks){
		Minecraft.getInstance().textureManager.bind(DropdownList.BACKGROUND_TEXTURE);
		GuiUtils.drawContinuousTexturedBox(pMatrixStack, x, y - 3, 0, 0, xSize, ySize + 6, 32, 32, 10, 10);

		glowing.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);

		hueReset.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		saturationReset.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		brightnessReset.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);

		hueSlider.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		saturationSlider.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		brightnessSlider.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
	}
}