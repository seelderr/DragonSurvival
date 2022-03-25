package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.EffectInstance2;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.VillageRelationshipsProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.*;
import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.FollowMobGoal;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.item.Item;
import net.minecraft.entity.merchant.villager.AbstractVillager;
import net.minecraft.entity.merchant.villager.Villager;
import net.minecraft.entity.merchant.villager.WanderingTrader;
import net.minecraft.entity.monster.Zombie;
import net.minecraft.entity.passive.IronGolem;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.potion.MobEffectInstance;
import net.minecraft.potion.MobEffects;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Level;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerLevel;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEntityDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntitySetAttackTargetEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class VillagerRelationsHandler{
	public static List<? extends EntityType<? extends Mob>> dragonHunters;
	//change to minutes
	private static int timeLeft = Functions.minutesToTicks(ConfigHandler.COMMON.princessSpawnDelay.get()) + Functions.minutesToTicks(ThreadLocalRandom.current().nextInt(30));

	@SubscribeEvent
	public static void onDeath(LivingEntityDeathEvent deathEvent){
		LivingEntity living = deathEvent.getEntityLivingEntity();
		Entity killer = deathEvent.getSource().get();
		if(killer instanceof Player){
			Player player = (Player)killer;
			if(living instanceof AbstractVillager){
				Level world = killer.level;
				living.getType().getRegistryName();
				if(!(living instanceof PrincesHorse)){

					if(DragonUtils.isDragon(killer)){
						AbstractVillager villager = (AbstractVillager)living;

						MerchantOffers merchantOffers = villager.getOffers();

						if(villager instanceof Villager){
							Villager villager = villager;

							int level = villager.getVillagerData().getLevel();

							if(world.random.nextInt(100) < 30){
								Optional<MerchantOffer> offer = merchantOffers.stream().filter(merchantOffer -> merchantOffer.getResult().getItem() != Items.EMERALD).collect(Collectors.toList()).stream().findAny();

								offer.ifPresent(merchantOffer -> world.addFreshEntity(new Item(world, villager.getX(), villager.getY(), villager.getZ(), merchantOffer.getResult())));
							}

							if(!world.isClientSide){
								player.giveExperiencePoints(level * ConfigHandler.COMMON.xpGain.get());
								//                                applyEvilMarker(playerEntity);
							}
						}else if(villager instanceof WanderingTrader){
							WanderingTrader wanderingTrader = (WanderingTrader)villager;
							if(!world.isClientSide){
								player.giveExperiencePoints(2 * ConfigHandler.COMMON.xpGain.get());
								if(world.random.nextInt(100) < 30){
									ItemStack itemStack = wanderingTrader.getOffers().stream().filter((merchantOffer -> merchantOffer.getResult().getItem() != Items.EMERALD)).collect(Collectors.toList()).get(wanderingTrader.getRandom().nextInt(wanderingTrader.getOffers().size())).getResult();
									world.addFreshEntity(new Item(world, wanderingTrader.getX(), wanderingTrader.getY(), wanderingTrader.getZ(), itemStack));
								}
								//                                applyEvilMarker(playerEntity);
							}
						}
					}
				}
			}else if(living instanceof DragonHunter){
				if(DragonUtils.isDragon(player)){
					//                    applyEvilMarker(playerEntity);
				}else if(living instanceof Knight){
					player.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, Functions.minutesToTicks(5)));
				}
			}
			String typeName = living.getType().getRegistryName().toString();
			if(DragonUtils.isDragon(player) && ConfigHandler.COMMON.evilDragonStatusGivers.get().contains(typeName)){
				applyEvilMarker(player);
			}
		}
	}

	public static void applyEvilMarker(Player player){
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				MobEffectInstance effectInstance = player.getEffect(DragonEffects.EVIL_DRAGON);
				if(effectInstance == null){
					player.addEffect(new EffectInstance2(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(1)));
				}else{
					int duration = effectInstance.getDuration();
					if(duration <= Functions.minutesToTicks(1)){
						player.addEffect(new EffectInstance2(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(5), 1));
					}else if(duration <= Functions.minutesToTicks(5)){
						player.addEffect(new EffectInstance2(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(10), 2));
					}else if(duration <= Functions.minutesToTicks(10)){
						player.addEffect(new EffectInstance2(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(15), 3));
					}else if(duration <= Functions.minutesToTicks(15)){
						player.addEffect(new EffectInstance2(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(20), 4));
					}else if(duration <= Functions.minutesToTicks(20)){
						player.addEffect(new EffectInstance2(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(25), 5));
					}else if(duration <= Functions.minutesToTicks(25)){
						player.addEffect(new EffectInstance2(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(30), 6));
					}else if(duration <= Functions.minutesToTicks(30)){
						player.addEffect(new EffectInstance2(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(60), 7));
					}else if(duration <= Functions.minutesToTicks(60)){
						player.addEffect(new EffectInstance2(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(120), 8));
					}else if(duration <= Functions.minutesToTicks(120)){
						player.addEffect(new EffectInstance2(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(240), 9));
					}else if(duration <= Functions.minutesToTicks(240)){
						player.addEffect(new EffectInstance2(DragonEffects.EVIL_DRAGON, Functions.minutesToTicks(400), 10));
					}
				}
			}
		});
	}

	@SubscribeEvent
	public static void entityTargets(LivingEntitySetAttackTargetEvent setAttackTargetEvent){
		Entity entity = setAttackTargetEvent.get();
		LivingEntity target = setAttackTargetEvent.getTarget();
		if(entity instanceof IronGolem){
			if(target instanceof DragonHunter){
				((IronGolem)entity).setTarget(null);
			}
		}else if(entity instanceof Zombie && (target instanceof Princess || target instanceof PrincesHorse)){
			((Zombie)entity).setTarget(null);
		}
	}

	@SubscribeEvent
	public static void voidEvilStatus(PotionEvent.PotionAddedEvent potionAddedEvent){
		MobEffectInstance effectInstance = potionAddedEvent.getPotionEffect();
		LivingEntity living = potionAddedEvent.getEntityLivingEntity();
		if(effectInstance.getEffect() == MobEffects.HERO_OF_THE_VILLAGE){
			living.removeEffect(DragonEffects.EVIL_DRAGON);
		}
	}

	@SubscribeEvent
	public static void specialTasks(EntityJoinWorldEvent joinWorldEvent){
		Level world = joinWorldEvent.getWorld();
		Entity entity = joinWorldEvent.get();
		if(entity instanceof IronGolem){
			IronGolem golem = (IronGolem)entity;
			golem.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(golem, Player.class, 0, true, false, living ->

				(DragonUtils.isDragon(living) && living.hasEffect(DragonEffects.EVIL_DRAGON))));
		}

		if(entity instanceof AbstractVillager && !(entity instanceof PrinceHorse)){
			AbstractVillager abstractVillager = (AbstractVillager)entity;
			abstractVillager.goalSelector.addGoal(10, new AvoidEntityGoal<>(abstractVillager, Player.class, living -> (DragonUtils.isDragon(living) && living.hasEffect(DragonEffects.EVIL_DRAGON)), 16.0F, 1.0D, 1.0D, EntityPredicates.NO_CREATIVE_OR_SPECTATOR::test));
		}
	}

	@SubscribeEvent
	public static void interactions(PlayerInteractEvent.EntityInteract event){
		Player player = event.getPlayer();
		Entity living = event.getTarget();
		if(living instanceof AbstractVillager){
			if(DragonUtils.isDragon(player) && player.hasEffect(DragonEffects.EVIL_DRAGON)){
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void hurt(LivingEntityDamageEvent attackEntityEvent){
		Entity attacked = attackEntityEvent.get();
		Player attacker = attackEntityEvent.getSource().get() instanceof Player ? ((Player)attackEntityEvent.getSource().get()) : null;

		if(attacker == null){
			return;
		}

		if(attacked instanceof AbstractVillager || attacked instanceof DragonHunter){
			if(DragonUtils.isDragon(attacker)){
				if(attacker.hasEffect(DragonEffects.EVIL_DRAGON)){
					int duration = attacker.getEffect(DragonEffects.EVIL_DRAGON).getDuration();
					int amplifier = attacker.getEffect(DragonEffects.EVIL_DRAGON).getAmplifier();
					attacker.addEffect(new EffectInstance2(DragonEffects.EVIL_DRAGON, duration + Functions.secondsToTicks(5), amplifier));
				}else{
					attacker.addEffect(new EffectInstance2(DragonEffects.EVIL_DRAGON, Functions.secondsToTicks(5)));
				}
			}
		}
	}

	@SubscribeEvent
	public static void spawnHunters(TickEvent.PlayerTickEvent playerTickEvent){
		if(!dragonHunters.isEmpty() && playerTickEvent.phase == TickEvent.Phase.END){
			Player player = playerTickEvent.player;
			if(DragonUtils.isDragon(player) && player.hasEffect(DragonEffects.EVIL_DRAGON) && !player.level.isClientSide && !player.isCreative() && !player.isSpectator() && player.isAlive()){
				ServerLevel serverLevel = (ServerLevel)player.level;
				if(serverWorld.dimension() == Level.OVERWORLD){
					VillageRelationshipsProvider.getVillageRelationships(player).ifPresent(villageRelationShips -> {
						if(villageRelationShips.hunterSpawnDelay == 0){
							BlockPos spawnPosition = Functions.findRandomSpawnPosition(player, 1, 4, 14.0F);
							if(spawnPosition != null && spawnPosition.getY() >= ConfigHandler.COMMON.riderSpawnLowerBound.get() && spawnPosition.getY() <= ConfigHandler.COMMON.riderSpawnUpperBound.get()){
								Optional<ResourceKey<Biome>> biomeResourceKey = serverWorld.getBiomeName(spawnPosition);
								if(biomeResourceKey.isPresent()){
									ResourceKey<Biome> biome = biomeResourceKey.get();
									if(BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)){
										return;
									}
								}
								int levelOfEvil = computeLevelOfEvil(player);
								for(int i = 0; i < levelOfEvil; i++){
									Functions.spawn(Objects.requireNonNull((dragonHunters.get(serverWorld.random.nextInt(dragonHunters.size()))).create(serverWorld)), spawnPosition, serverWorld);
								}
								if(serverWorld.isCloseToVillage(player.blockPosition(), 3)){
									villageRelationShips.hunterSpawnDelay = Functions.minutesToTicks(ConfigHandler.COMMON.hunterSpawnDelay.get() / 3) + Functions.minutesToTicks(serverWorld.random.nextInt(ConfigHandler.COMMON.hunterSpawnDelay.get() / 6));
								}else{
									villageRelationShips.hunterSpawnDelay = Functions.minutesToTicks(ConfigHandler.COMMON.hunterSpawnDelay.get()) + Functions.minutesToTicks(serverWorld.random.nextInt(ConfigHandler.COMMON.hunterSpawnDelay.get() / 3));
								}
							}
						}else{
							villageRelationShips.hunterSpawnDelay--;
						}
					});
				}
			}
		}
	}

	public static int computeLevelOfEvil(Player player){
		if(DragonUtils.isDragon(player) && player.hasEffect(DragonEffects.EVIL_DRAGON)){
			MobEffectInstance effectInstance = player.getEffect(DragonEffects.EVIL_DRAGON);
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
		if(ConfigHandler.COMMON.spawnPrinceAndPrincess.get()){
			Level world = serverTickEvent.world;
			if(world instanceof ServerLevel){
				ServerLevel serverLevel = (ServerLevel)world;
				if(!serverWorld.players().isEmpty() && serverWorld.dimension() == Level.OVERWORLD){
					if(timeLeft == 0){
						ServerPlayer player = serverWorld.getRandomPlayer();
						if(player != null && player.isAlive() && !player.isCreative() && !player.isSpectator()){
							BlockPos blockPos = Functions.findRandomSpawnPosition(player, 1, 2, 20.0F);
							if(blockPos != null && blockPos.getY() >= ConfigHandler.COMMON.riderSpawnLowerBound.get() && blockPos.getY() <= ConfigHandler.COMMON.riderSpawnUpperBound.get()){
								Optional<ResourceKey<Biome>> biomeResourceKey = serverWorld.getBiomeName(blockPos);
								if(biomeResourceKey.isPresent()){
									ResourceKey<Biome> biome = biomeResourceKey.get();
									if(BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)){
										return;
									}
								}
								EntityType<? extends PrincesHorse> entityType = world.random.nextBoolean() ? DSEntities.PRINCESS_ON_HORSE : DSEntities.PRINCE_ON_HORSE;
								PrincesHorse princess = entityType.create(world);
								princess.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
								princess.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(player.blockPosition()), MobSpawnType.NATURAL, null, null);

								serverWorld.addFreshEntity(princess);

								ListTag pattern = (new BannerPattern.Builder()).addPattern(BannerPattern.values()[world.random.nextInt((BannerPattern.values()).length)], DyeColor.values()[world.random.nextInt((DyeColor.values()).length)]).toListTag();

								int knights = world.random.nextInt(3) + 3;
								for(int i = 0; i < knights; i++){
									Knight knightHunter = DSEntities.KNIGHT.create(serverWorld);
									knightHunter.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
									knightHunter.goalSelector.addGoal(5, new FollowMobGoal(PrincesHorse.class, knightHunter, 8));
									knightHunter.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(player.blockPosition()), MobSpawnType.NATURAL, null, null);
									ItemStack itemStack = new ItemStack(Items.SHIELD);
									CompoundTag compoundNBT = new CompoundTag();
									compoundNBT.putInt("Base", princess.getColor());
									compoundNBT.put("Patterns", pattern);
									itemStack.addTagElement("BlockEntityTag", compoundNBT);
									knightHunter.setItemInHand(Hand.OFF_HAND, itemStack);
									serverWorld.addFreshEntity(knightHunter);
								}

								timeLeft = Functions.minutesToTicks(ConfigHandler.COMMON.princessSpawnDelay.get()) + Functions.minutesToTicks(world.random.nextInt(ConfigHandler.COMMON.princessSpawnDelay.get() / 2));
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
			Player player = playerTickEvent.player;
			if(!player.level.isClientSide){
				if(player.hasEffect(DragonEffects.EVIL_DRAGON)){
					VillageRelationshipsProvider.getVillageRelationships(player).ifPresent(villageRelationShips -> villageRelationShips.evilStatusDuration = player.getEffect(DragonEffects.EVIL_DRAGON).getDuration());
				}
			}
		}
	}
}