package by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonTraitHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive.ContrastShowerAbility;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncCapabilityDebuff;
import by.dragonsurvivalteam.dragonsurvival.registry.DamageSources;
import by.dragonsurvivalteam.dragonsurvival.registry.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class CaveDragonType extends AbstractDragonType{
	public int timeInRain;
	public int lavaAirSupply;

	@Override
	public CompoundTag writeNBT(){
		CompoundTag tag = new CompoundTag();
		tag.putInt("timeInRain", timeInRain);
		tag.putInt("lavaAirSupply", lavaAirSupply);
		return tag;
	}

	@Override
	public void readNBT(CompoundTag base){
		timeInRain = base.getInt("timeInRain");
		lavaAirSupply = base.getInt("lavaAirSupply");
	}

	@Override
	public void onPlayerUpdate(Player player, DragonStateHandler dragonStateHandler){
		Level world = player.level;
		BlockState feetBlock = player.getFeetBlockState();
		BlockState blockUnder = world.getBlockState(player.blockPosition().below());
		Block block = blockUnder.getBlock();
		Biome biome = world.getBiome(player.blockPosition()).value();

		boolean isInCauldron = DragonTraitHandler.isInCauldron(feetBlock, blockUnder);
		boolean isInSeaBlock = DragonConfigHandler.SEA_DRAGON_HYDRATION_BLOCKS != null && (DragonConfigHandler.SEA_DRAGON_HYDRATION_BLOCKS.contains(block) || DragonConfigHandler.SEA_DRAGON_HYDRATION_BLOCKS.contains(feetBlock.getBlock()) || isInCauldron);

		ContrastShowerAbility contrastShower = DragonAbilities.getAbility(player, ContrastShowerAbility.class);
		int maxRainTime = 0;
		if(contrastShower != null){
			maxRainTime += Functions.secondsToTicks(contrastShower.getDuration());
		}
		double oldRainTime = timeInRain;
		int oldLavaTicks = lavaAirSupply;

		if(ServerConfig.penalties && !player.hasEffect(DragonEffects.FIRE) && !player.isCreative() && !player.isSpectator() && ((player.isInWaterOrBubble() && ServerConfig.caveWaterDamage != 0.0) || (player.isInWaterOrRain() && !player.isInWater() && ServerConfig.caveRainDamage != 0.0) || isInSeaBlock && ServerConfig.caveRainDamage != 0.0)){
			if(player.isInWaterOrBubble() && player.tickCount % 10 == 0 && ServerConfig.caveWaterDamage != 0.0){
				player.hurt(DamageSources.WATER_BURN, ServerConfig.caveWaterDamage.floatValue());
			}else if((player.isInWaterOrRain() && !player.isInWaterOrBubble() || isInSeaBlock) && ServerConfig.caveRainDamage != 0.0){

				if(timeInRain >= maxRainTime){
					if(player.tickCount % 40 == 0){
						player.hurt(DamageSources.RAIN_BURN, ServerConfig.caveRainDamage.floatValue());
					}
				}else{
					if(!player.level.isClientSide){
						timeInRain++;
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
		}else if(timeInRain > 0){
			if(!player.level.isClientSide){
				if(maxRainTime > 0){
					timeInRain = Math.max(timeInRain - (int)Math.ceil(maxRainTime * 0.02F), 0);
				}else{
					timeInRain--;
				}
			}
		}

		if(player.isOnFire() && ServerConfig.bonuses && ServerConfig.caveFireImmunity){
			player.clearFire();
		}

		if(!player.level.isClientSide){
			if(player.isEyeInFluid(FluidTags.LAVA) && ServerConfig.bonuses && ServerConfig.caveLavaSwimming && ServerConfig.caveLavaSwimmingTicks != 0){
				if(!player.canBreatheUnderwater() && !player.getAbilities().invulnerable){
					lavaAirSupply--;
					if(lavaAirSupply == -20){
						lavaAirSupply = 0;
						if(!player.level.isClientSide){
							player.hurt(DamageSource.DROWN, 2F); //LAVA_YES
						}
					}
				}
				if(!player.level.isClientSide && player.isPassenger() && player.getVehicle() != null && !player.getVehicle().canBeRiddenInWater(player)){
					player.stopRiding();
				}
			}else if(lavaAirSupply < ServerConfig.caveLavaSwimmingTicks && !player.isEyeInFluid(FluidTags.WATER)){
				lavaAirSupply = Math.min(lavaAirSupply + (int)Math.ceil(ServerConfig.caveLavaSwimmingTicks * 0.0133333F), ServerConfig.caveLavaSwimmingTicks);
			}

			if(timeInRain != oldRainTime || lavaAirSupply != oldLavaTicks){
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncCapabilityDebuff(player.getId(), 0, 0, timeInRain, lavaAirSupply));
			}
		}
	}

	@Override
	public boolean isInManaCondition(Player player, DragonStateHandler cap){
		BlockState blockBelow = player.level.getBlockState(player.blockPosition().below());
		BlockState feetBlock = player.getFeetBlockState();

		if(player.isInLava() || player.isOnFire() || player.hasEffect(DragonEffects.BURN) || player.hasEffect(DragonEffects.FIRE)){
			return true;
		}

		//If cave dragon is ontop of a burning furnace
		if(DragonConfigHandler.DRAGON_MANA_BLOCKS != null && DragonConfigHandler.DRAGON_MANA_BLOCKS.containsKey(DragonTypes.CAVE)){
			if(DragonConfigHandler.DRAGON_MANA_BLOCKS.get(DragonTypes.CAVE).contains(blockBelow.getBlock())){
				if(blockBelow.getBlock() instanceof AbstractFurnaceBlock){
					if(blockBelow.hasProperty(AbstractFurnaceBlock.LIT)){
						if(blockBelow.getValue(AbstractFurnaceBlock.LIT)){
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	@Override
	public void onPlayerDeath(){
		timeInRain = 0;
	}

	@Override
	public List<Pair<ItemStack, FoodData>> validFoods(Player player, DragonStateHandler handler){
		return null;
	}

	@Override
	public List<TagKey<Block>> mineableBlocks(Player player){
		return List.of(BlockTags.MINEABLE_WITH_PICKAXE);
	}

	@Override
	public String getTypeName(){
		return "cave";
	}
}