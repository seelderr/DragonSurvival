package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.HunterEntityCheckProcedure;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.util.AnimationUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.entity.animal.WolfVariants;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class Hunter extends PathfinderMob implements DragonHunter, GeoEntity {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	private static final EntityDataAccessor<Boolean> IS_AGGRO = SynchedEntityData.defineId(Hunter.class, EntityDataSerializers.BOOLEAN);

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
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 0, true, false, living -> living.hasEffect(DSEffects.ROYAL_CHASE)));
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
	protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
		super.defineSynchedData(pBuilder);
		pBuilder.define(IS_AGGRO, false);
	}

	@Override
	public void setTarget(@Nullable LivingEntity target) {
		super.setTarget(target);
        this.setAggro(target != null);
	}

	public void setAggro(boolean aggro) {
		this.entityData.set(IS_AGGRO, aggro);
	}

	public boolean isAggro() {
		return this.entityData.get(IS_AGGRO);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "everything", 3, this::fullPredicate));
		controllers.add(new AnimationController<>(this, "arms", 3, this::armsPredicate));
		controllers.add(new AnimationController<>(this, "legs", 3, this::legsPredicate));
	}

	public boolean isNotIdle() {
		double movement = getDeltaMovement().length();
		return swingTime > 0 || movement > getWalkThreshold() || isAggro();
	}

	public PlayState fullPredicate(final AnimationState<Hunter> state) {
		if (isNotIdle()) {
			return PlayState.STOP;
		}

		return state.setAndContinue(getIdleAnim());
	}

	public PlayState armsPredicate(final AnimationState<Hunter> state) {
		if (swingTime > 0) {
			return state.setAndContinue(getAttackBlend());
		} else if(isAggro()) {
			return state.setAndContinue(getAggroBlend());
		}

		return PlayState.STOP;
	}

	public PlayState legsPredicate(final AnimationState<Hunter> state) {
		double movement = getDeltaMovement().length();

		if (movement > getRunThreshold()) {
			return state.setAndContinue(getRunBlend());
		} else if (movement > getWalkThreshold()) {
			return state.setAndContinue(getWalkBlend());
		} else if(isAggro()) {
			return state.setAndContinue(getIdleBlend());
		}

		return PlayState.STOP;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	public abstract double getRunThreshold();

	public abstract double getWalkThreshold();

	public abstract RawAnimation getAttackBlend();

	public abstract RawAnimation getAggroBlend();

	public abstract RawAnimation getIdleAnim();

	public abstract RawAnimation getIdleBlend();

	public abstract RawAnimation getRunBlend();

	public abstract RawAnimation getWalkBlend();
}