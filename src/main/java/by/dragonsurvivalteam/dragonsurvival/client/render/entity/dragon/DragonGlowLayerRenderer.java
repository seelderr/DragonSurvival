package by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.DragonSkins;
import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SkinPreset;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SkinPreset.SkinAgeGroup;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.util.Locale;

public class DragonGlowLayerRenderer extends GeoLayerRenderer<DragonEntity>{
	private final IGeoRenderer<DragonEntity> renderer;

	public DragonGlowLayerRenderer(IGeoRenderer<DragonEntity> entityRendererIn){
		super(entityRendererIn);
		renderer = entityRendererIn;
	}

	@Override
	public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch){
		if(!((DragonRenderer)renderer).shouldRenderLayers){
			return;
		}
		if(entitylivingbaseIn == ClientDragonRender.dragonArmor){
			return;
		}

		Player player = entitylivingbaseIn.getPlayer();
		DragonStateHandler handler = DragonUtils.getHandler(player);

		SkinPreset preset = handler.getSkinData().skinPreset;
		SkinAgeGroup ageGroup = preset.skinAges.get(handler.getLevel());

		ResourceLocation glowTexture = DragonSkins.getGlowTexture(player, handler.getType(), handler.getLevel());

		if(glowTexture == null || glowTexture.getPath().contains("/" + handler.getType().getTypeName().toLowerCase() + "_")){
			if(((DragonRenderer)renderer).glowTexture != null){
				glowTexture = ((DragonRenderer)renderer).glowTexture;
			}
		}

		if(glowTexture == null && handler.getSkinData().skinPreset.skinAges.get(handler.getLevel()).defaultSkin){
			ResourceLocation location = new ResourceLocation(DragonSurvivalMod.MODID, "textures/dragon/" + handler.getType().getTypeName().toLowerCase(Locale.ROOT) + "_" + handler.getLevel().name.toLowerCase(Locale.ROOT) + "_glow.png");
			if(Minecraft.getInstance().getResourceManager().getResource(location).isPresent()){
				glowTexture = location;
			}
		}

		if(glowTexture != null){
			RenderType type = RenderType.eyes(glowTexture);
			VertexConsumer vertexConsumer = bufferIn.getBuffer(type);
			((DragonRenderer)renderer).isRenderLayers = true;
			renderer.render(getEntityModel().getModel(getEntityModel().getModelResource(entitylivingbaseIn)), entitylivingbaseIn, partialTicks, type, matrixStackIn, bufferIn, vertexConsumer, 0, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
			((DragonRenderer)renderer).isRenderLayers = false;
		}else{
			ResourceLocation dynamicGlowKey = new ResourceLocation(DragonSurvivalMod.MODID, "dynamic_glow_" + entitylivingbaseIn.getPlayer().getStringUUID());
			((DragonRenderer)renderer).isRenderLayers = true;

			if(ageGroup.layerSettings.values().stream().anyMatch(s -> s.glowing)){
				RenderType type = RenderType.eyes(dynamicGlowKey);
				VertexConsumer vertexConsumer = bufferIn.getBuffer(type);
				renderer.render(ClientDragonRender.dragonModel.getModel(ClientDragonRender.dragonModel.getModelResource(null)), entitylivingbaseIn, partialTicks, type, matrixStackIn, bufferIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
			}
			((DragonRenderer)renderer).isRenderLayers = false;
		}
	}
}