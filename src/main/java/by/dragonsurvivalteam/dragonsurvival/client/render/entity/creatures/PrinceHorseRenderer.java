package by.dragonsurvivalteam.dragonsurvival.client.render.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.PrinceHorseEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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

public class PrinceHorseRenderer extends ExtendedGeoEntityRenderer<PrinceHorseEntity>{
	public PrinceHorseRenderer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<PrinceHorseEntity> modelProvider){
		super(renderManager, modelProvider);
	}

	@Override
	public void renderEarly(PrinceHorseEntity animatable, PoseStack stackIn, float ticks, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks){
		Minecraft.getInstance().getProfiler().push("prince_on_horse");
		super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
	}

	@Override
	public void renderLate(PrinceHorseEntity animatable, PoseStack stackIn, float ticks, MultiBufferSource renderTypeBuffer, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks){
		super.renderLate(animatable, stackIn, ticks, renderTypeBuffer, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
		Minecraft.getInstance().getProfiler().pop();
	}

	@Override
	protected boolean isArmorBone(GeoBone bone){
		return false;
	}

	@Nullable
	@Override
	protected ResourceLocation getTextureForBone(String boneName, PrinceHorseEntity currentEntity){
		return null;
	}

	protected ItemStack getHeldItemForBone(String boneName, PrinceHorseEntity currentEntity){
		if(boneName.equalsIgnoreCase("left_item")){
			return mainHand;
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
	protected BlockState getHeldBlockForBone(String boneName, PrinceHorseEntity currentEntity){
		return null;
	}

	@Override
	protected void preRenderItem(PoseStack matrixStack, ItemStack item, String boneName, PrinceHorseEntity currentEntity, IBone bone){
//		matrixStack.last().normal().mul(bone.getWorldSpaceNormal());
//		matrixStack.last().pose().multiply(bone.getWorldSpaceXform());
//		matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
		matrixStack.translate(0.0, -0.3, -0.5);
	}

	@Override
	protected void preRenderBlock(PoseStack matrixStack, BlockState block, String boneName, PrinceHorseEntity currentEntity){

	}

	@Override
	protected void postRenderItem(PoseStack matrixStack, ItemStack item, String boneName, PrinceHorseEntity currentEntity, IBone bone){

	}

	@Override
	protected void postRenderBlock(PoseStack matrixStack, BlockState block, String boneName, PrinceHorseEntity currentEntity){

	}
}