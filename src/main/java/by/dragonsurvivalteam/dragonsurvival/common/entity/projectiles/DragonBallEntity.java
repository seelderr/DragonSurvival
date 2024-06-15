package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;


import javax.annotation.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;


public abstract class DragonBallEntity extends Fireball implements GeoEntity {
	public static final EntityDataAccessor<Integer> SKILL_LEVEL = SynchedEntityData.defineId(DragonBallEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> LIFESPAN = SynchedEntityData.defineId(DragonBallEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Float> MOVE_DISTANCE = SynchedEntityData.defineId(DragonBallEntity.class, EntityDataSerializers.FLOAT);
	public static final float DRAGON_BALL_DISTANCE = 32.f;
	public static final int MAX_LIFESPAN = 1200; // 60 seconds
	private boolean hasExploded = false;
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public DragonBallEntity(EntityType<? extends Fireball> entityType, double x, double y, double z, Vec3 velocity, Level level){
		super(entityType, x, y, z, velocity, level);
	}

	public DragonBallEntity(EntityType<? extends Fireball> p_i50166_1_, Level p_i50166_2_){
		super(p_i50166_1_, p_i50166_2_);
	}

	public int getSkillLevel() {
		return entityData.get(SKILL_LEVEL);
	}

	public float getExplosivePower(){
		return getSkillLevel();
	}

	public void setLevel(int level){
		entityData.set(SKILL_LEVEL, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder pBuilder){
		super.defineSynchedData(pBuilder);
		pBuilder.define(SKILL_LEVEL, 1);
		pBuilder.define(MOVE_DISTANCE, 0f);
		pBuilder.define(LIFESPAN, 0);
	}

	@Override
	protected boolean canHitEntity(Entity p_230298_1_){
		return true;
	}

	protected boolean canSelfDamage(){
		return true;
	}

	@Override
	public void tick() {
		super.tick();
		if(!this.level().isClientSide || (getOwner() == null || !getOwner().isRemoved()) && this.level().hasChunkAt(this.blockPosition())) {
			entityData.set(MOVE_DISTANCE, entityData.get(MOVE_DISTANCE) + (float)getDeltaMovement().length());
			entityData.set(LIFESPAN, entityData.get(LIFESPAN) + 1);
		}
		if (entityData.get(MOVE_DISTANCE) > DRAGON_BALL_DISTANCE || entityData.get(LIFESPAN) > MAX_LIFESPAN) {
			// Call onHitBlock rather than onHit, since calling onHit using the helper function from
			// vanilla will result in HitResult.Miss from 1.20.6 onwards, causing nothing to happen
			this.onHitBlock(new BlockHitResult(this.position(), this.getDirection(), this.blockPosition(), false));
		}
	}

	protected DamageSource getDamageSource(Fireball pFireball, @Nullable Entity pIndirectEntity){
		return pFireball.damageSources().fireball(this, pIndirectEntity);
		//return DamageSource.fireball(pFireball, pIndirectEntity);
	}


	public PlayState predicate(final AnimationState<DragonBallEntity> state) {
		state.getController().setAnimation(IDLE);
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(final AnimatableManager.ControllerRegistrar registrar){
		registrar.add(new AnimationController<>(this, "everything", this::predicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	private static final RawAnimation EXPLOSION = RawAnimation.begin().thenLoop("explosion");
	private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
}