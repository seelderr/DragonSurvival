package by.jackraidenph.dragonsurvival.magic.entity;

import by.jackraidenph.dragonsurvival.magic.Abilities.Actives.BallLightningAbility;
import by.jackraidenph.dragonsurvival.magic.Abilities.DragonAbilities;
import by.jackraidenph.dragonsurvival.registration.EntityTypesInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class BallLightningEntity extends DragonBallEntity
{
	public BallLightningEntity(World p_i50168_9_, LivingEntity p_i50168_2_, double p_i50168_3_, double p_i50168_5_, double p_i50168_7_)
	{
		super(EntityTypesInit.BALL_LIGHTNING, p_i50168_2_, p_i50168_3_, p_i50168_5_, p_i50168_7_, p_i50168_9_);
	}
	
	public BallLightningEntity(EntityType<? extends AbstractFireballEntity> p_i50166_1_, World p_i50166_2_) {
		super(p_i50166_1_, p_i50166_2_);
	}
	
	@Override
	protected IParticleData getTrailParticle()
	{
		return ParticleTypes.WHITE_ASH;
	}
	
	protected boolean shouldBurn() {
		return false;
	}
	
	public void attackMobs()
	{
		int range = ((BallLightningAbility)DragonAbilities.BALL_LIGHTNING).getRange();
		List<Entity> entities = this.level.getEntities(null, new AxisAlignedBB(position().x - range, position().y - range, position().z - range, position().x + range, position().y + range, position().z + range));
		
		for(Entity ent : entities){
			if(ent == this) continue;
			if(ent.position().distanceTo(position()) > (range / 2f)) continue;
			if(ent == getOwner()) continue;
			
			if (!this.level.isClientSide) {
				ent.hurt(DamageSource.LIGHTNING_BOLT, BallLightningAbility.getDamage(getLevel()));
				
				if (getOwner() instanceof LivingEntity) {
					this.doEnchantDamageEffects((LivingEntity)getOwner(), ent);
				}
			}
			
			if(this.level.isClientSide) {
				for (int i = 0; i < 4; i++) {
					double d1 = level.random.nextFloat();
					double d2 = level.random.nextFloat();
					double d3 = level.random.nextFloat();
					level.addParticle(ParticleTypes.LARGE_SMOKE, ent.getX() + d1, ent.getY() + d2, ent.getZ() + d3, 0.0D, 0.0D, 0.0D);
				}
			}
		}
		
		this.remove();
	}
}
