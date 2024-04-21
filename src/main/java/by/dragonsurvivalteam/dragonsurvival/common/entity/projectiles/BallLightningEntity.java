package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;


import by.dragonsurvivalteam.dragonsurvival.client.particles.SeaDragon.LargeLightningParticleData;
import by.dragonsurvivalteam.dragonsurvival.client.particles.SeaDragon.SmallLightningParticleData;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.active.BallLightningAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.active.StormBreathAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.registry.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import by.dragonsurvivalteam.dragonsurvival.util.TargetingFunctions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BallLightningEntity extends DragonBallEntity{
	public BallLightningEntity(Level p_i50168_9_, LivingEntity p_i50168_2_, double p_i50168_3_, double p_i50168_5_, double p_i50168_7_){
		super(DSEntities.BALL_LIGHTNING.get(), p_i50168_2_, p_i50168_3_, p_i50168_5_, p_i50168_7_, p_i50168_9_);
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
		if(!level().isClientSide() && !isDead){
			float explosivePower = getSkillLevel() / 1.25f;
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
			level().explode(null, damagesource, null, getX(), getY(), getZ(), explosivePower, false, Level.ExplosionInteraction.MOB);

			if (!(getOwner() instanceof Player)) {
				level().playLocalSound(blockPosition(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.HOSTILE, 3.0f, 0.5f, false);
				return;
			}

			isDead = true;
			setDeltaMovement(0, 0, 0);

			attackMobs();
		}
	}
	
	@Override
	public void tick() {
		super.tick();
		if (level.getGameTime() % 5 == 0 && !isDead) // Once per 5 ticks (0.25 seconds)
			attackMobs();
	}

	@Override
	public void attackMobs(){
		int rn = 4;
		Entity owner = getOwner();
		
		if (owner instanceof Player)
			rn = DragonAbilities.getSelfAbility((Player) owner, BallLightningAbility.class).getRange();

		int range = rn;
		List<Entity> entities = level.getEntities(null, new AABB(position().x - range, position().y - range, position().z - range, position().x + range, position().y + range, position().z + range));
		entities.removeIf(e -> e == owner || e instanceof BallLightningEntity);
		entities.removeIf(e -> e.distanceTo(this) > range);
		entities.removeIf(e -> !(e instanceof LivingEntity));

		for(Entity ent : entities){
			if(!level.isClientSide){
				TargetingFunctions.attackTargets(owner, ent1 -> ent1.hurt(DamageSource.LIGHTNING_BOLT, BallLightningAbility.getDamage(getSkillLevel())), ent);

				if(ent instanceof LivingEntity livingEntity){
					if(livingEntity.getRandom().nextInt(100) < 40){
						if(!livingEntity.level().isClientSide() && !StormBreathAbility.chargedBlacklist.contains(ResourceHelper.getKey(livingEntity).toString())){
							livingEntity.addEffect(new MobEffectInstance(DragonEffects.CHARGED, Functions.secondsToTicks(10), 0, false, true));
						}
					}

					level().playLocalSound(blockPosition(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 2.0F, 0.5f, true);
				}

				if(owner instanceof LivingEntity livingEntity) {
					doEnchantDamageEffects(livingEntity, ent);
				}
			}

			if (level().isClientSide()) {
				// Creates a trail of particles between the entity and target(s)
				int steps = 10;

				for (int i = 0; i < steps; i++) {
					Vec3 distV = new Vec3(getX() - ent.getX(), getY() - ent.getY(), getZ() - ent.getZ());
					double distFrac = (steps - (double) (i)) / steps;
					// the current entity coordinate + ((the distance between it and the target) * (the fraction of the total))
					double stepX = ent.getX() + (distV.x * distFrac);
					double stepY = ent.getY() + (distV.y * distFrac);
					double stepZ = ent.getZ() + (distV.z * distFrac);
					if (level.random.nextInt() > 25)
						level.addParticle(new LargeLightningParticleData(16F, false), stepX, stepY, stepZ, 0.0, 0.0, 0.0);
					else
						level.addParticle(new LargeLightningParticleData(16F, false), stepX, stepY, stepZ, 0.0, 0.0, 0.0);
				}
			}
		}

		if (!level().isClientSide) {
			if (level().isThundering()) {
				if (level().random.nextInt(100) < 30) {
					if (level().canSeeSky(blockPosition())) {
						LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(level());
						lightningboltentity.moveTo(new Vec3(position().x, position().y, position().z));

						level().addFreshEntity(lightningboltentity);
					}
				}
			}
		}
	}
}