package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.fields;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.gui.GuiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TextField extends EditBox implements TooltipAccessor {
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	public List<FormattedCharSequence> tooltip;

	public TextField(int pX, int pY, int pWidth, int pHeight, Component pMessage){
		super(Minecraft.getInstance().font, pX, pY, pWidth, pHeight, pMessage);
		setBordered(false);
	}

	@Override
	public void renderButton(@NotNull final PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		int v = isHovered ? 32 : 0;
		GuiUtils.drawContinuousTexturedBox(poseStack, BACKGROUND_TEXTURE, x, y + 1, 0, v, width, height, 32, 32, 10, 10, 10, 10, (float)0);

		x += 5;
		y += 6;
		super.renderButton(poseStack, mouseX, mouseY, partialTicks);

		// Sets the prompt text
		if (getValue().isEmpty() && !getMessage().toString().isBlank()) {
			boolean isFocus = isFocused();
			setFocus(false);
			int curser = getCursorPosition();
			setCursorPosition(0);
			setTextColor(7368816);
			setValue(getMessage().getString());
			super.renderButton(poseStack, mouseX, mouseY, partialTicks);
			setValue("");
			setTextColor(14737632);
			setCursorPosition(curser);
			setFocus(isFocus);
		}

		x -= 5;
		y -= 6;
	}

	@Override
	public @NotNull List<FormattedCharSequence> getTooltip(){
		return tooltip;
	}
}