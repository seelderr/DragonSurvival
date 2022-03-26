package by.dragonsurvivalteam.dragonsurvival.client.util;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.GameRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class RenderUtils{
	static final double PI_TWO = (Math.PI * 2.0);

	public static void drawSmoothCircle(Matrix4f matrix4f, double x, double y, double z, double radius, int sides, double percent, double startAngle){
		double rad;
		double sin;
		double cos;

		float[] colors = RenderSystem.getShaderColor();

		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		final BufferBuilder buffer = Tesselator.getInstance().getBuilder();

		GL11.glEnable(GL20.GL_LINE_SMOOTH);
		GL11.glHint(GL20.GL_LINE_SMOOTH_HINT, GL20.GL_NICEST);

		buffer.begin(Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
		buffer.vertex(matrix4f, (float)x, (float)y, (float)z).color(colors[0], colors[1], colors[2], 1f).endVertex();

		for(int i = 0; i <= percent * sides; i++){
			rad = PI_TWO * ((double)i / (double)sides + startAngle);
			sin = Math.sin(rad);
			cos = Math.cos(rad);

			float xPos = (float)(x + sin * radius);
			float yPos = (float)(y + cos * radius);
			buffer.vertex(matrix4f, xPos, yPos, (float)z).color(colors[0], colors[1], colors[2], 1f).endVertex();
		}

		rad = PI_TWO * (percent + startAngle);
		sin = Math.sin(rad);
		cos = Math.cos(rad);
		buffer.vertex(matrix4f, (float)(x + sin * radius), (float)(y + cos * radius), (float)z).color(colors[0], colors[1], colors[2], 1f).endVertex();

		RenderSystem.disableBlend();
		GL20.glDisable(GL20.GL_LINE_SMOOTH);
		buffer.end();
		BufferUploader.end(buffer);
	}

	public static void drawTexturedCircle(Matrix4f matrix4f, double x, double y, double z, double radius, double u, double v, double texRadius, int sides, double percent, double startAngle){
		double rad;
		double sin;
		double cos;

		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		final BufferBuilder buffer = Tesselator.getInstance().getBuilder();

		GL11.glEnable(GL20.GL_LINE_SMOOTH);
		GL11.glHint(GL20.GL_LINE_SMOOTH_HINT, GL20.GL_NICEST);

		buffer.begin(Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_TEX);
		buffer.vertex(matrix4f, (float)x, (float)y, (float)z).uv((float)u, (float)v).endVertex();

		for(int i = 0; i <= percent * sides; i++){
			rad = PI_TWO * ((double)i / (double)sides + startAngle);
			sin = Math.sin(rad);
			cos = Math.cos(rad);

			float xPos = (float)(x + sin * radius);
			float yPos = (float)(y + cos * radius);
			buffer.vertex(matrix4f, xPos, yPos, (float)z).uv((float)(u + sin * texRadius), (float)(v + cos * texRadius)).endVertex();
		}

		if(percent == 1.0){
			rad = PI_TWO * (percent + startAngle);
			sin = Math.sin(rad);
			cos = Math.cos(rad);
			buffer.vertex(matrix4f, (float)(x + sin * radius), (float)(y + cos * radius), (float)z).uv((float)(u + sin * texRadius), (float)(v + cos * texRadius)).endVertex();
		}
		RenderSystem.disableBlend();
		GL20.glDisable(GL20.GL_LINE_SMOOTH);
		buffer.end();
		BufferUploader.end(buffer);
	}

	public static void drawTexturedRing(double x, double y, double z, double innerRadius, double outerRadius, double u, double v, double texInnerRadius, double texOuterRadius, int sides, double percent, double startAngle){
		double rad;
		double sin;
		double cos;

		RenderSystem.enableBlend();
		RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);

		GL11.glEnable(GL20.GL_LINE_SMOOTH);
		GL11.glHint(GL20.GL_LINE_SMOOTH_HINT, GL20.GL_NICEST);

		final BufferBuilder buffer = Tesselator.getInstance().getBuilder();
		buffer.begin(Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_TEX);

		for(int i = 0; i <= percent * sides; i++){
			rad = PI_TWO * ((double)i / (double)sides + startAngle);
			sin = Math.sin(rad);
			cos = -Math.cos(rad);

			buffer.vertex(x + sin * outerRadius, y + cos * outerRadius, z).uv((float)(u + sin * texOuterRadius), (float)(v + cos * texOuterRadius)).endVertex();

			buffer.vertex(x + sin * innerRadius, y + cos * innerRadius, z).uv((float)(u + sin * texInnerRadius), (float)(v + cos * texInnerRadius)).endVertex();
		}

		rad = PI_TWO * (percent + startAngle);
		sin = Math.sin(rad);
		cos = -Math.cos(rad);

		buffer.vertex(x + sin * outerRadius, y + cos * outerRadius, z).uv((float)(u + sin * texOuterRadius), (float)(v + cos * texOuterRadius)).endVertex();

		buffer.vertex(x + sin * innerRadius, y + cos * innerRadius, z).uv((float)(u + sin * texInnerRadius), (float)(v + cos * texInnerRadius)).endVertex();

		Tesselator.getInstance().end();
		GL20.glDisable(GL20.GL_LINE_SMOOTH);
		RenderSystem.disableBlend();
	}
}