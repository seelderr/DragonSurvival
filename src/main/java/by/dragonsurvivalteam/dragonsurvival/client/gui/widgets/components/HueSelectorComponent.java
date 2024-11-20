package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.components;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownList;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.ExtendedCheckbox;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.DragonEditorHandler;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.DragonPart;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.LayerSettings;
import by.dragonsurvivalteam.dragonsurvival.client.util.RenderingUtils;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.lang.LangKey;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class HueSelectorComponent extends AbstractContainerEventHandler implements Renderable {
    private static final ResourceLocation RESET_SETTINGS_TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/reset_icon.png");

    public boolean visible;

    private final ExtendedButton hueReset;
    private final ExtendedButton saturationReset;
    private final ExtendedButton brightnessReset;
    private final ExtendedCheckbox glowing;
    private final ExtendedSlider hueSlider;
    private final ExtendedSlider saturationSlider;
    private final ExtendedSlider brightnessSlider;
    private final Supplier<LayerSettings> settingsSupplier;

    private final int x;
    private final int y;
    private final int xSize;
    private final int ySize;

    private boolean hasModifiedColor(DragonPart dragonPart) {
        return dragonPart != null && (Float.compare(Math.round(settingsSupplier.get().hue * 360), Math.round(dragonPart.averageHue() * 360)) != 0 || !(Math.abs(settingsSupplier.get().saturation - 0.5f) < 0.05) || !(Math.abs(settingsSupplier.get().brightness - 0.5f) < 0.05));
    }

    public HueSelectorComponent(DragonEditorScreen screen, int x, int y, int xSize, int ySize, EnumSkinLayer layer) {
        this.x = x;
        this.y = y;
        this.xSize = xSize;
        this.ySize = ySize;

        settingsSupplier = () -> screen.preset.get(Objects.requireNonNull(screen.dragonLevel.getKey())).get().layerSettings.get(layer).get();
        LayerSettings settings = settingsSupplier.get();
        DragonPart dragonPart = DragonEditorHandler.getDragonPart(layer, settings.selectedSkin, DragonEditorScreen.HANDLER.getType());

        glowing = new ExtendedCheckbox(x + 3, y, 20, 20, 20, Component.translatable(LangKey.GUI_GLOWING), settings.glowing, action -> { /* Nothing to do */ }) {
            final Function<Boolean, Boolean> setGlowingAction = value -> {
                settingsSupplier.get().glowing = value;
                this.selected = settingsSupplier.get().glowing;
                DragonEditorScreen.HANDLER.getSkinData().compileSkin(screen.dragonLevel);
                screen.update();
                return !value;
            };

            @Override
            public void onPress() {
                screen.actionHistory.add(new DragonEditorScreen.EditorAction<>(setGlowingAction, !settingsSupplier.get().glowing));
            }
        };

        float[] hsb = new float[]{settings.hue, settings.saturation, settings.brightness};

        if (dragonPart == null) {
            hsb[0] = 0.5f;
            hsb[1] = 0.5f;
            hsb[2] = 0.5f;
        } else if (!settings.modifiedColor) {
            hsb[0] = dragonPart.averageHue();
            hsb[1] = 0.5f;
            hsb[2] = 0.5f;
        }

        hueSlider = new ExtendedSlider(x + 3, y + 24, xSize - 26, 20, Component.empty(), Component.empty(), 0, 360, hsb[0] * 360.0f, true) {
            private int previousHue = 0;

            private final Function<Integer, Integer> setHueAction = value -> {
                settingsSupplier.get().hue = value / 360f;
                settingsSupplier.get().modifiedColor = hasModifiedColor(dragonPart);
                DragonEditorScreen.HANDLER.getSkinData().compileSkin(screen.dragonLevel);
                screen.update();

                return previousHue;
            };

            @Override
            protected void applyValue() {
                super.applyValue();

                setHueAction.apply(this.getValueInt());
            }

            @Override
            public void setValue(double value) {
                super.setValue(value);
                this.applyValue();
            }

            @Override
            public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
                RenderingUtils.renderPureColorSquare(guiGraphics.pose(), getX(), getY(), getWidth(), getHeight());
                guiGraphics.blitSprite(this.getSprite(), this.getX() + (int) (this.value * (double) (this.width - 8)), this.getY(), 8, this.height);
            }

            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);
                previousHue = this.getValueInt();
            }

            @Override
            public void onRelease(double mouseX, double mouseY) {
                super.onRelease(mouseX, mouseY);
                screen.actionHistory.add(new DragonEditorScreen.EditorAction<>(setHueAction, this.getValueInt()));
            }
        };

        hueReset = new ExtendedButton(x + 3 + xSize - 26, y + 24, 20, 20, Component.empty(), button -> hueSlider.setValue(dragonPart != null ? Math.round(dragonPart.averageHue() * 360f) : 180)) {
            @Override
            public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
                super.renderWidget(guiGraphics, mouseX, mouseY, partial);
                guiGraphics.blit(RESET_SETTINGS_TEXTURE, getX() + 2, getY() + 2, 0, 0, 16, 16, 16, 16);
            }
        };

        saturationSlider = new ExtendedSlider(x + 3, y + 22 + 24, xSize - 26, 20, Component.empty(), Component.empty(), 0, 360, hsb[1] * 360, true) {
            private int previousSaturation = 0;

            private final Function<Integer, Integer> setSaturationAction = value -> {
                settingsSupplier.get().saturation = value / 360f;
                settingsSupplier.get().modifiedColor = hasModifiedColor(dragonPart);
                DragonEditorScreen.HANDLER.getSkinData().compileSkin(screen.dragonLevel);
                screen.update();

                return previousSaturation;
            };

            @Override
            protected void applyValue() {
                super.applyValue();

                setSaturationAction.apply(this.getValueInt());
            }

            @Override
            public void setValue(double value) {
                super.setValue(value);
                this.applyValue();
            }

            @Override
            public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
                if (visible) {
                    this.isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + getWidth() && mouseY < getY() + getHeight();
                    float value1 = (hueSlider.getValueInt()) / 360f;

                    int col1 = Color.getHSBColor(value1, 0f, 1f).getRGB();
                    int col2 = Color.getHSBColor(value1, 1f, 1f).getRGB();

                    RenderingUtils.drawGradientRect(guiGraphics.pose().last().pose(), 0, getX(), getY(), getX() + getWidth(), getY() + getHeight(), new int[]{col2, col1, col1, col2});
                    guiGraphics.blitSprite(this.getSprite(), this.getX() + (int) (this.value * (double) (this.width - 8)), this.getY(), 8, this.height);
                }
            }

            @Override
            public void onRelease(double mouseX, double mouseY) {
                super.onRelease(mouseX, mouseY);
                screen.actionHistory.add(new DragonEditorScreen.EditorAction<>(setSaturationAction, this.getValueInt()));
            }

            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);
                previousSaturation = this.getValueInt();
            }
        };

        saturationReset = new ExtendedButton(x + 3 + xSize - 26, y + 22 + 24, 20, 20, Component.empty(), button -> saturationSlider.setValue(180)) {
            @Override
            public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
                super.renderWidget(guiGraphics, mouseX, mouseY, partial);
                guiGraphics.blit(RESET_SETTINGS_TEXTURE, getX() + 2, getY() + 2, 0, 0, 16, 16, 16, 16);
            }
        };


        brightnessSlider = new ExtendedSlider(x + 3, y + 44 + 24, xSize - 26, 20, Component.empty(), Component.empty(), 0, 360, hsb[2] * 360, true) {
            private int previousBrightness = 0;

            private final Function<Integer, Integer> setBrightnessAction = value -> {
                settingsSupplier.get().brightness = value / 360f;

                DragonEditorScreen.HANDLER.getSkinData().compileSkin(screen.dragonLevel);
                screen.update();

                return previousBrightness;
            };

            @Override
            protected void applyValue() {
                super.applyValue();

                setBrightnessAction.apply(this.getValueInt());
            }

            @Override
            public void setValue(double value) {
                super.setValue(value);
                this.applyValue();
            }

            @Override
            public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
                if (visible) {
                    this.isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + getWidth() && mouseY < getY() + getHeight();
                    float value1 = (hueSlider.getValueInt()) / 360f;

                    int col1 = Color.getHSBColor(value1, 1f, 0f).getRGB();
                    int col2 = Color.getHSBColor(value1, 1f, 1f).getRGB();

                    RenderingUtils.drawGradientRect(guiGraphics.pose().last().pose(), 0, getX(), getY(), getX() + getWidth(), getY() + getHeight(), new int[]{col2, col1, col1, col2});
                    guiGraphics.blitSprite(this.getSprite(), this.getX() + (int) (this.value * (double) (this.width - 8)), this.getY(), 8, this.height);
                }
            }

            @Override
            public void onRelease(double mouseX, double mouseY) {
                super.onRelease(mouseX, mouseY);
                screen.actionHistory.add(new DragonEditorScreen.EditorAction<>(setBrightnessAction, this.getValueInt()));
            }

            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);
                previousBrightness = this.getValueInt();
            }
        };

        brightnessReset = new ExtendedButton(x + 3 + xSize - 26, y + 44 + 24, 20, 20, Component.empty(), button -> brightnessSlider.setValue(180)) {
            @Override
            public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
                super.renderWidget(guiGraphics, mouseX, mouseY, partial);
                guiGraphics.blit(RESET_SETTINGS_TEXTURE, getX() + 2, getY() + 2, 0, 0, 16, 16, 16, 16);
            }
        };
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return visible && pMouseY >= (double) y - 3 && pMouseY <= (double) y + ySize + 3 && pMouseX >= (double) x && pMouseX <= (double) x + xSize;
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return ImmutableList.of(hueSlider, saturationSlider, brightnessSlider, hueReset, saturationReset, brightnessReset, glowing);
    }

    @Override
    public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
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