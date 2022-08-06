package by.dragonsurvivalteam.dragonsurvival.client.render.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.KnightEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

public class KnightRenderer extends ExtendedGeoEntityRenderer<KnightEntity>{
	public KnightRenderer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<KnightEntity> modelProvider){
		super(renderManager, modelProvider);
	}

	@Override
	public void renderEarly(KnightEntity animatable, PoseStack stackIn, float ticks, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks){
		Minecraft.getInstance().getProfiler().push("knight");
		super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
	}

	@Override
	public void renderLate(KnightEntity animatable, PoseStack stackIn, float ticks, MultiBufferSource renderTypeBuffer, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks){
		super.renderLate(animatable, stackIn, ticks, renderTypeBuffer, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
		Minecraft.getInstance().getProfiler().pop();
	}

	@Override
	protected boolean isArmorBone(GeoBone bone){
		return false;
	}

	@Nullable
	@Override
	protected ResourceLocation getTextureForBone(String boneName, KnightEntity currentEntity){
		return null;
	}

	@org.jetbrains.annotations.Nullable
	@Override
	protected ItemStack getHeldItemForBone(String boneName, KnightEntity currentEntity){

		if(boneName.equalsIgnoreCase("left_item")){
			return mainHand;
		}else if(boneName.equalsIgnoreCase("right_item")){
			return offHand;
		}


		return null;
	}

	@Override
	protected TransformType getCameraTransformForItemAtBone(ItemStack boneItem, String boneName){
		if(boneName.equalsIgnoreCase("left_item")){
			return TransformType.THIRD_PERSON_LEFT_HAND;
		}else if(boneName.equalsIgnoreCase("right_item")){
			return TransformType.THIRD_PERSON_RIGHT_HAND;
		}
		return null;
	}

	@Nullable
	@Override
	protected BlockState getHeldBlockForBone(String boneName, KnightEntity currentEntity){
		return null;
	}

	@Override
	protected void preRenderItem(PoseStack matrixStack, ItemStack item, String boneName, KnightEntity currentEntity, IBone bone){
		if(boneName.equalsIgnoreCase("left_item")){
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
			matrixStack.translate(0.0, -0.3, -0.5);
		}else{
			matrixStack.translate(0.0, 0, -0.3);
		}

	}

	@Override
	protected void preRenderBlock(PoseStack matrixStack, BlockState block, String boneName, KnightEntity currentEntity){

	}

	@Override
	protected void postRenderItem(PoseStack matrixStack, ItemStack item, String boneName, KnightEntity currentEntity, IBone bone){

	}

	@Override
	protected void postRenderBlock(PoseStack matrixStack, BlockState block, String boneName, KnightEntity currentEntity){

	}
}