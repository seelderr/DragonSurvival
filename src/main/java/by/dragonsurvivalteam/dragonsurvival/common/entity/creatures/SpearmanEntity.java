package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.FollowSpecificMobGoal;
import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.WindupMeleeAttackGoal;
import by.dragonsurvivalteam.dragonsurvival.client.render.util.RandomAnimationPicker;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.util.AnimationUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animation.*;

public class SpearmanEntity extends Hunter {
	public SpearmanEntity(EntityType<? extends PathfinderMob> entityType, Level world){
		super(entityType, world);
	}

	private RawAnimation currentIdleAnim;
	private boolean isIdleAnimSet = false;

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(3, new WindupMeleeAttackGoal(this, 1.0));
		this.goalSelector.addGoal(8, new FollowSpecificMobGoal(this, 0.6, 10, 20, target -> target instanceof KnightEntity));
	}

	@Override
	public double getRunThreshold() {
		return 0.15;
	}

	@Override
	public double getWalkThreshold() {
		return 0.01;
	}

	@Override
	public boolean isWithinMeleeAttackRange(LivingEntity pEntity) {
		return this.getAttackBoundingBox().inflate(ServerConfig.spearmanBonusHorizontalReach, ServerConfig.spearmanBonusVerticalReach, ServerConfig.spearmanBonusHorizontalReach).intersects(pEntity.getHitbox());
	}

	@Override
	public int getCurrentSwingDuration() {
		return 16;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "everything", 3, this::fullPredicate));
		controllers.add(new AnimationController<>(this, "head", 3, this::headPredicate));
		controllers.add(new AnimationController<>(this, "arms", 3, this::armsPredicate));
		controllers.add(new AnimationController<>(this, "legs", 3, this::legsPredicate));
	}

	private boolean isNotIdle() {
		double movement = AnimationUtils.getMovementSpeed(this);
		return swingTime > 0 || movement > getWalkThreshold() || isAggro();
	}

	public PlayState fullPredicate(final AnimationState<SpearmanEntity> state) {
		if (isNotIdle()) {
			isIdleAnimSet = false;
			return PlayState.STOP;
		}

		return state.setAndContinue(getIdleAnim());
	}

	public PlayState headPredicate(final AnimationState<SpearmanEntity> state) {
		return state.setAndContinue(HEAD_BLEND);
	}

	public PlayState armsPredicate(final AnimationState<SpearmanEntity> state) {
		if (swingTime > 0) {
			return state.setAndContinue(ATTACK_BLEND);
		} else if(isAggro()) {
			return state.setAndContinue(AGGRO_BLEND);
		} else if(isNotIdle()) {
			return state.setAndContinue(WALK_ARMS_BLEND);
		}

		return PlayState.STOP;
	}

	public PlayState legsPredicate(final AnimationState<SpearmanEntity> state) {
		double movement = AnimationUtils.getMovementSpeed(this);

		if (movement > getRunThreshold()) {
			return state.setAndContinue(RUN_BLEND);
		} else if (movement > getWalkThreshold()) {
			return state.setAndContinue(WALK_BLEND);
		} else if(isAggro()) {
			return state.setAndContinue(IDLE_BLEND);
		}

		return PlayState.STOP;
	}

	public RawAnimation getIdleAnim() {
		if (!isIdleAnimSet) {
			currentIdleAnim = IDLE_ANIMS.pickRandomAnimation();
			isIdleAnimSet = true;
		}
		return currentIdleAnim;
	}

	private static final RandomAnimationPicker IDLE_ANIMS = new RandomAnimationPicker(
		new RandomAnimationPicker.WeightedAnimation(RawAnimation.begin().thenLoop("idle1"), 90),
		new RandomAnimationPicker.WeightedAnimation(RawAnimation.begin().thenLoop("idle2"), 9),
		new RandomAnimationPicker.WeightedAnimation(RawAnimation.begin().thenLoop("idle3"), 1)
	);

	private static final RawAnimation WALK_BLEND = RawAnimation.begin().thenLoop("blend_walk");

	private static final RawAnimation RUN_BLEND = RawAnimation.begin().thenLoop("blend_run");

	private static final RawAnimation IDLE_BLEND = RawAnimation.begin().thenLoop("blend_idle");

	private static final RawAnimation WALK_ARMS_BLEND = RawAnimation.begin().thenLoop("blend_walk_arms");

	private static final RawAnimation ATTACK_BLEND = RawAnimation.begin().thenLoop("blend_attack");

	private static final RawAnimation AGGRO_BLEND = RawAnimation.begin().thenLoop("blend_aggro");

	private static final RawAnimation HEAD_BLEND = RawAnimation.begin().thenLoop("blend_head");
}