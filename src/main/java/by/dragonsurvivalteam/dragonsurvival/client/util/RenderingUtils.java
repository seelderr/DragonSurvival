package by.dragonsurvivalteam.dragonsurvival.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

import java.awt.Color;

public class RenderingUtils{
	public static void clipRendering(int xPos, int yPos, int width, int height){
		double scale = Minecraft.getInstance().getWindow().getGuiScale();
		RenderSystem.enableScissor((int)(xPos * scale), (int)((height * scale) - (yPos * scale)), (int)(width * scale), (int)(height * scale));
	}

	public static void clipRendering(int xPos, int yPos, int width, int height, Runnable runnable){
		double scale = Minecraft.getInstance().getWindow().getGuiScale();
		RenderSystem.enableScissor((int)(xPos * scale), (int)(yPos * scale), (int)(width * scale), (int)(height * scale));
		runnable.run();
		RenderSystem.disableScissor();
	}


	public static void drawRect(PoseStack mStack, int x, int y, int width, int height, int color){
		Minecraft.getInstance().screen.hLine(mStack, x, x + width, y, color);
		Minecraft.getInstance().screen.hLine(mStack, x, x + width, y + height, color);
		Minecraft.getInstance().screen.vLine(mStack, x, y, y + height, color);
		Minecraft.getInstance().screen.vLine(mStack, x + width, y, y + height, color);
	}

	public static void drawRect(PoseStack mStack, int zLevel, int xPos, int yPos, int width, int heigth, int color){
		Color cc = new Color(color);
		Matrix4f mat = mStack.last().pose();
		BufferBuilder buffer = Tesselator.getInstance().getBuilder();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		buffer.vertex(mat, xPos + width, yPos, zLevel).color(cc.getRed(), cc.getGreen(), cc.getBlue(), cc.getAlpha()).endVertex();
		buffer.vertex(mat, xPos, yPos, zLevel).color(cc.getRed(), cc.getGreen(), cc.getBlue(), cc.getAlpha()).endVertex();
		buffer.vertex(mat, xPos, yPos + heigth, zLevel).color(cc.getRed(), cc.getGreen(), cc.getBlue(), cc.getAlpha()).endVertex();
		buffer.vertex(mat, xPos + width, yPos + heigth, zLevel).color(cc.getRed(), cc.getGreen(), cc.getBlue(), cc.getAlpha()).endVertex();
		buffer.end();
		BufferUploader.end(buffer);
	}

	public static void drawGradientRect(PoseStack mat, int zLevel, int left, int top, int right, int bottom, int[] color){
		drawGradientRect(mat.last().pose(), zLevel, left, top, right, bottom, color);
	}

