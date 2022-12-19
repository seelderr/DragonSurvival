package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;


import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.active.FireBallAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.util.TargetingFunctions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class FireBallEntity extends DragonBallEntity{
	public FireBallEntity(Level p_i50168_9_, LivingEntity p_i50168_2_, double p_i50168_3_, double p_i50168_5_, double p_i50168_7_){
		super(DSEntities.FIREBALL, p_i50168_2_, p_i50168_3_, p_i50168_5_, p_i50168_7_, p_i50168_9_);
	}

	public FireBallEntity(EntityType<? extends Fireball> p_i50166_1_, Level p_i50166_2_){
		super(p_i50166_1_, p_i50166_2_);
	}

	@Override
	protected ParticleOptions getTrailParticle(){
		return ParticleTypes.LARGE_SMOKE;
	}

	protected boolean shouldBurn(){
		return false;
	}

	protected void onHit(HitResult p_70227_1_){
		if(!this.level.isClientSide && !this.isDead){
			boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this.getOwner());
			float explosivePower = getSkillLevel();
			this.level.explode(null, this.getX(), this.getY(), this.getZ(), explosivePower, flag, flag ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE);

			isDead = true;
			setDeltaMovement(0, 0, 0);

			aoeDamage();
		}
	}

	private void aoeDamage(){
		int range = 2;
		List<Entity> entities = this.level.getEntities(null, new AABB(position().x - range, position().y - range, position().z - range, position().x + range, position().y + range, position().z + range));
		entities.removeIf((e) -> e == getOwner() || e instanceof FireBallEntity);
		entities.removeIf((e) -> e.distanceTo(this) > range);
		entities.removeIf((e) -> !(e instanceof LivingEntity));

		for(Entity ent : entities){
			if(!this.level.isClientSide){
				TargetingFunctions.attackTargets(getOwner(), ent1 -> ent1.hurt(DamageSource.explosion((Explosion)null), FireBallAbility.getDamage(getSkillLevel())), ent);

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

	protected void onHitEntity(EntityHitResult p_213868_1_){
		if(!this.level.isClientSide && !this.isDead){
			Entity entity = p_213868_1_.getEntity();
			Entity entity1 = this.getOwner();

			TargetingFunctions.attackTargets(getOwner(), ent1 -> ent1.hurt(DamageSource.fireball(this, entity1), FireBallAbility.getDamage(getSkillLevel())), entity);

			if(entity1 instanceof LivingEntity){
				this.doEnchantDamageEffects((LivingEntity)entity1, entity);
			}
			isDead = true;
			setDeltaMovement(0, 0, 0);
			aoeDamage();
		}
	}
}