package by.jackraidenph.dragonsurvival.handlers.Magic;

import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.capability.Capabilities;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.handlers.ServerSide.NetworkHandler;
import by.jackraidenph.dragonsurvival.magic.DragonAbilities;
import by.jackraidenph.dragonsurvival.magic.abilities.Actives.BreathAbilities.LightningBreathAbility;
import by.jackraidenph.dragonsurvival.magic.abilities.Passives.BurnAbility;
import by.jackraidenph.dragonsurvival.magic.abilities.Passives.SpectralImpactAbility;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.network.magic.ActivateClientAbility;
import by.jackraidenph.dragonsurvival.registration.DragonEffects;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
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
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingVisibilityEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.TargetPoint;

@EventBusSubscriber
public class MagicHandler
{
	@SubscribeEvent
	public static void magicUpdate(PlayerTickEvent event){
		PlayerEntity player = event.player;
		
		DragonStateProvider.getCap(player).ifPresent(cap -> {
			if (!cap.isDragon()) return;
			
			if(cap.getMagic().getCurrentlyCasting() != null){
				ActiveDragonAbility ability = cap.getMagic().getCurrentlyCasting();
				ability.player = player;
				
				if (ability.canRun(player, -1)) {
					if (ability.getCastingTime() <= 0 || ability.getCurrentCastTimer() >= ability.getCastingTime()) {
						if(!player.level.isClientSide) {
							ability.onActivation(player);
							
							TargetPoint point = new TargetPoint(player.position().x, player.position().y, player.position().z, 64, player.level.dimension());
							NetworkHandler.CHANNEL.send(PacketDistributor.NEAR.with(() -> point), new ActivateClientAbility(player.getId()));
						}
					} else {
						ability.tickCasting();
					}
					
				}else{
					cap.getMagic().setCurrentlyCasting(null);
				}
			}
			
			for(int i = 0; i < 4; i++){
				ActiveDragonAbility ability = cap.getMagic().getAbilityFromSlot(i);
				
				if(ability != null){
					ability.decreaseCooldownTimer();
				}
			}
		});
	}
	
	@SubscribeEvent
	public static void playerTick(PlayerTickEvent event){
		PlayerEntity player = event.player;
		
		DragonStateProvider.getCap(player).ifPresent(cap -> {
			if (!cap.isDragon()) return;
			
			for (DragonAbility ability : cap.getMagic().getAbilities()) {
				ability.player = player;
			}
			
			if (player.hasEffect(DragonEffects.HUNTER)) {
				BlockState bl = player.getFeetBlockState();
				BlockState below = player.level.getBlockState(player.blockPosition().below());
				
				if (bl.getMaterial() == Material.PLANT || bl.getMaterial() == Material.REPLACEABLE_PLANT || bl.getMaterial() == Material.GRASS || below.getMaterial() == Material.PLANT || below.getMaterial() == Material.REPLACEABLE_PLANT) {
					player.addEffect(new EffectInstance(Effects.INVISIBILITY, 10, 0, false, false));
				}
				
				player.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 20, 2, false, false));
			}
			
			if(!player.isCreative()) {
				if (cap.getMagic().getCurrentlyCasting() != null && cap.getMagic().getCurrentlyCasting().getCastingSlowness() > 0) {
					player.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 10, cap.getMagic().getCurrentlyCasting().getCastingSlowness(), false, false));
					player.addEffect(new EffectInstance(Effects.JUMP, 10, -cap.getMagic().getCurrentlyCasting().getCastingSlowness(), false, false));
					player.addEffect(new EffectInstance(Effects.SLOW_FALLING, 10, 0, false, false));
				}
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
		
		
		if(entity.hasEffect(DragonEffects.DRAIN)){
			DragonType type = DragonStateProvider.getCap(entity).map( cap -> cap.getType()).orElse(null);
			
			if(type != DragonType.FOREST){
				if (entity.tickCount % 20 == 0) {
					entity.hurt(DamageSource.MAGIC, 1.0F);
				}
			}
		}
		
		if(entity.hasEffect(DragonEffects.CHARGED)){
			if (entity.tickCount % 20 == 0) {
				DragonType type = DragonStateProvider.getCap(entity).map(cap -> cap.getType()).orElse(null);
				
				if (type != DragonType.SEA) {
					LightningBreathAbility.chargedEffectSparkle(entity, 6, 2, 1);
				}
			}
		}
		
		Capabilities.getGenericCapability(entity).ifPresent(cap -> {
			if (entity.tickCount % 20 == 0) {
				if (entity.hasEffect(DragonEffects.BURN)) {
					if(!entity.fireImmune()){
						if (cap.lastPos != null) {
							double distance = entity.distanceToSqr(cap.lastPos);
							float damage = MathHelper.clamp((float)distance, 0, 10);
							
							if (damage > 0) {
								//Short enough fire duration to not cause fire damage but still drop cooked items
								if(!entity.isOnFire()){
									entity.setRemainingFireTicks(1);
								}
								
								entity.hurt(DamageSource.ON_FIRE, damage);
							}
						}
					}
				}
				
				cap.lastPos = entity.position();
			}
		});
	}
	
	@SubscribeEvent
	public static void playerStruckByLightning(EntityStruckByLightningEvent event){
		if(event.getEntity() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)event.getEntity();
			
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if (!cap.isDragon()) return;
				
				if(cap.getType() == DragonType.SEA){
					event.setCanceled(true);
				}
			});
		}
	}
	
	@SubscribeEvent
	public static void playerDamaged(LivingDamageEvent event){
		if(event.getEntityLiving() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)event.getEntityLiving();
			LivingEntity target = event.getEntityLiving();
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
					event.setDamageModifier((float)((hunter.getAmplifier() + 1) * ConfigHandler.SERVER.hunterDamageBonus.get()));
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
					LivingEntity target = (LivingEntity)event.getEntity();
					DragonStateProvider.getCap(player).ifPresent(cap -> {
						if (!cap.isDragon()) return;
						
						if (cap.getType() == DragonType.SEA) {
							SpectralImpactAbility spectralImpact = (SpectralImpactAbility)cap.getMagic().getAbilityOrDefault(DragonAbilities.SPECTRAL_IMPACT);
							boolean hit = player.level.random.nextInt(100) <= spectralImpact.getChance();
							
							if (hit) {
								event.getSource().bypassArmor();
							}
						} else if (cap.getType() == DragonType.CAVE) {
							BurnAbility burnAbility = (BurnAbility)cap.getMagic().getAbilityOrDefault(DragonAbilities.BURN);
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
					int extra = (int)Math.min(ConfigHandler.SERVER.revealingTheSoulMaxEXP.get(), event.getDroppedExperience() * ConfigHandler.SERVER.revealingTheSoulMultiplier.get()); //TODO Change this to a config option for max exp gain
					event.setDroppedExperience(event.getDroppedExperience() + extra);
				}
			});
		}
	}
}
