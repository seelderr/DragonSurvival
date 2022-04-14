package by.dragonsurvivalteam.dragonsurvival.client.render.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.PrinceHorseEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.util.RenderUtils;

public class PrinceHorseRenderer extends GeoEntityRenderer<PrinceHorseEntity>{
	public PrinceHorseRenderer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<PrinceHorseEntity> modelProvider){
		super(renderManager, modelProvider);
	}

	@Override
	public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha){
		if(rtb != null && stack != null && bone != null && whTexture != null){
			if(bone.getName().equals("left_item") && !mainHand.isEmpty()){
				stack.pushPose();
				RenderUtils.moveToPivot(bone, stack);
				stack.mulPose(Vector3f.ZP.rotationDegrees(180));
				stack.translate(0.0, -0.3, -0.5);
				Minecraft.getInstance().getItemRenderer().renderStatic(mainHand, TransformType.THIRD_PERSON_LEFT_HAND, packedLightIn, packedOverlayIn, stack, rtb, 0);
				stack.popPose();
				bufferIn = rtb.getBuffer(RenderType.entitySmoothCutout(whTexture));
			}
		}
		super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}