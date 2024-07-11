package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.HurtByTargetGoalExtended;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public abstract class Hunter extends PathfinderMob implements GeoEntity {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	private static final EntityDataAccessor<Boolean> IS_AGGRO = SynchedEntityData.defineId(Hunter.class, EntityDataSerializers.BOOLEAN);
	private static final TagKey<EntityType<?>> HUNTERS_GOAL = TagKey.create(BuiltInRegistries.ENTITY_TYPE.key(), ResourceLocation.parse("dragonsurvival:hunters_goal"));

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

		this.targetSelector.addGoal(1, new HurtByTargetGoalExtended(this).setHeeders(Hunter.class).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 0, true, false, living -> living.hasEffect(DSEffects.ROYAL_CHASE)));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, 0,false, false, living -> living.getType().is(HUNTERS_GOAL)));
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
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	public abstract double getRunThreshold();

	public abstract double getWalkThreshold();
}