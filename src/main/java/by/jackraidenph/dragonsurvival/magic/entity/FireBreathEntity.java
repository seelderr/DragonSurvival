package by.jackraidenph.dragonsurvival.magic.entity;

import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.registration.EntityTypesInit;
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
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class FireBreathEntity extends AbstractFireballEntity
{
	public static final DataParameter<Integer> SKILL_LEVEL = EntityDataManager.defineId(DragonBallEntity.class, DataSerializers.INT);
	public int skillLevel = 1;
	
	public int getLevel(){
		return this.entityData.get(SKILL_LEVEL);
	}
	
	public FireBreathEntity(World p_i50168_9_, LivingEntity p_i50168_2_, double p_i50168_3_, double p_i50168_5_, double p_i50168_7_)
	{
		super(EntityTypesInit.FIRE_BREATH, p_i50168_2_, p_i50168_3_, p_i50168_5_, p_i50168_7_, p_i50168_9_);
	}
	
	public FireBreathEntity(EntityType<? extends AbstractFireballEntity> p_i50166_1_, World p_i50166_2_) {
		super(p_i50166_1_, p_i50166_2_);
	}
	
	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SKILL_LEVEL, 1);
	}
	
	public void setLevel(int level){
		this.skillLevel = level;
		this.entityData.set(SKILL_LEVEL, level);
	}
	
	@Override
	protected IParticleData getTrailParticle()
	{
		return ParticleTypes.CAMPFIRE_COSY_SMOKE;
	}
	
	protected boolean shouldBurn() {
		return false;
	}
	
	@Override
	protected boolean canHitEntity(Entity entity)
	{
		return super.canHitEntity(entity) && !(entity instanceof FireBreathEntity);
	}
	
	@Override
	protected void onHitEntity(EntityRayTraceResult result)
	{
		super.onHitEntity(result);
		result.getEntity().setSecondsOnFire(100);
	}
	
	@Override
	public void tick()
	{
		Entity entity = this.getOwner();
		if (this.level.isClientSide || (entity == null || !entity.removed) && this.level.hasChunkAt(this.blockPosition())) {
			RayTraceResult raytraceresult = ProjectileHelper.getHitResult(this, this::canHitEntity);
			
			if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
				this.onHit(raytraceresult);
			}
			
			this.checkInsideBlocks();
			Vector3d vector3d = this.getDeltaMovement();
			double d0 = this.getX() + vector3d.x;
			double d1 = this.getY() + vector3d.y + 0.02;
			double d2 = this.getZ() + vector3d.z;
			ProjectileHelper.rotateTowardsMovement(this, 0.2F);
			float f = 0.5F;
			
			if (this.isInWater()) {
				for(int i = 0; i < 4; ++i) {
					this.level.addParticle(ParticleTypes.BUBBLE, d0 - vector3d.x * 0.25D, d1 - vector3d.y * 0.25D, d2 - vector3d.z * 0.25D, vector3d.x, vector3d.y, vector3d.z);
				}
			}
			
//			for(int i = 0; i < 10; ++i) {
//				this.level.addParticle(ParticleTypes.FLAME, d0 + level.random.nextFloat(), d1 + level.random.nextFloat(), d2 + level.random.nextFloat(), vector3d.x, vector3d.y, vector3d.z);
//			}
			
			moveDist += distanceToSqr(d0, d1, d2);
			this.setDeltaMovement(vector3d.add(this.xPower, this.yPower, this.zPower).scale(f));
			this.level.addParticle(this.getTrailParticle(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
			this.setPos(d0, d1, d2);
		} else {
			this.remove();
		}
		
		if(tickCount >= Functions.secondsToTicks(3)){
			onHit(ProjectileHelper.getHitResult(this, this::canHitEntity));
			this.remove();
		}
	}
}
