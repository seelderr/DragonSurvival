/*package by.dragonsurvivalteam.dragonsurvival.client.models;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.Hunter;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class HunterModel<T extends Hunter> extends HierarchicalModel<T> implements ArmedModel, HeadedModel{
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart hat;
	private final ModelPart arms;
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;
	private final ModelPart rightArm;
	private final ModelPart leftArm;

	public HunterModel(ModelPart pRoot){
		root = pRoot;
		head = pRoot.getChild("head");
		hat = head.getChild("hat");
		hat.visible = false;
		arms = pRoot.getChild("arms");
		leftLeg = pRoot.getChild("left_leg");
		rightLeg = pRoot.getChild("right_leg");
		leftArm = pRoot.getChild("left_arm");
		rightArm = pRoot.getChild("right_arm");
	}

	public static LayerDefinition createBodyLayer(){
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
		partdefinition1.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.45F)), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F), PartPose.offset(0.0F, -2.0F, 0.0F));
		partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F).texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition partdefinition2 = partdefinition.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F).texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F), PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, -0.75F, 0.0F, 0.0F));
		partdefinition2.addOrReplaceChild("left_shoulder", CubeListBuilder.create().texOffs(44, 22).mirror().addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F), PartPose.ZERO);
		partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(-2.0F, 12.0F, 0.0F));
		partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(2.0F, 12.0F, 0.0F));
		partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(-5.0F, 2.0F, 0.0F));
		partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(5.0F, 2.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public ModelPart root(){
		return root;
	}

	@Override
	public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch){
		head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
		head.xRot = pHeadPitch * ((float)Math.PI / 180F);
		if(riding){
			rightArm.xRot = -(float)Math.PI / 5F;
			rightArm.yRot = 0.0F;
			rightArm.zRot = 0.0F;
			leftArm.xRot = -(float)Math.PI / 5F;
			leftArm.yRot = 0.0F;
			leftArm.zRot = 0.0F;
			rightLeg.xRot = -1.4137167F;
			rightLeg.yRot = (float)Math.PI / 10F;
			rightLeg.zRot = 0.07853982F;
			leftLeg.xRot = -1.4137167F;
			leftLeg.yRot = -(float)Math.PI / 10F;
			leftLeg.zRot = -0.07853982F;
		}else{
			rightArm.xRot = Mth.cos(pLimbSwing * 0.6662F + (float)Math.PI) * 2.0F * pLimbSwingAmount * 0.5F;
			rightArm.yRot = 0.0F;
			rightArm.zRot = 0.0F;
			leftArm.xRot = Mth.cos(pLimbSwing * 0.6662F) * 2.0F * pLimbSwingAmount * 0.5F;
			leftArm.yRot = 0.0F;
			leftArm.zRot = 0.0F;
			rightLeg.xRot = Mth.cos(pLimbSwing * 0.6662F) * 1.4F * pLimbSwingAmount * 0.5F;
			rightLeg.yRot = 0.0F;
			rightLeg.zRot = 0.0F;
			leftLeg.xRot = Mth.cos(pLimbSwing * 0.6662F + (float)Math.PI) * 1.4F * pLimbSwingAmount * 0.5F;
			leftLeg.yRot = 0.0F;
			leftLeg.zRot = 0.0F;
		}

		AbstractIllager.IllagerArmPose abstractillager$illagerarmpose = pEntity.getArmPose();
		if(abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.ATTACKING){
			if(pEntity.getMainHandItem().isEmpty()){
				AnimationUtils.animateZombieArms(leftArm, rightArm, true, attackTime, pAgeInTicks);
			}else{
				AnimationUtils.swingWeaponDown(rightArm, leftArm, pEntity, attackTime, pAgeInTicks);
			}
		}else if(abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.SPELLCASTING){
			rightArm.z = 0.0F;
			rightArm.x = -5.0F;
			leftArm.z = 0.0F;
			leftArm.x = 5.0F;
			rightArm.xRot = Mth.cos(pAgeInTicks * 0.6662F) * 0.25F;
			leftArm.xRot = Mth.cos(pAgeInTicks * 0.6662F) * 0.25F;
			rightArm.zRot = 2.3561945F;
			leftArm.zRot = -2.3561945F;
			rightArm.yRot = 0.0F;
			leftArm.yRot = 0.0F;
		}else if(abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.BOW_AND_ARROW){
			rightArm.yRot = -0.1F + head.yRot;
			rightArm.xRot = -(float)Math.PI / 2F + head.xRot;
			leftArm.xRot = -0.9424779F + head.xRot;
			leftArm.yRot = head.yRot - 0.4F;
			leftArm.zRot = (float)Math.PI / 2F;
		}else if(abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.CROSSBOW_HOLD){
			AnimationUtils.animateCrossbowHold(rightArm, leftArm, head, true);
		}else if(abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE){
			AnimationUtils.animateCrossbowCharge(rightArm, leftArm, pEntity, true);
		}else if(abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.CELEBRATING){
			rightArm.z = 0.0F;
			rightArm.x = -5.0F;
			rightArm.xRot = Mth.cos(pAgeInTicks * 0.6662F) * 0.05F;
			rightArm.zRot = 2.670354F;
			rightArm.yRot = 0.0F;
			leftArm.z = 0.0F;
			leftArm.x = 5.0F;
			leftArm.xRot = Mth.cos(pAgeInTicks * 0.6662F) * 0.05F;
			leftArm.zRot = -2.3561945F;
			leftArm.yRot = 0.0F;
		}

		boolean flag = abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.CROSSED;
		arms.visible = flag;
		leftArm.visible = !flag;
		rightArm.visible = !flag;
	}

	public ModelPart getHat(){
		return hat;
	}

	@Override
	public ModelPart getHead(){
		return head;
	}

	@Override
	public void translateToHand(HumanoidArm pSide, PoseStack pPoseStack){
		getArm(pSide).translateAndRotate(pPoseStack);
	}

	private ModelPart getArm(HumanoidArm pArm){
		return pArm == HumanoidArm.LEFT ? leftArm : rightArm;
	}
}*/