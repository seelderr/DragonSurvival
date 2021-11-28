package by.jackraidenph.dragonsurvival.magic.abilities.Actives.BreathAbilities;

import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.particles.SeaDragon.LargeLightningParticleData;
import by.jackraidenph.dragonsurvival.particles.SeaDragon.SmallLightningParticleData;
import by.jackraidenph.dragonsurvival.registration.DragonEffects;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;

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
			for (int i = 0; i < 24; i++) {
				double xSpeed = speed * 1f * xComp;
				double ySpeed = speed * 1f * yComp;
				double zSpeed = speed * 1f * zComp;
				player.level.addParticle(new LargeLightningParticleData(37, true), x, y, z, xSpeed, ySpeed, zSpeed);
			}
			
			for (int i = 0; i < 10; i++) {
				double xSpeed = speed * xComp + (spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - xComp * xComp)));
				double ySpeed = speed * yComp + (spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - yComp * yComp)));
				double zSpeed = speed * zComp + (spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - zComp * zComp)));
				player.level.addParticle(new SmallLightningParticleData(37, false), x, y, z, xSpeed, ySpeed, zSpeed);
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
	
	private static Field chargedCreeperField;
	
	@Override
	public void onDamage(LivingEntity entity) {
		onDamageChecks(entity);
	}
	
	public void onEntityHit(LivingEntity entityHit){
		hurtTarget(entityHit);
		LightningBreathAbility.chargedEffectSparkle(entityHit, 6, 2, 1);
	}
	
	public static void onDamageChecks(LivingEntity entity){
		if(entity instanceof CreeperEntity){
			CreeperEntity creeper = (CreeperEntity)entity;
			
			if(!creeper.isPowered()){
				if(chargedCreeperField == null){
					chargedCreeperField = ObfuscationReflectionHelper.findField(CreeperEntity.class, "field_184714_b"); //DATA_IS_POWERED
					chargedCreeperField.setAccessible(true);
				}
				
				if(chargedCreeperField != null){
					try {
						DataParameter<Boolean> powered = (DataParameter<Boolean>)chargedCreeperField.get(creeper);
						creeper.getEntityData().set(powered, true);
					} catch (IllegalAccessException e) {}
				}
			}
		}
	}
	
	public void hurtTarget(LivingEntity entity){
		entity.hurt(DamageSource.playerAttack(player), getDamage());
		onDamage(entity);
		
		if(player.level.random.nextInt(100) < 50){
			if(!player.level.isClientSide) {
				player.addEffect(new EffectInstance(DragonEffects.CHARGED, Functions.secondsToTicks(30)));
			}
		}
		
		if(!entity.level.isClientSide) {
			if (entity.level.random.nextInt(100) < 40) {
				entity.addEffect(new EffectInstance(DragonEffects.CHARGED, Functions.secondsToTicks(10), 0, false, true));
			}
		}
	}
	
	public static void spark(LivingEntity source, LivingEntity target){
		if(source.level.isClientSide) {
			float eyeHeight = source instanceof PlayerEntity ? 0f : source.getEyeHeight();
			Vector3d start = source.getPosition(eyeHeight);
			Vector3d end = target.getPosition(target.getEyeHeight());
			
			int parts = 20;
			
			double xDif = (end.x - start.x) / parts;
			double yDif = (end.y - start.y) / parts;
			double zDif = (end.z - start.z) / parts;
			
			for (int i = 0; i < parts; i++) {
				double x = start.x + (xDif * i);
				double y = start.y + (yDif * i) + eyeHeight;
				double z = start.z + (zDif * i);
				source.level.addParticle(new RedstoneParticleData(0f, 1F, 1F, 1f), x, y, z, 0, 0, 0);
			}
		}
	}
	
	public static void chargedEffectSparkle(LivingEntity source, int chainRange, int maxChainTargets, int damage){
		List<LivingEntity> secondaryTargets = getEntityLivingBaseNearby(source, source.getX(), source.getY() + source.getBbHeight() / 2, source.getZ(), chainRange);
		secondaryTargets.removeIf(e -> !isValidTarget(source, e));
		
		if(secondaryTargets.size() > maxChainTargets){
			secondaryTargets.sort((c1, c2) -> Boolean.compare(c1.hasEffect(DragonEffects.CHARGED), c2.hasEffect(DragonEffects.CHARGED)));
			secondaryTargets = secondaryTargets.subList(0, maxChainTargets);
		}
		
		secondaryTargets.add(source);
		
		for(LivingEntity target : secondaryTargets){
			target.hurt(DamageSource.mobAttack(source), damage);
			onDamageChecks(target);
			
			if(target != source) {
				if(!target.level.isClientSide) {
					if (target.level.random.nextInt(100) < 40) {
						target.addEffect(new EffectInstance(DragonEffects.CHARGED, Functions.secondsToTicks(10), 0, false, true));
					}
				}
				
				spark(source, target);
			}
		}
	}
	
	@Override
	public void onBlock(BlockPos pos, BlockState blockState)
	{
		if(!player.level.isClientSide){
			if(player.tickCount % 40 == 0) {
				if (player.level.isThundering()) {
					if (player.level.random.nextInt(100) < 30) {
						if (player.level.canSeeSky(pos)) {
							LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(player.level);
							lightningboltentity.moveTo(new Vector3d(pos.getX(), pos.getY(), pos.getZ()));
							lightningboltentity.setCause((ServerPlayerEntity)player);
							player.level.addFreshEntity(lightningboltentity);
							player.level.playSound(player, pos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 5F, 1.0F);
						}
					}
				}
			}
		}
	}

	public static int getDamage(int level){
		return level;
	}

	public int getDamage(){
		return getDamage(getLevel());
	}
	
	public static boolean isValidTarget(LivingEntity attacker, LivingEntity target){
		if(target == null || attacker == null) return false;
		if(target == attacker) return false;
		if(target instanceof FakePlayer) return false;
		if(target instanceof TameableEntity && ((TameableEntity)target).getOwner() == attacker) return false;
		if(attacker instanceof TameableEntity && !isValidTarget(((TameableEntity)attacker).getOwner(), target)) return false;
		if(target.getLastHurtByMob() == attacker && target.getLastHurtByMobTimestamp() + Functions.secondsToTicks(1) < target.tickCount) return false;
		if(DragonStateProvider.getCap(target).map(cap -> cap.getType()).orElse(null) == DragonType.SEA) return false;
		
		return true;
	}
}
