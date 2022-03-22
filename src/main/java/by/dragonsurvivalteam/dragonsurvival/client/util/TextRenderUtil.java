package by.dragonsurvivalteam.dragonsurvival.client.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class TextRenderUtil{

	public static void drawScaledText(MatrixStack matrix, float x, float y, float scale, String text, int color){
		drawScaledText(matrix, x, y, scale, text, color, 0);
	}
	public static void drawScaledText(MatrixStack matrix, float x, float y, float scale, String text, int color, int zLevel){
		matrix.pushPose();
		matrix.translate(x - x * scale, y - y * scale, zLevel);
		matrix.scale(scale, scale, 1);
		Minecraft.getInstance().font.draw(matrix, text, x, y, color);
		matrix.popPose();
	}

	public static void drawCenteredScaledText(MatrixStack matrix, int x, int y, float scale, String text, int color){
		drawCenteredScaledText(matrix, x, y, scale, text, color, 0);
	}
	public static void drawCenteredScaledText(MatrixStack matrix, int x, int y, float scale, String text, int color, int zLevel){
		matrix.pushPose();
		matrix.translate(x - x * scale, y - y * scale, zLevel);
		matrix.scale(scale, scale, 1);
		AbstractGui.drawCenteredString(matrix, Minecraft.getInstance().font, text, x, y, color);
		matrix.popPose();
	}

	public static void drawScaledTextSplit(MatrixStack matrix, float x, float y, float scale, String text, int color, int maxLength, int zLevel){
		List<IReorderingProcessor> lines = Minecraft.getInstance().font.split(new StringTextComponent(text), (int)(maxLength / scale));

		matrix.pushPose();
		matrix.translate(x - x * scale, y - y * scale, zLevel);
		matrix.scale(scale, scale, 1);

		for(int i = 0; i < lines.size(); i++){
			IReorderingProcessor line = lines.get(i);
			Minecraft.getInstance().font.draw(matrix, line, x, y + i * Minecraft.getInstance().font.lineHeight, color);
		}

		matrix.popPose();
	}
	public static void drawCenteredScaledTextSplit(MatrixStack matrix, int x, int y, float scale, String text, int color, int maxLength, int zLevel){
		List<IReorderingProcessor> lines = Minecraft.getInstance().font.split(new StringTextComponent(text), (int)(maxLength / scale));

		matrix.pushPose();
		matrix.translate(x - x * scale, y - y * scale, zLevel);
		matrix.scale(scale, scale, 1);

		for(int i = 0; i < lines.size(); i++){
			IReorderingProcessor line = lines.get(i);
			Minecraft.getInstance().font.drawShadow(matrix, line, (float)(x - Minecraft.getInstance().font.width(line) / 2), (float)y + i * Minecraft.getInstance().font.lineHeight, color);
		}

		matrix.popPose();
	}

	public static void drawScaledText(MatrixStack matrix, float x, float y, float scale, ITextComponent text, int color){
		drawScaledText(matrix, x, y, scale, text, color, 0);
	}
	public static void drawScaledText(MatrixStack matrix, float x, float y, float scale, ITextComponent text, int color, int zLevel){
		matrix.pushPose();
		matrix.translate(x - x * scale, y - y * scale, zLevel);
		matrix.scale(scale, scale, 1);
		Minecraft.getInstance().font.draw(matrix, text, x, y, color);
		matrix.popPose();
	}

	public static void drawCenteredScaledText(MatrixStack matrix, int x, int y, float scale, ITextComponent text, int color){
		drawCenteredScaledText(matrix, x, y, scale, text, color, 0);
	}

	public static void drawCenteredScaledText(MatrixStack matrix, int x, int y, float scale, ITextComponent text, int color, int zLevel){
		matrix.pushPose();
		matrix.translate(x - x * scale, y - y * scale, zLevel);
		matrix.scale(scale, scale, 1);
		AbstractGui.drawCenteredString(matrix, Minecraft.getInstance().font, text, x, y, color);
		matrix.popPose();
	}

	public static void drawScaledTextSplit(MatrixStack matrix, float x, float y, float scale, ITextComponent text, int color, int maxLength, int zLevel){
		List<IReorderingProcessor> lines = Minecraft.getInstance().font.split(text, (int)(maxLength / scale));

		matrix.pushPose();
		matrix.translate(x - x * scale, y - y * scale, zLevel);
		matrix.scale(scale, scale, 1);

		for(int i = 0; i < lines.size(); i++){
			IReorderingProcessor line = lines.get(i);
			Minecraft.getInstance().font.drawShadow(matrix, line, x, y + i * Minecraft.getInstance().font.lineHeight, color);
		}

		matrix.popPose();
	}

	public static void drawCenteredScaledTextSplit(MatrixStack matrix, int x, int y, float scale, ITextComponent text, int color, int maxLength, int zLevel){
		List<IReorderingProcessor> lines = Minecraft.getInstance().font.split(text, (int)(maxLength / scale));

		matrix.pushPose();
		matrix.translate(x - x * scale, y - y * scale, zLevel);
		matrix.scale(scale, scale, 1);

		for(int i = 0; i < lines.size(); i++){
			IReorderingProcessor line = lines.get(i);
			Minecraft.getInstance().font.drawShadow(matrix, line, (float)(x - Minecraft.getInstance().font.width(line) / 2), (float)y + i * Minecraft.getInstance().font.lineHeight, color);
		}

		matrix.popPose();
	}
}