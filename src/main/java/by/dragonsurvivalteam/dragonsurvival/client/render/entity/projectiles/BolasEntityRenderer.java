package by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.Bolas;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BolasEntityRenderer extends EntityRenderer<Bolas> {
	public BolasEntityRenderer(final Context context) {
		super(context);
	}

	@Override
	public void render(final Bolas bolas, float p_225623_2_, float p_225623_3_, @NotNull final PoseStack poseStack, @NotNull final MultiBufferSource bufferSource, int p_225623_6_) {
		if (bolas.tickCount >= 2 || !(entityRenderDispatcher.camera.getEntity().distanceToSqr(bolas) < 12.25D)) {
			poseStack.pushPose();
			poseStack.translate(0F, -0.2F, 0F);
			poseStack.scale(2.0F, 2.0F, 2.0F);
			poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
			poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
			Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(DSItems.lightningTextureItem), ItemDisplayContext.GROUND, p_225623_6_, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, bolas.level(), 0);

			poseStack.popPose();
			super.render(bolas, p_225623_2_, p_225623_3_, poseStack, bufferSource, p_225623_6_);
		}

		super.render(bolas, p_225623_2_, p_225623_3_, poseStack, bufferSource, p_225623_6_);
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull final Bolas ignored) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}