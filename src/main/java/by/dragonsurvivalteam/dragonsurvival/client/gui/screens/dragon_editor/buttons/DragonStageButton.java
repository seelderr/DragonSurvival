package by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.util.TextRenderUtil;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonStage;
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

    public DragonStageButton(final DragonEditorScreen screen, final ResourceKey<DragonStage> dragonStage, int xOffset) {
        super(screen.width / 2 + xOffset, screen.guiTop - 30, 120, 20, DragonStage.translatableName(dragonStage), button -> {
            //noinspection DataFlowIssue -> registry is expected to be present
            screen.actionHistory.add(new DragonEditorScreen.EditorAction<>(screen.selectLevelAction, CommonHooks.resolveLookup(DragonStage.REGISTRY).getOrThrow(dragonStage)));
        }, DEFAULT_NARRATION);

        this.screen = screen;
        this.dragonStage = dragonStage;
    }

    @Override
    public void renderWidget(@NotNull final GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        active = visible = screen.showUi;
        int color = isHovered || screen.dragonStage.is(dragonStage) ? WHITE : LIGHT_GRAY;
        TextRenderUtil.drawCenteredScaledText(graphics, getX() + width / 2, getY() + 4, 1.5f, getMessage().getString(), color); // TODO :: previously used alpha - but does that ever change for this widget?
    }
}
