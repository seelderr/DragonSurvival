package by.dragonsurvivalteam.dragonsurvival.common.handlers.magic;

import by.dragonsurvivalteam.dragonsurvival.client.particles.DSParticles;
import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.capability.GenericCapability;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.GenericCapabilityProvider;
import by.dragonsurvivalteam.dragonsurvival.common.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Actives.BreathAbilities.BreathAbility.BreathDamage;
import by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Actives.BreathAbilities.StormBreathAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Passives.BurnAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Passives.SpectralImpactAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.magic.ActivateClientAbility;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncAbilityCastTime;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.TickEvent.Phase;
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
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

@EventBusSubscriber
public class MagicHandler{
	private static final UUID DRAGON_PASSIVE_MOVEMENT_SPEED = UUID.fromString("cdc3be6e-e17d-4efa-90f4-9dd838e9b000");

	@SubscribeEvent
	public static void magicUpdate(PlayerTickEvent event){
		if(event.phase == Phase.START){
			return;
		}

		Player player = event.player;

		AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);

		DragonStateProvider.getCap(player).ifPresent(cap -> {
			if(player.isSpectator()){
				if(cap.getMagic().getCurrentlyCasting() != null){
					cap.getMagic().getCurrentlyCasting().stopCasting();
					cap.getMagic().setCurrentlyCasting(null);
				}
				return;
			}

			if(!cap.isDragon() || cap.getLevel() != DragonLevel.ADULT){
				if(moveSpeed.getModifier(DRAGON_PASSIVE_MOVEMENT_SPEED) != null){
					moveSpeed.removeModifier(DRAGON_PASSIVE_MOVEMENT_SPEED);
				}
			}

			if(cap.getLevel() == DragonLevel.ADULT){
				AttributeModifier move_speed = new AttributeModifier(DRAGON_PASSIVE_MOVEMENT_SPEED, "DRAGON_MOVE_SPEED", 0.2F, AttributeModifier.Operation.MULTIPLY_TOTAL);

				if(moveSpeed.getModifier(DRAGON_PASSIVE_MOVEMENT_SPEED) == null){
					moveSpeed.addTransientModifier(move_speed);
				}
			}

			if(cap.getMagic().getCurrentlyCasting() != null){
				ActiveDragonAbility ability = cap.getMagic().getCurrentlyCasting();
				ability.player = player;

				if(!player.level.isClientSide){
					if(ability.getCastingTime() <= 0 || ability.getCurrentCastTimer() >= ability.getCastingTime()){
						player.causeFoodExhaustion(0.1F * ability.getManaCost());
						ability.onActivation(player);

						NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ActivateClientAbility(player.getId()));
					}else{
						player.causeFoodExhaustion(0.1F);
						ability.tickCasting();

						if(!player.level.isClientSide){
							NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncAbilityCastTime(player.getId(), ability.getCurrentCastTimer()));
						}
					}
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
		if(event.phase == Phase.START){
			return;
		}

		Player player = event.player;

		DragonStateProvider.getCap(player).ifPresent(cap -> {
			if(!cap.isDragon()){
				return;
			}

			for(DragonAbility ability : cap.getMagic().getAbilities()){
				ability.player = player;
			}

			if(player.hasEffect(DragonEffects.WATER_VISION) && player.isEyeInFluid(FluidTags.WATER)){
				player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 10, 0, false, false));
			}

			if(player.hasEffect(DragonEffects.HUNTER)){
				BlockState bl = player.getFeetBlockState();
				BlockState below = player.level.getBlockState(player.blockPosition().below());

				if(bl.getMaterial() == Material.PLANT || bl.getMaterial() == Material.REPLACEABLE_PLANT || bl.getMaterial() == Material.GRASS || below.getMaterial() == Material.PLANT || below.getMaterial() == Material.REPLACEABLE_PLANT){
					player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 10, 0, false, false));
				}

				player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 2, false, false));
			}
		});
	}

	@SubscribeEvent
	public static void livingVisibility(LivingVisibilityEvent event){
		if(event.getEntityLiving() instanceof Player){
			Player player = (Player)event.getEntityLiving();
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if(!cap.isDragon()){
					return;
				}

				if(player.hasEffect(DragonEffects.HUNTER)){
					event.modifyVisibility(0);
				}
			});
		}
	}

	@SubscribeEvent
	public static void livingTick(LivingUpdateEvent event){
		LivingEntity entity = event.getEntityLiving();

		if(entity.hasEffect(DragonEffects.BURN)){
			if(entity.isEyeInFluid(FluidTags.WATER) || entity.isInWaterRainOrBubble()){
				entity.removeEffect(DragonEffects.BURN);
			}
		}


		if(entity.hasEffect(DragonEffects.DRAIN)){
			DragonType type = DragonStateProvider.getCap(entity).map(cap -> cap.getType()).orElse(null);

			if(type != DragonType.FOREST){
				if(entity.tickCount % 20 == 0){
					GenericCapability cap = GenericCapabilityProvider.getGenericCapability(entity).orElse(null);
					Player player = cap != null && cap.lastAfflicted != -1 && entity.level.getEntity(cap.lastAfflicted) instanceof Player ? ((Player)entity.level.getEntity(cap.lastAfflicted)) : null;
					if(player != null){
						entity.hurt(new EntityDamageSource("magic", player).bypassArmor().setMagic(), 1.0F);
					}else{
						entity.hurt(DamageSource.MAGIC, 1.0F);
					}
				}
			}
		}

		if(entity.hasEffect(DragonEffects.CHARGED)){
			if(entity.tickCount % 20 == 0){
				DragonType type = DragonStateProvider.getCap(entity).map(cap -> cap.getType()).orElse(null);
				GenericCapability cap = GenericCapabilityProvider.getGenericCapability(entity).orElse(null);
				Player player = cap != null && cap.lastAfflicted != -1 && entity.level.getEntity(cap.lastAfflicted) instanceof Player ? ((Player)entity.level.getEntity(cap.lastAfflicted)) : null;
				if(type != DragonType.SEA){
					StormBreathAbility.chargedEffectSparkle(player, entity, ServerConfig.chargedChainRange, ServerConfig.chargedEffectChainCount, ServerConfig.chargedEffectDamage);
				}
			}
		}else{
			GenericCapability cap = GenericCapabilityProvider.getGenericCapability(entity).orElse(null);

			if(cap != null && cap.lastAfflicted != -1){
				cap.lastAfflicted = -1;
			}
		}

		GenericCapabilityProvider.getGenericCapability(entity).ifPresent(cap -> {
			if(entity.tickCount % 20 == 0){
				if(entity.hasEffect(DragonEffects.BURN)){
					if(!entity.fireImmune()){
						if(cap.lastPos != null){
							double distance = entity.distanceToSqr(cap.lastPos);
							float damage = Mth.clamp((float)distance, 0, 10);

							if(damage > 0){
								//Short enough fire duration to not cause fire damage but still drop cooked items
								if(!entity.isOnFire()){
									entity.setRemainingFireTicks(1);
								}
								Player player = cap != null && cap.lastAfflicted != -1 && entity.level.getEntity(cap.lastAfflicted) instanceof Player ? ((Player)entity.level.getEntity(cap.lastAfflicted)) : null;
								if(player != null){
									entity.hurt(new EntityDamageSource("onFire", player).bypassArmor().setIsFire(), damage);
								}else{
									entity.hurt(DamageSource.ON_FIRE, damage);
								}
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
		if(event.getEntity() instanceof Player){
			Player player = (Player)event.getEntity();

			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if(!cap.isDragon()){
					return;
				}

				if(cap.getType() == DragonType.SEA){
					event.setCanceled(true);
				}
			});
		}
	}

	@SubscribeEvent
	public static void playerDamaged(LivingDamageEvent event){
		if(event.getEntityLiving() instanceof Player){
			Player player = (Player)event.getEntityLiving();
			LivingEntity target = event.getEntityLiving();
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if(!cap.isDragon()){
					return;
				}

				if(player.hasEffect(DragonEffects.HUNTER)){
					player.removeEffect(DragonEffects.HUNTER);
				}
			});
		}
	}

	@SubscribeEvent
	public static void playerHitEntity(CriticalHitEvent event){
		if(event.getEntityLiving() instanceof Player){
			Player player = (Player)event.getEntityLiving();

			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if(!cap.isDragon()){
					return;
				}

				if(player.hasEffect(DragonEffects.HUNTER)){
					MobEffectInstance hunter = player.getEffect(DragonEffects.HUNTER);
					player.removeEffect(DragonEffects.HUNTER);
					event.setDamageModifier((float)((hunter.getAmplifier() + 1) * ServerConfig.hunterDamageBonus));
					event.setResult(Result.ALLOW);
				}
			});
		}
	}

	@SubscribeEvent
	public static void livingHurt(LivingAttackEvent event){
		if(event.getSource() instanceof EntityDamageSource && !(event.getSource() instanceof IndirectEntityDamageSource) && !(event.getSource() instanceof BreathDamage)){
			if(event.getEntity() instanceof LivingEntity){
				if(event.getSource() != null && event.getSource().getEntity() != null){
					if(event.getSource().getEntity() instanceof Player){
						Player player = (Player)event.getSource().getEntity();
						LivingEntity target = (LivingEntity)event.getEntity();
						DragonStateProvider.getCap(player).ifPresent(cap -> {
							if(!cap.isDragon()){
								return;
							}

							if(cap.getType() == DragonType.SEA){
								SpectralImpactAbility spectralImpact = (SpectralImpactAbility)cap.getMagic().getAbilityOrDefault(DragonAbilities.SPECTRAL_IMPACT);
								boolean hit = player.level.random.nextInt(100) <= spectralImpact.getChance();

								if(hit){
									event.getSource().bypassArmor();
									double d0 = -Mth.sin(player.yRot * ((float)Math.PI / 180F));
									double d1 = Mth.cos(player.yRot * ((float)Math.PI / 180F));

									if(player.level instanceof ServerLevel){
										((ServerLevel)player.level).sendParticles(DSParticles.seaSweep, player.getX() + d0, player.getY(0.5D), player.getZ() + d1, 0, d0, 0.0D, d1, 0.0D);
									}
								}
							}else if(cap.getType() == DragonType.CAVE){
								BurnAbility burnAbility = (BurnAbility)cap.getMagic().getAbilityOrDefault(DragonAbilities.BURN);
								boolean hit = player.level.random.nextInt(100) < burnAbility.getChance();

								if(hit){
									GenericCapability cap1 = GenericCapabilityProvider.getGenericCapability(event.getEntity()).orElse(null);

									if(cap1 != null){
										cap1.lastAfflicted = player.getId();
									}

									if(!player.level.isClientSide){
										((LivingEntity)event.getEntity()).addEffect(new MobEffectInstance(DragonEffects.BURN, Functions.secondsToTicks(30)));
									}
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
		Player player = event.getAttackingPlayer();

		if(player != null){
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if(!cap.isDragon()){
					return;
				}

				if(player.hasEffect(DragonEffects.REVEALING_THE_SOUL)){
					int extra = (int)Math.min(ServerConfig.revealingTheSoulMaxEXP, event.getDroppedExperience() * ServerConfig.revealingTheSoulMultiplier);
					event.setDroppedExperience(event.getDroppedExperience() + extra);
				}
			});
		}
	}
}