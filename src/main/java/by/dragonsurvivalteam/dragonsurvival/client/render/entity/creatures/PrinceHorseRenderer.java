package by.dragonsurvivalteam.dragonsurvival.client.render.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.PrinceHorseEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.specialty.DynamicGeoEntityRenderer;

public class PrinceHorseRenderer extends DynamicGeoEntityRenderer<PrinceHorseEntity> {
	public PrinceHorseRenderer(final EntityRendererProvider.Context context, final GeoModel<PrinceHorseEntity> model) {
		super(context, model);
		getRenderLayers().add(new CustomBlockAndItemGeoLayer<>(this));
	}

//	@Override
//	protected ItemStack getHeldItemForBone(String boneName, PrinceHorseEntity currentEntity){
//		if(boneName.equalsIgnoreCase("left_item")){
//			return currentEntity.getItemBySlot(EquipmentSlot.MAINHAND);
//			//return mainHand;
//		}
//
//		return null;
//	}

//	@Override
//	protected void preRenderItem(PoseStack matrixStack, ItemStack item, String boneName, PrinceHorseEntity currentEntity, IBone bone){
////		matrixStack.last().normal().mul(bone.getWorldSpaceNormal());
////		matrixStack.last().pose().multiply(bone.getWorldSpaceXform());
////		matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
//		matrixStack.translate(0.0, -0.3, -0.5);
//	}
}