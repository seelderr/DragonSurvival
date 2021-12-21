package by.jackraidenph.dragonsurvival.gecko.entity.dragon;

import net.minecraft.client.Minecraft;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

import java.util.HashMap;
import java.util.List;

class LandingAnimationController extends AnimationController
{
	public LandingAnimationController(IAnimatable animatable, IAnimationPredicate<DragonEntity> predicate)
	{
		super(animatable, "landing", 2, predicate);
	}
	
	public double speed = 1;
	public double ticks = 0;
	
	@Override
	public void process(double tick, AnimationEvent event, List modelRendererList, HashMap boneSnapshotCollection, MolangParser parser, boolean crashWhenCantFindBone)
	{
		ticks += Minecraft.getInstance().getDeltaFrameTime() * speed;
		super.process(ticks, event, modelRendererList, boneSnapshotCollection, parser, crashWhenCantFindBone);
	}
}

