package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class DragonBallEntity extends AbstractFireballEntity implements IAnimatable{
	public static final DataParameter<Integer> SKILL_LEVEL = EntityDataManager.defineId(DragonBallEntity.class, DataSerializers.INT);
	AnimationFactory animationFactory = new AnimationFactory(this);
	protected boolean isDead;
	protected int deadTicks;

	public DragonBallEntity(EntityType<? extends AbstractFireballEntity> p_i50168_1_, LivingEntity p_i50168_2_, double p_i50168_3_, double p_i50168_5_, double p_i50168_7_, World p_i50168_9_){
		super(p_i50168_1_, p_i50168_2_, p_i50168_3_, p_i50168_5_, p_i50168_7_, p_i50168_9_);
	}

	public DragonBallEntity(EntityType<? extends AbstractFireballEntity> p_i50166_1_, World p_i50166_2_){
		super(p_i50166_1_, p_i50166_2_);
	}

	public int getLevel(){
		return this.entityData.get(SKILL_LEVEL);
	}

	@Override
	protected void defineSynchedData(){
		super.defineSynchedData();
		this.entityData.define(SKILL_LEVEL, 1);
	}

	public void setLevel(int level){
		this.entityData.set(SKILL_LEVEL, level);
	}

	@Override
	public void tick(){
		if(isDead){
			deadTicks++;

			if(deadTicks >= 26){
				this.remove();
			}
			return;
		}

		Entity entity = this.getOwner();
		if(this.level.isClientSide || (entity == null || !entity.removed) && this.level.hasChunkAt(this.blockPosition())){
			RayTraceResult raytraceresult = ProjectileHelper.getHitResult(this, this::canHitEntity);

			if(raytraceresult.getType() != RayTraceResult.Type.MISS){
				this.onHit(raytraceresult);
			}

			this.checkInsideBlocks();
			Vector3d vector3d = this.getDeltaMovement();
			double d0 = this.getX() + vector3d.x;
			double d1 = this.getY() + vector3d.y;
			double d2 = this.getZ() + vector3d.z;
			ProjectileHelper.rotateTowardsMovement(this, 0.2F);
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
			onHit(ProjectileHelper.getHitResult(this, this::canHitEntity));
		}
	}

	@Override
	protected IParticleData getTrailParticle(){
		return ParticleTypes.WHITE_ASH;
	}

	@Override
	public IPacket<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	protected boolean canHitEntity(Entity p_230298_1_){
		return true;
	}

	protected boolean shouldBurn(){
		return false;
	}

	@Override
	protected void onHit(RayTraceResult p_70227_1_){
		attackMobs();
		setDeltaMovement(0, 0, 0);
		isDead = true;
	}

	public void attackMobs(){}

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