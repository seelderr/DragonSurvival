package by.jackraidenph.dragonsurvival.gecko;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import javax.annotation.Nullable;
import java.awt.*;

public class DragonRenderer extends GeoEntityRenderer<DragonEntity> {
	public ResourceLocation glowTexture = null;

	public DragonRenderer(EntityRendererManager renderManager, AnimatedGeoModel<DragonEntity> modelProvider) {
        super(renderManager, modelProvider);
		this.addLayer(new glowLayerRender(this));
    }

	public Color renderColor = new Color(255, 255, 255);

	@Override
	public Color getRenderColor(DragonEntity animatable, float partialTicks, MatrixStack stack,
			@Nullable IRenderTypeBuffer renderTypeBuffer,
			@Nullable IVertexBuilder vertexBuilder, int packedLightIn)
	{
		return renderColor;
	}

	public class glowLayerRender extends GeoLayerRenderer<DragonEntity>
	{
		private final IGeoRenderer<DragonEntity> renderer;

		public glowLayerRender(IGeoRenderer<DragonEntity> entityRendererIn)
		{
			super(entityRendererIn);
			this.renderer = entityRendererIn;
		}

		@Override
		public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
		{
			if (glowTexture != null) {
				RenderType type = RenderType.eyes(glowTexture);
				IVertexBuilder vertexConsumer = bufferIn.getBuffer(type);

				renderer.render(getEntityModel().getModel(getEntityModel().getModelLocation(entitylivingbaseIn)),
				                entitylivingbaseIn,
				                partialTicks,
				                type,
				                matrixStackIn,
				                bufferIn,
				                vertexConsumer,
				                0,
				                OverlayTexture.NO_OVERLAY,
				                1.0F, 1.0F, 1.0F, 1.0F
				);
			}
		}
	}
}
