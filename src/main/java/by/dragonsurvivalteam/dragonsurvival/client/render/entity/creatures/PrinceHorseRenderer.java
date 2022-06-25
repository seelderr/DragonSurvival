package by.dragonsurvivalteam.dragonsurvival.client.render.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.PrinceHorseEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class PrinceHorseRenderer extends GeoEntityRenderer<PrinceHorseEntity>{
	public PrinceHorseRenderer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<PrinceHorseEntity> modelProvider){
		super(renderManager, modelProvider);
	}


	@Override
	public void render(PrinceHorseEntity entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn){
		super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
		GeoModel model = getGeoModelProvider().getModel(getGeoModelProvider().getModelLocation(entity));

		if(!mainHand.isEmpty()){
			model.getBone("left_item").ifPresent(bone -> {
				PoseStack newMatrixStack = new PoseStack();
				newMatrixStack.last().normal().mul(bone.getWorldSpaceNormal());
				newMatrixStack.last().pose().multiply(bone.getWorldSpaceXform());
				newMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
				newMatrixStack.translate(0.0, -0.3, -0.5);
				Minecraft.getInstance().getItemRenderer().renderStatic(mainHand, TransformType.THIRD_PERSON_LEFT_HAND, packedLightIn, 0, newMatrixStack, bufferIn, 0);
			});
		}
	}
}