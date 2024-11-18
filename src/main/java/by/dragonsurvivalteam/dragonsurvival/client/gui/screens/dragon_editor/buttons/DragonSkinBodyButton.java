package by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.SkinsScreen;
import by.dragonsurvivalteam.dragonsurvival.common.dragon.DragonBody;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class DragonSkinBodyButton extends Button {
    private static final ResourceLocation location = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/body_type_icon_skintab.png");

    private final SkinsScreen screen;
    private final Holder<DragonBody> dragonBody;
    private final int pos;

    public DragonSkinBodyButton(SkinsScreen screen, int x, int y, int xSize, int ySize, Holder<DragonBody> body, int pos) {
        super(x, y, xSize, ySize, Component.literal(body.toString()), action -> screen.dragonBody = body, DEFAULT_NARRATION);
        this.screen = screen;
        this.dragonBody = body;
        this.pos = pos;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int i = 0;

        if (dragonBody.is(screen.handler.getBody())) {
            i = 2;
        } else if (this.isHoveredOrFocused()) {
            i = 1;
        }

        guiGraphics.blit(location, getX(), getY(), pos * this.width, i * this.height, this.width, this.height);
    }
}
