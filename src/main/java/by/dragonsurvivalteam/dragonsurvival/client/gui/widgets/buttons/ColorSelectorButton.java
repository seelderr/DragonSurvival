package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.components.ColorSelectorComponent;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.components.HueSelectorComponent;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.DragonEditorHandler;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.DragonEditorObject.DragonTextureMetadata;
import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayerUtils;
import by.dragonsurvivalteam.dragonsurvival.client.util.RenderingUtils;
import by.dragonsurvivalteam.dragonsurvival.mixins.client.ScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ColorSelectorButton extends ExtendedButton {
    public final EnumSkinLayer layer;

    private final DragonEditorScreen screen;
    private HueSelectorComponent hueComponent;
    private ColorSelectorComponent colorComponent;
    private Renderable renderButton;

    private final int xSize;
    private final int ySize;
    private boolean toggled;

    public ColorSelectorButton(DragonEditorScreen screen, EnumSkinLayer layer, int x, int y, int xSize, int ySize) {
        super(x, y, xSize, ySize, Component.empty(), action -> { /* Nothing to do */ });
        this.xSize = xSize;
        this.ySize = ySize;
        this.screen = screen;
        this.layer = layer;
        visible = true;
    }

    @Override
    public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float patialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, patialTick);
        active = !screen.preset.skinAges.get(screen.level).get().defaultSkin;

        if (visible) {
            RenderingUtils.drawGradientRect(guiGraphics.pose().last().pose(), 100, getX() + 2, getY() + 2, getX() + xSize - 2, getY() + ySize - 2, new int[]{Color.red.getRGB(), Color.GREEN.getRGB(), Color.BLUE.getRGB(), Color.yellow.getRGB()});
        }

        if (toggled && (!visible || !isMouseOver(mouseX, mouseY) && (hueComponent == null || !hueComponent.isMouseOver(mouseX, mouseY)) && (colorComponent == null || !colorComponent.isMouseOver(mouseX, mouseY)))) {
            toggled = false;
            Screen screen = Minecraft.getInstance().screen;
            screen.children().removeIf(s -> s == colorComponent);
            screen.children().removeIf(s -> s == hueComponent);
            screen.renderables.removeIf(s -> s == renderButton);
        }

        DragonTextureMetadata texture = DragonEditorHandler.getSkinTextureMetadata(FakeClientPlayerUtils.getFakePlayer(0, DragonEditorScreen.HANDLER), layer, screen.preset.skinAges.get(screen.level).get().layerSettings.get(layer).get().selectedSkin, DragonEditorScreen.HANDLER.getType());
        visible = texture != null && texture.colorable;
    }

    @Override
    public @NotNull Component getMessage() {
        return Component.empty();
    }

    @Override
    public void onPress() {
        if (!toggled) {
            DragonTextureMetadata texture = DragonEditorHandler.getSkinTextureMetadata(FakeClientPlayerUtils.getFakePlayer(0, DragonEditorScreen.HANDLER), layer, screen.preset.skinAges.get(screen.level).get().layerSettings.get(layer).get().selectedSkin, DragonEditorScreen.HANDLER.getType());

            renderButton = new ExtendedButton(0, 0, 0, 0, Component.empty(), action -> { /* Nothing to do */ }) {
                @Override
                public void renderWidget(@NotNull final GuiGraphics guiGraphics, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
                    active = visible = false;

                    if (hueComponent != null && texture.defaultColor == null) {
                        hueComponent.visible = ColorSelectorButton.this.visible;
                        if (hueComponent.visible)
                            hueComponent.render(guiGraphics, p_230430_2_, p_230430_3_, p_230430_4_);
                    }

                    if (colorComponent != null && texture.defaultColor != null) {
                        colorComponent.visible = ColorSelectorButton.this.visible;
                        if (colorComponent.visible)
                            colorComponent.render(guiGraphics, p_230430_2_, p_230430_3_, p_230430_4_);
                    }
                }
            };

            Screen screen = Minecraft.getInstance().screen;

            if (texture.defaultColor == null) {
                int offset = screen.height - (getY() + 80);
                hueComponent = new HueSelectorComponent(this.screen, getX() + xSize - 120, getY() + Math.min(offset, 0), 120, 90, layer);
                ((ScreenAccessor) screen).dragonSurvival$children().add(0, hueComponent);
                ((ScreenAccessor) screen).dragonSurvival$children().add(hueComponent);
            } else {
                int offset = screen.height - (getY() + 80);
                colorComponent = new ColorSelectorComponent(this.screen, getX() + xSize - 120, getY() + Math.min(offset, 0), 120, 90, layer);
                ((ScreenAccessor) screen).dragonSurvival$children().add(0, colorComponent);
                ((ScreenAccessor) screen).dragonSurvival$children().add(colorComponent);
            }
            screen.renderables.add(renderButton);
        } else {
            screen.children().removeIf(s -> s == colorComponent);
            screen.children().removeIf(s -> s == hueComponent);
            screen.renderables.removeIf(s -> s == renderButton);
        }

        toggled = !toggled;
    }
}