	public static void drawGradientRect(Matrix4f mat, int zLevel, int left, int top, int right, int bottom, int[] color){
		float[] alpha = new float[4];
		float[] red = new float[4];
		float[] green = new float[4];
		float[] blue = new float[4];

		for(int i = 0; i < 4; i++){
			alpha[i] = (float)(color[i] >> 24 & 255) / 255.0F;
			red[i] = (float)(color[i] >> 16 & 255) / 255.0F;
			green[i] = (float)(color[i] >> 8 & 255) / 255.0F;
			blue[i] = (float)(color[i] & 255) / 255.0F;
		}

		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		bufferbuilder.vertex(mat, right, top, zLevel).color(red[0], green[0], blue[0], alpha[0]).endVertex();
		bufferbuilder.vertex(mat, left, top, zLevel).color(red[1], green[1], blue[1], alpha[1]).endVertex();
		bufferbuilder.vertex(mat, left, bottom, zLevel).color(red[2], green[2], blue[2], alpha[2]).endVertex();
		bufferbuilder.vertex(mat, right, bottom, zLevel).color(red[3], green[3], blue[3], alpha[3]).endVertex();
		bufferbuilder.end();
		BufferUploader.end(bufferbuilder);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	public static void renderPureColorSquare(PoseStack mStack, int x, int y, int width, int height){
		Matrix4f mat = mStack.last().pose();
		int zLevel = 100;
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder buffer = tessellator.getBuilder();
		buffer.begin(Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

		for(int i = 0; i <= width; i++){
			float val = ((((float)i / width) * 360f) / 360f);
			Color top = new Color(Color.HSBtoRGB(val, 1f, 1f));
			buffer.vertex(mat, x + i, y, zLevel).color(top.getRed() / 255f, top.getGreen() / 255f, top.getBlue() / 255f, top.getAlpha() / 255f).endVertex();
			buffer.vertex(mat, x + i, y + height, zLevel).color(top.getRed() / 255f, top.getGreen() / 255f, top.getBlue() / 255f, top.getAlpha() / 255f).endVertex();
		}
		buffer.end();
		BufferUploader.end(buffer);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	public static void renderColorSquare(PoseStack mStack, int x, int y, int width, int height){
		Matrix4f mat = mStack.last().pose();
		int zLevel = 200;
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		bufferbuilder.begin(Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

		for(int i = 0; i < width; i++){
			float val = ((((float)i / width) * 360f) / 360f);
			Color top = new Color(Color.HSBtoRGB(val, 1f, 0f));
			Color bot = new Color(Color.HSBtoRGB(val, 1f, 1f));

			bufferbuilder.vertex(mat, x + i, y, zLevel).color(top.getRed() / 255f, top.getGreen() / 255f, top.getBlue() / 255f, top.getAlpha() / 255f).endVertex();
			bufferbuilder.vertex(mat, x + i, y + (height / 2f), zLevel).color(bot.getRed() / 255f, bot.getGreen() / 255f, bot.getBlue() / 255f, bot.getAlpha() / 255f).endVertex();
		}

		for(int i = 0; i < width; i++){
			float val = ((((float)i / width) * 360f) / 360f);
			Color top = new Color(Color.HSBtoRGB(val, 1f, 1f));
			Color bot = new Color(Color.HSBtoRGB(val, 0f, 1f));

			bufferbuilder.vertex(mat, x + i, y + (height / 2f), zLevel).color(top.getRed() / 255f, top.getGreen() / 255f, top.getBlue() / 255f, top.getAlpha() / 255f).endVertex();
			bufferbuilder.vertex(mat, x + i, y + height, zLevel).color(bot.getRed() / 255f, bot.getGreen() / 255f, bot.getBlue() / 255f, bot.getAlpha() / 255f).endVertex();
		}

		bufferbuilder.end();
		BufferUploader.end(bufferbuilder);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	public static void fill(PoseStack mStack, double pMinX, double pMinY, double pMaxX, double pMaxY, int pColor){
		Matrix4f pMatrix = mStack.last().pose();

		if(pMinX < pMaxX){
			double i = pMinX;
			pMinX = pMaxX;
			pMaxX = i;
		}

		if(pMinY < pMaxY){
			double j = pMinY;
			pMinY = pMaxY;
			pMaxY = j;
		}

		float f3 = (float)(pColor >> 24 & 255) / 255.0F;
		float f = (float)(pColor >> 16 & 255) / 255.0F;
		float f1 = (float)(pColor >> 8 & 255) / 255.0F;
		float f2 = (float)(pColor & 255) / 255.0F;
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		bufferbuilder.vertex(pMatrix, (float)pMinX, (float)pMaxY, 0.0F).color(f, f1, f2, f3).endVertex();
		bufferbuilder.vertex(pMatrix, (float)pMaxX, (float)pMaxY, 0.0F).color(f, f1, f2, f3).endVertex();
		bufferbuilder.vertex(pMatrix, (float)pMaxX, (float)pMinY, 0.0F).color(f, f1, f2, f3).endVertex();
		bufferbuilder.vertex(pMatrix, (float)pMinX, (float)pMinY, 0.0F).color(f, f1, f2, f3).endVertex();
		bufferbuilder.end();
		BufferUploader.end(bufferbuilder);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}
}