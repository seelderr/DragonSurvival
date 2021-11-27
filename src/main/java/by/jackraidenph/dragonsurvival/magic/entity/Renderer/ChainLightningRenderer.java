package by.jackraidenph.dragonsurvival.magic.entity.Renderer;

import by.jackraidenph.dragonsurvival.magic.entity.EntityChainLightning;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT)
public class ChainLightningRenderer extends EntityRenderer<EntityChainLightning>
{
	public ChainLightningRenderer(EntityRendererManager p_i46179_1_)
	{
		super(p_i46179_1_);
	}
	
	public void render(EntityChainLightning entity, float p_225623_2_, float p_225623_3_, MatrixStack stack, IRenderTypeBuffer buffer, int p_225623_6_) {
		Vector3d start = entity.position();
		Vector3d end = entity.getTarget();
		
		Vector3d view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
		
		stack.pushPose();
		stack.translate(-view.x(), -view.y(), -view.z());
		IVertexBuilder ivertexbuilder = buffer.getBuffer(RenderType.lightning());
		stack.translate(-0.005f, -0.005f, -0.005f);
		stack.scale(1.01f, 1.01f, 1.01f);
		stack.translate(start.x(), start.y(), start.z());
		drawShearedBox(ivertexbuilder, start.x, start.y, start.z, end.x, end.y, end.z, 100F, 1F, 1F, 1F, 1F);
		drawShearedBox(ivertexbuilder, 0, 0, 0, 2, 2, 2, 100F, 1F, 1F, 1F, 1F);
		
		stack.popPose();
	}
	
	/** Draws a single box for one segment of the arc, from the point (x1, y1, z1) to the point (x2, y2, z2), with given width and colour. */
	private void drawShearedBox(IVertexBuilder buffer, double x1, double y1, double z1, double x2, double y2, double z2, float width, float r, float g, float b, float a){
		buffer.vertex(x1-width, y1-width, z1).color(r, g, b, a).endVertex();
		buffer.vertex(x2-width, y2-width, z2).color(r, g, b, a).endVertex();
		buffer.vertex(x1-width, y1+width, z1).color(r, g, b, a).endVertex();
		buffer.vertex(x2-width, y2+width, z2).color(r, g, b, a).endVertex();
		buffer.vertex(x1+width, y1+width, z1).color(r, g, b, a).endVertex();
		buffer.vertex(x2+width, y2+width, z2).color(r, g, b, a).endVertex();
		buffer.vertex(x1+width, y1-width, z1).color(r, g, b, a).endVertex();
		buffer.vertex(x2+width, y2-width, z2).color(r, g, b, a).endVertex();
		buffer.vertex(x1-width, y1-width, z1).color(r, g, b, a).endVertex();
		buffer.vertex(x2-width, y2-width, z2).color(r, g, b, a).endVertex();
	}
	
	private static void quad(Matrix4f p_229116_0_, IVertexBuilder p_229116_1_, float p_229116_2_, float p_229116_3_, int p_229116_4_, float p_229116_5_, float p_229116_6_, float p_229116_7_, float p_229116_8_, float p_229116_9_, float p_229116_10_, float p_229116_11_, boolean p_229116_12_, boolean p_229116_13_, boolean p_229116_14_, boolean p_229116_15_) {
		p_229116_1_.vertex(p_229116_0_, p_229116_2_ + (p_229116_12_ ? p_229116_11_ : -p_229116_11_), (float)(p_229116_4_ * 16), p_229116_3_ + (p_229116_13_ ? p_229116_11_ : -p_229116_11_)).color(p_229116_7_, p_229116_8_, p_229116_9_, 0.3F).endVertex();
		p_229116_1_.vertex(p_229116_0_, p_229116_5_ + (p_229116_12_ ? p_229116_10_ : -p_229116_10_), (float)((p_229116_4_ + 1) * 16), p_229116_6_ + (p_229116_13_ ? p_229116_10_ : -p_229116_10_)).color(p_229116_7_, p_229116_8_, p_229116_9_, 0.3F).endVertex();
		p_229116_1_.vertex(p_229116_0_, p_229116_5_ + (p_229116_14_ ? p_229116_10_ : -p_229116_10_), (float)((p_229116_4_ + 1) * 16), p_229116_6_ + (p_229116_15_ ? p_229116_10_ : -p_229116_10_)).color(p_229116_7_, p_229116_8_, p_229116_9_, 0.3F).endVertex();
		p_229116_1_.vertex(p_229116_0_, p_229116_2_ + (p_229116_14_ ? p_229116_11_ : -p_229116_11_), (float)(p_229116_4_ * 16), p_229116_3_ + (p_229116_15_ ? p_229116_11_ : -p_229116_11_)).color(p_229116_7_, p_229116_8_, p_229116_9_, 0.3F).endVertex();
	}
	
	@Override
	public ResourceLocation getTextureLocation(EntityChainLightning p_110775_1_)
	{
		return AtlasTexture.LOCATION_BLOCKS;
	}
}
