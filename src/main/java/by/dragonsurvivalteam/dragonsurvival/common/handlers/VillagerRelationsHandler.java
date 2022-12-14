package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.VillageRelationShips;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.*;
import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.FollowMobGoal;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.registry.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber
public class VillagerRelationsHandler{
	public static List<? extends EntityType<? extends PathfinderMob>> dragonHunters;
	//change to minutes
	private static int timeLeft = Functions.minutesToTicks(ServerConfig.princessSpawnDelay) + Functions.minutesToTicks(ThreadLocalRandom.current().nextInt(30));

	@SubscribeEvent
	public static void onDeath(LivingDeathEvent deathEvent){
		LivingEntity livingEntity = deathEvent.getEntityLiving();
		Entity killer = deathEvent.getSource().getEntity();
		if(killer instanceof Player playerEntity){
			if(livingEntity instanceof AbstractVillager){
				Level world = killer.level;
				if(!(livingEntity instanceof PrincesHorseEntity)){

					if(DragonUtils.isDragon(killer)){
						AbstractVillager villagerEntity = (AbstractVillager)livingEntity;

						MerchantOffers merchantOffers = villagerEntity.getOffers();

						if(villagerEntity instanceof Villager villager){
							int level = villager.getVillagerData().getLevel();

							if(world.random.nextInt(100) < 30){
								Optional<MerchantOffer> offer = merchantOffers.stream().filter(merchantOffer -> merchantOffer.getResult().getItem() != Items.EMERALD).toList().stream().findAny();

								offer.ifPresent(merchantOffer -> world.addFreshEntity(new ItemEntity(world, villager.getX(), villager.getY(), villager.getZ(), merchantOffer.getResult())));
							}

							if(!world.isClientSide){
								playerEntity.giveExperiencePoints(level * ServerConfig.xpGain);
								//                                applyEvilMarker(playerEntity);
							}
						}else if(villagerEntity instanceof WanderingTrader){
							WanderingTrader wanderingTrader = (WanderingTrader)villagerEntity;
							if(!world.isClientSide){
								playerEntity.giveExperiencePoints(2 * ServerConfig.xpGain);
								if(world.random.nextInt(100) < 30){
									ItemStack itemStack = wanderingTrader.getOffers().stream().filter((merchantOffer -> merchantOffer.getResult().getItem() != Items.EMERALD)).toList().get(wanderingTrader.getRandom().nextInt(wanderingTrader.getOffers().size())).getResult();
									world.addFreshEntity(new ItemEntity(world, wanderingTrader.getX(), wanderingTrader.getY(), wanderingTrader.getZ(), itemStack));
								}
								//                                applyEvilMarker(playerEntity);
							}
						}
					}
				}
			}else if(livingEntity instanceof DragonHunter){
				if(DragonUtils.isDragon(playerEntity)){
					//                    applyEvilMarker(playerEntity);
				}else if(livingEntity instanceof KnightEntity){
					playerEntity.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, Functions.minutesToTicks(5)));
				}
			}
			String typeName = livingEntity.getType().getRegistryName().toString();
			if(DragonUtils.isDragon(playerEntity) && ServerConfig.evilDragonStatusGivers.contains(typeName)){
				applyEvilMarker(playerEntity);
			}
		}
	}

	public static void applyEvilMarker(Player playerEntity){
		DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				MobEffectInstance effectInstance = playerEntity.getEffect(DragonEffects.EVIL_DRAGON);
				if(effectInstance == null){
					playerEntity.addEffect(new MobEffectInstance(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(1)));
				}else{
					int duration = effectInstance.getDuration();
					if(duration <= Functions.minutesToTicks(1)){
						playerEntity.addEffect(new MobEffectInstance(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(5), 1));
					}else if(duration <= Functions.minutesToTicks(5)){
						playerEntity.addEffect(new MobEffectInstance(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(10), 2));
					}else if(duration <= Functions.minutesToTicks(10)){
						playerEntity.addEffect(new MobEffectInstance(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(15), 3));
					}else if(duration <= Functions.minutesToTicks(15)){
						playerEntity.addEffect(new MobEffectInstance(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(20), 4));
					}else if(duration <= Functions.minutesToTicks(20)){
						playerEntity.addEffect(new MobEffectInstance(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(25), 5));
					}else if(duration <= Functions.minutesToTicks(25)){
						playerEntity.addEffect(new MobEffectInstance(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(30), 6));
					}else if(duration <= Functions.minutesToTicks(30)){
						playerEntity.addEffect(new MobEffectInstance(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(60), 7));
					}else if(duration <= Functions.minutesToTicks(60)){
						playerEntity.addEffect(new MobEffectInstance(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(120), 8));
					}else if(duration <= Functions.minutesToTicks(120)){
						playerEntity.addEffect(new MobEffectInstance(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(240), 9));
					}else if(duration <= Functions.minutesToTicks(240)){
						playerEntity.addEffect(new MobEffectInstance(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(400), 10));
					}
				}
			}
		});
	}

	@SubscribeEvent
	public static void entityTargets(LivingSetAttackTargetEvent setAttackTargetEvent){
		Entity entity = setAttackTargetEvent.getEntity();
		LivingEntity target = setAttackTargetEvent.getTarget();
		if(entity instanceof IronGolem){
			if(target instanceof DragonHunter){
				((IronGolem)entity).setTarget(null);
			}
		}else if(entity instanceof Zombie && (target instanceof Princess || target instanceof PrincesHorseEntity)){
			((Zombie)entity).setTarget(null);
		}
	}

	@SubscribeEvent
	public static void voidEvilStatus(PotionEvent.PotionAddedEvent potionAddedEvent){
		MobEffectInstance effectInstance = potionAddedEvent.getPotionEffect();
		LivingEntity livingEntity = potionAddedEvent.getEntityLiving();
		if(effectInstance.getEffect() == MobEffects.HERO_OF_THE_VILLAGE){
			livingEntity.removeEffect(DragonEffects.EVIL_DRAGON);
		}
	}

	@SubscribeEvent
	public static void specialTasks(EntityJoinWorldEvent joinWorldEvent){
		Level world = joinWorldEvent.getWorld();
		Entity entity = joinWorldEvent.getEntity();
		if(entity instanceof IronGolem golemEntity){
			golemEntity.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(golemEntity, Player.class, 0, true, false, livingEntity -> DragonUtils.isDragon(livingEntity) && livingEntity.hasEffect(DragonEffects.EVIL_DRAGON)));
		}

		if(entity instanceof AbstractVillager abstractVillager && !(entity instanceof PrinceHorseEntity)){
			abstractVillager.goalSelector.addGoal(10, new AvoidEntityGoal<>(abstractVillager, Player.class, livingEntity -> (DragonUtils.isDragon(livingEntity) && livingEntity.hasEffect(DragonEffects.EVIL_DRAGON)), 16.0F, 1.0D, 1.0D, (pMob) -> true));
		}
	}

	@SubscribeEvent
	public static void interactions(PlayerInteractEvent.EntityInteract event){
		Player playerEntity = event.getPlayer();
		Entity livingEntity = event.getTarget();
		if(livingEntity instanceof AbstractVillager){
			if(DragonUtils.isDragon(playerEntity) && playerEntity.hasEffect(DragonEffects.EVIL_DRAGON)){
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void hurtEntity(LivingDamageEvent attackEntityEvent){
		Entity attacked = attackEntityEvent.getEntity();
		Player attacker = attackEntityEvent.getSource().getEntity() instanceof Player ? ((Player)attackEntityEvent.getSource().getEntity()) : null;

		if(attacker == null){
			return;
		}

		if(attacked instanceof AbstractVillager || attacked instanceof DragonHunter){
			if(DragonUtils.isDragon(attacker)){
				if(attacker.hasEffect(DragonEffects.EVIL_DRAGON)){
					int duration = attacker.getEffect(DragonEffects.EVIL_DRAGON).getDuration();
					int amplifier = attacker.getEffect(DragonEffects.EVIL_DRAGON).getAmplifier();
					attacker.addEffect(new MobEffectInstance(DragonEffects.EVIL_DRAGON, duration + Functions.secondsToTicks(5), amplifier));
				}else{
					attacker.addEffect(new MobEffectInstance(DragonEffects.EVIL_DRAGON, Functions.secondsToTicks(5)));
				}
			}
		}
	}

	@SubscribeEvent
	public static void spawnHunters(TickEvent.PlayerTickEvent playerTickEvent){
		if(!dragonHunters.isEmpty() && playerTickEvent.phase == TickEvent.Phase.END){
			Player player = playerTickEvent.player;
			if(DragonUtils.isDragon(player) && player.hasEffect(DragonEffects.EVIL_DRAGON) && !player.level.isClientSide && !player.isCreative() && !player.isSpectator() && player.isAlive()){
				ServerLevel serverWorld = (ServerLevel)player.level;
				if(serverWorld.dimension() == Level.OVERWORLD){
					VillageRelationShips villageRelationShips = DragonUtils.getHandler(player).getVillageRelationShips();
						if(villageRelationShips.hunterSpawnDelay == 0){
							BlockPos spawnPosition = Functions.findRandomSpawnPosition(player, 1, 4, 14.0F);
							if(spawnPosition != null && spawnPosition.getY() >= ServerConfig.riderSpawnLowerBound && spawnPosition.getY() <= ServerConfig.riderSpawnUpperBound){
								Optional<ResourceKey<Biome>> biomeRegistryKey = serverWorld.getBiome(spawnPosition).unwrapKey();
								if(biomeRegistryKey.isPresent()){
									ResourceKey<Biome> biome = biomeRegistryKey.get();
									if(BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)){
										return;
									}
								}
								int levelOfEvil = computeLevelOfEvil(player);
								for(int i = 0; i < levelOfEvil; i++){
									Functions.spawn(Objects.requireNonNull((dragonHunters.get(serverWorld.random.nextInt(dragonHunters.size()))).create(serverWorld)), spawnPosition, serverWorld);
								}
								if(serverWorld.isCloseToVillage(player.blockPosition(), 3)){
									villageRelationShips.hunterSpawnDelay = Functions.minutesToTicks(ServerConfig.hunterSpawnDelay / 3) + Functions.minutesToTicks(serverWorld.random.nextInt(ServerConfig.hunterSpawnDelay / 6));
								}else{
									villageRelationShips.hunterSpawnDelay = Functions.minutesToTicks(ServerConfig.hunterSpawnDelay) + Functions.minutesToTicks(serverWorld.random.nextInt(ServerConfig.hunterSpawnDelay / 3));
								}
							}
						}else{
							villageRelationShips.hunterSpawnDelay--;
						}
				}
			}
		}
	}

	public static int computeLevelOfEvil(Player playerEntity){
		if(DragonUtils.isDragon(playerEntity) && playerEntity.hasEffect(DragonEffects.EVIL_DRAGON)){
			MobEffectInstance effectInstance = playerEntity.getEffect(DragonEffects.EVIL_DRAGON);
			assert effectInstance != null;
			int timeLeft = effectInstance.getDuration();
			if(timeLeft >= Functions.minutesToTicks(240)){
				return 10;
			}
			if(timeLeft >= Functions.minutesToTicks(120)){
				return 9;
			}
			if(timeLeft >= Functions.minutesToTicks(60)){
				return 8;
			}
			if(timeLeft >= Functions.minutesToTicks(30)){
				return 7;
			}
			if(timeLeft >= Functions.minutesToTicks(25)){
				return 6;
			}
			if(timeLeft >= Functions.minutesToTicks(20)){
				return 5;
			}
			if(timeLeft >= Functions.minutesToTicks(15)){
				return 4;
			}
			if(timeLeft >= Functions.minutesToTicks(10)){
				return 3;
			}
			if(timeLeft >= Functions.minutesToTicks(5)){
				return 2;
			}
			if(timeLeft >= Functions.minutesToTicks(1)){
				return 1;
			}
		}
		return 0;
	}

	@SubscribeEvent
	public static void spawnPrinceOrPrincess(TickEvent.WorldTickEvent serverTickEvent){
		if(ServerConfig.spawnPrinceAndPrincess){
			Level world = serverTickEvent.world;
			if(world instanceof ServerLevel serverWorld){
				if(!serverWorld.players().isEmpty() && serverWorld.dimension() == Level.OVERWORLD){
					if(timeLeft == 0){
						ServerPlayer player = serverWorld.getRandomPlayer();
						if(player != null && player.isAlive() && !player.isCreative() && !player.isSpectator()){
							BlockPos blockPos = Functions.findRandomSpawnPosition(player, 1, 2, 20.0F);
							if(blockPos != null && blockPos.getY() >= ServerConfig.riderSpawnLowerBound && blockPos.getY() <= ServerConfig.riderSpawnUpperBound && serverWorld.isVillage(blockPos)){
								Optional<ResourceKey<Biome>> biomeRegistryKey = serverWorld.getBiome(blockPos).unwrapKey();
								if(biomeRegistryKey.isPresent()){
									ResourceKey<Biome> biome = biomeRegistryKey.get();
									if(BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)){
										return;
									}
								}
								EntityType<? extends PrincesHorseEntity> entityType = world.random.nextBoolean() ? DSEntities.PRINCESS_ON_HORSE : DSEntities.PRINCE_ON_HORSE;
								PrincesHorseEntity princessEntity = entityType.create(world);
								princessEntity.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
								princessEntity.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(player.blockPosition()), MobSpawnType.NATURAL, null, null);
								serverWorld.addFreshEntity(princessEntity);

								int knights = world.random.nextInt(3) + 3;
								for(int i = 0; i < knights; i++){
									KnightEntity knightHunter = DSEntities.KNIGHT.create(serverWorld);
									knightHunter.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
									knightHunter.goalSelector.addGoal(5, new FollowMobGoal(PrincesHorseEntity.class, knightHunter, 8));
									knightHunter.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(player.blockPosition()), MobSpawnType.NATURAL, null, null);
								}

								timeLeft = Functions.minutesToTicks(ServerConfig.princessSpawnDelay) + Functions.minutesToTicks(world.random.nextInt(ServerConfig.princessSpawnDelay / 2));
							}
						}
					}else{
						timeLeft--;
					}
				}
			}
		}
	}

	/**
	 * Save duration of 'evil dragon'
	 */
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent playerTickEvent){
		if(playerTickEvent.phase == TickEvent.Phase.END){
			Player playerEntity = playerTickEvent.player;
			if(!playerEntity.level.isClientSide){
				if(playerEntity.hasEffect(DragonEffects.EVIL_DRAGON)){
					DragonUtils.getHandler(playerEntity).getVillageRelationShips().evilStatusDuration = playerEntity.getEffect(DragonEffects.EVIL_DRAGON).getDuration();
				}
			}
		}
	}
}