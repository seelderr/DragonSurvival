package by.dragonsurvivalteam.dragonsurvival.client.render.util;

import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animatable.model.CoreGeoModel;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.state.BoneSnapshot;

import java.util.Map;

public class CustomTickAnimationController extends AnimationController<DragonEntity> {
	public double speed = 1;
	public double lastTick = 0;

	public CustomTickAnimationController(final DragonEntity animatable, final String name, int transitionTickTime, final AnimationController.AnimationStateHandler<DragonEntity> animationHandler) {
		super(animatable, name, transitionTickTime, animationHandler);
	}

	@Override
	public void process(final CoreGeoModel<DragonEntity> model, final AnimationState<DragonEntity> state, final Map<String, CoreGeoBone> bones, final Map<String, BoneSnapshot> snapshots, double seekTime, boolean crashWhenCantFindBone) {
		double tickDifference = seekTime - lastTick;
		lastTick = seekTime;

		super.process(model, state, bones, snapshots, (seekTime + tickDifference * (speed - 1)), crashWhenCantFindBone);
	}
}