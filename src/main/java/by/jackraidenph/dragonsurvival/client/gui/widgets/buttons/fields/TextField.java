package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.fields;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

import java.util.List;


public class TextField extends EditBox implements TooltipAccessor
{
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	private Option option;
	private ValueSpec spec;
	public List<FormattedCharSequence> tooltip;
	
	public TextField(int pX, int pY, int pWidth, int pHeight, Component pMessage)
	{
		this(null, null, pX, pY, pWidth, pHeight, pMessage);
	}
	
	public TextField(ValueSpec spec, Option option, int pX, int pY, int pWidth, int pHeight, Component pMessage)
	{
		super(Minecraft.getInstance().font, pX, pY, pWidth, pHeight, pMessage);
		setBordered(false);
		this.option = option;
		this.spec = spec;
	}
	
	@Override
	public void renderButton(PoseStack pPoseStack , int pMouseX, int pMouseY, float pPartialTicks)
	{
		GuiUtils.drawContinuousTexturedBox(pPoseStack, BACKGROUND_TEXTURE , x, y + 1, 0, isHovered ? 32 : 0, width, height, 32, 32, 10, 0);
		
		this.x += 5;
		this.y += 6;
		super.renderButton(pPoseStack , pMouseX, pMouseY, pPartialTicks);
		
		if(getValue().isEmpty()){
			boolean isFocus = isFocused();
			setFocus(false);
			int curser = getCursorPosition();
			setCursorPosition(0);
			setTextColor(7368816);
			setValue(this.getMessage().getString());
			super.renderButton(pPoseStack , pMouseX, pMouseY, pPartialTicks);
			setValue("");
			setTextColor(14737632);
			setCursorPosition(curser);
			setFocus(isFocus);
		}
		
		this.x -= 5;
		this.y -= 6;
	}
	public List<FormattedCharSequence> getTooltip() {
		return this.tooltip;
	}
}
