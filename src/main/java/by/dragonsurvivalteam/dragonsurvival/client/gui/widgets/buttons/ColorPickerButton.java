package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.util.RenderingUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.awt.Color;
import java.util.function.Consumer;

public class ColorPickerButton extends ExtendedButton{
	public Color defaultColor;
	public Consumer<Color> colorConsumer;
	double selectorX;
	double selectorY;

	public ColorPickerButton(int xPos, int yPos, int width, int height, Color defaultColor, Consumer<Color> colorConsumer){
		super(xPos, yPos, width, height, StringTextComponent.EMPTY, null);
		this.defaultColor = defaultColor;
		this.colorConsumer = colorConsumer;

		float[] hsb = Color.RGBtoHSB(defaultColor.getRed(), defaultColor.getGreen(), defaultColor.getBlue(), null);
		selectorX = hsb[0] * width;

		selectorY = hsb[2] < 1.0f ? (hsb[2]) * (height / 2f) : (height / 2f) + (1 - hsb[1]) * (height / 2f);
	}

	@Override
	public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial){
		RenderingUtils.renderColorSquare(mStack, x, y, width, height);
		mStack.pushPose();
		mStack.translate(0, 0, 200);
		RenderingUtils.fill(mStack, x + selectorX - 2, y + selectorY - 2, x + selectorX + 2, y + selectorY + 2, Color.black.getRGB());
		RenderingUtils.fill(mStack, x + selectorX - 1, y + selectorY - 1, x + selectorX + 1, y + selectorY + 1, getColor().getRGB());
		mStack.popPose();
	}

	public Color getColor(){
		double hue = ((selectorX / width) * 360.0) / 360.0;

		if(selectorY > (height / 2f)){
			double saturation = 1f - (((selectorY - (height / 2f)) / (height / 2f)) * 360.0) / 360.0;
			return Color.getHSBColor((float)hue, (float)saturation, 1f);
		}else{
			double brightness = ((selectorY / (height / 2f)) * 360.0) / 360.0;
			return Color.getHSBColor((float)hue, 1f, (float)brightness);
		}
	}

	@Override
	public void onClick(double pMouseX, double pMouseY){
		selectorX = MathHelper.clamp(pMouseX - x, 0, width);
		selectorY = MathHelper.clamp(pMouseY - y, 0, height);
		colorConsumer.accept(getColor());
	}

	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY){
		selectorX = MathHelper.clamp(pMouseX - x, 0, width);
		selectorY = MathHelper.clamp(pMouseY - y, 0, height);
		colorConsumer.accept(getColor());

		return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
	}

	//Removes button sound
	public void playDownSound(SoundHandler pHandler){}
}