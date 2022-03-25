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
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;


public class DragonBallEntity extends Fireball implements IAnimatable{
	public static final EntityDataAccessor<Integer> SKILL_LEVEL = SynchedEntityData.defineId(DragonBallEntity.class, EntityDataSerializers.INT);
	AnimationFactory animationFactory = new AnimationFactory(this);
	protected boolean isDead;
	protected int deadTicks;

	public DragonBallEntity(EntityType<? extends Fireball> p_i50168_1_, LivingEntity p_i50168_2_, double p_i50168_3_, double p_i50168_5_, double p_i50168_7_, Level p_i50168_9_){
		super(p_i50168_1_, p_i50168_2_, p_i50168_3_, p_i50168_5_, p_i50168_7_, p_i50168_9_);
	}

	public DragonBallEntity(EntityType<? extends Fireball> p_i50166_1_, Level p_i50166_2_){
		super(p_i50166_1_, p_i50166_2_);
	}

	public int getSkillLevel(){
		return this.entityData.get(SKILL_LEVEL);
	}

	public void setLevel(int level){
		this.entityData.set(SKILL_LEVEL, level);
	}

	@Override
	protected void defineSynchedData(){
		super.defineSynchedData();
		this.entityData.define(SKILL_LEVEL, 1);
	}

	@Override
	public void tick(){
		if(isDead){
			deadTicks++;

			if(deadTicks >= 26){
				this.remove(RemovalReason.DISCARDED);
			}
			return;
		}

		Entity entity = this.getOwner();

		if(this.level.isClientSide || (entity == null || !entity.isRemoved()) && this.level.hasChunkAt(this.blockPosition())){
			HitResult raytraceresult = ProjectileUtil.getHitResult(this, this::canHitEntity);

			if(raytraceresult.getType() != HitResult.Type.MISS){

				this.onHit(raytraceresult);
			}

			this.checkInsideBlocks();
			Vec3 vector3d = this.getDeltaMovement();
			double d0 = this.getX() + vector3d.x;
			double d1 = this.getY() + vector3d.y;
			double d2 = this.getZ() + vector3d.z;
			ProjectileUtil.rotateTowardsMovement(this, 0.2F);
			float f = this.getInertia();
			if(this.isInWater()){
				f = 0.8F;
			}
			moveDist += (float)distanceToSqr(d0, d1, d2);
			this.setDeltaMovement(vector3d.add(this.xPower, this.yPower, this.zPower).scale(f));
			this.level.addParticle(this.getTrailParticle(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
			this.setPos(d0, d1, d2);
		}else{
			isDead = true;
			//this.remove();
		}

		if(moveDist >= 32){
			onHit(ProjectileUtil.getHitResult(this, this::canHitEntity));
		}
	}

	@Override
	protected ParticleOptions getTrailParticle(){
		return ParticleTypes.WHITE_ASH;
	}

	protected boolean canHitEntity(Entity p_230298_1_){
		return true;
	}

	@Override
	protected void onHit(HitResult p_70227_1_){
		attackMobs();
		setDeltaMovement(0, 0, 0);
		isDead = true;
	}

	public void attackMobs(){}

	@Override
	public Packet<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	protected boolean shouldBurn(){
		return false;
	}

	@Override
	public void registerControllers(AnimationData data){
		data.addAnimationController(new AnimationController<>(this, "everything", 3, event -> {
			AnimationBuilder animationBuilder = new AnimationBuilder();

			if(isDead){
				animationBuilder.addAnimation("explosion", true);
			}else{
				animationBuilder.addAnimation("idle", true);
			}

			event.getController().setAnimation(animationBuilder);
			return PlayState.CONTINUE;
		}));
	}

	@Override
	public AnimationFactory getFactory(){
		return animationFactory;
	}
}