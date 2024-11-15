package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class ClientDietComponent implements ClientTooltipComponent {
    private final DietComponent component;
    private final Component tooltip;

    public ClientDietComponent(final DietComponent component) {
        this.component = component;
        this.tooltip = Component.translatable(component.item().getDescriptionId()).append(": ").append(ToolTipHandler.getFoodTooltipData(component.item(), component.type()));
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public int getWidth(@NotNull final Font font) {
        return /* Icon */ 20 + font.width(tooltip);
    }

    @Override
    public void renderImage(@NotNull final Font font, int x, int y, @NotNull final GuiGraphics graphics) {
        graphics.renderFakeItem(component.item().getDefaultInstance(), x, y); // TODO :: unsure if we should the item stack or re-create it
    }

    @Override
    public void renderText(@NotNull final Font font, int mouseX, int mouseY, @NotNull final Matrix4f matrix, @NotNull final MultiBufferSource.BufferSource bufferSource) {
        // X offset to render the text to the right of the item
        // Y offset to align the height of the text with the center of the item
        font.drawInBatch(tooltip, mouseX + 20, mouseY + 4, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
    }
}
