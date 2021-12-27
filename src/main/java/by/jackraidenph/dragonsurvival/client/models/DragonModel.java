package by.jackraidenph.dragonsurvival.client.models;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.DragonEntity;
import by.jackraidenph.dragonsurvival.server.handlers.ServerFlightHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

public class DragonModel extends AnimatedGeoModel<DragonEntity> {

    private ResourceLocation currentTexture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/dragon/cave_newborn.png");

    @Override
    public ResourceLocation getModelLocation(DragonEntity dragonEntity) {
        return new ResourceLocation(DragonSurvivalMod.MODID, "geo/dragon_model.geo.json");
    }

    public void setCurrentTexture(ResourceLocation currentTexture) {
        this.currentTexture = currentTexture;
    }

    @Override
    public ResourceLocation getTextureLocation(DragonEntity dragonEntity) {
        return currentTexture;
    }

	@Override
	public ResourceLocation getAnimationFileLocation(DragonEntity animatable) {
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/dragon.animations.json");
	}
	
	private final double lookSpeed = 0.05;
	private final double lookDistance = 10;
	
	@Override
	public void setMolangQueries(IAnimatable animatable, double currentTick)
	{
		super.setMolangQueries(animatable, currentTick);
		if(!(animatable instanceof DragonEntity))return;
		
		DragonEntity dragonEntity = (DragonEntity)animatable;
		MolangParser parser = GeckoLibCache.getInstance().parser;
		PlayerEntity player = dragonEntity.getPlayer();
		
		if(player == null) return;
		
		DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
		
		float headRot = player.yRot != 0.0 ? player.yRot : player.yHeadRot;
		double bodyYaw = handler.getMovementData().bodyYaw;
		float bodyAndHeadYawDiff = (((float)bodyYaw) - headRot);
		
		parser.setValue("query.delta_y", player.getDeltaMovement().y);
		parser.setValue("query.head_yaw", bodyAndHeadYawDiff);
		parser.setValue("query.head_pitch", handler.getMovementData().headPitch);
		
		double bodyYawChange = handler.getMovementData().bodyYawLastTick - handler.getMovementData().bodyYaw;
		double headYawChange = handler.getMovementData().headYawLastTick - handler.getMovementData().headYaw;
		double headPitchChange = handler.getMovementData().headPitchLastTick - handler.getMovementData().headPitch;
		ModifiableAttributeInstance gravity = player.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
		double g = gravity.getValue();
		
		dragonEntity.tailSwing += 0.003 * (dragonEntity.tailSwingDir ? 1 : -1);
		if(dragonEntity.tailSwing >= 0.4 && dragonEntity.tailSwingDir){
			dragonEntity.tailSwingDir = false;
		}else if(dragonEntity.tailSwing <= -0.4 && !dragonEntity.tailSwingDir){
			dragonEntity.tailSwingDir = true;
		}
		
		dragonEntity.tailMotionMax = Math.max(0, MathHelper.lerp(0.1, dragonEntity.tailMotionMax, 0));
		
		if(Math.abs(bodyYawChange) > dragonEntity.tailMotionMax){
			dragonEntity.tailMotionMax += Math.abs(bodyYawChange);
		}
		
		double tailMultiplier = MathHelper.clamp(dragonEntity.tailMotionMax, 0, 4);
		
		dragonEntity.tailMotionUp = MathHelper.lerp(0.25, dragonEntity.tailMotionUp, ServerFlightHandler.isFlying(player) ? 0 : (player.getDeltaMovement().y + g) * 1.5);
		dragonEntity.tailMotionSide = MathHelper.lerp(0.1, dragonEntity.tailMotionSide, (bodyYawChange / Math.max(1, tailMultiplier)) + dragonEntity.tailSwing);
		
		if(((DragonEntity)animatable).tailLocked){
			dragonEntity.tailMotionUp = 0;
			dragonEntity.tailMotionSide = 0;
		}
		
		dragonEntity.bodyYawAverage.add(bodyYawChange);
		while(dragonEntity.bodyYawAverage.size() > 10) dragonEntity.bodyYawAverage.remove(0);
		
		dragonEntity.headYawAverage.add(headYawChange);
		while(dragonEntity.headYawAverage.size() > 10) dragonEntity.headYawAverage.remove(0);
		
		dragonEntity.headPitchAverage.add(headPitchChange);
		while(dragonEntity.headPitchAverage.size() > 10) dragonEntity.headPitchAverage.remove(0);
		
		dragonEntity.tailSideAverage.add(dragonEntity.tailMotionSide);
		while(dragonEntity.tailSideAverage.size() > 10) dragonEntity.tailSideAverage.remove(0);
		
		dragonEntity.tailUpAverage.add(dragonEntity.tailMotionUp * -1);
		while(dragonEntity.tailUpAverage.size() > 10) dragonEntity.tailUpAverage.remove(0);
		
		double bodyYawAvg = dragonEntity.bodyYawAverage.stream().mapToDouble(a -> a).sum() / dragonEntity.bodyYawAverage.size();
		double headYawAvg = dragonEntity.headYawAverage.stream().mapToDouble(a -> a).sum() / dragonEntity.headYawAverage.size();
		double headPitchAvg = dragonEntity.headPitchAverage.stream().mapToDouble(a -> a).sum() / dragonEntity.headPitchAverage.size();
		double tailSideAvg = MathHelper.clamp(dragonEntity.tailSideAverage.stream().mapToDouble(a -> a).sum() / dragonEntity.tailSideAverage.size(), -3, 3);
		double tailUpAvg = MathHelper.clamp(dragonEntity.tailUpAverage.stream().mapToDouble(a -> a).sum() / dragonEntity.tailUpAverage.size(), -3, 2);
		
		double query_body_yaw_change = Math.abs(MathHelper.degreesDifference((float)bodyYawAvg, (float)dragonEntity.body_yaw_change)) > 0.3 ? MathHelper.lerp(0.1, dragonEntity.body_yaw_change, bodyYawAvg) : bodyYawAvg;
		double query_head_yaw_change = Math.abs(MathHelper.degreesDifference((float)headYawAvg, (float)dragonEntity.head_yaw_change)) > 0.3 ? MathHelper.lerp(0.1, dragonEntity.head_yaw_change, headYawAvg) : headYawAvg;
		double query_head_pitch_change = Math.abs(MathHelper.degreesDifference((float)headPitchAvg, (float)dragonEntity.head_pitch_change)) > 0.3 ? MathHelper.lerp(0.1, dragonEntity.head_pitch_change, headPitchAvg) : headPitchAvg;
		double query_tail_motion_up = Math.abs(MathHelper.degreesDifference((float)tailUpAvg, (float)dragonEntity.tail_motion_up)) > 0.3 ? MathHelper.lerp(0.1, dragonEntity.tail_motion_up, tailUpAvg) : tailUpAvg;
		double query_tail_motion_side = Math.abs(MathHelper.degreesDifference((float)tailSideAvg, (float)dragonEntity.tail_motion_side)) > 0.3 ? MathHelper.lerp(0.1, dragonEntity.tail_motion_side, tailSideAvg) : tailSideAvg;
		
		parser.setValue("query.body_yaw_change", query_body_yaw_change);
		parser.setValue("query.head_yaw_change", query_head_yaw_change);
		parser.setValue("query.head_pitch_change", query_head_pitch_change);
		parser.setValue("query.tail_motion_up", query_tail_motion_up);
		parser.setValue("query.tail_motion_side", query_tail_motion_side);
		
		dragonEntity.body_yaw_change = query_body_yaw_change;
		dragonEntity.head_yaw_change = query_head_yaw_change;
		dragonEntity.head_pitch_change = query_head_pitch_change;
		dragonEntity.tail_motion_up = query_tail_motion_up;
		dragonEntity.tail_motion_side = query_tail_motion_side;
		
		if(handler.getEmotes().getCurrentEmote() != null) {
			EntityPredicate predicate = new EntityPredicate().range(lookDistance).allowSameTeam().allowInvulnerable().allowNonAttackable().selector(player::canSee);
			Entity lookAt = player.level.getNearestLoadedEntity(LivingEntity.class, predicate, player, player.getX(), player.getEyeY(), player.getZ(), player.getBoundingBox().inflate(lookDistance, 3.0D, lookDistance));
			
			if (lookAt != null && lookAt.isAlive()) {
				if (player.distanceToSqr(lookAt) <= (lookDistance * lookDistance)) {
					float xRotD = 0;
					float yRotD = 0;
					
					{
						double d0 = lookAt.getX() - player.getX();
						double d1 = lookAt.getEyeY() - player.getEyeY();
						double d2 = lookAt.getZ() - player.getZ();
						double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
						xRotD = (float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI)));
					}
					
					{
						double d0 = lookAt.getX() - player.getX();
						double d1 = lookAt.getZ() - player.getZ();
						yRotD = (float)(MathHelper.atan2(d1, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
					}
					
					double bodyRot = handler.getMovementData().bodyYaw - (((int)handler.getMovementData().bodyYaw) / 360) * 360;
					double yawDif = Math.abs(MathHelper.degreesDifference((float)yRotD, (float)bodyRot));
					
					if (yawDif <= 90 && Math.abs(xRotD) <= 30) {
						dragonEntity.lookYaw = MathHelper.lerp(lookSpeed, dragonEntity.lookYaw, MathHelper.wrapDegrees(-bodyRot + yRotD));
						dragonEntity.lookPitch = MathHelper.lerp(lookSpeed, dragonEntity.lookPitch, xRotD);
					}  else {
						dragonEntity.lookYaw = Math.round(MathHelper.lerp(lookSpeed, dragonEntity.lookYaw, 0)*100.0)/100.0;
						dragonEntity.lookPitch = Math.round(MathHelper.lerp(lookSpeed, dragonEntity.lookPitch, 0)*100.0)/100.0;
						
						if(Math.abs(dragonEntity.lookYaw) < 0.1) dragonEntity.lookYaw = 0;
						if(Math.abs(dragonEntity.lookPitch) < 0.1) dragonEntity.lookPitch = 0;
					}
				}
			}
		} else {
			dragonEntity.lookYaw = Math.round(MathHelper.lerp(lookSpeed, dragonEntity.lookYaw, 0)*100.0)/100.0;
			dragonEntity.lookPitch = Math.round(MathHelper.lerp(lookSpeed, dragonEntity.lookPitch, 0)*100.0)/100.0;
			
			if(Math.abs(dragonEntity.lookYaw) < 0.1) dragonEntity.lookYaw = 0;
			if(Math.abs(dragonEntity.lookPitch) < 0.1) dragonEntity.lookPitch = 0;
		}
		
		double query_look_at_yaw = Math.abs(MathHelper.degreesDifference((float)dragonEntity.lookYaw, (float)dragonEntity.look_at_yaw)) > 0.3 ? MathHelper.lerp(0.1, dragonEntity.look_at_yaw, dragonEntity.lookYaw) : dragonEntity.lookYaw;
		double query_look_at_pitch = Math.abs(MathHelper.degreesDifference((float)dragonEntity.lookPitch, (float)dragonEntity.look_at_pitch)) > 0.3 ? MathHelper.lerp(0.1, dragonEntity.look_at_pitch, dragonEntity.lookPitch) : dragonEntity.lookPitch;
		
		parser.setValue("query.look_at_yaw", query_look_at_yaw);
		parser.setValue("query.look_at_pitch", query_look_at_pitch);
		
		dragonEntity.look_at_yaw = query_look_at_yaw;
		dragonEntity.look_at_pitch = query_look_at_pitch;
	}
	
