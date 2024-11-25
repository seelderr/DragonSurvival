package by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.util.TextRenderUtil;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.stage.DragonStage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.NotNull;

public class DragonStageButton extends Button {
    private static final int WHITE = FastColor.ABGR32.color(255, 255, 255, 255);
    private static final int LIGHT_GRAY = FastColor.ABGR32.color(255, 160, 160, 160);

    private final DragonEditorScreen screen;
    private final ResourceKey<DragonStage> dragonStage;
    private final boolean isInteractive;
    private final int renderedWidth;

    public DragonStageButton(final DragonEditorScreen screen, final ResourceKey<DragonStage> dragonStage, int xOffset) {
        super(screen.width / 2 + xOffset, screen.guiTop - 30, 120, 20, DragonStage.translatableName(dragonStage), button -> {
            //noinspection DataFlowIssue -> registry is expected to be present
            screen.actionHistory.add(new DragonEditorScreen.EditorAction<>(screen.selectStageAction, CommonHooks.resolveLookup(DragonStage.REGISTRY).getOrThrow(dragonStage)));
        }, DEFAULT_NARRATION);

        this.screen = screen;
        this.dragonStage = dragonStage;
        this.isInteractive = true;
        this.renderedWidth = 120;
    }

    public DragonStageButton(final DragonEditorScreen screen, final ResourceKey<DragonStage> dragonStage, int xOffset, boolean isInteractive) {
        super(screen.width / 2 + xOffset, screen.guiTop - 30, 0, 0, DragonStage.translatableName(dragonStage), button -> {}, DEFAULT_NARRATION);
        this.screen = screen;
        this.dragonStage = dragonStage;
        this.isInteractive = isInteractive;
        this.renderedWidth = 120;
    }

    @Override
    public void renderWidget(@NotNull final GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        active = visible = screen.showUi;
        int color = LIGHT_GRAY;

        if (isInteractive && (isHovered || dragonStage != null && screen.dragonStage.is(dragonStage))) {
            color = WHITE;
        }

        TextRenderUtil.drawCenteredScaledText(graphics, getX() + renderedWidth / 2, getY() + 4, 1.5f, getMessage().getString(), color);
    }
}
