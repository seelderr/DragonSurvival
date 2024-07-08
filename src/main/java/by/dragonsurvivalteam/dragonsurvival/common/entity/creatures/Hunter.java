package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import javax.annotation.Nullable;

import by.dragonsurvivalteam.dragonsurvival.client.render.util.AnimationTimer;
import by.dragonsurvivalteam.dragonsurvival.client.render.util.RandomAnimationPicker;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.CrossbowAttackGoal;
import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.FollowMobGoal;
import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.HunterEntityCheckProcedure;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.util.AnimationUtils;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class Hunter extends PathfinderMob implements DragonHunter, GeoEntity {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public Hunter(EntityType<? extends PathfinderMob> entityType, Level world){
		super(entityType, world);
	}

	@Override
	public void tick(){
		updateSwingTime();
		super.tick();
	}

	protected void registerGoals() {
		super.registerGoals();

		this.targetSelector.addGoal(1, new HurtByTargetGoal(this, DragonHunter.class).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 1, true, false, living -> living.hasEffect(MobEffects.BAD_OMEN) || living.hasEffect(DSEffects.ROYAL_CHASE)));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, false, false) {
			@Override
			public boolean canUse() {
				return super.canUse() && HunterEntityCheckProcedure.execute(this.target);
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && HunterEntityCheckProcedure.execute(this.target);
			}
		});

		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8));
		this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.6));
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "main", 3, this::predicate));
	}

	private PlayState predicate(final AnimationState<Hunter> state) {
		double movement = AnimationUtils.getMovementSpeed(this);

		if (swingTime > 0) {
			// Move to a separate predicate potentially so that we can attack and move at the same time?
			return state.setAndContinue(getAttackAnim());
		} else if (isInWater()) {
			return state.setAndContinue(getSwimAnim());
		} else if (movement > 0.5) {
			return state.setAndContinue(getRunAnim());
		} else if (movement > 0.05) {
			return state.setAndContinue(getWalkAnim());
		} else {
			return state.setAndContinue(getIdleAnim());
		}
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	public abstract RawAnimation getSwimAnim();

	public abstract RawAnimation getAttackAnim();

	public abstract RawAnimation getIdleAnim();

	public abstract RawAnimation getRunAnim();

	public abstract RawAnimation getWalkAnim();
}