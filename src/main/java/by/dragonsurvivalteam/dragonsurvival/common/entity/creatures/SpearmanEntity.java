package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.client.render.util.RandomAnimationPicker;
import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.FollowMobGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
		// FIXME: This attack goal results in the Spearman attacking instantly when the player gets near, then playing the attack animation and attacking correctly for subsequent attacks.
		this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, false));
		this.goalSelector.addGoal(8, new FollowMobGoal<>(KnightEntity.class, this, 15));
	}

	@Override
	public double getRunThreshold() {
		return 0.15;
	}

	@Override
	public double getWalkThreshold() {
		return 0.05;
	}

	@Override
	public boolean isWithinMeleeAttackRange(LivingEntity pEntity) {
		return this.getAttackBoundingBox().inflate(1, 0, 1).intersects(pEntity.getHitbox());
	}

	@Override
	public int getCurrentSwingDuration() {
		return 16;
	}

	@Override
	public void tick() {
		if(isNotIdle()) {
			isIdleAnimSet = false;
		}
		super.tick();
	}

	@Override
	public RawAnimation getAttackBlend() {
		return ATTACK_BLEND;
	}

	@Override
	public RawAnimation getAggroBlend() {
		return AGGRO_BLEND;
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
	public RawAnimation getIdleBlend() {
		return IDLE_BLEND;
	}

	@Override
	public RawAnimation getRunBlend() {
		return RUN_BLEND;
	}

	@Override
	public RawAnimation getWalkBlend() {
		return WALK_BLEND;
	}

	private static final RandomAnimationPicker IDLE_ANIMS = new RandomAnimationPicker(
		new RandomAnimationPicker.WeightedAnimation(RawAnimation.begin().thenLoop("idle1"), 90),
		new RandomAnimationPicker.WeightedAnimation(RawAnimation.begin().thenLoop("idle2"), 9),
		new RandomAnimationPicker.WeightedAnimation(RawAnimation.begin().thenLoop("idle3"), 1)
	);

	private static final RawAnimation WALK_BLEND = RawAnimation.begin().thenLoop("blend_walk");

	private static final RawAnimation RUN_BLEND = RawAnimation.begin().thenLoop("blend_run");

	private static final RawAnimation IDLE_BLEND = RawAnimation.begin().thenLoop("blend_idle");

	private static final RawAnimation ATTACK_BLEND = RawAnimation.begin().thenLoop("blend_attack");

	private static final RawAnimation AGGRO_BLEND = RawAnimation.begin().thenLoop("blend_aggro");
}