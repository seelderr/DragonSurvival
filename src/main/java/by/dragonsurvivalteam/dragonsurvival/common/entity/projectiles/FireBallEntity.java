package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FireBallEntity extends DragonBallEntity{
	public FireBallEntity(double x, double y, double z, Vec3 velocity, Level level){
		super(DSEntities.FIREBALL.get(), x, y, z, velocity, level);
	}

	public FireBallEntity(EntityType<? extends Fireball> p_i50166_1_, Level p_i50166_2_){
		super(p_i50166_1_, p_i50166_2_);
	}

	@Override
	protected ParticleOptions getTrailParticle(){
		return ParticleTypes.LARGE_SMOKE;
	}

	@Override
	protected boolean shouldBurn(){
		return false;
	}

	@Override
	protected boolean canSelfDamage(){
		return false;
	}

	private void onCommonHit() {
		if((getOwner() == null || !getOwner().isRemoved()) && this.level().hasChunkAt(this.blockPosition())) {
			float explosivePower = getExplosivePower();
			DamageSource damagesource;
			if(getOwner() == null){
				damagesource = getDamageSource(this, this);
			} else {
				damagesource = getDamageSource(this, getOwner());
			}
			Entity attacker = canSelfDamage() ? this : getOwner();
			level().explode(attacker, damagesource, null, getX(), getY(), getZ(), explosivePower, true, Level.ExplosionInteraction.BLOCK);
			this.discard();
		}
	}

	@Override
	protected void onHitBlock(BlockHitResult pResult){
		super.onHitBlock(pResult);
		onCommonHit();
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource){
		return damageSource.is(DamageTypeTags.IS_EXPLOSION) || super.isInvulnerableTo(damageSource);
	}

	@Override
	protected void onHitEntity(EntityHitResult hitResult){
		super.onHitEntity(hitResult);

		// Apply the explosion damage using math from the real explosion formula
		float explosivePower = getSkillLevel();
		// From Explosion.class on line 215 (the left side of the formula is 1.0f if you are at the center of the explosion)
		float damage = 7.0f * explosivePower * 2.0f + 1.0f;

		Entity attacker = getOwner();
		hitResult.getEntity().hurt(getDamageSource(this, attacker), damage);
		hitResult.getEntity().setRemainingFireTicks(getSkillLevel() + 5);
		onCommonHit();
	}
}