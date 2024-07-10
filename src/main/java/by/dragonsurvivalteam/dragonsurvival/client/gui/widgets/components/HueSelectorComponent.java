package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.components;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownList;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.ExtendedCheckbox;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.DragonEditorHandler;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.DragonEditorObject;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.LayerSettings;
import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayerUtils;
import by.dragonsurvivalteam.dragonsurvival.client.util.RenderingUtils;
import com.google.common.collect.ImmutableList;
import java.awt.*;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import org.jetbrains.annotations.NotNull;

public class HueSelectorComponent extends AbstractContainerEventHandler implements Renderable {
	public static final ResourceLocation resetSettingsTexture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/reset_icon.png");
	private final ExtendedButton hueReset;
	private final ExtendedButton saturationReset;
	private final ExtendedButton brightnessReset;
	private final ExtendedCheckbox glowing;
    private final int x;
	private final int y;
	private final int xSize;
	private final int ySize;
	private final Supplier<LayerSettings> settings;
	public boolean visible;
	private final ExtendedSlider hueSlider;
	private final ExtendedSlider saturationSlider;
	private final ExtendedSlider brightnessSlider;

	public HueSelectorComponent(DragonEditorScreen screen, int x, int y, int xSize, int ySize, EnumSkinLayer layer){
        this.x = x;
		this.y = y;
		this.xSize = xSize;
		this.ySize = ySize;

		settings = () -> screen.preset.skinAges.get(screen.level).get().layerSettings.get(layer).get();
		LayerSettings set = settings.get();
		DragonEditorObject.Texture text = DragonEditorHandler.getSkin(FakeClientPlayerUtils.getFakePlayer(0, DragonEditorScreen.HANDLER), layer, set.selectedSkin, DragonEditorScreen.HANDLER.getType());

		glowing = new ExtendedCheckbox(x + 3, y, 20, 20, 20, Component.translatable("ds.gui.dragon_editor.glowing"), set.glowing, box -> {
			settings.get().glowing = !settings.get().glowing;
			box.selected = settings.get().glowing;
			DragonEditorScreen.HANDLER.getSkinData().compileSkin();
		});

		float[] hsb = new float[]{set.hue, set.saturation, set.brightness};

		if(text == null){
			hsb[0] = 0.5f;
			hsb[1] = 0.5f;
			hsb[2] = 0.5f;
		} else if (!set.modifiedColor) {
			hsb[0] = text.average_hue;
			hsb[1] = 0.5f;
			hsb[2] = 0.5f;
		}

		hueSlider = new ExtendedSlider(x + 3, y + 24, xSize - 26, 20, Component.empty(), Component.empty(), 0, 360, hsb[0] * 360.0f, true){
			@Override
			protected void applyValue(){
				super.applyValue();

				float value = (hueSlider.getValueInt()) / 360f;
				float value1 = (saturationSlider.getValueInt()) / 360f;
				float value2 = (brightnessSlider.getValueInt()) / 360f;

				settings.get().hue = value;
				settings.get().saturation = value1;
				settings.get().brightness = value2;
                settings.get().modifiedColor = text != null && (Float.compare(Math.round(value * 360), Math.round(text.average_hue * 360)) != 0 || !(Math.abs(value1 - 0.5f) < 0.05) || !(Math.abs(value2 - 0.5f) < 0.05));

				DragonEditorScreen.HANDLER.getSkinData().compileSkin();
				screen.update();
			}

			@Override
			public void setValue(double value) {
				super.setValue(value);
				this.applyValue();
			}

			@Override
			public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partial){
				RenderingUtils.renderPureColorSquare(guiGraphics.pose(), getX(), getY(), getWidth(), getHeight());
				guiGraphics.blitSprite(this.getSprite(), this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 8, this.height);
			}
		};

