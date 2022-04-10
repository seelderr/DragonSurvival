package by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.DragonSkins;
import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class DragonGlowLayerRenderer extends GeoLayerRenderer<DragonEntity>{
	private final IGeoRenderer<DragonEntity> renderer;

	public DragonGlowLayerRenderer(IGeoRenderer<DragonEntity> entityRendererIn){
		super(entityRendererIn);
		this.renderer = entityRendererIn;
	}

	@Override
	public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch){
		if(!((DragonRenderer)renderer).renderLayers){
			return;
		}
		if(entitylivingbaseIn == ClientDragonRender.dragonArmor){
			return;
		}

		Player player = entitylivingbaseIn.getPlayer();
		DragonStateHandler handler = DragonUtils.getHandler(player);

		ResourceLocation glowTexture = DragonSkins.getGlowTexture(player, handler.getType(), handler.getLevel());

		if(glowTexture == null || glowTexture.getPath().contains("/" + handler.getType().name().toLowerCase() + "_")){
			if(((DragonRenderer)renderer).glowTexture != null){
				glowTexture = ((DragonRenderer)renderer).glowTexture;
			}
		}

		if(glowTexture != null){
			RenderType type = RenderType.eyes(glowTexture);
			VertexConsumer vertexConsumer = bufferIn.getBuffer(type);
			((DragonRenderer)renderer).isLayer = true;
			renderer.render(getEntityModel().getModel(getEntityModel().getModelLocation(entitylivingbaseIn)), entitylivingbaseIn, partialTicks, type, matrixStackIn, bufferIn, vertexConsumer, 0, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
			((DragonRenderer)renderer).isLayer = false;
		}
	}
}