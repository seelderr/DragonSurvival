package by.dragonsurvivalteam.dragonsurvival.client.models;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.DragonEditorHandler;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SkinPreset.SkinAgeGroup;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.config.ClientConfig;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.molang.MolangParser;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.resource.GeckoLibCache;

import java.util.Collections;
import java.util.Locale;

public class DragonModel extends AnimatedGeoModel<DragonEntity>{
	private final double lookSpeed = 0.05;
	private final double lookDistance = 10;
	private final ResourceLocation defaultTexture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/dragon/cave_newborn.png");
	private ResourceLocation currentTexture = defaultTexture;

	@Override
	public ResourceLocation getModelLocation(DragonEntity dragon){
		return new ResourceLocation(DragonSurvivalMod.MODID, "geo/dragon_model.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(DragonEntity dragon){
		if(dragon.player != null || dragon.getPlayer() != null){
			DragonStateHandler handler = DragonUtils.getHandler(dragon.getPlayer());
			SkinAgeGroup ageGroup = handler.getSkinData().skinPreset.skinAges.get(handler.getLevel());

			if(handler.getSkinData().recompileSkin){
				DragonEditorHandler.generateSkinTextures(dragon);
			}

			if(handler.getSkinData().blankSkin){
				return new ResourceLocation(DragonSurvivalMod.MODID, "textures/dragon/blank_skin_" + handler.getType().getTypeName().toLowerCase(Locale.ROOT) + ".png");
			}

			if(ageGroup.defaultSkin){
				if(currentTexture != null){
					return currentTexture;
				}
				return new ResourceLocation(DragonSurvivalMod.MODID, "textures/dragon/" + handler.getType().getTypeName().toLowerCase(Locale.ROOT) + "_" + handler.getLevel().name.toLowerCase(Locale.ROOT) + ".png");
			}

			if(handler.getSkinData().isCompiled && currentTexture == null){
				return new ResourceLocation(DragonSurvivalMod.MODID, "dynamic_normal_" + dragon.getPlayer().getStringUUID());
			}
		}
		return currentTexture == null ? defaultTexture : currentTexture;
	}

	public void setCurrentTexture(ResourceLocation currentTexture){
		this.currentTexture = currentTexture;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(DragonEntity animatable){
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/dragon.animations.json");
	}

	/**
	 * Copied code from Geckolib pre version 3.0.47 which broke dragon rendering
	 * @link <a href="https://github.com/bernie-g/geckolib/blob/4e864bd2d4a0a8dceea01f600b7031cb2fba3a3b/Forge/src/main/java/software/bernie/geckolib3/model/AnimatedGeoModel.java#L51">Github link</a>
	 */
	@Override
	public void setCustomAnimations(DragonEntity entity, int uniqueID, AnimationEvent customPredicate) {
		AnimationData manager = entity.getFactory().getOrCreateAnimationData(uniqueID);

		if (manager.startTick == -1) {
			manager.startTick = entity.tickCount + Minecraft.getInstance().getFrameTime();
		}

		if (!Minecraft.getInstance().isPaused() || manager.shouldPlayWhilePaused) {
			manager.tick = getCurrentTick() - manager.startTick;
			double gameTick = manager.tick;
			double deltaTicks = gameTick - lastGameTickTime;
			seekTime += deltaTicks;
			lastGameTickTime = gameTick;
		}

		AnimationEvent<DragonEntity> predicate;
		if (customPredicate == null) {
			predicate = new AnimationEvent<DragonEntity>(entity, 0, 0, (float) (manager.tick - lastGameTickTime), false,
			                                             Collections.emptyList());
		} else {
			predicate = customPredicate;
		}

		predicate.animationTick = seekTime;
		getAnimationProcessor().preAnimationSetup(predicate.getAnimatable(), seekTime);

		if (!getAnimationProcessor().getModelRendererList().isEmpty()) {
			getAnimationProcessor().tickAnimation(entity, uniqueID, seekTime, predicate, GeckoLibCache.getInstance().parser, shouldCrashOnMissing);
		}
	}

	@Override
	public void setMolangQueries(IAnimatable animatable, double currentTick){
		super.setMolangQueries(animatable, currentTick);
		if(!(animatable instanceof DragonEntity dragon) || dragon.player == null){
			return;
		}

		MolangParser parser = GeckoLibCache.getInstance().parser;
		Player player = dragon.getPlayer();

		if(player == null){
			return;
		}

		DragonStateHandler handler = DragonUtils.getHandler(player);

		parser.setValue("query.delta_y", () -> player.getDeltaMovement().y);
		parser.setValue("query.head_yaw", () -> DragonUtils.getHandler(player).getMovementData().headYaw);
		parser.setValue("query.head_pitch", () -> DragonUtils.getHandler(player).getMovementData().headPitch);

		double bodyYawChange = Functions.angleDifference((float)handler.getMovementData().bodyYawLastTick, (float)handler.getMovementData().bodyYaw);
		double headYawChange = Functions.angleDifference((float)handler.getMovementData().headYawLastTick, (float)handler.getMovementData().headYaw);
		double headPitchChange = Functions.angleDifference((float)handler.getMovementData().headPitchLastTick, (float)handler.getMovementData().headPitch);

		AttributeInstance gravity = player.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
		double g = gravity.getValue();

		dragon.tailMotionUp = Mth.clamp(Mth.lerp(0.25, dragon.tailMotionUp, ServerFlightHandler.isFlying(player) ? 0 : (player.getDeltaMovement().y + g) * 50), -10, 10);
		dragon.tailMotionSide = Mth.lerp(0.1, Mth.clamp(dragon.tailMotionSide + (ServerFlightHandler.isGliding(player) ? 0 : bodyYawChange), -50, 50), 0);

		dragon.bodyYawAverage.add(bodyYawChange);
		while(dragon.bodyYawAverage.size() > 10){
			dragon.bodyYawAverage.remove(0);
		}

		dragon.headYawAverage.add(headYawChange);
		while(dragon.headYawAverage.size() > 10){
			dragon.headYawAverage.remove(0);
		}

		dragon.headPitchAverage.add(headPitchChange);
		while(dragon.headPitchAverage.size() > 10){
			dragon.headPitchAverage.remove(0);
		}

		dragon.tailSideAverage.add(dragon.tailMotionSide);
		while(dragon.tailSideAverage.size() > 10){
			dragon.tailSideAverage.remove(0);
		}

		dragon.tailUpAverage.add(dragon.tailMotionUp * -1);
		while(dragon.tailUpAverage.size() > 10){
			dragon.tailUpAverage.remove(0);
		}

		double bodyYawAvg = dragon.bodyYawAverage.stream().mapToDouble(a -> a).sum() / dragon.bodyYawAverage.size();
		double headYawAvg = dragon.headYawAverage.stream().mapToDouble(a -> a).sum() / dragon.headYawAverage.size();
		double headPitchAvg = dragon.headPitchAverage.stream().mapToDouble(a -> a).sum() / dragon.headPitchAverage.size();
		double tailSideAvg = dragon.tailSideAverage.stream().mapToDouble(a -> a).sum() / dragon.tailSideAverage.size();
		double tailUpAvg = dragon.tailUpAverage.stream().mapToDouble(a -> a).sum() / dragon.tailUpAverage.size();

		double query_body_yaw_change = Mth.lerp(0.1, dragon.body_yaw_change, bodyYawAvg);
		double query_head_yaw_change = Mth.lerp(0.1, dragon.head_yaw_change, headYawAvg);
		double query_head_pitch_change = Mth.lerp(0.1, dragon.head_pitch_change, headPitchAvg);
		double query_tail_motion_up = Mth.lerp(0.1, dragon.tail_motion_up, tailUpAvg);
		double query_tail_motion_side = Mth.lerp(0.1, dragon.tail_motion_side, tailSideAvg);

		if(((DragonEntity)animatable).tailLocked || !ClientConfig.enableTailPhysics){
			dragon.tailMotionUp = 0;
			dragon.tailMotionSide = 0;

			dragon.tail_motion_up = 0;
			dragon.tail_motion_side = 0;

			query_tail_motion_up = 0;
			query_tail_motion_side = 0;
		}

		parser.setValue("query.body_yaw_change", () -> query_body_yaw_change);
		parser.setValue("query.head_yaw_change", () -> query_head_yaw_change);
		parser.setValue("query.head_pitch_change", () -> query_head_pitch_change);

		double finalQuery_tail_motion_up = query_tail_motion_up;
		parser.setValue("query.tail_motion_up", () -> finalQuery_tail_motion_up);

		double finalQuery_tail_motion_side = query_tail_motion_side;
		parser.setValue("query.tail_motion_side", () -> finalQuery_tail_motion_side);

		dragon.body_yaw_change = query_body_yaw_change;
		dragon.head_yaw_change = query_head_yaw_change;
		dragon.head_pitch_change = query_head_pitch_change;
		dragon.tail_motion_up = query_tail_motion_up;
		dragon.tail_motion_side = query_tail_motion_side;
	}
}