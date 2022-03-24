package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.fields;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IBidiTooltip;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.List;
import java.util.Optional;

public class TextField extends TextFieldWidget implements IBidiTooltip{
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	public final AbstractOption option;
	private final ValueSpec spec;

	public TextField(int pX, int pY, int pWidth, int pHeight, ITextComponent pMessage){
		this(null, null, pX, pY, pWidth, pHeight, pMessage);
	}

	public TextField(ValueSpec spec, AbstractOption option, int pX, int pY, int pWidth, int pHeight, ITextComponent pMessage){
		super(Minecraft.getInstance().font, pX, pY, pWidth, pHeight, pMessage);
		setBordered(false);
		this.option = option;
		this.spec = spec;
	}

	@Override
	public void renderButton(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks){
		Minecraft.getInstance().textureManager.bind(BACKGROUND_TEXTURE);
		GuiUtils.drawContinuousTexturedBox(pMatrixStack, x, y + 1, 0, isHovered ? 32 : 0, width, height, 32, 32, 10, 0);

		this.x += 5;
		this.y += 6;
		super.renderButton(pMatrixStack, pMouseX, pMouseY, pPartialTicks);

		if(getValue().isEmpty()){
			boolean isFocus = isFocused();
			setFocus(false);
			int curser = getCursorPosition();
			setCursorPosition(0);
			setTextColor(7368816);
			setValue(this.getMessage().getString());
			super.renderButton(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
			setValue("");
			setTextColor(14737632);
			setCursorPosition(curser);
			setFocus(isFocus);
		}

		this.x -= 5;
		this.y -= 6;
	}

	@Override
	public Optional<List<IReorderingProcessor>> getTooltip(){
		return option != null ? option.getTooltip() : Optional.empty();
	}
}