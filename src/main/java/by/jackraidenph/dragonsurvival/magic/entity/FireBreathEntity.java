package by.jackraidenph.dragonsurvival.magic.entity;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.magic.entity.particle.ParticleSnowFlake;
import by.jackraidenph.dragonsurvival.registration.EntityTypesInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;

public class FireBreathEntity extends AbstractFireballEntity
{
	private static final int RANGE = 10;
	private static final int ARC = 45;
	
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
			
			if (this.isInWater()) {
				this.remove();
			}
			
			float yaw = (float) Math.toRadians(-yRot);
			float pitch = (float) Math.toRadians(-xRot);
			float speed = 0.56f; //Changes distance
			float xComp = (float) (Math.sin(yaw) * Math.cos(pitch));
			float yComp = (float) (Math.sin(pitch));
			float zComp = (float) (Math.cos(yaw) * Math.cos(pitch));
			
			if(level.isClientSide) {
				for (int i = 0; i < 24; i++) {
					double xSpeed = speed * 1f * xComp;
					double ySpeed = speed * 1f * yComp;
					double zSpeed = speed * 1f * zComp;
					level.addParticle(new ParticleSnowFlake.SnowflakeData(37f, true), getX(), getY(), getZ(), xSpeed, ySpeed, zSpeed);
				}
			}
			
