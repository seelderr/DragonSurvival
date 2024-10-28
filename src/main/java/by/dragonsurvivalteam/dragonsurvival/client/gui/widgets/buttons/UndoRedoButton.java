package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.ArrowButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.nullness.qual.NonNull;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class UndoRedoButton extends ArrowButton {
	public static final ResourceLocation undo = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/arrow_undo.png");
	public static final ResourceLocation redo = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/arrow_redo.png");

	public UndoRedoButton(int x, int y, int xSize, int ySize, boolean next, OnPress pressable) {
		super(x, y, xSize, ySize, next, pressable);
	}

	@Override
	public void renderWidget(@NonNull final GuiGraphics guiGraphics, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
		if (next) {
			guiGraphics.blit(redo, getX(), getY(), 0, 0, width, height, width, height);
		} else {
			guiGraphics.blit(undo, getX(), getY(), 0, 0, width, height, width, height);
		}
	}
}