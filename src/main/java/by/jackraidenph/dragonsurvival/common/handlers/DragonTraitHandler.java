package by.jackraidenph.dragonsurvival.common.handlers;

import by.jackraidenph.dragonsurvival.common.DamageSources;
import by.jackraidenph.dragonsurvival.common.DragonEffects;
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.magic.DragonAbilities;
import by.jackraidenph.dragonsurvival.common.magic.abilities.Passives.AthleticsAbility;
import by.jackraidenph.dragonsurvival.common.magic.abilities.Passives.ContrastShowerAbility;
import by.jackraidenph.dragonsurvival.common.magic.abilities.Passives.LightInDarknessAbility;
import by.jackraidenph.dragonsurvival.common.magic.abilities.Passives.WaterAbility;
import by.jackraidenph.dragonsurvival.common.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.entity.player.SyncCapabilityDebuff;
import by.jackraidenph.dragonsurvival.util.Functions;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;


@Mod.EventBusSubscriber
public class DragonTraitHandler {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent playerTickEvent) {
        if (playerTickEvent.phase != Phase.END)
            return;
        Player playerEntity = playerTickEvent.player;
        DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
            if (dragonStateHandler.isDragon()) {
                Level world = playerEntity.level;
                BlockState feetBlock = playerEntity.getFeetBlockState();
                BlockState blockUnder = world.getBlockState(playerEntity.blockPosition().below());
                Block block = blockUnder.getBlock();
                Biome biome = world.getBiome(playerEntity.blockPosition());
    
                
                //Because it is used for both cave and sea dragon it is added here
                boolean isInCauldron = false;
                if(blockUnder.getBlock() == Blocks.WATER_CAULDRON){
                    if(blockUnder.hasProperty(LayeredCauldronBlock.LEVEL)) {
                        int level = blockUnder.getValue(LayeredCauldronBlock.LEVEL);
            
                        if(level > 0){
                            isInCauldron = true;
                        }
                    }
                }else if(feetBlock.getBlock() == Blocks.WATER_CAULDRON){
                    if(feetBlock.hasProperty(LayeredCauldronBlock.LEVEL)) {
                        int level = feetBlock.getValue(LayeredCauldronBlock.LEVEL);
        
                        if(level > 0){
                            isInCauldron = true;
                        }
                    }
                }
                
                boolean isInSeaBlock = DragonConfigHandler.SEA_DRAGON_HYDRATION_BLOCKS != null &&
                                       (DragonConfigHandler.SEA_DRAGON_HYDRATION_BLOCKS.contains(block)
                                        || DragonConfigHandler.SEA_DRAGON_HYDRATION_BLOCKS.contains(feetBlock.getBlock()) || isInCauldron);
                
                if (!world.isClientSide && ConfigHandler.SERVER.bonuses.get() && ConfigHandler.SERVER.speedupEffectLevel.get() > 0 && DragonConfigHandler.DRAGON_SPEEDUP_BLOCKS != null && DragonConfigHandler.DRAGON_SPEEDUP_BLOCKS.get(dragonStateHandler.getType()).contains(block)) {
                    int duration =
                            dragonStateHandler.getType() == DragonType.SEA && dragonStateHandler.getMagic().getAbility(DragonAbilities.SEA_ATHLETICS) != null ?  ((AthleticsAbility)dragonStateHandler.getMagic().getAbility(DragonAbilities.SEA_ATHLETICS)).getDuration() :
                            dragonStateHandler.getType() == DragonType.FOREST && dragonStateHandler.getMagic().getAbility(DragonAbilities.FOREST_ATHLETICS) != null ? ((AthleticsAbility)dragonStateHandler.getMagic().getAbility(DragonAbilities.FOREST_ATHLETICS)).getDuration() :
                            dragonStateHandler.getType() == DragonType.CAVE && dragonStateHandler.getMagic().getAbility(DragonAbilities.CAVE_ATHLETICS) != null ? ((AthleticsAbility)dragonStateHandler.getMagic().getAbility(DragonAbilities.CAVE_ATHLETICS)).getDuration() :
                            0;
    
                    int level =
                        dragonStateHandler.getType() == DragonType.SEA ? dragonStateHandler.getMagic().getAbilityLevel(DragonAbilities.SEA_ATHLETICS) :
                        dragonStateHandler.getType() == DragonType.FOREST ? dragonStateHandler.getMagic().getAbilityLevel(DragonAbilities.FOREST_ATHLETICS) :
                        dragonStateHandler.getType() == DragonType.CAVE ? dragonStateHandler.getMagic().getAbilityLevel(DragonAbilities.CAVE_ATHLETICS) : 0;
    
                    int maxLevel =
                            dragonStateHandler.getType() == DragonType.SEA && dragonStateHandler.getMagic().getAbility(DragonAbilities.SEA_ATHLETICS) != null ?  ((AthleticsAbility)dragonStateHandler.getMagic().getAbility(DragonAbilities.SEA_ATHLETICS)).getMaxLevel() :
                                    dragonStateHandler.getType() == DragonType.FOREST && dragonStateHandler.getMagic().getAbility(DragonAbilities.FOREST_ATHLETICS) != null ? ((AthleticsAbility)dragonStateHandler.getMagic().getAbility(DragonAbilities.FOREST_ATHLETICS)).getMaxLevel() :
                                            dragonStateHandler.getType() == DragonType.CAVE && dragonStateHandler.getMagic().getAbility(DragonAbilities.CAVE_ATHLETICS) != null ? ((AthleticsAbility)dragonStateHandler.getMagic().getAbility(DragonAbilities.CAVE_ATHLETICS)).getMaxLevel() :
                                                    0;
                    if(duration > 0){
                        playerEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Functions.secondsToTicks(duration), (ConfigHandler.SERVER.speedupEffectLevel.get() - 1) + (level == maxLevel ? 1 : 0), false, false));
                    }
                }
                

                
                switch (dragonStateHandler.getType()) {
                    case CAVE:
                        DragonAbility contrastShower = dragonStateHandler.getMagic().getAbility(DragonAbilities.CONTRAST_SHOWER);
                        int maxRainTime = 0;
    
                        if(contrastShower != null){
                            maxRainTime +=  Functions.secondsToTicks(((ContrastShowerAbility)contrastShower).getDuration());
                        }
    
                        double oldRainTime = dragonStateHandler.getDebuffData().timeInRain;
    
                        if (ConfigHandler.SERVER.penalties.get() && !playerEntity.hasEffect(DragonEffects.FIRE) && !playerEntity.isCreative() && !playerEntity.isSpectator() && ((playerEntity.isInWaterOrBubble() && ConfigHandler.SERVER.caveWaterDamage.get() != 0.0)
                             || (playerEntity.isInWaterOrRain() && !playerEntity.isInWater() && ConfigHandler.SERVER.caveRainDamage.get() != 0.0)
                             || isInSeaBlock && ConfigHandler.SERVER.caveRainDamage.get() != 0.0)) {
                            
                            if (playerEntity.isInWaterOrBubble() && playerEntity.tickCount % 10 == 0 && ConfigHandler.SERVER.caveWaterDamage.get() != 0.0){
                                playerEntity.hurt(DamageSources.WATER_BURN, ConfigHandler.SERVER.caveWaterDamage.get().floatValue());
                                
                          }else if (((playerEntity.isInWaterOrRain() && !playerEntity.isInWaterOrBubble())
                                      || isInSeaBlock) && ConfigHandler.SERVER.caveRainDamage.get() != 0.0) {
    
                                
                                    if (dragonStateHandler.getDebuffData().timeInRain >= maxRainTime) {
                                        if (playerEntity.tickCount % 40 == 0) {
                                            playerEntity.hurt(DamageSources.WATER_BURN, ConfigHandler.SERVER.caveRainDamage.get().floatValue());
                                        }
                                    } else {
                                        if (!playerEntity.level.isClientSide) {
                                            dragonStateHandler.getDebuffData().timeInRain++;
                                        }
                                    }
                                }
                            
                          
                            if (playerEntity.tickCount % 40 == 0) {
                                playerEntity.playSound(SoundEvents.LAVA_EXTINGUISH, 1.0F, (playerEntity.getRandom().nextFloat() - playerEntity.getRandom().nextFloat()) * 0.2F + 1.0F);
                            }
                            
                            if(playerEntity.tickCount % 10 == 0){
                                if (world.isClientSide) {
                                    world.addParticle(ParticleTypes.POOF, playerEntity.getX() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1), playerEntity.getY() + 0.5F, playerEntity.getZ() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1), 0, 0, 0);
                                }
                            }
                            
                        }else if(dragonStateHandler.getDebuffData().timeInRain > 0){
                            if (!playerEntity.level.isClientSide) {
                                if (maxRainTime > 0) {
                                    dragonStateHandler.getDebuffData().timeInRain = (Math.max(dragonStateHandler.getDebuffData().timeInRain - (int)Math.ceil(maxRainTime * 0.02F), 0));
                                } else {
                                    dragonStateHandler.getDebuffData().timeInRain--;
                                }
                            }
                        }
    
                        if(dragonStateHandler.getDebuffData().timeInRain != oldRainTime) {
                            if (!playerEntity.level.isClientSide) {
                                NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> playerEntity), new SyncCapabilityDebuff(playerEntity.getId(), dragonStateHandler.getDebuffData().timeWithoutWater, dragonStateHandler.getDebuffData().timeInDarkness, dragonStateHandler.getDebuffData().timeInRain));
                            }
                        }
                        
                        if (playerEntity.isOnFire() && ConfigHandler.SERVER.bonuses.get() && ConfigHandler.SERVER.caveFireImmunity.get()) {
                            playerEntity.clearFire();
                        }
                        
                        if (playerEntity.isEyeInFluid(FluidTags.LAVA) && ConfigHandler.SERVER.bonuses.get() && ConfigHandler.SERVER.caveLavaSwimming.get() && ConfigHandler.SERVER.caveLavaSwimmingTicks.get() != 0) {
                            if (!playerEntity.canBreatheUnderwater() && !playerEntity.getAbilities().invulnerable) {
                                dragonStateHandler.setLavaAirSupply(dragonStateHandler.getLavaAirSupply() - 1);
                                if (dragonStateHandler.getLavaAirSupply() == -20) {
                                    dragonStateHandler.setLavaAirSupply(0);
                                    if (!playerEntity.level.isClientSide)
                                        playerEntity.hurt(DamageSource.DROWN, 2F); //LAVA_YES
                                }
                            }
                            if (!playerEntity.level.isClientSide && playerEntity.isPassenger() && playerEntity.getVehicle() != null && !playerEntity.getVehicle().canBeRiddenInWater(playerEntity)) {
                                playerEntity.stopRiding();
                            }
                        } else if (dragonStateHandler.getLavaAirSupply() < ConfigHandler.SERVER.caveLavaSwimmingTicks.get() && !playerEntity.isEyeInFluid(FluidTags.WATER))
                            dragonStateHandler.setLavaAirSupply(Math.min(dragonStateHandler.getLavaAirSupply() + (int) Math.ceil(ConfigHandler.SERVER.caveLavaSwimmingTicks.get() * 0.0133333F), ConfigHandler.SERVER.caveLavaSwimmingTicks.get()));
                        break;
                    case FOREST:
                        int maxStressTicks = ConfigHandler.SERVER.forestStressTicks.get();
                        DragonAbility lightInDarkness = dragonStateHandler.getMagic().getAbility(DragonAbilities.LIGHT_IN_DARKNESS);
    
                        if(lightInDarkness != null){
                            maxStressTicks +=  Functions.secondsToTicks(((LightInDarknessAbility)lightInDarkness).getDuration());
                        }
                        
                        if (ConfigHandler.SERVER.penalties.get() && !playerEntity.hasEffect(DragonEffects.MAGIC) && ConfigHandler.SERVER.forestStressTicks.get() > 0 && !playerEntity.isCreative() && !playerEntity.isSpectator()) {
                            double oldDarknessTime = dragonStateHandler.getDebuffData().timeInDarkness;
    
                            if(!playerEntity.level.isClientSide) {
                                LevelLightEngine lightManager = world.getChunkSource().getLightEngine();
                                if ((lightManager.getLayerListener(LightLayer.BLOCK).getLightValue(playerEntity.blockPosition()) < 3 && (lightManager.getLayerListener(LightLayer.SKY).getLightValue(playerEntity.blockPosition()) < 3 && lightManager.getLayerListener(LightLayer.SKY).getLightValue(
                                        playerEntity.blockPosition().above()) < 3))) {
                                    if (dragonStateHandler.getDebuffData().timeInDarkness < maxStressTicks) {
                                        dragonStateHandler.getDebuffData().timeInDarkness++;
                                    }
        
                                    if (dragonStateHandler.getDebuffData().timeInDarkness >= maxStressTicks && playerEntity.tickCount % 21 == 0) {
                                        playerEntity.addEffect(new MobEffectInstance(DragonEffects.STRESS, ConfigHandler.SERVER.forestStressEffectDuration.get() * 20));
                                    }
        
                                } else  {
                                    dragonStateHandler.getDebuffData().timeInDarkness = (Math.max(dragonStateHandler.getDebuffData().timeInDarkness - (int)Math.ceil(maxStressTicks * 0.02F), 0));
                                }
    
                                dragonStateHandler.getDebuffData().timeInDarkness = Math.min(dragonStateHandler.getDebuffData().timeInDarkness, maxStressTicks);
    
                                if (dragonStateHandler.getDebuffData().timeInDarkness != oldDarknessTime) {
                                    NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> playerEntity), new SyncCapabilityDebuff(playerEntity.getId(), dragonStateHandler.getDebuffData().timeWithoutWater, dragonStateHandler.getDebuffData().timeInDarkness, dragonStateHandler.getDebuffData().timeInRain));
                                }
                            }
                        }
                        
                        break;
                    case SEA:
                        int maxTicksOutofWater = ConfigHandler.SERVER.seaTicksWithoutWater.get();
                        DragonAbility waterAbility = dragonStateHandler.getMagic().getAbility(DragonAbilities.WATER);
                        
                        if(waterAbility != null){
                           maxTicksOutofWater +=  Functions.secondsToTicks(((WaterAbility)waterAbility).getDuration());
                        }
                        
                        if ((playerEntity.hasEffect(DragonEffects.PEACE) || playerEntity.isEyeInFluid(FluidTags.WATER)) && playerEntity.getAirSupply() < playerEntity.getMaxAirSupply())
                            playerEntity.setAirSupply(playerEntity.getMaxAirSupply());
                        
                        if (ConfigHandler.SERVER.penalties.get() && !playerEntity.hasEffect(DragonEffects.PEACE) && maxTicksOutofWater > 0 && !playerEntity.isCreative() && !playerEntity.isSpectator()) {
                            DragonStateHandler.DragonDebuffData debuffData = dragonStateHandler.getDebuffData();
                            double oldWaterTime = debuffData.timeWithoutWater;
                            
                            if(!playerEntity.level.isClientSide) {
                                if (!playerEntity.isInWaterRainOrBubble() && !isInSeaBlock) {
                                    boolean hotBiome = biome.getPrecipitation() == Biome.Precipitation.NONE && biome.getBaseTemperature() > 1.0;
                                    double timeIncrement = (world.isNight() ? 0.5F : 1.0) * (hotBiome ? biome.getBaseTemperature() : 1F);
                                    debuffData.timeWithoutWater += ConfigHandler.SERVER.seaTicksBasedOnTemperature.get() ? timeIncrement : 1;
                                }
    
                                if (playerEntity.isInWaterRainOrBubble() || isInSeaBlock) {
                                    debuffData.timeWithoutWater = (Math.max(debuffData.timeWithoutWater - (int)Math.ceil(maxTicksOutofWater * 0.005F), 0));
                                }
    
                                debuffData.timeWithoutWater = Math.min(debuffData.timeWithoutWater, maxTicksOutofWater * 2);
    
                                if (oldWaterTime != debuffData.timeWithoutWater) {
                                    NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> playerEntity), new SyncCapabilityDebuff(playerEntity.getId(), debuffData.timeWithoutWater, debuffData.timeInDarkness, dragonStateHandler.getDebuffData().timeInRain));
                                }
    
                                float hydrationDamage = ConfigHandler.SERVER.seaDehydrationDamage.get().floatValue();
    
                                if (debuffData.timeWithoutWater > maxTicksOutofWater && debuffData.timeWithoutWater < maxTicksOutofWater * 2) {
                                    if (playerEntity.tickCount % 40 == 0) {
                                        playerEntity.hurt(DamageSources.DEHYDRATION, hydrationDamage);
                                    }
                                } else if (debuffData.timeWithoutWater >= maxTicksOutofWater * 2) {
                                    if (playerEntity.tickCount % 20 == 0) {
                                        playerEntity.hurt(DamageSources.DEHYDRATION, hydrationDamage);
                                    }
                                }
                            }
                        } else if (playerEntity.hasEffect(DragonEffects.PEACE)) {
                            if(!playerEntity.level.isClientSide) {
                                DragonStateHandler.DragonDebuffData debuffData = dragonStateHandler.getDebuffData();
                                if (debuffData.timeWithoutWater > 0) {
                                    debuffData.timeWithoutWater = 0;
                                    NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> playerEntity), new SyncCapabilityDebuff(playerEntity.getId(), debuffData.timeWithoutWater, debuffData.timeInDarkness, dragonStateHandler.getDebuffData().timeInRain));
                                }
                            }
                        }
                        break;
                }
    
                int maxTicksOutofWater = ConfigHandler.SERVER.seaTicksWithoutWater.get();
                DragonAbility waterAbility = dragonStateHandler.getMagic().getAbility(DragonAbilities.WATER);
    
                if(waterAbility != null){
                    maxTicksOutofWater +=  Functions.secondsToTicks(((WaterAbility)waterAbility).getDuration());
                }
                
                // Dragon Particles
                // TODO: Randomize along dragon body
                if (world.isClientSide && !playerEntity.isCreative() && !playerEntity.isSpectator()) {
                    if (dragonStateHandler.getType() == DragonType.SEA && !playerEntity.hasEffect(DragonEffects.PEACE) && dragonStateHandler.getDebuffData().timeWithoutWater >= maxTicksOutofWater)
                        world.addParticle(ParticleTypes.WHITE_ASH,
                                playerEntity.getX() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1),
                                playerEntity.getY() + 0.5F,
                                playerEntity.getZ() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1),
                                0, 0, 0);
                    if (dragonStateHandler.getType() == DragonType.FOREST && !playerEntity.hasEffect(DragonEffects.MAGIC) && dragonStateHandler.getDebuffData().timeInDarkness == ConfigHandler.SERVER.forestStressTicks.get())
                        world.addParticle(ParticleTypes.SMOKE,
                                playerEntity.getX() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1),
                                playerEntity.getY() + 0.5F,
                                playerEntity.getZ() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1),
                                0, 0, 0);
                }
            }
        });
    }
	
}
