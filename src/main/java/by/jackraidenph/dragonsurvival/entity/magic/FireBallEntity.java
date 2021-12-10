package by.jackraidenph.dragonsurvival.entity.magic;

import by.jackraidenph.dragonsurvival.magic.abilities.Actives.FireBallAbility;
import by.jackraidenph.dragonsurvival.registration.EntityTypesInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class FireBallEntity extends DragonBallEntity
{
	public FireBallEntity(World p_i50168_9_, LivingEntity p_i50168_2_, double p_i50168_3_, double p_i50168_5_, double p_i50168_7_)
	{
		super(EntityTypesInit.FIREBALL, p_i50168_2_, p_i50168_3_, p_i50168_5_, p_i50168_7_, p_i50168_9_);
	}
	
	public FireBallEntity(EntityType<? extends AbstractFireballEntity> p_i50166_1_, World p_i50166_2_) {
		super(p_i50166_1_, p_i50166_2_);
	}
	
	@Override
	protected IParticleData getTrailParticle()
	{
		return ParticleTypes.LARGE_SMOKE;
	}
	
	protected boolean shouldBurn() {
		return false;
	}
	
	@Override
	public boolean isInvulnerableTo(DamageSource p_180431_1_)
	{
		return p_180431_1_.isExplosion() || super.isInvulnerableTo(p_180431_1_);
	}
	
	protected void onHit(RayTraceResult p_70227_1_) {
		if (!this.level.isClientSide && !this.isDead) {
			boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this.getOwner());
			float explosivePower = getLevel();
			this.level.explode((Entity)null, this.getX(), this.getY(), this.getZ(), explosivePower, flag, flag ? Explosion.Mode.DESTROY : Explosion.Mode.NONE);
			isDead = true;
		}
	}
	
	protected void onHitEntity(EntityRayTraceResult p_213868_1_) {
		if (!this.level.isClientSide && !this.isDead) {
			Entity entity = p_213868_1_.getEntity();
			Entity entity1 = this.getOwner();
			entity.hurt(DamageSource.fireball(this, entity1), FireBallAbility.getDamage(getLevel()));
			if (entity1 instanceof LivingEntity) {
				this.doEnchantDamageEffects((LivingEntity)entity1, entity);
			}
			isDead = true;
		}
	}
}
