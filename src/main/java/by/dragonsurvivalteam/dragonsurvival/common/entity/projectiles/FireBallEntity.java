package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.common.entity.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Actives.FireBallAbility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.List;

public class FireBallEntity extends DragonBallEntity{
	public FireBallEntity(World p_i50168_9_, LivingEntity p_i50168_2_, double p_i50168_3_, double p_i50168_5_, double p_i50168_7_){
		super(DSEntities.FIREBALL, p_i50168_2_, p_i50168_3_, p_i50168_5_, p_i50168_7_, p_i50168_9_);
	}

	public FireBallEntity(EntityType<? extends AbstractFireballEntity> p_i50166_1_, World p_i50166_2_){
		super(p_i50166_1_, p_i50166_2_);
	}

	@Override
	protected IParticleData getTrailParticle(){
		return ParticleTypes.LARGE_SMOKE;
	}

	protected boolean shouldBurn(){
		return false;
	}

	protected void onHit(RayTraceResult p_70227_1_){
		if(!this.level.isClientSide && !this.isDead){
			boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this.getOwner());
			float explosivePower = getLevel();
			this.level.explode(null, this.getX(), this.getY(), this.getZ(), explosivePower, flag, flag ? Explosion.Mode.DESTROY : Explosion.Mode.NONE);
			isDead = true;
			setDeltaMovement(0, 0, 0);

			if(!flag){
				aoeDamage();
			}
		}
	}

	private void aoeDamage(){
		int range = 2;
		List<Entity> entities = this.level.getEntities(null, new AxisAlignedBB(position().x - range, position().y - range, position().z - range, position().x + range, position().y + range, position().z + range));
		entities.removeIf((e) -> e == getOwner() || e instanceof FireBallEntity);
		entities.removeIf((e) -> e.distanceTo(this) > range);
		entities.removeIf((e) -> !(e instanceof LivingEntity));

		for(Entity ent : entities){
			if(!this.level.isClientSide){
				ent.hurt(DamageSource.explosion((Explosion)null), FireBallAbility.getDamage(getLevel()));

				if(getOwner() instanceof LivingEntity){
					this.doEnchantDamageEffects((LivingEntity)getOwner(), ent);
				}
			}
		}
	}

	@Override
	public boolean isInvulnerableTo(DamageSource p_180431_1_){
		return p_180431_1_.isExplosion() || super.isInvulnerableTo(p_180431_1_);
	}

	protected void onHitEntity(EntityRayTraceResult p_213868_1_){
		if(!this.level.isClientSide && !this.isDead){
			Entity entity = p_213868_1_.getEntity();
			Entity entity1 = this.getOwner();
			entity.hurt(DamageSource.fireball(this, entity1), FireBallAbility.getDamage(getLevel()));
			if(entity1 instanceof LivingEntity){
				this.doEnchantDamageEffects((LivingEntity)entity1, entity);
			}
			isDead = true;
			setDeltaMovement(0, 0, 0);
			aoeDamage();
		}
	}
}