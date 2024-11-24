package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class PlusMinusButton extends Button {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/magic_gui.png");

    public boolean next;

    public PlusMinusButton(int x, int y, int xSize, int ySize, boolean next, OnPress pressable) {
        super(x, y, xSize, ySize, Component.empty(), pressable, DEFAULT_NARRATION);
        this.next = next;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderTexture(0, TEXTURE);

        float xSize = (float) width / 34F;
        float ySize = (float) height / 34F;

        graphics.pose().pushPose();
        graphics.pose().translate(getX() - getX() * xSize, getY() - getY() * ySize, 0);
        graphics.pose().scale(xSize, ySize, 0);

        int uOffset;
        int vOffset;

        if (next) {
            uOffset = isHovered ? 34 : 0;
            vOffset = 34;
        } else {
            uOffset = isHovered ? 34 : 0;
            vOffset = 0;
        }

        graphics.blit(TEXTURE, getX(), getY(), uOffset, vOffset, 34, 34);
        graphics.pose().popPose();
    }
}