package by.dragonsurvivalteam.dragonsurvival.client.models;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.DragonEditorHandler;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.objects.SkinPreset.SkinAgeGroup;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.SkinCap;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

import java.util.Locale;
import java.util.Objects;

public class DragonModel extends AnimatedGeoModel<DragonEntity>{

	private final double lookSpeed = 0.05;
	private final double lookDistance = 10;
	private ResourceLocation currentTexture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/dragon/cave_newborn.png");

	@Override
	public ResourceLocation getModelLocation(DragonEntity dragonEntity){
		return new ResourceLocation(DragonSurvivalMod.MODID, "geo/dragon_model.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(DragonEntity dragonEntity){
		DragonStateHandler handler = DragonStateProvider.getCap(dragonEntity.getPlayer()).orElse(null);

		if(handler.getSkin().blankSkin){
			return new ResourceLocation(DragonSurvivalMod.MODID, "textures/dragon/blank_skin_" + handler.getType().name().toLowerCase(Locale.ROOT) + ".png");
		}

		if(currentTexture == null){
			SkinAgeGroup ageGroup = handler.getSkin().skinPreset.skinAges.get(handler.getLevel());

			if(ageGroup.defaultSkin){
				return new ResourceLocation(DragonSurvivalMod.MODID, "textures/dragon/" + handler.getType().name().toLowerCase(Locale.ROOT) + "_" + handler.getLevel().name.toLowerCase(Locale.ROOT) + ".png");
			}else{
				String skin = ageGroup.layerSettings.get(EnumSkinLayer.BASE).selectedSkin;
				ResourceLocation location = DragonEditorHandler.getSkinTexture(dragonEntity.getPlayer(), EnumSkinLayer.BASE, Objects.equals(skin, SkinCap.defaultSkinValue) ? "Skin" : skin, DragonUtils.getDragonType(dragonEntity.getPlayer()));

				if(location != null){
					return location;
				}
			}
		}
		return currentTexture;
	}

	public void setCurrentTexture(ResourceLocation currentTexture){
		this.currentTexture = currentTexture;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(DragonEntity animatable){
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/dragon.animations.json");
	}

	@Override
	public void setMolangQueries(IAnimatable animatable, double currentTick){
		super.setMolangQueries(animatable, currentTick);
		if(!(animatable instanceof DragonEntity)){
			return;
		}

		DragonEntity dragonEntity = (DragonEntity)animatable;
		MolangParser parser = GeckoLibCache.getInstance().parser;
		PlayerEntity player = dragonEntity.getPlayer();

		if(player == null){
			return;
		}

		DragonStateHandler handler = DragonUtils.getHandler(player);

		parser.setValue("query.delta_y", player.getDeltaMovement().y);
		parser.setValue("query.head_yaw", handler.getMovementData().headYaw);
		parser.setValue("query.head_pitch", handler.getMovementData().headPitch);

		double bodyYawChange = Functions.angleDifference((float)handler.getMovementData().bodyYawLastTick, (float)handler.getMovementData().bodyYaw);
		double headYawChange = Functions.angleDifference((float)handler.getMovementData().headYawLastTick, (float)handler.getMovementData().headYaw);
		double headPitchChange = Functions.angleDifference((float)handler.getMovementData().headPitchLastTick, (float)handler.getMovementData().headPitch);

		ModifiableAttributeInstance gravity = player.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
		double g = gravity.getValue();

		dragonEntity.tailMotionUp = MathHelper.clamp(MathHelper.lerp(0.25, dragonEntity.tailMotionUp, ServerFlightHandler.isFlying(player) ? 0 : (player.getDeltaMovement().y + g) * 50), -10, 10);
		dragonEntity.tailMotionSide = MathHelper.lerp(0.1, MathHelper.clamp(dragonEntity.tailMotionSide + (ServerFlightHandler.isGliding(player) ? 0 : bodyYawChange), -50, 50), 0);

		dragonEntity.bodyYawAverage.add(bodyYawChange);
		while(dragonEntity.bodyYawAverage.size() > 10){
			dragonEntity.bodyYawAverage.remove(0);
		}

		dragonEntity.headYawAverage.add(headYawChange);
		while(dragonEntity.headYawAverage.size() > 10){
			dragonEntity.headYawAverage.remove(0);
		}

		dragonEntity.headPitchAverage.add(headPitchChange);
		while(dragonEntity.headPitchAverage.size() > 10){
			dragonEntity.headPitchAverage.remove(0);
		}

		dragonEntity.tailSideAverage.add(dragonEntity.tailMotionSide);
		while(dragonEntity.tailSideAverage.size() > 10){
			dragonEntity.tailSideAverage.remove(0);
		}

		dragonEntity.tailUpAverage.add(dragonEntity.tailMotionUp * -1);
		while(dragonEntity.tailUpAverage.size() > 10){
			dragonEntity.tailUpAverage.remove(0);
		}

		double bodyYawAvg = dragonEntity.bodyYawAverage.stream().mapToDouble(a -> a).sum() / dragonEntity.bodyYawAverage.size();
		double headYawAvg = dragonEntity.headYawAverage.stream().mapToDouble(a -> a).sum() / dragonEntity.headYawAverage.size();
		double headPitchAvg = dragonEntity.headPitchAverage.stream().mapToDouble(a -> a).sum() / dragonEntity.headPitchAverage.size();
		double tailSideAvg = dragonEntity.tailSideAverage.stream().mapToDouble(a -> a).sum() / dragonEntity.tailSideAverage.size();
		double tailUpAvg = dragonEntity.tailUpAverage.stream().mapToDouble(a -> a).sum() / dragonEntity.tailUpAverage.size();

		double query_body_yaw_change = MathHelper.lerp(0.1, dragonEntity.body_yaw_change, bodyYawAvg);
		double query_head_yaw_change = MathHelper.lerp(0.1, dragonEntity.head_yaw_change, headYawAvg);
		double query_head_pitch_change = MathHelper.lerp(0.1, dragonEntity.head_pitch_change, headPitchAvg);
		double query_tail_motion_up = MathHelper.lerp(0.1, dragonEntity.tail_motion_up, tailUpAvg);
		double query_tail_motion_side = MathHelper.lerp(0.1, dragonEntity.tail_motion_side, tailSideAvg);

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

		if(!handler.getEmotes().currentEmotes.isEmpty()){
			EntityPredicate predicate = new EntityPredicate().range(lookDistance).allowSameTeam().allowInvulnerable().allowNonAttackable().selector(player::canSee);
			Entity lookAt = player.level.getNearestLoadedEntity(LivingEntity.class, predicate, player, player.getX(), player.getEyeY(), player.getZ(), player.getBoundingBox().inflate(lookDistance, 3.0D, lookDistance));

			if(lookAt != null && lookAt.isAlive()){
				if(player.distanceToSqr(lookAt) <= (lookDistance * lookDistance)){
					float xRotD = 0;
					float yRotD = 0;

					{
						double d0 = lookAt.getX() - player.getX();
						double d1 = lookAt.getEyeY() - player.getEyeY();
						double d2 = lookAt.getZ() - player.getZ();
						double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
						xRotD = (float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI)));
					}

					{
						double d0 = lookAt.getX() - player.getX();
						double d1 = lookAt.getZ() - player.getZ();
						yRotD = (float)(MathHelper.atan2(d1, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
					}

					double bodyRot = handler.getMovementData().bodyYaw - (((int)handler.getMovementData().bodyYaw) / 360) * 360;
					double yawDif = Math.abs(MathHelper.degreesDifference(yRotD, (float)bodyRot));

					if(yawDif <= 90 && Math.abs(xRotD) <= 30){
						dragonEntity.lookYaw = MathHelper.lerp(lookSpeed, dragonEntity.lookYaw, MathHelper.wrapDegrees(-bodyRot + yRotD));
						dragonEntity.lookPitch = MathHelper.lerp(lookSpeed, dragonEntity.lookPitch, xRotD);
					}else{
						dragonEntity.lookYaw = Math.round(MathHelper.lerp(lookSpeed, dragonEntity.lookYaw, 0) * 100.0) / 100.0;
						dragonEntity.lookPitch = Math.round(MathHelper.lerp(lookSpeed, dragonEntity.lookPitch, 0) * 100.0) / 100.0;

						if(Math.abs(dragonEntity.lookYaw) < 0.1){
							dragonEntity.lookYaw = 0;
						}
						if(Math.abs(dragonEntity.lookPitch) < 0.1){
							dragonEntity.lookPitch = 0;
						}
					}
				}
			}
		}else{
			dragonEntity.lookYaw = Math.round(MathHelper.lerp(lookSpeed, dragonEntity.lookYaw, 0) * 100.0) / 100.0;
			dragonEntity.lookPitch = Math.round(MathHelper.lerp(lookSpeed, dragonEntity.lookPitch, 0) * 100.0) / 100.0;

			if(Math.abs(dragonEntity.lookYaw) < 0.1){
				dragonEntity.lookYaw = 0;
			}
			if(Math.abs(dragonEntity.lookPitch) < 0.1){
				dragonEntity.lookPitch = 0;
			}
		}

		double query_look_at_yaw = Math.abs(MathHelper.degreesDifference((float)dragonEntity.lookYaw, (float)dragonEntity.look_at_yaw)) > 0.3 ? MathHelper.lerp(0.1, dragonEntity.look_at_yaw, dragonEntity.lookYaw) : dragonEntity.lookYaw;
		double query_look_at_pitch = Math.abs(MathHelper.degreesDifference((float)dragonEntity.lookPitch, (float)dragonEntity.look_at_pitch)) > 0.3 ? MathHelper.lerp(0.1, dragonEntity.look_at_pitch, dragonEntity.lookPitch) : dragonEntity.lookPitch;

		parser.setValue("query.look_at_yaw", query_look_at_yaw);
		parser.setValue("query.look_at_pitch", query_look_at_pitch);

		dragonEntity.look_at_yaw = query_look_at_yaw;
		dragonEntity.look_at_pitch = query_look_at_pitch;
	}
}