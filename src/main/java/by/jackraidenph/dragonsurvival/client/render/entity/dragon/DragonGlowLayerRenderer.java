package by.jackraidenph.dragonsurvival.client.render.entity.dragon;

import by.jackraidenph.dragonsurvival.client.render.ClientDragonRender;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.DragonEntity;
import by.jackraidenph.dragonsurvival.client.handlers.DragonSkins;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class DragonGlowLayerRenderer extends GeoLayerRenderer<DragonEntity>
{
	private final IGeoRenderer<DragonEntity> renderer;
	
	public DragonGlowLayerRenderer(IGeoRenderer<DragonEntity> entityRendererIn)
	{
		super(entityRendererIn);
		this.renderer = entityRendererIn;
	}
	
	@Override
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
	{
		if(!((DragonRenderer)renderer).renderLayers) return;
		if(entitylivingbaseIn == ClientDragonRender.dragonArmor) return;
		
		PlayerEntity player = entitylivingbaseIn.getPlayer();
		DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
		
		if(handler != null){
			ResourceLocation glowTexture = DragonSkins.getGlowTexture(player, handler.getType(), handler.getLevel());
			
			if(glowTexture == null){
				if(((DragonRenderer)renderer).glowTexture != null){
					glowTexture = ((DragonRenderer)renderer).glowTexture;
				}
			}
			
			if (glowTexture != null) {
				RenderType type = RenderType.eyes(glowTexture);
				IVertexBuilder vertexConsumer = bufferIn.getBuffer(type);
				
				renderer.render(getEntityModel().getModel(getEntityModel().getModelLocation(entitylivingbaseIn)), entitylivingbaseIn, partialTicks, type, matrixStackIn, bufferIn, vertexConsumer, 0, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
			}
		}
	}
}
