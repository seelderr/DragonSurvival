package by.jackraidenph.dragonsurvival.magic;

import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.capability.Capabilities;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.magic.Abilities.DragonAbilities;
import by.jackraidenph.dragonsurvival.magic.Abilities.Passives.BurnAbility;
import by.jackraidenph.dragonsurvival.magic.Abilities.Passives.SpectralImpactAbility;
import by.jackraidenph.dragonsurvival.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.registration.DragonEffects;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingVisibilityEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class MagicHandler
{
	public static boolean isPlayerInGoodConditions(PlayerEntity player){
		BlockState blockBelow = player.level.getBlockState(player.blockPosition().below());
		return DragonStateProvider.getCap(player).map(cap -> {
			switch (cap.getType()) {
				case SEA:
					if (player.isInWaterRainOrBubble() || blockBelow.getMaterial() == Material.SNOW || blockBelow.getMaterial() == Material.WATER) {
						return true;
					}
					break;
				
				case FOREST:
					if (player.level.canSeeSky(player.blockPosition()) && player.level.isDay()) {
						return true;
					}
					break;
				
				case CAVE:
					if (player.isInLava() || blockBelow.getMaterial() == Material.LAVA || blockBelow.getMaterial() == Material.FIRE || player.isOnFire()
					    || blockBelow.getMaterial().getColor() == MaterialColor.NETHER || blockBelow.getBlock() == Blocks.MAGMA_BLOCK
					    || blockBelow.getBlock() == Blocks.NETHERRACK) {
						return true;
					}
					break;
			}
			
			return false;
		}).orElse(false);
	}
	
	@SubscribeEvent
	public static void playerTick(PlayerTickEvent event){
		PlayerEntity player = event.player;
		
		if(!player.level.isClientSide) {
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				boolean goodConditions = isPlayerInGoodConditions(player);
				
				int timeToRecover = goodConditions ? 5 : 15;
				
				if (player.tickCount % Functions.secondsToTicks(timeToRecover) == 0) {
					if (cap.lastTick == -1 || cap.lastTick != player.tickCount) {
						cap.lastTick = player.tickCount; //It was activating twice for some reason
						if (cap.getCurrentMana() < DragonStateProvider.getMaxMana(player)) {
							DragonStateProvider.replenishMana(player, 1);
						}
					}
				}
			});
		}
		
		DragonStateProvider.getCap(player).ifPresent(cap -> {
			if(!cap.isDragon()) return;
			
			for (DragonAbility ability : cap.getAbilities()) {
				ability.player = player;
			}
			
			if(player.hasEffect(DragonEffects.HUNTER)){
				BlockState bl = player.getFeetBlockState();
				BlockState below = player.level.getBlockState(player.blockPosition().below());
				
				if(bl.getBlock() instanceof DoublePlantBlock || below.getBlock() instanceof DoublePlantBlock || bl.getMaterial() == Material.REPLACEABLE_PLANT || below.getMaterial() == Material.REPLACEABLE_PLANT){
					player.addEffect(new EffectInstance(Effects.INVISIBILITY, 10, 0, false, false));
				}
				
				player.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 20, 2, false, false));
			}
			
			if(cap.getCurrentlyCasting() != null && cap.getCurrentlyCasting().getCastingSlowness() > 0){
				player.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 10, cap.getCurrentlyCasting().getCastingSlowness(), false, false));
				player.addEffect(new EffectInstance(Effects.JUMP, 10, -cap.getCurrentlyCasting().getCastingSlowness(), false, false));
				
			}
		});
	}
	
	@SubscribeEvent
	public static void livingVisibility(LivingVisibilityEvent event){
		if(event.getEntityLiving() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)event.getEntityLiving();
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if (!cap.isDragon()) return;
				
				if (player.hasEffect(DragonEffects.HUNTER)) {
					event.modifyVisibility(0);
				}
			});
		}
	}
	
	@SubscribeEvent
	public static void livingTick(LivingUpdateEvent event){
		LivingEntity entity = event.getEntityLiving();
		
		if (entity.hasEffect(DragonEffects.BURN)) {
			if (entity.isEyeInFluid(FluidTags.WATER) || entity.isInWaterRainOrBubble()) {
				entity.removeEffect(DragonEffects.BURN);
			}
		}
		
		Capabilities.getGenericCapability(entity).ifPresent(cap -> {
			if (entity.tickCount % 20 == 0) {
				if (entity.hasEffect(DragonEffects.BURN)) {
					if (cap.lastPos != null) {
						double distance = entity.distanceToSqr(cap.lastPos);
						float damage = MathHelper.clamp((float)distance, 0, 10);
						
						if (damage > 0) {
							entity.hurt(DamageSource.ON_FIRE, damage);
						}
						
					}
				}
				
				cap.lastPos = entity.position();
			}
		});
		
		
		int durationForDebuff = Functions.secondsToTicks(2);
		//Apply breath debuffs
		Capabilities.getGenericCapability(entity).ifPresent(cap -> {
			if(cap.burnTimer >= durationForDebuff){
				cap.burnTimer = 0;
				entity.addEffect(new EffectInstance(DragonEffects.BURN, Functions.secondsToTicks(30), 0, false, false));
			}
			
			if(cap.chargedTimer >= durationForDebuff){
				cap.chargedTimer = 0;
				//entity.addEffect(new EffectInstance(DragonEffects.BURN, Functions.secondsToTicks(30), 0, false, false));
			}
			
			if(cap.drainTimer >= durationForDebuff){
				cap.drainTimer = 0;
				//entity.addEffect(new EffectInstance(DragonEffects.BURN, Functions.secondsToTicks(30), 0, false, false));
			}
			
			if(entity.tickCount % 2 == 0) {
				cap.burnTimer = Math.min(0, cap.burnTimer - 1);
				cap.chargedTimer = Math.min(0, cap.chargedTimer - 1);
				cap.drainTimer = Math.min(0, cap.drainTimer - 1);
			}
		});
	}
	
	@SubscribeEvent
	public static void playerDamaged(LivingDamageEvent event){
		if(event.getEntityLiving() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)event.getEntityLiving();
			
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if (!cap.isDragon()) return;
				
				if (player.hasEffect(DragonEffects.HUNTER)) {
					player.removeEffect(DragonEffects.HUNTER);
				}
			});
		}
	}
	
	@SubscribeEvent
	public static void playerHitEntity(CriticalHitEvent event){
		if(event.getEntityLiving() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)event.getEntityLiving();
			
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if (!cap.isDragon()) return;
				
				if (player.hasEffect(DragonEffects.HUNTER)) {
					EffectInstance hunter = player.getEffect(DragonEffects.HUNTER);
					player.removeEffect(DragonEffects.HUNTER);
					event.setDamageModifier((hunter.getAmplifier() + 1) * 1.5F);
					event.setResult(Result.ALLOW);
				}
			});
		}
	}
	
	@SubscribeEvent
	public static void livingHurt(LivingAttackEvent event){
		if(event.getSource() instanceof EntityDamageSource && !(event.getSource() instanceof IndirectEntityDamageSource)) {
			if(event.getEntity() instanceof LivingEntity){
			if (event.getSource() != null && event.getSource().getEntity() != null) {
				if (event.getSource().getEntity() instanceof PlayerEntity) {
					PlayerEntity player = (PlayerEntity)event.getSource().getEntity();
					
					DragonStateProvider.getCap(player).ifPresent(cap -> {
						if (!cap.isDragon()) return;
						
						if (cap.getType() == DragonType.SEA) {
							SpectralImpactAbility spectralImpact = (SpectralImpactAbility)cap.getAbilityOrDefault(DragonAbilities.SPECTRAL_IMPACT);
							boolean hit = player.level.random.nextInt(100) <= spectralImpact.getChance();
							
							if (hit) {
								event.getSource().bypassArmor();
							}
						} else if (cap.getType() == DragonType.CAVE) {
							BurnAbility burnAbility = (BurnAbility)cap.getAbilityOrDefault(DragonAbilities.BURN);
							boolean hit = player.level.random.nextInt(100) <= burnAbility.getChance();
							
							if (hit) {
								((LivingEntity)event.getEntity()).addEffect(new EffectInstance(DragonEffects.BURN, Functions.secondsToTicks(30)));
							}
						}
					});
				}
			}
			}
		}
	}
	
	
	@SubscribeEvent
	public static void experienceDrop(LivingExperienceDropEvent event){
		PlayerEntity player = event.getAttackingPlayer();
		
		if(player != null) {
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if (!cap.isDragon()) return;
				
				if (player.hasEffect(DragonEffects.REVEALING_THE_SOUL)) {
					int extra = Math.min(20, event.getDroppedExperience()); //TODO Change this to a config option for max exp gain
					event.setDroppedExperience(event.getDroppedExperience() + extra);
				}
			});
		}
	}
}