			if (entity instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) entity;
				Vector3d vector3d = player.getViewVector(1.0F);
				absMoveTo(player.getX() + vector3d.x, player.getY() + player.getStandingEyeHeight(player.getPose(), player.getDimensions(player.getPose())), player.getZ() + vector3d.z, player.yRot, player.xRot);
			}
			
		} else {
			this.remove();
		}
		
		hitEntities();
		freezeBlocks();
		
		if(tickCount >= 65){
			onHit(ProjectileHelper.getHitResult(this, this::canHitEntity));
			this.remove();
		}
		
		if(entity instanceof PlayerEntity){
			PlayerEntity player = (PlayerEntity) entity;
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if(cap.getCurrentlyCasting() == null){
					this.remove();
				}
			});
		}
	}
	
	public void hitEntities() {
		List<LivingEntity> entitiesHit = getEntityLivingBaseNearby(RANGE, RANGE, RANGE, RANGE);
		float damage = getLevel();
		for (LivingEntity entityHit : entitiesHit) {
			if (entityHit == getOwner()) continue;
			
			float entityHitYaw = (float) ((Math.atan2(entityHit.getZ() - getZ(), entityHit.getX() - getX()) * (180 / Math.PI) - 90) % 360);
			float entityAttackingYaw = yRot % 360;
			if (entityHitYaw < 0) {
				entityHitYaw += 360;
			}
			if (entityAttackingYaw < 0) {
				entityAttackingYaw += 360;
			}
			float entityRelativeYaw = entityHitYaw - entityAttackingYaw;
			
			float xzDistance = (float) Math.sqrt((entityHit.getZ() - getZ()) * (entityHit.getZ() - getZ()) + (entityHit.getX() - getX()) * (entityHit.getX() - getX()));
			double hitY = entityHit.getY() + entityHit.getBbHeight() / 2.0;
			float entityHitPitch = (float) ((Math.atan2((hitY - getY()), xzDistance) * (180 / Math.PI)) % 360);
			float entityAttackingPitch = -xRot % 360;
			if (entityHitPitch < 0) {
				entityHitPitch += 360;
			}
			if (entityAttackingPitch < 0) {
				entityAttackingPitch += 360;
			}
			float entityRelativePitch = entityHitPitch - entityAttackingPitch;
			
			float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - getZ()) * (entityHit.getZ() - getZ()) + (entityHit.getX() - getX()) * (entityHit.getX() - getX()) + (hitY - getY()) * (hitY - getY()));
			
			boolean inRange = entityHitDistance <= RANGE;
			boolean yawCheck = (entityRelativeYaw <= ARC / 2f && entityRelativeYaw >= -ARC / 2f) || (entityRelativeYaw >= 360 - ARC / 2f || entityRelativeYaw <= -360 + ARC / 2f);
			boolean pitchCheck = (entityRelativePitch <= ARC / 2f && entityRelativePitch >= -ARC / 2f) || (entityRelativePitch >= 360 - ARC / 2f || entityRelativePitch <= -360 + ARC / 2f);
			if (inRange && yawCheck && pitchCheck) {
				// Raytrace to mob center to avoid damaging through walls
				Vector3d from = this.position();
				Vector3d to = entityHit.position().add(0, entityHit.getEyeHeight() / 2.0f, 0);
				BlockRayTraceResult result = level.clip(new RayTraceContext(from, to, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
				
				if (result.getType() == RayTraceResult.Type.BLOCK) {
					continue;
				}
				
				if (entityHit.hurt(DamageSource.indirectMobAttack(this, (LivingEntity)getOwner()), damage)) {
					entityHit.setDeltaMovement(entityHit.getDeltaMovement().multiply(0.25, 1, 0.25));
				}
			}
		}
	}
	
	public void freezeBlocks() {
		int checkDist = 10;
		for (int i = (int)getX() - checkDist; i < (int)getX() + checkDist; i++) {
			for (int j = (int)getY() - checkDist; j < (int)getY() + checkDist; j++) {
				for (int k = (int)getZ() - checkDist; k < (int)getZ() + checkDist; k++) {
					BlockPos pos = new BlockPos(i, j, k);
					
					BlockState blockState = level.getBlockState(pos);
					BlockState blockStateAbove = level.getBlockState(pos.above());
					if (blockState.getBlock() != Blocks.WATER || blockStateAbove.getBlock() != Blocks.AIR) {
						continue;
					}
					
					float blockHitYaw = (float) ((Math.atan2(pos.getZ() - getZ(), pos.getX() - getX()) * (180 / Math.PI) - 90) % 360);
					float entityAttackingYaw = yRot % 360;
					if (blockHitYaw < 0) {
						blockHitYaw += 360;
					}
					if (entityAttackingYaw < 0) {
						entityAttackingYaw += 360;
					}
					float blockRelativeYaw = blockHitYaw - entityAttackingYaw;
					
					float xzDistance = (float) Math.sqrt((pos.getZ() - getZ()) * (pos.getZ() - getZ()) + (pos.getX() - getX()) * (pos.getX() - getX()));
					float blockHitPitch = (float) ((Math.atan2((pos.getY() - getY()), xzDistance) * (180 / Math.PI)) % 360);
					float entityAttackingPitch = -xRot % 360;
					if (blockHitPitch < 0) {
						blockHitPitch += 360;
					}
					if (entityAttackingPitch < 0) {
						entityAttackingPitch += 360;
					}
					float blockRelativePitch = blockHitPitch - entityAttackingPitch;
					
					float blockHitDistance = (float) Math.sqrt((pos.getZ() - getZ()) * (pos.getZ() - getZ()) + (pos.getX() - getX()) * (pos.getX() - getX()) + (pos.getY() - getY()) * (pos.getY() - getY()));
					
					boolean inRange = blockHitDistance <= RANGE;
					boolean yawCheck = (blockRelativeYaw <= ARC / 2f && blockRelativeYaw >= -ARC / 2f) || (blockRelativeYaw >= 360 - ARC / 2f || blockRelativeYaw <= -360 + ARC / 2f);
					boolean pitchCheck = (blockRelativePitch <= ARC / 2f && blockRelativePitch >= -ARC / 2f) || (blockRelativePitch >= 360 - ARC / 2f || blockRelativePitch <= -360 + ARC / 2f);
					if (inRange && yawCheck && pitchCheck) {
						level.setBlock(pos, Blocks.ICE.defaultBlockState(), 0);
						//EntityBlockSwapper.swapBlock(world, pos, Blocks.ICE.getDefaultState(), 140, false, false);
					}
				}
			}
		}
	}
	
	public List<LivingEntity> getEntityLivingBaseNearby(double distanceX, double distanceY, double distanceZ, double radius) {
		return getEntitiesNearby(LivingEntity.class, distanceX, distanceY, distanceZ, radius);
	}
	
	public <T extends Entity> List<T> getEntitiesNearby(Class<T> entityClass, double dX, double dY, double dZ, double r) {
		return level.getEntitiesOfClass(entityClass, getBoundingBox().inflate(dX, dY, dZ), e -> e != this && distanceTo(e) <= r + e.getBbWidth() / 2f && e.getY() <= getY() + dY);
	}
}
