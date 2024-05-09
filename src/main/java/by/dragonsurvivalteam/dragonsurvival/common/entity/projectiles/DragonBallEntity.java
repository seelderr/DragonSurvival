package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;


import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;


public abstract class DragonBallEntity extends Fireball implements IAnimatable{
	public static final EntityDataAccessor<Integer> SKILL_LEVEL = SynchedEntityData.defineId(DragonBallEntity.class, EntityDataSerializers.INT);
	public static final float DRAGON_BALL_DISTANCE = 32.f;
	AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);
	AnimationBuilder animationBuilder = new AnimationBuilder();
	float moveDistance = 0;

	public DragonBallEntity(EntityType<? extends Fireball> p_i50168_1_, LivingEntity p_i50168_2_, double p_i50168_3_, double p_i50168_5_, double p_i50168_7_, Level p_i50168_9_){
		super(p_i50168_1_, p_i50168_2_, p_i50168_3_, p_i50168_5_, p_i50168_7_, p_i50168_9_);
	}

	public DragonBallEntity(EntityType<? extends Fireball> p_i50166_1_, Level p_i50166_2_){
		super(p_i50166_1_, p_i50166_2_);
	}

	public int getSkillLevel(){
		return entityData.get(SKILL_LEVEL);
	}

	public void setLevel(int level){
		entityData.set(SKILL_LEVEL, level);
	}

	@Override
	protected void defineSynchedData(){
		super.defineSynchedData();
		entityData.define(SKILL_LEVEL, 1);
	}

	@Override
	public Packet<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected boolean canHitEntity(Entity p_230298_1_){
		return true;
	}

	@Override
	public void tick() {
		super.tick();
		moveDistance += (float)getDeltaMovement().length();
		if (moveDistance > DRAGON_BALL_DISTANCE) {
			this.onHit(ProjectileUtil.getHitResult(this, this::canHitEntity));
		}
	}

	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event)
	{
		event.getController().setAnimation(animationBuilder);
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data){
		data.addAnimationController(new AnimationController(this, "everything", 0, this::predicate));
		animationBuilder.addAnimation("idle", EDefaultLoopTypes.LOOP);
	}

	@Override
	public AnimationFactory getFactory(){
		return animationFactory;
	}
}