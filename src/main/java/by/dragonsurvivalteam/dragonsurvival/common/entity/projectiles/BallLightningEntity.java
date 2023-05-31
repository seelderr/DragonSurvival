package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;


import by.dragonsurvivalteam.dragonsurvival.client.particles.SeaDragon.LargeLightningParticleData;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.active.BallLightningAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.registry.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import by.dragonsurvivalteam.dragonsurvival.util.TargetingFunctions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BallLightningEntity extends DragonBallEntity{
	public BallLightningEntity(Level p_i50168_9_, LivingEntity p_i50168_2_, double p_i50168_3_, double p_i50168_5_, double p_i50168_7_){
		super(DSEntities.BALL_LIGHTNING, p_i50168_2_, p_i50168_3_, p_i50168_5_, p_i50168_7_, p_i50168_9_);
	}

	public BallLightningEntity(EntityType<? extends Fireball> p_i50166_1_, Level p_i50166_2_){

		super(p_i50166_1_, p_i50166_2_);
	}

	@Override
	protected ParticleOptions getTrailParticle(){

		return ParticleTypes.WHITE_ASH;
	}

	@Override
	protected boolean shouldBurn(){
		return false;
	}

	@Override
	protected void onHit(HitResult p_70227_1_){
		if(!level.isClientSide && !isDead){
			boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(level, getOwner());
			float explosivePower = getSkillLevel() / 1.25f;
			Entity attacker = getOwner();
			DamageSource damagesource;
			if(attacker == null){
				damagesource = DamageSource.fireball(this, this);
			}else{
				damagesource = DamageSource.fireball(this, attacker);
				if(attacker instanceof LivingEntity attackerEntity){
					attackerEntity.setLastHurtMob(attacker);
				}
			}
			level.explode(null, damagesource,null, getX(), getY(), getZ(), explosivePower, flag, flag ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE);

			isDead = true;
			setDeltaMovement(0, 0, 0);

			attackMobs();
		}
	}

	@Override
	public void attackMobs(){
		if (!(getOwner() instanceof Player))
		{
			level.playLocalSound(getX(), getY(), getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.HOSTILE, 3.0F, 0.5f, false);
			return;
		}
		int range = DragonAbilities.getSelfAbility((Player)getOwner(), BallLightningAbility.class).getRange();
		List<Entity> entities = level.getEntities(null, new AABB(position().x - range, position().y - range, position().z - range, position().x + range, position().y + range, position().z + range));
		entities.removeIf(e -> e == getOwner() || e instanceof BallLightningEntity);
		entities.removeIf(e -> e.distanceTo(this) > range);
		entities.removeIf(e -> !(e instanceof LivingEntity));

		for(Entity ent : entities){
			if(!level.isClientSide){
				TargetingFunctions.attackTargets(getOwner(), ent1 -> ent1.hurt(DamageSource.LIGHTNING_BOLT, BallLightningAbility.getDamage(getSkillLevel())), ent);

				if(ent instanceof LivingEntity livingEntity){
					if(livingEntity.getRandom().nextInt(100) < 40){
						if(!livingEntity.level.isClientSide){
							livingEntity.addEffect(new MobEffectInstance(DragonEffects.CHARGED, Functions.secondsToTicks(10), 0, false, true));
						}
					}
				}

				if(getOwner() instanceof LivingEntity){
					doEnchantDamageEffects((LivingEntity)getOwner(), ent);
				}
			}

			if(level.isClientSide){
				for(int i = 0; i < 10; i++){
					double d1 = level.random.nextFloat();
					double d2 = level.random.nextFloat();
					double d3 = level.random.nextFloat();
					level.addParticle(new LargeLightningParticleData(37F, false), ent.getX() + d1, ent.getY() + 0.5 + d2, ent.getZ() + d3, 0.0D, 0.0D, 0.0D);
				}
			}
		}

		if(!level.isClientSide){
			if(level.isThundering()){
				if(level.random.nextInt(100) < 30){
					if(level.canSeeSky(blockPosition())){
						LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(level);
						lightningboltentity.moveTo(new Vec3(position().x, position().y, position().z));

						level.addFreshEntity(lightningboltentity);
					}
				}
			}
		}

		if(level.isClientSide){
			float f = range;
			float f5 = (float)Math.PI * f * f;

			for(int k1 = 0; (float)k1 < f5; ++k1){
				float f6 = random.nextFloat() * ((float)Math.PI * 2F);
				float f7 = Mth.sqrt(random.nextFloat()) * f;
				float f8 = Mth.cos(f6) * f7;
				float f9 = Mth.sin(f6) * f7;
				level.addParticle(new LargeLightningParticleData(37F, false), getX() + (double)f8, getY(), getZ() + (double)f9, 0, 0, 0);
			}
		}


		level.playLocalSound(getX(), getY(), getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.HOSTILE, 3.0F, 0.5f, false);
	}
}