		hueReset = new ExtendedButton(x + 3 + xSize - 26, y + 24, 20, 20, Component.empty(), button -> hueSlider.setValue(text != null ? Math.round(text.average_hue * 360f) : 180)) {
			@Override
			public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partial){
				super.renderWidget(guiGraphics, mouseX, mouseY, partial);
				guiGraphics.blit(resetSettingsTexture, getX() + 2, getY() + 2, 0, 0, 16, 16, 16, 16);
			}
		};

		saturationSlider = new ExtendedSlider(x + 3, y + 22 + 24, xSize - 26, 20, Component.empty(), Component.empty(), 0, 360, hsb[1] * 360, true){
			@Override
			protected void applyValue(){
				super.applyValue();

				float value = (hueSlider.getValueInt()) / 360f;
				float value1 = (saturationSlider.getValueInt()) / 360f;
				float value2 = (brightnessSlider.getValueInt()) / 360f;

				settings.get().hue = value;
				settings.get().saturation = value1;
				settings.get().brightness = value2;

				settings.get().modifiedColor = text != null && (Float.compare(Math.round(value * 360), Math.round(text.average_hue * 360)) != 0 || !(Math.abs(value1 - 0.5f) < 0.05) || !(Math.abs(value2 - 0.5f) < 0.05));

				DragonEditorScreen.HANDLER.getSkinData().compileSkin();
				screen.update();
			}

			@Override
			public void setValue(double value) {
				super.setValue(value);
				this.applyValue();
			}

			@Override
			public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partial){
				if(visible){
					this.isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + getWidth() && mouseY < getY() + getHeight();
					float value1 = (hueSlider.getValueInt()) / 360f;

					int col1 = Color.getHSBColor(value1, 0f, 1f).getRGB();
					int col2 = Color.getHSBColor(value1, 1f, 1f).getRGB();

					RenderingUtils.drawGradientRect(guiGraphics.pose().last().pose(), 0, getX(), getY(), getX() + getWidth(), getY() + getHeight(), new int[]{col2, col1, col1, col2});
					guiGraphics.blitSprite(this.getSprite(), this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 8, this.height);
				}
			}
		};

		saturationReset = new ExtendedButton(x + 3 + xSize - 26, y + 22 + 24, 20, 20, Component.empty(), button -> saturationSlider.setValue(180)) {
			@Override
			public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partial){
				super.renderWidget(guiGraphics, mouseX, mouseY, partial);
				guiGraphics.blit(resetSettingsTexture, getX() + 2, getY() + 2, 0, 0, 16, 16, 16, 16);
			}
		};

		brightnessSlider = new ExtendedSlider(x + 3, y + 44 + 24, xSize - 26, 20, Component.empty(), Component.empty(), 0, 360, hsb[2] * 360, true){
			@Override
			protected void applyValue(){
				super.applyValue();

				float value = (hueSlider.getValueInt()) / 360f;
				float value1 = (saturationSlider.getValueInt()) / 360f;
				float value2 = (brightnessSlider.getValueInt()) / 360f;

				settings.get().hue = value;
				settings.get().saturation = value1;
				settings.get().brightness = value2;
				settings.get().modifiedColor = text != null && (Float.compare(Math.round(value * 360), Math.round(text.average_hue * 360)) != 0 || !(Math.abs(value1 - 0.5f) < 0.05) || !(Math.abs(value2 - 0.5f) < 0.05));

				DragonEditorScreen.HANDLER.getSkinData().compileSkin();
				screen.update();
			}

			@Override
			public void setValue(double value) {
				super.setValue(value);
				this.applyValue();
			}

			@Override
			public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partial){
				if(visible){
					this.isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + getWidth() && mouseY < getY() + getHeight();
					float value1 = (hueSlider.getValueInt()) / 360f;

					int col1 = Color.getHSBColor(value1, 1f, 0f).getRGB();
					int col2 = Color.getHSBColor(value1, 1f, 1f).getRGB();

					RenderingUtils.drawGradientRect(guiGraphics.pose().last().pose(), 0, getX(), getY(), getX() + getWidth(), getY() + getHeight(), new int[]{col2, col1, col1, col2});
					guiGraphics.blitSprite(this.getSprite(), this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 8, this.height);
				}
			}
		};

		brightnessReset = new ExtendedButton(x + 3 + xSize - 26, y + 44 + 24, 20, 20, Component.empty(), button -> brightnessSlider.setValue(180)) {
			@Override
			public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partial){
				super.renderWidget(guiGraphics, mouseX, mouseY, partial);
				guiGraphics.blit(resetSettingsTexture, getX() + 2, getY() + 2, 0, 0, 16, 16, 16, 16);
			}
		};
	}

	@Override
	public boolean isMouseOver(double pMouseX, double pMouseY){
		return visible && pMouseY >= (double) y - 3 && pMouseY <= (double)y + ySize + 3 && pMouseX >= (double)x && pMouseX <= (double)x + xSize;
	}

	@Override
	public @NotNull List<? extends GuiEventListener> children() {
		return ImmutableList.of(hueSlider, saturationSlider, brightnessSlider, hueReset, saturationReset, brightnessReset, glowing);
	}

	@Override
	public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks){
		guiGraphics.pose().pushPose();
		// Render pop-up menu content above the other elements
		guiGraphics.pose().translate(0, 0, 150);
		guiGraphics.blitWithBorder(DropdownList.BACKGROUND_TEXTURE, x, y - 3, 0, 0, xSize, ySize + 6, 32, 32, 10, 10, 10, 10);

		glowing.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);

		hueReset.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
		saturationReset.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
		brightnessReset.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);

		hueSlider.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
		saturationSlider.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
		brightnessSlider.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
		guiGraphics.pose().popPose();
	}
}