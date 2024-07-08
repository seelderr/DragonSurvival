package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.client.render.util.RandomAnimationPicker;
import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.FollowMobGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animation.RawAnimation;

public class SpearmanEntity extends Hunter {
	public SpearmanEntity(EntityType<? extends PathfinderMob> entityType, Level world){
		super(entityType, world);
	}

	private RawAnimation currentIdleAnim;
	private boolean isIdleAnimSet = false;

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.5, false));
		this.goalSelector.addGoal(8, new FollowMobGoal<>(KnightEntity.class, this, 15));
	}

	@Override
	public RawAnimation getAttackAnim() {
		isIdleAnimSet = false;
		return ATTACK_ANIM;
	}

	@Override
	public RawAnimation getIdleAnim() {
		if (!isIdleAnimSet) {
			currentIdleAnim = IDLE_ANIMS.pickRandomAnimation();
			isIdleAnimSet = true;
		}
		return currentIdleAnim;
	}

	@Override
	public RawAnimation getRunAnim() {
		isIdleAnimSet = false;
		return RUN_ANIM;
	}

	@Override
	public RawAnimation getWalkAnim() {
		isIdleAnimSet = false;
		return WALK_ANIM;
	}

	@Override
	public RawAnimation getSwimAnim() {
		isIdleAnimSet = false;
		return SWIM_ANIM;
	}

	private static final RandomAnimationPicker IDLE_ANIMS = new RandomAnimationPicker(
		new RandomAnimationPicker.WeightedAnimation(RawAnimation.begin().thenLoop("idle1"), 90),
		new RandomAnimationPicker.WeightedAnimation(RawAnimation.begin().thenLoop("idle2"), 9),
		new RandomAnimationPicker.WeightedAnimation(RawAnimation.begin().thenLoop("idle3"), 1)
	);

	private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk1");

	private static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("run");

	private static final RawAnimation SWIM_ANIM = RawAnimation.begin().thenLoop("swim");

	private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenLoop("attack");
}