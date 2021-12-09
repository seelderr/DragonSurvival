package by.jackraidenph.dragonsurvival.gecko.renderer;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.gecko.entity.DragonEntity;
import by.jackraidenph.dragonsurvival.handlers.ClientSide.ClientEvents;
import by.jackraidenph.dragonsurvival.util.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
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
		this.addLayer(new teethRenderLayer(this));
		this.addLayer(new clawsRenderLayer(this));
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
	
	public class teethRenderLayer extends GeoLayerRenderer<DragonEntity>{
		
		private final IGeoRenderer<DragonEntity> renderer;
		
		public teethRenderLayer(IGeoRenderer<DragonEntity> entityRendererIn)
		{
			super(entityRendererIn);
			this.renderer = entityRendererIn;
		}
		
		@Override
		public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
		{
			DragonStateHandler handler = DragonStateProvider.getCap(entitylivingbaseIn.getPlayer()).orElse(null);
			if(handler == null || !handler.getClawInventory().renderClaws) return;
			
			String text = constructTeethTexture(entitylivingbaseIn.getPlayer());
			
			if(text != null) {
				ResourceLocation texture = new ResourceLocation(DragonSurvivalMod.MODID, text);
				clawsRenderLayer.renderToolLayer(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, partialTicks, texture, renderer, getEntityModel());
			}
		}
		
		public  String constructTeethTexture(PlayerEntity playerEntity) {
			String texture = "textures/armor/";
			ItemStack swordItem = DragonStateProvider.getCap(playerEntity).orElse(null).getClawInventory().getClawsInventory().getItem(0);
			
			if(!swordItem.isEmpty() && swordItem.getItem() instanceof TieredItem){
				texture = ClientEvents.getMaterial(texture, swordItem);
			}else{
				return null;
			}
			
			return texture + "dragon_teeth.png";
		}
	}
	
	
	public static class clawsRenderLayer extends GeoLayerRenderer<DragonEntity>{
		
		private final IGeoRenderer<DragonEntity> renderer;
		
		public clawsRenderLayer(IGeoRenderer<DragonEntity> entityRendererIn)
		{
			super(entityRendererIn);
			this.renderer = entityRendererIn;
		}
		
		@Override
		public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
		{
			DragonStateHandler handler = DragonStateProvider.getCap(entitylivingbaseIn.getPlayer()).orElse(null);
			if(handler == null || !handler.getClawInventory().renderClaws) return;
			
			String text = constructClaws(entitylivingbaseIn.getPlayer());
			
			if(text != null) {
				ResourceLocation texture = new ResourceLocation(DragonSurvivalMod.MODID, text);
				renderToolLayer(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, partialTicks, texture, renderer, getEntityModel());
			}
		}
		
		public static String constructClaws(PlayerEntity playerEntity) {
			String texture = "textures/armor/";
			DragonStateHandler handler = DragonStateProvider.getCap(playerEntity).orElse(null);
			ItemStack clawItem = handler.getClawInventory().getClawsInventory().getItem(handler.getType() == DragonType.CAVE ? 1 : handler.getType() == DragonType.FOREST ? 2 : 3);
			if(!clawItem.isEmpty() && clawItem.getItem() instanceof TieredItem){
				texture = ClientEvents.getMaterial(texture, clawItem);
			}else{
				return null;
			}
			
			return texture + "dragon_claws.png";
		}
		
		private static void renderToolLayer(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float partialTicks, ResourceLocation texture, IGeoRenderer<DragonEntity> renderer, GeoModelProvider entityModel)
		{
			RenderType type = renderer.getRenderType(entitylivingbaseIn, partialTicks, matrixStackIn, bufferIn, null, packedLightIn, texture);
			IVertexBuilder vertexConsumer = bufferIn.getBuffer(type);
			
			renderer.render(entityModel.getModel(entityModel.getModelLocation(entitylivingbaseIn)),
			                entitylivingbaseIn,
			                partialTicks,
			                type,
			                matrixStackIn,
			                bufferIn,
			                vertexConsumer,
			                packedLightIn,
			                OverlayTexture.NO_OVERLAY,
			                1.0F, 1.0F, 1.0F, 1.0F
			);
		}
	}
}
