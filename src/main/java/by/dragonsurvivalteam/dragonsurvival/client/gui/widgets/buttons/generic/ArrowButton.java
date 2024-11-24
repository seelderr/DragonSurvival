package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic;

import by.dragonsurvivalteam.dragonsurvival.client.gui.hud.MagicHUD;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ArrowButton extends Button {
    private final Type type;

    public ArrowButton(final Type type, int x, int y, int width, int height, final OnPress action) {
        super(x, y, width, height, Component.empty(), action, Supplier::get);
        this.type = type;
    }

    public enum Type {PREVIOUS, NEXT}

    @Override
    public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderTexture(0, MagicHUD.WIDGET_TEXTURES);

        int uOffset;

        if (type == Type.PREVIOUS) {
            uOffset = isHoveredOrFocused() ? 22 : 0;
        } else {
            uOffset = isHoveredOrFocused() ? 66 : 44;
        }

        guiGraphics.blit(MagicHUD.WIDGET_TEXTURES, getX(), getY(), (float) uOffset / 2, (float) 222 / 2, 11, 17, 128, 128);
    }
}
