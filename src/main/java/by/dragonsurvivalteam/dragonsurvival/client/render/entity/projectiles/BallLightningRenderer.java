package by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.BallLightningEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class BallLightningRenderer extends GeoEntityRenderer<BallLightningEntity> {
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DragonSurvivalMod.MODID, "textures/entity/lightning_texture.png");

	public BallLightningRenderer(EntityRendererProvider.Context renderManager, GeoModel<BallLightningEntity> modelProvider) {
		super(renderManager, modelProvider);
	}

	@Override
	protected int getBlockLightLevel(@NotNull final BallLightningEntity entity, @NotNull final BlockPos position) {
		return 15;
	}

	@Override
	public void actuallyRender(PoseStack poseStack, @NotNull BallLightningEntity entity, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha_){
		if(entity.tickCount >= 2 || !(entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25D)){
			poseStack.pushPose();
			poseStack.translate(0F, -0.2F, 0F);
			poseStack.scale(2.0F, 2.0F, 2.0F);
			poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
			poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
			poseStack.popPose();
		}
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull final BallLightningEntity entity) {
		return TEXTURE_LOCATION;
	}
}