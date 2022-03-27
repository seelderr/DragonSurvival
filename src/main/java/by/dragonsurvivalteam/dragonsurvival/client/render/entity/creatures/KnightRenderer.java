package by.dragonsurvivalteam.dragonsurvival.client.render.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.KnightEntity;
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

public class KnightRenderer extends GeoEntityRenderer<KnightEntity>{
	public KnightRenderer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<KnightEntity> modelProvider){
		super(renderManager, modelProvider);
	}

	@Override
	public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha){
		if(bone.getName().equals("left_item")){
			stack.pushPose();
			RenderUtils.moveToPivot(bone, stack);
			stack.mulPose(Vector3f.ZP.rotationDegrees(180));
			stack.translate(0.0, -0.3, -0.5);
			Minecraft.getInstance().getItemRenderer().renderStatic(mainHand, TransformType.THIRD_PERSON_LEFT_HAND, packedLightIn, packedOverlayIn, stack, rtb, 0);
			stack.popPose();
			bufferIn = rtb.getBuffer(RenderType.entitySmoothCutout(whTexture));
		}else if(bone.getName().equals("right_item")){
			stack.pushPose();
			RenderUtils.moveToPivot(bone, stack);
			stack.mulPose(Vector3f.XP.rotationDegrees(180));
			stack.translate(-0.05, 0.1, 0.1);
			stack.mulPose(Vector3f.XP.rotationDegrees(90));
			Minecraft.getInstance().getItemRenderer().renderStatic(offHand, TransformType.THIRD_PERSON_RIGHT_HAND, packedLightIn, packedOverlayIn, stack, rtb, 0);
			stack.popPose();
			bufferIn = rtb.getBuffer(RenderType.entitySmoothCutout(whTexture));
		}
		super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}