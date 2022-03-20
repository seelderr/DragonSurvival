package by.dragonsurvivalteam.dragonsurvival.client.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import org.lwjgl.opengl.GL11;

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


	public static void drawRect(MatrixStack mStack, int x, int y, int width, int height, int color){
		Minecraft.getInstance().screen.hLine(mStack, x, x + width, y, color);
		Minecraft.getInstance().screen.hLine(mStack, x, x + width, y + height, color);
		Minecraft.getInstance().screen.vLine(mStack, x, y, y + height, color);
		Minecraft.getInstance().screen.vLine(mStack, x + width, y, y + height, color);
	}

	public static void drawRect(MatrixStack mStack, int zLevel, int xPos, int yPos, int width, int heigth, int color){
		Color cc = new Color(color);
		Matrix4f mat = mStack.last().pose();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuilder();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		buffer.vertex(mat, xPos + width, yPos, zLevel).color(cc.getRed(), cc.getGreen(), cc.getBlue(), cc.getAlpha()).endVertex();
		buffer.vertex(mat, xPos, yPos, zLevel).color(cc.getRed(), cc.getGreen(), cc.getBlue(), cc.getAlpha()).endVertex();
		buffer.vertex(mat, xPos, yPos + heigth, zLevel).color(cc.getRed(), cc.getGreen(), cc.getBlue(), cc.getAlpha()).endVertex();
		buffer.vertex(mat, xPos + width, yPos + heigth, zLevel).color(cc.getRed(), cc.getGreen(), cc.getBlue(), cc.getAlpha()).endVertex();
		tessellator.end();
	}

	public static void drawGradientRect(MatrixStack mat, int zLevel, int left, int top, int right, int bottom, int[] color){
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

		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.shadeModel(GL11.GL_SMOOTH);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuilder();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		buffer.vertex(mat, right, top, zLevel).color(red[0], green[0], blue[0], alpha[0]).endVertex();
		buffer.vertex(mat, left, top, zLevel).color(red[1], green[1], blue[1], alpha[1]).endVertex();
		buffer.vertex(mat, left, bottom, zLevel).color(red[2], green[2], blue[2], alpha[2]).endVertex();
		buffer.vertex(mat, right, bottom, zLevel).color(red[3], green[3], blue[3], alpha[3]).endVertex();
		tessellator.end();

		RenderSystem.shadeModel(GL11.GL_FLAT);
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
	}

	public static void renderPureColorSquare(MatrixStack mStack, int x, int y, int width, int height){
		Matrix4f mat = mStack.last().pose();
		int zLevel = 100;
		RenderSystem.enableDepthTest();
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.shadeModel(GL11.GL_SMOOTH);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuilder();
		buffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR);

		for(int i = 0; i <= width; i++){
			float val = ((((float)i / width) * 360f) / 360f);
			Color top = new Color(Color.HSBtoRGB(val, 1f, 1f));
			buffer.vertex(mat, x + i, y, zLevel).color(top.getRed() / 255f, top.getGreen() / 255f, top.getBlue() / 255f, top.getAlpha() / 255f).endVertex();
			buffer.vertex(mat, x + i, y + height, zLevel).color(top.getRed() / 255f, top.getGreen() / 255f, top.getBlue() / 255f, top.getAlpha() / 255f).endVertex();
		}
		tessellator.end();

		RenderSystem.shadeModel(GL11.GL_FLAT);
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
		RenderSystem.disableDepthTest();
	}

	public static void renderColorSquare(MatrixStack mStack, int x, int y, int width, int height){
		Matrix4f mat = mStack.last().pose();
		int zLevel = 200;
		RenderSystem.enableDepthTest();
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.shadeModel(GL11.GL_SMOOTH);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuilder();
		buffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR);

		for(int i = 0; i < width; i++){
			float val = ((((float)i / width) * 360f) / 360f);
			Color top = new Color(Color.HSBtoRGB(val, 1f, 0f));
			Color bot = new Color(Color.HSBtoRGB(val, 1f, 1f));

			buffer.vertex(mat, x + i, y, zLevel).color(top.getRed() / 255f, top.getGreen() / 255f, top.getBlue() / 255f, top.getAlpha() / 255f).endVertex();
			buffer.vertex(mat, x + i, y + (height / 2f), zLevel).color(bot.getRed() / 255f, bot.getGreen() / 255f, bot.getBlue() / 255f, bot.getAlpha() / 255f).endVertex();
		}

		for(int i = 0; i < width; i++){
			float val = ((((float)i / width) * 360f) / 360f);
			Color top = new Color(Color.HSBtoRGB(val, 1f, 1f));
			Color bot = new Color(Color.HSBtoRGB(val, 0f, 1f));

			buffer.vertex(mat, x + i, y + (height / 2f), zLevel).color(top.getRed() / 255f, top.getGreen() / 255f, top.getBlue() / 255f, top.getAlpha() / 255f).endVertex();
			buffer.vertex(mat, x + i, y + height, zLevel).color(bot.getRed() / 255f, bot.getGreen() / 255f, bot.getBlue() / 255f, bot.getAlpha() / 255f).endVertex();
		}

		tessellator.end();

		RenderSystem.shadeModel(GL11.GL_FLAT);
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
	}

	public static void fill(MatrixStack mStack, double pMinX, double pMinY, double pMaxX, double pMaxY, int pColor){
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
		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.vertex(pMatrix, (float)pMinX, (float)pMaxY, 0.0F).color(f, f1, f2, f3).endVertex();
		bufferbuilder.vertex(pMatrix, (float)pMaxX, (float)pMaxY, 0.0F).color(f, f1, f2, f3).endVertex();
		bufferbuilder.vertex(pMatrix, (float)pMaxX, (float)pMinY, 0.0F).color(f, f1, f2, f3).endVertex();
		bufferbuilder.vertex(pMatrix, (float)pMinX, (float)pMinY, 0.0F).color(f, f1, f2, f3).endVertex();
		bufferbuilder.end();
		WorldVertexBufferUploader.end(bufferbuilder);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}
}