	@Override
	public void setLivingAnimations(DragonEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
		
		// TODO Replace temp head turn with system that includes vertical
		PlayerEntity player = entity.getPlayer();
		if (player != null && !player.isSleeping() && !player.isPassenger()) {
			DragonStateProvider.getCap(player).ifPresent(playerStateHandler -> {
				if(entity.neckLocked){
					return;
				}
				/*IBone neck = this.getAnimationProcessor().getBone("Neck"); // rot(0, -22.5, 0)
				IBone neck4 = this.getAnimationProcessor().getBone("Neck4"); // rot(0, 0, -10)
				IBone neck3 = this.getAnimationProcessor().getBone("Neck3"); // rot(12.5, -15, 30), mov(-0.25, 0, 0)
				IBone neck2 = this.getAnimationProcessor().getBone("Neck2"); // rot(9.04, -5, 35.55), mov(0, 0.75, 0)
				IBone neck1 = this.getAnimationProcessor().getBone("Neck1"); // rot(5, -17.5, 30), mov(0.25, -0.25, 0)
				IBone head = this.getAnimationProcessor().getBone("Head"); // rot(-39.41, -35, 55), mov(2.25, -1.25, -1.25)*/
				// neck0: rot(-115, 0, 0), mov(-4, 16, -15)
				// neck1: rot(25, 0, 0), mov(-4, 16, -15)
				// neck2: rot(30, 0, 0), mov(-2.01, 27.92, -12.3163)
				// neck3: rot(30, 0, 0), mov(-2, 22.7551, -13.1526)
				// neck4: rot(10, 0, 0), mov(-3, 19.4825, -13.4911)
				// head: rot(20, 0, 0), mov(2.1, 37.4402, -12.953)
				AnimationProcessor animationProcessor = getAnimationProcessor();
				IBone neck = animationProcessor.getBone("Neck");
				IBone neck1 = animationProcessor.getBone("Neck1");
				IBone neck2 = animationProcessor.getBone("Neck2");
				IBone neck3 = animationProcessor.getBone("Neck3");
				IBone neck4 = animationProcessor.getBone("Neck4");
				IBone head = animationProcessor.getBone("Head");
				float rotation = -1F * (((float) playerStateHandler.getMovementData().bodyYaw) - (float) playerStateHandler.getMovementData().headYaw) * (float) Math.PI / 180F;
				if (rotation > (float) Math.PI)
					rotation = (float) Math.PI;
				if (rotation < -(float) Math.PI)
					rotation = -(float) Math.PI;
				neck.setRotationY(-0.125F * rotation);
				neck4.setRotationZ(-1F * -0.0555556F * rotation);
				neck3.setRotationX((rotation >= 0 ? -1F : 1F) * 0.06944F * rotation + 0.523599F);
				neck3.setRotationY(-0.083333F * rotation);
				neck3.setRotationZ(-1F * 0.166667F * rotation);
				neck3.setPositionX(-1F * -0.07957F * rotation);
				neck2.setRotationX((rotation >= 0 ? -1F : 1F) * 0.0502222F * rotation + 0.523599F);
				neck2.setRotationY(-0.0277778F * rotation);
				neck2.setRotationZ(-1F * 0.1975F * rotation);
				neck2.setPositionY(-1F * (rotation >= 0 ? -1F : 1F) * 0.2387324F * rotation);
				neck1.setRotationX((rotation >= 0 ? -1F : 1F) * 0.02777763F * rotation + 0.174533F);
				neck1.setRotationY(-0.0972222F * rotation);
				neck1.setRotationZ(-1F * 0.1666667F * rotation);
				neck1.setPositionX(-1F * 0.0795775F * rotation);
				neck1.setPositionY(-1F * (rotation >= 0 ? -1F : 1F) * -0.0795775F * rotation);
				head.setRotationX((rotation >= 0 ? -1F : 1F) * -0.2189445F * rotation + 0.349066F);
				head.setRotationY(-0.1944444F * rotation);
				head.setRotationZ(-1F * 0.3055555F * rotation);
				head.setPositionX(-1F * 0.716197F * rotation);
				head.setPositionY(-1F * (rotation >= 0 ? -1F : 1F) * -0.397887F * rotation);
				head.setPositionZ((rotation >= 0 ? 1F : -1F) * -0.397887F * rotation);
			});
		}
	}
}
