package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.fields;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

public class TextField extends EditBox /*implements TooltipAccessor*/ {
	private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/textbox.png");
	public List<FormattedCharSequence> tooltip;

	public TextField(int pX, int pY, int pWidth, int pHeight, Component pMessage){
		super(Minecraft.getInstance().font, pX, pY, pWidth, pHeight, pMessage);
		setBordered(false);
	}

	@Override
	public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		int v = isHovered ? 32 : 0;
		guiGraphics.blitWithBorder(BACKGROUND_TEXTURE, getX(), getY() + 1, 0, v, width, height, 32, 32, 10, 10, 10, 10);

		setX(getX() + 5);
		setY(getY() + 6);
		super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);

		// Sets the prompt text
		if (getValue().isEmpty() && !getMessage().toString().isBlank()) {
			boolean isFocus = isFocused();
			setFocused(false);
			int curser = getCursorPosition();
			setCursorPosition(0);
			setTextColor(7368816);
			setValue(getMessage().getString());
			super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
			setValue("");
			setTextColor(14737632);
			setCursorPosition(curser);
			setFocused(isFocus);
		}

		setX(getX() - 5);
		setY(getY() - 6);
	}
}