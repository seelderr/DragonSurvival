package by.dragonsurvivalteam.dragonsurvival.client.render.util;

import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.molang.MolangParser;

import java.util.List;
import java.util.Map;

public class CustomTickAnimationController extends AnimationController{
	public double speed = 1;
	public double lastTick = 0;

	public CustomTickAnimationController(IAnimatable animatable, String name, int transitionLength, IAnimationPredicate<DragonEntity> predicate){
		super(animatable, name, transitionLength, predicate);
		markNeedsReload();
	}

	@Override
	public void process(double tick, AnimationEvent event, List modelRendererList, Map boneSnapshotCollection, MolangParser parser, boolean crashWhenCantFindBone){
		double tickDif = tick - lastTick;
		lastTick = tick;
		super.process(tick + (tickDif * (speed - 1.0)), event, modelRendererList, boneSnapshotCollection, parser, crashWhenCantFindBone);
	}
}