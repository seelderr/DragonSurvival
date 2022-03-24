package by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/render/entity/dragon/DragonGlowLayerRenderer.java
import by.jackraidenph.dragonsurvival.client.handlers.DragonSkins;
import by.jackraidenph.dragonsurvival.client.render.ClientDragonRender;
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.DragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
=======
import by.dragonsurvivalteam.dragonsurvival.client.handlers.DragonSkins;
import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/render/entity/dragon/DragonGlowLayerRenderer.java
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
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/render/entity/dragon/DragonGlowLayerRenderer.java
	public void render(PoseStack pStack, MultiBufferSource bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
	{
		if(!((DragonRenderer)renderer).renderLayers) return;
		if(entitylivingbaseIn == ClientDragonRender.dragonArmor) return;
		
		Player player = entitylivingbaseIn.getPlayer();
		DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
		
=======
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch){
		if(!((DragonRenderer)renderer).renderLayers){
			return;
		}
		if(entitylivingbaseIn == ClientDragonRender.dragonArmor){
			return;
		}

		PlayerEntity player = entitylivingbaseIn.getPlayer();
		DragonStateHandler handler = DragonUtils.getHandler(player);

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/render/entity/dragon/DragonGlowLayerRenderer.java
		if(handler != null){
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
				renderer.render(getEntityModel().getModel(getEntityModel().getModelLocation(entitylivingbaseIn)), entitylivingbaseIn, partialTicks, type, pStack, bufferIn, vertexConsumer, 0, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
				((DragonRenderer)renderer).isLayer = false;
			}
		}
	}
}