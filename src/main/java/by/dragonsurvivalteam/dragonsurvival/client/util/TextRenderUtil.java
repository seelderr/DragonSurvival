package by.dragonsurvivalteam.dragonsurvival.client.util;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class TextRenderUtil{

	public static void drawScaledText(PoseStack matrix, float x, float y, float scale, String text, int color){
		drawScaledText(matrix, x, y, scale, text, color, 0);
	}

	public static void drawScaledText(PoseStack matrix, float x, float y, float scale, String text, int color, int zLevel){
		matrix.pushPose();
		matrix.translate(x - x * scale, y - y * scale, zLevel);
		matrix.scale(scale, scale, 1);
		Minecraft.getInstance().font.draw(matrix, text, x, y, color);
		matrix.popPose();
	}

	public static void drawCenteredScaledText(PoseStack matrix, int x, int y, float scale, String text, int color){
		drawCenteredScaledText(matrix, x, y, scale, text, color, 0);
	}

	public static void drawCenteredScaledText(PoseStack matrix, int x, int y, float scale, String text, int color, int zLevel){
		matrix.pushPose();
		matrix.translate(x - x * scale, y - y * scale, zLevel);
		matrix.scale(scale, scale, 1);
		Gui.drawCenteredString(matrix, Minecraft.getInstance().font, text, x, y, color);
		matrix.popPose();
	}

	public static void drawScaledTextSplit(PoseStack matrix, float x, float y, float scale, String text, int color, int maxLength, int zLevel){
		List<FormattedCharSequence> lines = Minecraft.getInstance().font.split(new TextComponent(text), (int)(maxLength / scale));

		matrix.pushPose();
		matrix.translate(x - x * scale, y - y * scale, zLevel);
		matrix.scale(scale, scale, 1);

		for(int i = 0; i < lines.size(); i++){
			FormattedCharSequence line = lines.get(i);
			Minecraft.getInstance().font.draw(matrix, line, x, y + i * Minecraft.getInstance().font.lineHeight, color);
		}

		matrix.popPose();
	}

	public static void drawCenteredScaledTextSplit(PoseStack matrix, int x, int y, float scale, String text, int color, int maxLength, int zLevel){
		List<FormattedCharSequence> lines = Minecraft.getInstance().font.split(new TextComponent(text), (int)(maxLength / scale));

		matrix.pushPose();
		matrix.translate(x - x * scale, y - y * scale, zLevel);
		matrix.scale(scale, scale, 1);

		for(int i = 0; i < lines.size(); i++){
			FormattedCharSequence line = lines.get(i);
			Minecraft.getInstance().font.drawShadow(matrix, line, (float)(x - Minecraft.getInstance().font.width(line) / 2), (float)y + i * Minecraft.getInstance().font.lineHeight, color);
		}

		matrix.popPose();
	}

	public static void drawScaledText(PoseStack matrix, float x, float y, float scale, Component text, int color){
		drawScaledText(matrix, x, y, scale, text, color, 0);
	}

	public static void drawScaledText(PoseStack matrix, float x, float y, float scale, Component text, int color, int zLevel){
		matrix.pushPose();
		matrix.translate(x - x * scale, y - y * scale, zLevel);
		matrix.scale(scale, scale, 1);
		Minecraft.getInstance().font.draw(matrix, text, x, y, color);
		matrix.popPose();
	}

	public static void drawCenteredScaledText(PoseStack matrix, int x, int y, float scale, Component text, int color){
		drawCenteredScaledText(matrix, x, y, scale, text, color, 0);
	}

	public static void drawCenteredScaledText(PoseStack matrix, int x, int y, float scale, Component text, int color, int zLevel){
		matrix.pushPose();
		matrix.translate(x - x * scale, y - y * scale, zLevel);
		matrix.scale(scale, scale, 1);
		Gui.drawCenteredString(matrix, Minecraft.getInstance().font, text, x, y, color);
		matrix.popPose();
	}

	public static void drawScaledTextSplit(PoseStack matrix, float x, float y, float scale, Component text, int color, int maxLength, int zLevel){
		List<FormattedCharSequence> lines = Minecraft.getInstance().font.split(text, (int)(maxLength / scale));

		matrix.pushPose();
		matrix.translate(x - x * scale, y - y * scale, zLevel);
		matrix.scale(scale, scale, 1);

		for(int i = 0; i < lines.size(); i++){
			FormattedCharSequence line = lines.get(i);
			Minecraft.getInstance().font.drawShadow(matrix, line, x, y + i * Minecraft.getInstance().font.lineHeight, color);
		}

		matrix.popPose();
	}

	public static void drawCenteredScaledTextSplit(PoseStack matrix, int x, int y, float scale, Component text, int color, int maxLength, int zLevel){
		List<FormattedCharSequence> lines = Minecraft.getInstance().font.split(text, (int)(maxLength / scale));

		matrix.pushPose();
		matrix.translate(x - x * scale, y - y * scale, zLevel);
		matrix.scale(scale, scale, 1);

		for(int i = 0; i < lines.size(); i++){
			FormattedCharSequence line = lines.get(i);
			Minecraft.getInstance().font.drawShadow(matrix, line, (float)(x - Minecraft.getInstance().font.width(line) / 2), (float)y + i * Minecraft.getInstance().font.lineHeight, color);
		}

		matrix.popPose();
	}
}