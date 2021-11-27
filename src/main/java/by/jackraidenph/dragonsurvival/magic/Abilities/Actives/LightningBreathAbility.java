package by.jackraidenph.dragonsurvival.magic.Abilities.Actives;

import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.capability.Capabilities;
import by.jackraidenph.dragonsurvival.magic.entity.particle.SeaDragon.LargeLightningParticleData;
import by.jackraidenph.dragonsurvival.magic.entity.particle.SeaDragon.SmallLightningParticleData;
import by.jackraidenph.dragonsurvival.registration.DragonEffects;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class LightningBreathAbility extends BreathAbility
{
	public LightningBreathAbility(String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels)
	{
		super(id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
	}

	@Override
	public LightningBreathAbility createInstance()
	{
		return new LightningBreathAbility(id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}
	
	@Override
	public void onActivation(PlayerEntity player)
	{
		tickCost();
		super.onActivation(player);
		
		Vector3d viewVector = player.getViewVector(1.0F);
		
		double x = player.getX() + viewVector.x;
		double y = player.getY() + 1 + viewVector.y;
		double z = player.getZ() + viewVector.z;
		
		if(player.level.isClientSide) {
			if (player.tickCount % 30 == 0) {
				player.playSound(SoundEvents.FIRE_AMBIENT, 0.15F, 0.01F);
			}
		}
		
		if(player.level.isClientSide) {
			for (int i = 0; i < 24; i++) {
				double xSpeed = speed * 1f * xComp;
				double ySpeed = speed * 1f * yComp;
				double zSpeed = speed * 1f * zComp;
				player.level.addParticle(new SmallLightningParticleData(37, true), x, y, z, xSpeed, ySpeed, zSpeed);
			}

			for (int i = 0; i < 10; i++) {
				double xSpeed = speed * xComp + (spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - xComp * xComp)));
				double ySpeed = speed * yComp + (spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - yComp * yComp)));
				double zSpeed = speed * zComp + (spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - zComp * zComp)));
				player.level.addParticle(new LargeLightningParticleData(37, false), x, y, z, xSpeed, ySpeed, zSpeed);
			}
		}

		hitEntities();
		
		if (player.tickCount % 20 == 0) {
			hitBlocks();
		}
	}
	
	@Override
	public boolean canHitEntity(LivingEntity entity)
	{
		return true;
	}
	
	@Override
	public void tickEffect(LivingEntity entity)
	{
		Capabilities.getGenericCapability(entity).ifPresent(cap -> {
			cap.chargedTimer++;
		});
	}
	
	@Override
	public void onDamage(LivingEntity entity) {}
	
	@Override
	public void onBlock(BlockPos pos, BlockState blockState)
	{
		if(blockState.getMaterial().isSolidBlocking()) {
			if(!player.level.isClientSide) {
				if(player.level.random.nextInt(100) < 30){
					AreaEffectCloudEntity entity = new AreaEffectCloudEntity(EntityType.AREA_EFFECT_CLOUD, player.level);
					entity.setWaitTime(0);
					entity.setPos(pos.above().getX(), pos.above().getY(), pos.above().getZ());
					entity.setPotion(new Potion(new EffectInstance(DragonEffects.CHARGED, Functions.secondsToTicks(30) * 4))); //Effect duration is divided by 4 normaly
					entity.setDuration(Functions.secondsToTicks(2));
					entity.setRadius(1);
					entity.setParticle(new SmallLightningParticleData(37, false));
					player.level.addFreshEntity(entity);
				}
			}
			
			//							if(player.level.isClientSide) {
			//								for (int z = 0; z < 4; ++z) {
			//									if (player.level.random.nextInt(100) < 20) {
			//										player.level.addParticle(ParticleTypes.LAVA, i, j, k, 0, 0.05, 0);
			//									}
			//								}
			//							}
		}
	}

	public static int getDamage(int level){
		return level;
	}

	public int getDamage(){
		return getDamage(getLevel());
	}
}
