package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;


import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
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
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DragonBallEntity extends Fireball implements GeoEntity {
	public static final EntityDataAccessor<Integer> SKILL_LEVEL = SynchedEntityData.defineId(DragonBallEntity.class, EntityDataSerializers.INT);

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	protected boolean isDead;
	protected int deadTicks;

	public DragonBallEntity(EntityType<? extends Fireball> p_i50168_1_, LivingEntity p_i50168_2_, double p_i50168_3_, double p_i50168_5_, double p_i50168_7_, Level p_i50168_9_){
		super(p_i50168_1_, p_i50168_2_, p_i50168_3_, p_i50168_5_, p_i50168_7_, p_i50168_9_);
	}

	public DragonBallEntity(EntityType<? extends Fireball> p_i50166_1_, Level p_i50166_2_){
		super(p_i50166_1_, p_i50166_2_);
	}

	public int getSkillLevel() {
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
	public void tick(){
		if(isDead){
			deadTicks++;

			if(deadTicks >= 3){
				remove(RemovalReason.DISCARDED);
			}
			return;
		}

		Entity entity = getOwner();

		if(level().isClientSide() || (entity == null || !entity.isRemoved()) && level().hasChunkAt(blockPosition())){
			HitResult raytraceresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

			if(raytraceresult.getType() != HitResult.Type.MISS){
				onHit(raytraceresult);
			}

			checkInsideBlocks();
			Vec3 vector3d = getDeltaMovement();
			double d0 = getX() + vector3d.x;
			double d1 = getY() + vector3d.y;
			double d2 = getZ() + vector3d.z;
			ProjectileUtil.rotateTowardsMovement(this, 0.2F);
			float f = getInertia();
			if(isInWater()){
				f = 0.8F;
			}
			moveDist += (float)distanceToSqr(d0, d1, d2);
			setDeltaMovement(vector3d.add(xPower, yPower, zPower).scale(f));
			level().addParticle(getTrailParticle(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
			setPos(d0, d1, d2);
		}else{
			isDead = true;
			//this.remove();
		}

		if(moveDist >= 32){
			onHit(ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity));
		}
	}

	@Override
	protected @NotNull ParticleOptions getTrailParticle() {
		return ParticleTypes.WHITE_ASH;
	}

	@Override
	public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected boolean canHitEntity(Entity p_230298_1_){
		return true;
	}

	@Override
	protected boolean shouldBurn(){
		return false;
	}

	@Override
	protected void onHit(@NotNull final HitResult result) {
		attackMobs();
		setDeltaMovement(0, 0, 0);
		isDead = true;
	}

	public void attackMobs(){}

	@Override
	public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "everything", 3, state -> {
			if (isDead) {
				return state.setAndContinue(EXPLOSION);
			} else {
				return state.setAndContinue(IDLE);
			}
		}));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	private static final RawAnimation EXPLOSION = RawAnimation.begin().thenLoop("explosion");
	private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
}