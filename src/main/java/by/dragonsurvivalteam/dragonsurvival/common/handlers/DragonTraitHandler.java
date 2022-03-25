package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.DamageSources;
import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.objects.DragonDebuffData;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Passives.AthleticsAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Passives.ContrastShowerAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Passives.LightInDarknessAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Passives.WaterAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.entity.player.SyncCapabilityDebuff;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.MobEffectInstance;
import net.minecraft.potion.MobEffects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.living.LivingEntityDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import static by.dragonsurvivalteam.dragonsurvival.misc.DragonType.*;

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
				Biome biome = world.getBiome(player.blockPosition());


				//Because it is used for both cave and sea dragon it is added here
				boolean isInCauldron = false;
				if(blockUnder.getBlock() == Blocks.CAULDRON){
					if(blockUnder.hasProperty(CauldronBlock.LEVEL)){
						int level = blockUnder.getValue(CauldronBlock.LEVEL);

						if(level > 0){
							isInCauldron = true;
						}
					}
				}else if(feetBlock.getBlock() == Blocks.CAULDRON){
					if(feetBlock.hasProperty(CauldronBlock.LEVEL)){
						int level = feetBlock.getValue(CauldronBlock.LEVEL);

						if(level > 0){
							isInCauldron = true;
						}
					}
				}

				boolean isInSeaBlock = DragonConfigHandler.SEA_DRAGON_HYDRATION_BLOCKS != null && (DragonConfigHandler.SEA_DRAGON_HYDRATION_BLOCKS.contains(block) || DragonConfigHandler.SEA_DRAGON_HYDRATION_BLOCKS.contains(feetBlock.getBlock()) || isInCauldron);

				if(!world.isClientSide && ConfigHandler.SERVER.bonuses.get() && ConfigHandler.SERVER.speedupEffectLevel.get() > 0 && DragonConfigHandler.DRAGON_SPEEDUP_BLOCKS != null && DragonConfigHandler.DRAGON_SPEEDUP_BLOCKS.get(dragonStateHandler.getType()).contains(block)){
					int duration = dragonStateHandler.getType() == SEA && dragonStateHandler.getMagic().getAbility(DragonAbilities.SEA_ATHLETICS) != null
						? ((AthleticsAbility)dragonStateHandler.getMagic().getAbility(DragonAbilities.SEA_ATHLETICS)).getDuration()
						: dragonStateHandler.getType() == FOREST && dragonStateHandler.getMagic().getAbility(DragonAbilities.FOREST_ATHLETICS) != null ? ((AthleticsAbility)dragonStateHandler.getMagic().getAbility(DragonAbilities.FOREST_ATHLETICS)).getDuration() : dragonStateHandler.getType() == CAVE && dragonStateHandler.getMagic().getAbility(DragonAbilities.CAVE_ATHLETICS) != null ? ((AthleticsAbility)dragonStateHandler.getMagic().getAbility(DragonAbilities.CAVE_ATHLETICS)).getDuration() : 0;

					int level = dragonStateHandler.getType() == SEA ? dragonStateHandler.getMagic().getAbilityLevel(DragonAbilities.SEA_ATHLETICS) : dragonStateHandler.getType() == FOREST ? dragonStateHandler.getMagic().getAbilityLevel(DragonAbilities.FOREST_ATHLETICS) : dragonStateHandler.getType() == CAVE ? dragonStateHandler.getMagic().getAbilityLevel(DragonAbilities.CAVE_ATHLETICS) : 0;

					int maxLevel = dragonStateHandler.getType() == SEA && dragonStateHandler.getMagic().getAbility(DragonAbilities.SEA_ATHLETICS) != null
						? dragonStateHandler.getMagic().getAbility(DragonAbilities.SEA_ATHLETICS).getMaxLevel()
						: dragonStateHandler.getType() == FOREST && dragonStateHandler.getMagic().getAbility(DragonAbilities.FOREST_ATHLETICS) != null ? dragonStateHandler.getMagic().getAbility(DragonAbilities.FOREST_ATHLETICS).getMaxLevel() : dragonStateHandler.getType() == CAVE && dragonStateHandler.getMagic().getAbility(DragonAbilities.CAVE_ATHLETICS) != null ? dragonStateHandler.getMagic().getAbility(DragonAbilities.CAVE_ATHLETICS).getMaxLevel() : 0;
					if(duration > 0){
						player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Functions.secondsToTicks(duration), (ConfigHandler.SERVER.speedupEffectLevel.get() - 1) + (level == maxLevel ? 1 : 0), false, false));
					}
				}


				switch(dragonStateHandler.getType()){
					case CAVE:
						DragonAbility contrastShower = dragonStateHandler.getMagic().getAbility(DragonAbilities.CONTRAST_SHOWER);
						int maxRainTime = 0;

						if(contrastShower != null){
							maxRainTime += Functions.secondsToTicks(((ContrastShowerAbility)contrastShower).getDuration());
						}

						double oldRainTime = dragonStateHandler.getDebuffData().timeInRain;

						if(ConfigHandler.SERVER.penalties.get() && !player.hasEffect(DragonEffects.FIRE) && !player.isCreative() && !player.isSpectator() && ((player.isInWaterOrBubble() && ConfigHandler.SERVER.caveWaterDamage.get() != 0.0) || (player.isInWaterOrRain() && !player.isInWater() && ConfigHandler.SERVER.caveRainDamage.get() != 0.0) || isInSeaBlock && ConfigHandler.SERVER.caveRainDamage.get() != 0.0)){

							if(player.isInWaterOrBubble() && player.tickCount % 10 == 0 && ConfigHandler.SERVER.caveWaterDamage.get() != 0.0){
								player.hurt(DamageSources.WATER_BURN, ConfigHandler.SERVER.caveWaterDamage.get().floatValue());
							}else if(((player.isInWaterOrRain() && !player.isInWaterOrBubble()) || isInSeaBlock) && ConfigHandler.SERVER.caveRainDamage.get() != 0.0){


								if(dragonStateHandler.getDebuffData().timeInRain >= maxRainTime){
									if(player.tickCount % 40 == 0){
										player.hurt(DamageSources.WATER_BURN, ConfigHandler.SERVER.caveRainDamage.get().floatValue());
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

						if(player.isOnFire() && ConfigHandler.SERVER.bonuses.get() && ConfigHandler.SERVER.caveFireImmunity.get()){
							player.clearFire();
						}

						if(player.isEyeInFluid(FluidTags.LAVA) && ConfigHandler.SERVER.bonuses.get() && ConfigHandler.SERVER.caveLavaSwimming.get() && ConfigHandler.SERVER.caveLavaSwimmingTicks.get() != 0){
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
						}else if(dragonStateHandler.getLavaAirSupply() < ConfigHandler.SERVER.caveLavaSwimmingTicks.get() && !player.isEyeInFluid(FluidTags.WATER)){
							dragonStateHandler.setLavaAirSupply(Math.min(dragonStateHandler.getLavaAirSupply() + (int)Math.ceil(ConfigHandler.SERVER.caveLavaSwimmingTicks.get() * 0.0133333F), ConfigHandler.SERVER.caveLavaSwimmingTicks.get()));
						}
						break;
					case FOREST:
						int maxStressTicks = ConfigHandler.SERVER.forestStressTicks.get();
						DragonAbility lightInDarkness = dragonStateHandler.getMagic().getAbility(DragonAbilities.LIGHT_IN_DARKNESS);

						if(lightInDarkness != null){
							maxStressTicks += Functions.secondsToTicks(((LightInDarknessAbility)lightInDarkness).getDuration());
						}

						if(ConfigHandler.SERVER.penalties.get() && !player.hasEffect(DragonEffects.MAGIC) && ConfigHandler.SERVER.forestStressTicks.get() > 0 && !player.isCreative() && !player.isSpectator()){
							double oldDarknessTime = dragonStateHandler.getDebuffData().timeInDarkness;

							if(!player.level.isClientSide){
								WorldLightManager lightManager = world.getChunkSource().getLightEngine();
								if((lightManager.getLayerListener(LightType.BLOCK).getLightValue(player.blockPosition()) < 3 && (lightManager.getLayerListener(LightType.SKY).getLightValue(player.blockPosition()) < 3 && lightManager.getLayerListener(LightType.SKY).getLightValue(player.blockPosition().above()) < 3))){
									if(dragonStateHandler.getDebuffData().timeInDarkness < maxStressTicks){
										dragonStateHandler.getDebuffData().timeInDarkness++;
									}

									if(dragonStateHandler.getDebuffData().timeInDarkness >= maxStressTicks && player.tickCount % 21 == 0){
										player.addEffect(new MobEffectInstance(DragonEffects.STRESS, ConfigHandler.SERVER.forestStressEffectDuration.get() * 20));
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

						break;
					case SEA:
						int maxTicksOutofWater = ConfigHandler.SERVER.seaTicksWithoutWater.get();
						DragonAbility waterAbility = dragonStateHandler.getMagic().getAbility(DragonAbilities.WATER);

						if(waterAbility != null){
							maxTicksOutofWater += Functions.secondsToTicks(((WaterAbility)waterAbility).getDuration());
						}

						if((player.hasEffect(DragonEffects.PEACE) || player.isEyeInFluid(FluidTags.WATER)) && player.getAirSupply() < player.getMaxAirSupply()){
							player.setAirSupply(player.getMaxAirSupply());
						}

						if(ConfigHandler.SERVER.penalties.get() && !player.hasEffect(DragonEffects.PEACE) && maxTicksOutofWater > 0 && !player.isCreative() && !player.isSpectator()){
							DragonDebuffData debuffData = dragonStateHandler.getDebuffData();
							double oldWaterTime = debuffData.timeWithoutWater;

							if(!player.level.isClientSide){
								if(!player.isInWaterRainOrBubble() && !isInSeaBlock){
									boolean hotBiome = biome.getPrecipitation() == RainType.NONE && biome.getBaseTemperature() > 1.0;
									double timeIncrement = (world.isNight() ? 0.5F : 1.0) * (hotBiome ? biome.getBaseTemperature() : 1F);
									debuffData.timeWithoutWater += ConfigHandler.SERVER.seaTicksBasedOnTemperature.get() ? timeIncrement : 1;
								}

								if(player.isInWaterRainOrBubble() || isInSeaBlock){
									debuffData.timeWithoutWater = (Math.max(debuffData.timeWithoutWater - (int)Math.ceil(maxTicksOutofWater * 0.005F), 0));
								}

								debuffData.timeWithoutWater = Math.min(debuffData.timeWithoutWater, maxTicksOutofWater * 2);

								if(oldWaterTime != debuffData.timeWithoutWater){
									NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncCapabilityDebuff(player.getId(), debuffData.timeWithoutWater, debuffData.timeInDarkness, dragonStateHandler.getDebuffData().timeInRain));
								}

								float hydrationDamage = ConfigHandler.SERVER.seaDehydrationDamage.get().floatValue();

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
						break;
				}

				int maxTicksOutofWater = ConfigHandler.SERVER.seaTicksWithoutWater.get();
				DragonAbility waterAbility = dragonStateHandler.getMagic().getAbility(DragonAbilities.WATER);

				if(waterAbility != null){
					maxTicksOutofWater += Functions.secondsToTicks(((WaterAbility)waterAbility).getDuration());
				}

				// Dragon Particles
				// TODO: Randomize along dragon body
				if(world.isClientSide && !player.isCreative() && !player.isSpectator()){
					if(dragonStateHandler.getType() == SEA && !player.hasEffect(DragonEffects.PEACE) && dragonStateHandler.getDebuffData().timeWithoutWater >= maxTicksOutofWater){
						world.addParticle(ParticleTypes.WHITE_ASH, player.getX() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1), player.getY() + 0.5F, player.getZ() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1), 0, 0, 0);
					}
					if(dragonStateHandler.getType() == FOREST && !player.hasEffect(DragonEffects.MAGIC) && dragonStateHandler.getDebuffData().timeInDarkness == ConfigHandler.SERVER.forestStressTicks.get()){
						world.addParticle(ParticleTypes.SMOKE, player.getX() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1), player.getY() + 0.5F, player.getZ() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1), 0, 0, 0);
					}
				}
			}
		});
	}

	@SubscribeEvent
	public static void onDeath(LivingEntityDeathEvent event){
		if(event.get() instanceof Player){
			DragonStateHandler handler = DragonUtils.getHandler(event.get());
			handler.getDebuffData().onDeath();
		}
	}
}