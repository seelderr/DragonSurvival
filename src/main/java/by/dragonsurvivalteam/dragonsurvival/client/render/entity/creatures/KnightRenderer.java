package by.dragonsurvivalteam.dragonsurvival.client.render.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.KnightEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.DynamicGeoEntityRenderer;

public class KnightRenderer extends DynamicGeoEntityRenderer<KnightEntity> {
	public KnightRenderer(final EntityRendererProvider.Context context, final GeoModel<KnightEntity> model) {
		super(context, model);
	}

	@Override
	public void preRender(final PoseStack poseStack, final KnightEntity animatable, final BakedGeoModel model, final MultiBufferSource bufferSource, final VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Minecraft.getInstance().getProfiler().push("knight");
		super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void postRender(final PoseStack poseStack, final KnightEntity animatable, final BakedGeoModel model, final MultiBufferSource bufferSource, final VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		super.postRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
		Minecraft.getInstance().getProfiler().pop();
	}

	// TODO 1.20 :: FIX

//	@Override
//	protected boolean isArmorBone(GeoBone bone){
//		return false;
//	}
//
//	@Nullable
//	@Override
//	protected ResourceLocation getTextureForBone(String boneName, KnightEntity currentEntity){
//		return null;
//	}




//	@Override
//	protected ItemStack getHeldItemForBone(String boneName, KnightEntity currentEntity){
//
//		if(boneName.equalsIgnoreCase("left_item")){
//			return mainHand;
//		}else if(boneName.equalsIgnoreCase("right_item")){
//			return offHand;
//		}
//
//
//		return null;
//	}
//
//	@Override
//	protected TransformType getCameraTransformForItemAtBone(ItemStack boneItem, String boneName) {
//		if(boneName.equalsIgnoreCase("left_item")){
//			return TransformType.THIRD_PERSON_LEFT_HAND;
//		}else if(boneName.equalsIgnoreCase("right_item")){
//			return TransformType.THIRD_PERSON_RIGHT_HAND;
//		}
//		return null;
//	}
//
//	@Nullable
//	@Override
//	protected BlockState getHeldBlockForBone(String boneName, KnightEntity currentEntity) {
//		return null;
//	}
//
//	@Override
//	protected void preRenderItem(PoseStack matrixStack, ItemStack item, String boneName, KnightEntity currentEntity, IBone bone){
//		if(boneName.equalsIgnoreCase("left_item")){
//			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
//			matrixStack.translate(0.0, -0.3, -0.5);
//		}else{
//			matrixStack.translate(0.0, 0, -0.3);
//		}
//
//	}
}