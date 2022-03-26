package by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon;

import by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationLayer;
import by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationObject.Texture;
import by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.DragonCustomizationHandler;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.DragonSkins;
import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.Dragon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.util.HashMap;

public class DragonCustomizationLayer extends GeoLayerRenderer<Dragon>{
	private final IGeoRenderer<Dragon> renderer;

	public DragonCustomizationLayer(IGeoRenderer<Dragon> entityRendererIn){
		super(entityRendererIn);
		this.renderer = entityRendererIn;
	}

	@Override
	public void render(PoseStack pStack, MultiBufferSource bufferIn, int packedLightIn, Dragon entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch){
		if(entitylivingbaseIn.hasEffect(MobEffects.INVISIBILITY)){
			return;
		}

		DragonStateHandler handler = DragonStateProvider.getCap(entitylivingbaseIn.getPlayer()).orElse(null);
		if(handler == null){
			return;
		}

		for(CustomizationLayer layer : CustomizationLayer.values()){
			String key = handler.getSkin().playerSkinLayers.getOrDefault(handler.getLevel(), new HashMap<>()).getOrDefault(layer, null);

			if(key != null){
				Texture text = DragonCustomizationHandler.getSkin(entitylivingbaseIn.getPlayer(), layer, key, handler.getType());

				if(text != null && !text.glowing){
					ResourceLocation texture = DragonCustomizationHandler.getSkinTexture(entitylivingbaseIn.getPlayer(), layer, key, handler.getType());
					if(ClientDragonRender.dragonModel.getTextureLocation(entitylivingbaseIn) == texture){
						return;
					}

					if(DragonSkins.getPlayerSkin(entitylivingbaseIn.getPlayer(), handler.getType(), handler.getLevel()) != null){
						return;
					}

					if(texture != null){
						RenderType type = renderer.getRenderType(entitylivingbaseIn, partialTicks, pStack, bufferIn, null, packedLightIn, texture);
						VertexConsumer vertexConsumer = bufferIn.getBuffer(type);
						((DragonRenderer)renderer).isLayer = true;
						renderer.render(ClientDragonRender.dragonModel.getModel(ClientDragonRender.dragonModel.getModelLocation(null)), entitylivingbaseIn, partialTicks, type, pStack, bufferIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1F);
						((DragonRenderer)renderer).isLayer = false;
					}
				}
			}
		}
	}
}