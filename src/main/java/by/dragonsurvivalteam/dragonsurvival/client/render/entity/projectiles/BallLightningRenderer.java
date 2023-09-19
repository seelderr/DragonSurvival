package by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.BallLightningEntity;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
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
	public void render(final BallLightningEntity entity, float p_225623_2_, float p_225623_3_, @NotNull final PoseStack poseStack, @NotNull final MultiBufferSource buffer, int p_225623_6_) {
		if (entity.tickCount >= 2 || !(entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25D)) {
			poseStack.pushPose();
			poseStack.translate(0F, -0.2F, 0F);
			poseStack.scale(2.0F, 2.0F, 2.0F);
			poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
			poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
			Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(DSItems.lightningTextureItem), ItemDisplayContext.GROUND, p_225623_6_, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), 0);
			poseStack.popPose();

			super.render(entity, p_225623_2_, p_225623_3_, poseStack, buffer, p_225623_6_);
		}

		super.render(entity, p_225623_2_, p_225623_3_, poseStack, buffer, p_225623_6_);
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull final BallLightningEntity entity) {
		return TEXTURE_LOCATION;
	}
}