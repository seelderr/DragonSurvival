package by.jackraidenph.dragonsurvival.client.models;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationLayer;
import by.jackraidenph.dragonsurvival.client.SkinCustomization.DragonCustomizationHandler;
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.DragonEntity;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.server.handlers.ServerFlightHandler;
import by.jackraidenph.dragonsurvival.util.Functions;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

import java.util.HashMap;

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
		if(currentTexture == null){
			DragonStateHandler handler = DragonStateProvider.getCap(dragonEntity.getPlayer()).orElse(null);
			ResourceLocation location = DragonCustomizationHandler.getSkinTexture(dragonEntity.getPlayer(), CustomizationLayer.BASE, handler.getSkin().playerSkinLayers.getOrDefault(handler.getLevel(), new HashMap<>()).getOrDefault(CustomizationLayer.BASE, "Skin"), DragonStateProvider.getDragonType(dragonEntity.getPlayer()));
		
			if(location != null){
				return location;
			}
		}
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
		Player player = dragonEntity.getPlayer();
		
		if(player == null) return;
		
		DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
		
		boolean renderRot = ConfigHandler.CLIENT.renderOtherPlayerRotation.get() || Minecraft.getInstance().player == player;
		renderRot = false;
		
		{
			double curFlightY = Mth.lerp(0.1, dragonEntity.flightY, player.getDeltaMovement().y);
			if(Float.isNaN((float)curFlightY)){
				curFlightY = 0;
			}
			
			dragonEntity.flightY = curFlightY;
			
			parser.setValue("query.flight_y", renderRot ? curFlightY : 0);
		}
		
		{
			Vec3 vector3d1 = player.getDeltaMovement();
			Vec3 vector3d = player.getViewVector(1f);
			double d0 = vector3d.horizontalDistanceSqr();
			double d1 = vector3d1.horizontalDistanceSqr();
			double d2 = (vector3d1.x * vector3d.x + vector3d1.z * vector3d.z) / Math.sqrt(d0 * d1);
			double d3 = vector3d1.x * vector3d.z - vector3d1.z * vector3d.x;
			
			float rot = ((float)(Math.signum(d3) * Math.acos(d2))) * 2;
			
			double curFlightX = Mth.lerp(0.1, Float.isNaN((float)dragonEntity.flightX) ? 0 : dragonEntity.flightX, rot);
			if(Float.isNaN((float)curFlightX)){
				curFlightX = 0;
			}
			
			dragonEntity.flightX = curFlightX;
			
			parser.setValue("query.flight_x", renderRot ? curFlightX : 0);
		}
		
		parser.setValue("query.delta_y", player.getDeltaMovement().y);
		parser.setValue("query.head_yaw", handler.getMovementData().headYaw);
		parser.setValue("query.head_pitch", handler.getMovementData().headPitch);
		
		double bodyYawChange = Functions.angleDifference((float)handler.getMovementData().bodyYawLastTick, (float)handler.getMovementData().bodyYaw);
		double headYawChange = Functions.angleDifference((float)handler.getMovementData().headYawLastTick, (float)handler.getMovementData().headYaw);
		double headPitchChange = Functions.angleDifference((float)handler.getMovementData().headPitchLastTick, (float)handler.getMovementData().headPitch);
		
		AttributeInstance gravity = player.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
		double g = gravity.getValue();
		
		dragonEntity.tailMotionUp = Mth.lerp(0.25, dragonEntity.tailMotionUp, ServerFlightHandler.isFlying(player) ? 0 : (player.getDeltaMovement().y + g) * 50);
		dragonEntity.tailMotionSide = Mth.lerp(0.1, Mth.clamp(dragonEntity.tailMotionSide + (ServerFlightHandler.isGliding(player) ? 0 : bodyYawChange), -50, 50), 0);
		
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
		double tailSideAvg = dragonEntity.tailSideAverage.stream().mapToDouble(a -> a).sum() / dragonEntity.tailSideAverage.size();
		double tailUpAvg = dragonEntity.tailUpAverage.stream().mapToDouble(a -> a).sum() / dragonEntity.tailUpAverage.size();
		
		double query_body_yaw_change = Mth.lerp(0.1, dragonEntity.body_yaw_change, bodyYawAvg);
		double query_head_yaw_change = Mth.lerp(0.1, dragonEntity.head_yaw_change, headYawAvg);
		double query_head_pitch_change = Mth.lerp(0.1, dragonEntity.head_pitch_change, headPitchAvg);
		double query_tail_motion_up = Mth.lerp(0.1, dragonEntity.tail_motion_up, tailUpAvg);
		double query_tail_motion_side =  Mth.lerp(0.1, dragonEntity.tail_motion_side, tailSideAvg);
		
		if(((DragonEntity)animatable).tailLocked || !ConfigHandler.CLIENT.enableTailPhysics.get()){
			dragonEntity.tailMotionUp = 0;
			dragonEntity.tailMotionSide = 0;
			
			dragonEntity.tail_motion_up = 0;
			dragonEntity.tail_motion_side = 0;
			
			query_tail_motion_up = 0;
			query_tail_motion_side = 0;
		}
		
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
	}
}
