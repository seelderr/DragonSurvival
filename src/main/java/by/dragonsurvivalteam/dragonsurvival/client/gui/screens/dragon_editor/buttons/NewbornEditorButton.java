package by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.util.TextRenderUtil;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class NewbornEditorButton extends Button{
	private final DragonEditorScreen dragonEditorScreen;

	public NewbornEditorButton(DragonEditorScreen dragonEditorScreen){
		super(dragonEditorScreen.width / 2 - 180, dragonEditorScreen.guiTop - 30, 120, 20, Component.translatable("ds.level.newborn"), btn -> {
			dragonEditorScreen.actionHistory.add(new DragonEditorScreen.EditorAction<>(dragonEditorScreen.selectLevelAction, DragonLevel.NEWBORN));
		}, DEFAULT_NARRATION);
		this.dragonEditorScreen = dragonEditorScreen;
	}

	@Override
	public void renderWidget(@NotNull final GuiGraphics guiGraphics, int p_230431_2_, int p_230431_3_, float p_230431_4_){
		active = visible = dragonEditorScreen.showUi;
		int j = isHovered || dragonEditorScreen.level == DragonLevel.NEWBORN ? 16777215 : 10526880;
		TextRenderUtil.drawCenteredScaledText(guiGraphics, getX() + width / 2, getY() + 4, 1.5f, getMessage().getString(), j | Mth.ceil(alpha * 255.0F) << 24);
	}
}