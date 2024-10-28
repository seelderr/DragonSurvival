package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.components;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.ColorPickerButton;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

import static by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownList.BACKGROUND_TEXTURE;

public class BackgroundColorSelectorComponent extends AbstractContainerEventHandler implements Renderable {
    public final ExtendedButton colorPicker;
    private final DragonEditorScreen screen;
    private final int x;
    private final int y;
    private final int xSize;
    private final int ySize;
    public boolean visible;

    public BackgroundColorSelectorComponent(DragonEditorScreen screen, int x, int y, int xSize, int ySize) {
        this.screen = screen;
        this.x = x;
        this.y = y;
        this.xSize = xSize;
        this.ySize = ySize;

        Color defaultC = new Color(screen.backgroundColor);

        float f3 = (float) (screen.backgroundColor >> 24 & 255) / 255.0F;

        colorPicker = new ColorPickerButton(x + 3, y, xSize - 5, ySize, defaultC, c -> {
            Color c1 = new Color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, f3);
            screen.backgroundColor = c1.getRGB();
        });
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return visible && pMouseY >= (double) y - 3 && pMouseY <= (double) y + ySize + 3 && pMouseX >= (double) x && pMouseX <= (double) x + xSize;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return ImmutableList.of(colorPicker);
    }

    @Override
    public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        if (visible) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 100);
            guiGraphics.blitWithBorder(BACKGROUND_TEXTURE, x, y - 3, 0, 0, xSize, ySize + 6, 32, 32, 10, 10, 10, 10);
            guiGraphics.pose().translate(0, 0, 100);
            colorPicker.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
            guiGraphics.pose().popPose();
        }
    }
}