package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.registry.DamageSources;
import by.dragonsurvivalteam.dragonsurvival.registry.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.objects.DragonDebuffData;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive.CaveAthleticsAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive.ContrastShowerAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive.ForestAthleticsAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive.LightInDarknessAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.passive.SeaAthleticsAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.passive.WaterAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.AthleticsAbility;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncCapabilityDebuff;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import static by.dragonsurvivalteam.dragonsurvival.util.DragonType.*;

@Mod.EventBusSubscriber
public class DragonTraitHandler{
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent playerTickEvent){
		if(playerTickEvent.phase != Phase.END){
			return;
		}
		Player player = playerTickEvent.player;
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				Level world = player.level;
				BlockState feetBlock = player.getFeetBlockState();
				BlockState blockUnder = world.getBlockState(player.blockPosition().below());
				Block block = blockUnder.getBlock();
				Biome biome = world.getBiome(player.blockPosition()).value();


				//Because it is used for both cave and sea dragon it is added here
				boolean isInCauldron = false;
				if(blockUnder.getBlock() instanceof LayeredCauldronBlock){
					if(blockUnder.hasProperty(LayeredCauldronBlock.LEVEL)){
						int level = blockUnder.getValue(LayeredCauldronBlock.LEVEL);

						if(level > 0){
							isInCauldron = true;
						}
					}
				}else if(feetBlock.getBlock() instanceof LayeredCauldronBlock){
					if(feetBlock.hasProperty(LayeredCauldronBlock.LEVEL)){
						int level = feetBlock.getValue(LayeredCauldronBlock.LEVEL);

						if(level > 0){
							isInCauldron = true;
						}
					}
				}

				boolean isInSeaBlock = DragonConfigHandler.SEA_DRAGON_HYDRATION_BLOCKS != null && (DragonConfigHandler.SEA_DRAGON_HYDRATION_BLOCKS.contains(block) || DragonConfigHandler.SEA_DRAGON_HYDRATION_BLOCKS.contains(feetBlock.getBlock()) || isInCauldron);

				if(!world.isClientSide && ServerConfig.bonuses && ServerConfig.speedupEffectLevel > 0 && DragonConfigHandler.DRAGON_SPEEDUP_BLOCKS != null && DragonConfigHandler.DRAGON_SPEEDUP_BLOCKS.get(dragonStateHandler.getType()).contains(block)){
					SeaAthleticsAbility SEA_ATHLETICS = DragonAbilities.getAbility(player, SeaAthleticsAbility.class);
					ForestAthleticsAbility FOREST_ATHLETICS = DragonAbilities.getAbility(player, ForestAthleticsAbility.class);
					CaveAthleticsAbility CAVE_ATHLETICS = DragonAbilities.getAbility(player, CaveAthleticsAbility.class);

					int duration = dragonStateHandler.getType() == SEA && SEA_ATHLETICS != null
						? SEA_ATHLETICS.getDuration()
						: dragonStateHandler.getType() == FOREST && FOREST_ATHLETICS != null ? ((AthleticsAbility)FOREST_ATHLETICS).getDuration() : dragonStateHandler.getType() == CAVE && CAVE_ATHLETICS != null ? ((AthleticsAbility)CAVE_ATHLETICS).getDuration() : 0;

					int level = dragonStateHandler.getType() == SEA ? SEA_ATHLETICS.getLevel() : dragonStateHandler.getType() == FOREST ? FOREST_ATHLETICS.getLevel() : dragonStateHandler.getType() == CAVE ? CAVE_ATHLETICS.getLevel() : 0;

					int maxLevel = dragonStateHandler.getType() == SEA && SEA_ATHLETICS != null
						? SEA_ATHLETICS.getMaxLevel()
						: dragonStateHandler.getType() == FOREST && FOREST_ATHLETICS != null ? FOREST_ATHLETICS.getMaxLevel() : dragonStateHandler.getType() == CAVE && CAVE_ATHLETICS != null ? CAVE_ATHLETICS.getMaxLevel() : 0;
					if(duration > 0){
						player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Functions.secondsToTicks(duration), ServerConfig.speedupEffectLevel - 1 + (level == maxLevel ? 1 : 0), false, false));
					}
				}


				if(CAVE.equals(dragonStateHandler.getType())){
					ContrastShowerAbility contrastShower = DragonAbilities.getAbility(player, ContrastShowerAbility.class);
					int maxRainTime = 0;
					if(contrastShower != null){
						maxRainTime += Functions.secondsToTicks(contrastShower.getDuration());
					}
					double oldRainTime = dragonStateHandler.getDebuffData().timeInRain;
					if(ServerConfig.penalties && !player.hasEffect(DragonEffects.FIRE) && !player.isCreative() && !player.isSpectator() && ((player.isInWaterOrBubble() && ServerConfig.caveWaterDamage != 0.0) || (player.isInWaterOrRain() && !player.isInWater() && ServerConfig.caveRainDamage != 0.0) || isInSeaBlock && ServerConfig.caveRainDamage != 0.0)){

						if(player.isInWaterOrBubble() && player.tickCount % 10 == 0 && ServerConfig.caveWaterDamage != 0.0){
							player.hurt(DamageSources.WATER_BURN, ServerConfig.caveWaterDamage.floatValue());
						}else if((player.isInWaterOrRain() && !player.isInWaterOrBubble() || isInSeaBlock) && ServerConfig.caveRainDamage != 0.0){

							if(dragonStateHandler.getDebuffData().timeInRain >= maxRainTime){
								if(player.tickCount % 40 == 0){
									player.hurt(DamageSources.WATER_BURN, ServerConfig.caveRainDamage.floatValue());
								}
							}else{
								if(!player.level.isClientSide){
									dragonStateHandler.getDebuffData().timeInRain++;
								}
							}
						}


						if(player.tickCount % 40 == 0){
							player.playSound(SoundEvents.LAVA_EXTINGUISH, 1.0F, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F);
						}

						if(player.tickCount % 10 == 0){
							if(world.isClientSide){
								world.addParticle(ParticleTypes.POOF, player.getX() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1), player.getY() + 0.5F, player.getZ() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1), 0, 0, 0);
							}
						}
					}else if(dragonStateHandler.getDebuffData().timeInRain > 0){
						if(!player.level.isClientSide){
							if(maxRainTime > 0){
								dragonStateHandler.getDebuffData().timeInRain = (Math.max(dragonStateHandler.getDebuffData().timeInRain - (int)Math.ceil(maxRainTime * 0.02F), 0));
							}else{
								dragonStateHandler.getDebuffData().timeInRain--;
							}
						}
					}
					if(dragonStateHandler.getDebuffData().timeInRain != oldRainTime){
						if(!player.level.isClientSide){
							NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncCapabilityDebuff(player.getId(), dragonStateHandler.getDebuffData().timeWithoutWater, dragonStateHandler.getDebuffData().timeInDarkness, dragonStateHandler.getDebuffData().timeInRain));
						}
					}
					if(player.isOnFire() && ServerConfig.bonuses && ServerConfig.caveFireImmunity){
						player.clearFire();
					}
					if(player.isEyeInFluid(FluidTags.LAVA) && ServerConfig.bonuses && ServerConfig.caveLavaSwimming && ServerConfig.caveLavaSwimmingTicks != 0){
						if(!player.canBreatheUnderwater() && !player.getAbilities().invulnerable){
							dragonStateHandler.setLavaAirSupply(dragonStateHandler.getLavaAirSupply() - 1);
							if(dragonStateHandler.getLavaAirSupply() == -20){
								dragonStateHandler.setLavaAirSupply(0);
								if(!player.level.isClientSide){
									player.hurt(DamageSource.DROWN, 2F); //LAVA_YES
								}
							}
						}
						if(!player.level.isClientSide && player.isPassenger() && player.getVehicle() != null && !player.getVehicle().canBeRiddenInWater(player)){
							player.stopRiding();
						}
					}else if(dragonStateHandler.getLavaAirSupply() < ServerConfig.caveLavaSwimmingTicks && !player.isEyeInFluid(FluidTags.WATER)){
						dragonStateHandler.setLavaAirSupply(Math.min(dragonStateHandler.getLavaAirSupply() + (int)Math.ceil(ServerConfig.caveLavaSwimmingTicks * 0.0133333F), ServerConfig.caveLavaSwimmingTicks));
					}
				}else if(FOREST.equals(dragonStateHandler.getType())){
					int maxStressTicks = ServerConfig.forestStressTicks;
					LightInDarknessAbility lightInDarkness = DragonAbilities.getAbility(player, LightInDarknessAbility.class);
					if(lightInDarkness != null){
						maxStressTicks += Functions.secondsToTicks(lightInDarkness.getDuration());
					}
					if(ServerConfig.penalties && !player.hasEffect(DragonEffects.MAGIC) && ServerConfig.forestStressTicks > 0 && !player.isCreative() && !player.isSpectator()){
						double oldDarknessTime = dragonStateHandler.getDebuffData().timeInDarkness;

						if(!player.level.isClientSide){
							LevelLightEngine lightManager = world.getChunkSource().getLightEngine();
							if((lightManager.getLayerListener(LightLayer.BLOCK).getLightValue(player.blockPosition()) < 3 && (lightManager.getLayerListener(LightLayer.SKY).getLightValue(player.blockPosition()) < 3 && lightManager.getLayerListener(LightLayer.SKY).getLightValue(player.blockPosition().above()) < 3))){
								if(dragonStateHandler.getDebuffData().timeInDarkness < maxStressTicks){
									dragonStateHandler.getDebuffData().timeInDarkness++;
								}

								if(dragonStateHandler.getDebuffData().timeInDarkness >= maxStressTicks && player.tickCount % 21 == 0){
									player.addEffect(new MobEffectInstance(DragonEffects.STRESS, ServerConfig.forestStressEffectDuration * 20));
								}
							}else{
								dragonStateHandler.getDebuffData().timeInDarkness = (Math.max(dragonStateHandler.getDebuffData().timeInDarkness - (int)Math.ceil(maxStressTicks * 0.02F), 0));
							}

							dragonStateHandler.getDebuffData().timeInDarkness = Math.min(dragonStateHandler.getDebuffData().timeInDarkness, maxStressTicks);

							if(dragonStateHandler.getDebuffData().timeInDarkness != oldDarknessTime){
								NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncCapabilityDebuff(player.getId(), dragonStateHandler.getDebuffData().timeWithoutWater, dragonStateHandler.getDebuffData().timeInDarkness, dragonStateHandler.getDebuffData().timeInRain));
							}
						}
					}
				}else if(SEA.equals(dragonStateHandler.getType())){
					int maxTicksOutofWater = ServerConfig.seaTicksWithoutWater;
					WaterAbility waterAbility = DragonAbilities.getAbility(player, WaterAbility.class);

					if(waterAbility != null){
						maxTicksOutofWater += Functions.secondsToTicks(waterAbility.getDuration());
					}

					if((player.hasEffect(DragonEffects.PEACE) || player.isEyeInFluid(FluidTags.WATER)) && player.getAirSupply() < player.getMaxAirSupply()){
						player.setAirSupply(player.getMaxAirSupply());
					}
					if(ServerConfig.penalties && !player.hasEffect(DragonEffects.PEACE) && maxTicksOutofWater > 0 && !player.isCreative() && !player.isSpectator()){
						DragonDebuffData debuffData = dragonStateHandler.getDebuffData();
						double oldWaterTime = debuffData.timeWithoutWater;

						if(!player.level.isClientSide){
							if(!player.isInWaterRainOrBubble() && !isInSeaBlock){
								boolean hotBiome = biome.getPrecipitation() == Precipitation.NONE && biome.getBaseTemperature() > 1.0;
								double timeIncrement = (world.isNight() ? 0.5F : 1.0) * (hotBiome ? biome.getBaseTemperature() : 1F);
								debuffData.timeWithoutWater += ServerConfig.seaTicksBasedOnTemperature ? timeIncrement : 1;
							}

							if(player.isInWaterRainOrBubble() || isInSeaBlock){
								debuffData.timeWithoutWater = (Math.max(debuffData.timeWithoutWater - (int)Math.ceil(maxTicksOutofWater * 0.005F), 0));
							}

							debuffData.timeWithoutWater = Math.min(debuffData.timeWithoutWater, maxTicksOutofWater * 2);

							if(oldWaterTime != debuffData.timeWithoutWater){
								NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncCapabilityDebuff(player.getId(), debuffData.timeWithoutWater, debuffData.timeInDarkness, dragonStateHandler.getDebuffData().timeInRain));
							}

							float hydrationDamage = ServerConfig.seaDehydrationDamage.floatValue();

							if(debuffData.timeWithoutWater > maxTicksOutofWater && debuffData.timeWithoutWater < maxTicksOutofWater * 2){
								if(player.tickCount % 40 == 0){
									player.hurt(DamageSources.DEHYDRATION, hydrationDamage);
								}
							}else if(debuffData.timeWithoutWater >= maxTicksOutofWater * 2){
								if(player.tickCount % 20 == 0){
									player.hurt(DamageSources.DEHYDRATION, hydrationDamage);
								}
							}
						}
					}else if(player.hasEffect(DragonEffects.PEACE)){
						if(!player.level.isClientSide){
							DragonDebuffData debuffData = dragonStateHandler.getDebuffData();
							if(debuffData.timeWithoutWater > 0){
								debuffData.timeWithoutWater = 0;
								NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncCapabilityDebuff(player.getId(), debuffData.timeWithoutWater, debuffData.timeInDarkness, dragonStateHandler.getDebuffData().timeInRain));
							}
						}
					}
				}

				int maxTicksOutofWater = ServerConfig.seaTicksWithoutWater;
				WaterAbility waterAbility = DragonAbilities.getAbility(player, WaterAbility.class);

				if(waterAbility != null){
					maxTicksOutofWater += Functions.secondsToTicks(waterAbility.getDuration());
				}

				// Dragon Particles
				// TODO: Randomize along dragon body
				if(world.isClientSide && !player.isCreative() && !player.isSpectator()){
					if(dragonStateHandler.getType() == SEA && !player.hasEffect(DragonEffects.PEACE) && dragonStateHandler.getDebuffData().timeWithoutWater >= maxTicksOutofWater){
						world.addParticle(ParticleTypes.WHITE_ASH, player.getX() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1), player.getY() + 0.5F, player.getZ() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1), 0, 0, 0);
					}
					if(dragonStateHandler.getType() == FOREST && !player.hasEffect(DragonEffects.MAGIC) && dragonStateHandler.getDebuffData().timeInDarkness == ServerConfig.forestStressTicks){
						world.addParticle(ParticleTypes.SMOKE, player.getX() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1), player.getY() + 0.5F, player.getZ() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1), 0, 0, 0);
					}
				}
			}
		});
	}

	@SubscribeEvent
	public static void onDeath(LivingDeathEvent event){
		if(event.getEntity() instanceof Player){
			DragonStateHandler handler = DragonUtils.getHandler(event.getEntity());
			handler.getDebuffData().onDeath();
		}
	}
}