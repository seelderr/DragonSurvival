<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/render/entity/dragon/ClawsAndTeethRenderLayer.java
package by.jackraidenph.dragonsurvival.client.render.entity.dragon;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.handlers.ClientEvents;
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.DragonEntity;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
=======
package by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientEvents;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/render/entity/dragon/ClawsAndTeethRenderLayer.java
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class ClawsAndTeethRenderLayer extends GeoLayerRenderer<DragonEntity>{

	private final IGeoRenderer<DragonEntity> renderer;

	public ClawsAndTeethRenderLayer(IGeoRenderer<DragonEntity> entityRendererIn){
		super(entityRendererIn);
		this.renderer = entityRendererIn;
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/render/entity/dragon/ClawsAndTeethRenderLayer.java
	public void render(PoseStack pStack, MultiBufferSource bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
	{
		if(!((DragonRenderer)renderer).renderLayers) return;
		if(entitylivingbaseIn.hasEffect(MobEffects.INVISIBILITY)) return;
		
		DragonStateHandler handler = DragonStateProvider.getCap(entitylivingbaseIn.getPlayer()).orElse(null);
		if (handler == null || (!handler.getClawInventory().renderClaws && (ConfigHandler.SERVER.syncClawRender.get() || entitylivingbaseIn.getPlayer() == Minecraft.getInstance().player))) return;
		
=======
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch){
		if(!((DragonRenderer)renderer).renderLayers){
			return;
		}
		if(entitylivingbaseIn.hasEffect(Effects.INVISIBILITY)){
			return;
		}

		DragonStateHandler handler = DragonStateProvider.getCap(entitylivingbaseIn.getPlayer()).orElse(null);
		if(handler == null || !handler.getClawInventory().renderClaws){
			return;
		}

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/render/entity/dragon/ClawsAndTeethRenderLayer.java
		String clawTexture = constructClaws(entitylivingbaseIn.getPlayer());

		if(clawTexture != null){
			ResourceLocation texture = new ResourceLocation(DragonSurvivalMod.MODID, clawTexture);
			((DragonRenderer)renderer).isLayer = true;
			renderToolLayer(pStack, bufferIn, packedLightIn, entitylivingbaseIn, partialTicks, texture, renderer, getEntityModel());
			((DragonRenderer)renderer).isLayer = false;
		}

		String teethTexture = constructTeethTexture(entitylivingbaseIn.getPlayer());

		if(teethTexture != null){
			ResourceLocation texture = new ResourceLocation(DragonSurvivalMod.MODID, teethTexture);
			((DragonRenderer)renderer).isLayer = true;
			renderToolLayer(pStack, bufferIn, packedLightIn, entitylivingbaseIn, partialTicks, texture, renderer, getEntityModel());
			((DragonRenderer)renderer).isLayer = false;
		}
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/render/entity/dragon/ClawsAndTeethRenderLayer.java
	
	public String constructClaws(Player playerEntity)
	{
=======

	public String constructClaws(PlayerEntity playerEntity){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/render/entity/dragon/ClawsAndTeethRenderLayer.java
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
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/render/entity/dragon/ClawsAndTeethRenderLayer.java
	
	public String constructTeethTexture(Player playerEntity)
	{
=======

	public String constructTeethTexture(PlayerEntity playerEntity){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/render/entity/dragon/ClawsAndTeethRenderLayer.java
		String texture = "textures/armor/";
		ItemStack swordItem = DragonStateProvider.getCap(playerEntity).orElse(null).getClawInventory().getClawsInventory().getItem(0);

		if(!swordItem.isEmpty() && swordItem.getItem() instanceof TieredItem){
			texture = ClientEvents.getMaterial(texture, swordItem);
		}else{
			return null;
		}

		return texture + "dragon_teeth.png";
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/render/entity/dragon/ClawsAndTeethRenderLayer.java
	
	private void renderToolLayer(PoseStack  pStack, MultiBufferSource bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float partialTicks, ResourceLocation texture, IGeoRenderer<DragonEntity> renderer, GeoModelProvider entityModel)
	{
		RenderType type = renderer.getRenderType(entitylivingbaseIn, partialTicks, pStack, bufferIn, null, packedLightIn, texture);
		VertexConsumer vertexConsumer = bufferIn.getBuffer(type);
		
		renderer.render(entityModel.getModel(entityModel.getModelLocation(entitylivingbaseIn)), entitylivingbaseIn, partialTicks, type, pStack, bufferIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
=======

	private void renderToolLayer(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float partialTicks, ResourceLocation texture, IGeoRenderer<DragonEntity> renderer, GeoModelProvider entityModel){
		RenderType type = renderer.getRenderType(entitylivingbaseIn, partialTicks, matrixStackIn, bufferIn, null, packedLightIn, texture);
		IVertexBuilder vertexConsumer = bufferIn.getBuffer(type);

		renderer.render(entityModel.getModel(entityModel.getModelLocation(entitylivingbaseIn)), entitylivingbaseIn, partialTicks, type, matrixStackIn, bufferIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/render/entity/dragon/ClawsAndTeethRenderLayer.java
	}
}