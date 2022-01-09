package by.jackraidenph.dragonsurvival.client.render.entity.dragon;

import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationLayer;
import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationObject.Texture;
import by.jackraidenph.dragonsurvival.client.SkinCustomization.DragonCustomizationHandler;
import by.jackraidenph.dragonsurvival.client.handlers.DragonSkins;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.DragonEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.util.HashMap;

public class DragonGlowCustomizationLayer extends GeoLayerRenderer<DragonEntity>
{
	private final IGeoRenderer<DragonEntity> renderer;
	
	public DragonGlowCustomizationLayer(IGeoRenderer<DragonEntity> entityRendererIn)
	{
		super(entityRendererIn);
		this.renderer = entityRendererIn;
	}
	
	@Override
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
	{
		if(!((DragonRenderer)renderer).renderLayers) return;
		if(entitylivingbaseIn.hasEffect(Effects.INVISIBILITY)) return;
		
		DragonStateHandler handler = DragonStateProvider.getCap(entitylivingbaseIn.getPlayer()).orElse(null);
		if (handler == null) return;
		
		for(CustomizationLayer layer : CustomizationLayer.values()){
			String key = handler.getSkin().playerSkinLayers.getOrDefault(handler.getLevel(), new HashMap<>()).getOrDefault(layer, null);
			
			if(key != null){
				Texture text = DragonCustomizationHandler.getSkin(entitylivingbaseIn.getPlayer(), layer, key, handler.getType());
				
				if(text != null && text.glowing) {
					ResourceLocation texture = DragonCustomizationHandler.getSkinTexture(entitylivingbaseIn.getPlayer(), layer, key, handler.getType());
					
					if(DragonSkins.getPlayerSkin(entitylivingbaseIn.getPlayer(), handler.getType(), handler.getLevel()) != null) {
						return;
					}
					
					if (texture != null) {
						RenderType type = RenderType.eyes(texture);
						IVertexBuilder vertexConsumer = bufferIn.getBuffer(type);
						renderer.render(getEntityModel().getModel(getEntityModel().getModelLocation(entitylivingbaseIn)), entitylivingbaseIn, partialTicks, type, matrixStackIn, bufferIn, vertexConsumer, 0, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
					}
				}
			}
		}
		
	}
}
