package by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.SkinsScreen;
import by.dragonsurvivalteam.dragonsurvival.common.dragon.DragonBody;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DragonSkinBodyButton extends Button {
    private final SkinsScreen screen;
    private final Holder<DragonBody> dragonBody;
    private final ResourceLocation location;

    public DragonSkinBodyButton(SkinsScreen screen, int x, int y, int xSize, int ySize, Holder<DragonBody> body) {
        super(x, y, xSize, ySize, Component.literal(body.toString()), action -> screen.handler.setBody(body), DEFAULT_NARRATION);
        this.screen = screen;
        this.dragonBody = body;

        //noinspection DataFlowIssue -> key is present
        ResourceLocation bodyLocation = dragonBody.getKey().location();

        this.location = ResourceLocation.fromNamespaceAndPath(bodyLocation.getNamespace(), DragonBodyButton.LOCATION_PREFIX + bodyLocation.getPath() + "/default.png");
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        int state = 0;

        if (DragonUtils.isBody(dragonBody, screen.handler.getBody())) {
            state = DragonBodyButton.SELECTED;
        } else if (this.isHoveredOrFocused()) {
            state = DragonBodyButton.HOVERED;
        }

        graphics.blit(location, getX(), getY(), 0, state * this.height, this.width, this.height, 32, 104);
    }
}
