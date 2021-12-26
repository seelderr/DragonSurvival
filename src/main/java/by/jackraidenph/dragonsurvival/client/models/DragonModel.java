package by.jackraidenph.dragonsurvival.client.models;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.DragonEntity;
import by.jackraidenph.dragonsurvival.server.handlers.ServerFlightHandler;
import net.minecraft.client.Minecraft;
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
	
	@Override
	public void setMolangQueries(IAnimatable animatable, double currentTick)
	{
		super.setMolangQueries(animatable, currentTick);
		MolangParser parser = GeckoLibCache.getInstance().parser;
		Minecraft minecraftInstance = Minecraft.getInstance();
		PlayerEntity player = minecraftInstance.player;
		DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
		
		float headRot = player.yRot != 0.0 ? player.yRot : player.yHeadRot;
		double bodyYaw = handler.getMovementData().bodyYaw;
		float bodyAndHeadYawDiff = (((float)bodyYaw) - headRot);
		
		parser.setValue("query.delta_y", player.getDeltaMovement().y);
		parser.setValue("query.head_yaw", bodyAndHeadYawDiff);
		parser.setValue("query.head_pitch", handler.getMovementData().headPitch);
		
		double bodyYawChange = MathHelper.clamp(handler.getMovementData().bodyYawLastTick - handler.getMovementData().bodyYaw, -3, 3);
		double headYawChange = handler.getMovementData().headYawLastTick - handler.getMovementData().headYaw;
		double headPitchChange = handler.getMovementData().headPitchLastTick - handler.getMovementData().headPitch;
		
		ModifiableAttributeInstance gravity = player.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
		double g = gravity.getValue();
		
		if(Math.abs(bodyYawChange) <= 0.5){
			//tailSwingDir
			tailSwing += 0.003 * (tailSwingDir ? 1 : -1);
			
			if(tailSwing >= 0.4 && tailSwingDir){
				tailSwingDir = false;
			}else if(tailSwing <= -0.4 && !tailSwingDir){
				tailSwingDir = true;
			}
		}else{
			tailSwing = MathHelper.lerp(0.1, tailSwing, 0);
			tailSwingDir = bodyYawChange < 0;
		}
		
		tailMotionUp = MathHelper.lerp(0.1, tailMotionUp, ServerFlightHandler.isFlying(player) ? 0 : (player.getDeltaMovement().y + g));
		tailMotionSide = MathHelper.lerp(0.1, tailMotionSide, bodyYawChange + tailSwing);
		
		if(((DragonEntity)animatable).tailLocked){
			tailMotionUp = 0;
			tailMotionSide = 0;
		}
		
		parser.setValue("query.body_yaw_change", bodyYawChange);
		parser.setValue("query.head_yaw_change", headYawChange);
		parser.setValue("query.head_pitch_change", headPitchChange);
		parser.setValue("query.tail_motion_up", tailMotionUp * -1);
		parser.setValue("query.tail_motion_side", tailMotionSide);
	}
	
	private double tailMotionSide;
	private double tailMotionUp;
	private boolean tailSwingDir = false;
	private double tailSwing = 0;
	
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
