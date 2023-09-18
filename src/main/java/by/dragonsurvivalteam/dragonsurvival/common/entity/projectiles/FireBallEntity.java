package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;


import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.active.FireBallAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.util.TargetingFunctions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class FireBallEntity extends DragonBallEntity{
	public FireBallEntity(Level p_i50168_9_, LivingEntity p_i50168_2_, double p_i50168_3_, double p_i50168_5_, double p_i50168_7_){
		super(DSEntities.FIREBALL.get(), p_i50168_2_, p_i50168_3_, p_i50168_5_, p_i50168_7_, p_i50168_9_);
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
	protected void onHit(HitResult p_70227_1_){
		if(!level().isClientSide() && !isDead){
			float explosivePower = getSkillLevel();
			Entity attacker = getOwner();
			DamageSource damagesource;
			if(attacker == null){
				damagesource = damageSources().fireball(this, this);
			}else{
				damagesource = damageSources().fireball(this, attacker);
				if(attacker instanceof LivingEntity attackerEntity){
					attackerEntity.setLastHurtMob(attacker);
				}
			}
			level().explode(null, damagesource, null, getX(), getY(), getZ(), explosivePower, true, Level.ExplosionInteraction.MOB);

			isDead = true;
			setDeltaMovement(0, 0, 0);

			aoeDamage();
		}
	}

	private void aoeDamage(){
		int range = 2;
		List<Entity> entities = level().getEntities(null, new AABB(position().x - range, position().y - range, position().z - range, position().x + range, position().y + range, position().z + range));
		entities.removeIf(e -> e == getOwner() || e instanceof FireBallEntity);
		entities.removeIf(e -> e.distanceTo(this) > range);
		entities.removeIf(e -> !(e instanceof LivingEntity));

		for(Entity ent : entities){
			if(!level().isClientSide()){
				TargetingFunctions.attackTargets(getOwner(), ent1 -> ent1.hurt(damageSources().explosion(null), FireBallAbility.getDamage(getSkillLevel())), ent);

				if(getOwner() instanceof LivingEntity){
					doEnchantDamageEffects((LivingEntity)getOwner(), ent);
				}
			}
		}
	}


	@Override
	public boolean isInvulnerableTo(DamageSource damageSource){
		return damageSource.is(DamageTypeTags.IS_EXPLOSION) || super.isInvulnerableTo(damageSource);
	}

	@Override
	protected void onHitEntity(EntityHitResult p_213868_1_){
		if(!level().isClientSide() && !isDead){
			Entity entity = p_213868_1_.getEntity();
			Entity entity1 = getOwner();

			TargetingFunctions.attackTargets(getOwner(), ent1 -> ent1.hurt(damageSources().fireball(this, entity1), FireBallAbility.getDamage(getSkillLevel())), entity);

			if(entity1 instanceof LivingEntity){
				doEnchantDamageEffects((LivingEntity)entity1, entity);
			}
			isDead = true;
			setDeltaMovement(0, 0, 0);
			aoeDamage();
		}
	}
}