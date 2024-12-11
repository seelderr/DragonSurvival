package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

public class ClickHoverButton extends ExtendedButton {
    private boolean isClicking;
    private final ResourceLocation click;
    private final ResourceLocation hover;
    private final ResourceLocation main;
    private final int uOffset;
    private final int vOffset;
    private final int textureWidth;
    private final int textureHeight;

    public ClickHoverButton(int xPos, int yPos, int width, int height, int uOffset, int vOffset, int textureWidth, int textureHeight, Component displayString, OnPress handler, ResourceLocation click, ResourceLocation hover, ResourceLocation main) {
        super(xPos, yPos, width, height, displayString, handler);
        this.click = click;
        this.hover = hover;
        this.main = main;
        this.uOffset = uOffset;
        this.vOffset = vOffset;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if(isHovered()) {
            if(isClicking) {
                guiGraphics.blit(click, getX(), getY(), uOffset, vOffset, width, height, textureWidth, textureHeight);
            } else {
                guiGraphics.blit(hover, getX(), getY(), uOffset, vOffset, width, height, textureWidth, textureHeight);
            }
        } else {
            guiGraphics.blit(main, getX(), getY(), uOffset, vOffset, width, height, textureWidth, textureHeight);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        isClicking = true;
    }

    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        isClicking = false;
    }
}
