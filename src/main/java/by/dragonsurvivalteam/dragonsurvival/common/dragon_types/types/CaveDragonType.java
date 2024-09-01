package by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonTraitHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive.ContrastShowerAbility;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncDragonType;
import by.dragonsurvivalteam.dragonsurvival.registry.DSDamageTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.network.PacketDistributor;

public class CaveDragonType extends AbstractDragonType{
	public int timeInRain;
	public int lavaAirSupply;

	public CaveDragonType() {
		slotForBonus = 1;
	}

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
		Level world = player.level();
		BlockState feetBlock = player.getBlockStateOn();
		BlockState blockUnder = world.getBlockState(player.blockPosition().below());
		Block block = blockUnder.getBlock();

		boolean isInCauldron = DragonTraitHandler.isInCauldron(feetBlock, blockUnder);
		boolean isInSeaBlock = DragonConfigHandler.SEA_DRAGON_HYDRATION_BLOCKS != null && (DragonConfigHandler.SEA_DRAGON_HYDRATION_BLOCKS.contains(block) || DragonConfigHandler.SEA_DRAGON_HYDRATION_BLOCKS.contains(feetBlock.getBlock()) || isInCauldron);

		ContrastShowerAbility contrastShower = DragonAbilities.getSelfAbility(player, ContrastShowerAbility.class);
		int maxRainTime = 1;
		if(contrastShower != null){
			maxRainTime = Functions.secondsToTicks(contrastShower.getDuration());
		}
		double oldRainTime = timeInRain;
		int oldLavaTicks = lavaAirSupply;

		if(ServerConfig.penalties
		   && !player.hasEffect(DSEffects.FIRE)
		   && !player.isCreative()
		   && !player.isSpectator()) {
			if(!world.isClientSide() ) {
				if (player.isInWaterOrBubble() && ServerConfig.caveWaterDamage != 0.0 || player.isInWaterOrRain() && !player.isInWater() && ServerConfig.caveRainDamage != 0.0 || isInSeaBlock && ServerConfig.caveRainDamage != 0.0) {
					if (player.isInWaterOrBubble() && player.tickCount % 10 == 0 && ServerConfig.caveWaterDamage != 0.0) {
						player.hurt(new DamageSource(DSDamageTypes.get(player.level(), DSDamageTypes.WATER_BURN)), ServerConfig.caveWaterDamage.floatValue());
					} else if ((player.isInWaterOrRain() && !player.isInWaterOrBubble() || isInSeaBlock) && ServerConfig.caveRainDamage != 0.0) {
						timeInRain++;
					}
					
					if (timeInRain >= maxRainTime) {
						if (player.tickCount % 40 == 0) {
							player.hurt(new DamageSource(DSDamageTypes.get(player.level(), DSDamageTypes.RAIN_BURN)), ServerConfig.caveRainDamage.floatValue());
						}
					}
					
					if (player.tickCount % 40 == 0) {
						player.playSound(SoundEvents.LAVA_EXTINGUISH, 1.0F, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F);
					}
					
				} else if (timeInRain > 0) {
					if (maxRainTime > 0) {
						timeInRain = Math.max(timeInRain - (int)Math.ceil(maxRainTime * 0.02F), 0);
					} else {
						timeInRain--;
					}
				}
			}
			
			if (world.isClientSide()) {
				if (player.tickCount % 10 == 0 && timeInRain > 0) {
					world.addParticle(ParticleTypes.POOF, player.getX() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1), player.getY() + 0.5F, player.getZ() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1), 0, 0, 0);
				}
			}
		}

		if(player.isOnFire() && ServerConfig.bonuses && ServerConfig.caveFireImmunity){
			player.clearFire();
		}

		if(!player.level().isClientSide()){
			if(player.isEyeInFluidType(NeoForgeMod.LAVA_TYPE.value())
			   && ServerConfig.bonuses
			   && ServerConfig.caveLavaSwimming
			   && ServerConfig.caveLavaSwimmingTicks != 0){
				if(!player.canBreatheUnderwater() && !player.getAbilities().invulnerable){
					lavaAirSupply--;
					if(lavaAirSupply == -20){
						lavaAirSupply = 0;
						if(!player.level().isClientSide()){
							player.hurt(player.damageSources().drown(), 2F); //LAVA_YES
						}
					}
				}
				if(!player.level().isClientSide() && player.isPassenger() && player.getVehicle() != null && !player.getVehicle().canBeRiddenUnderFluidType(NeoForgeMod.WATER_TYPE.value(), player)){
					player.stopRiding();
				}
			}else if(lavaAirSupply < ServerConfig.caveLavaSwimmingTicks && !player.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value())){
				lavaAirSupply = Math.min(lavaAirSupply + (int)Math.ceil(ServerConfig.caveLavaSwimmingTicks * 0.0133333F), ServerConfig.caveLavaSwimmingTicks);
			}
		}
		
		if(!world.isClientSide() && (oldLavaTicks != lavaAirSupply || timeInRain != oldRainTime)){
			PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncDragonType.Data(player.getId(), dragonStateHandler.getType().writeNBT()));
		}
	}

	@Override
	public boolean isInManaCondition(Player player, DragonStateHandler cap){
		BlockState blockBelow = player.level().getBlockState(player.blockPosition().below());

		if(player.isInLava() || player.isOnFire() || player.hasEffect(DSEffects.BURN) || player.hasEffect(DSEffects.FIRE)){
			return true;
		}

		//If cave dragon is ontop of a burning furnace
		if(DragonConfigHandler.DRAGON_MANA_BLOCKS != null && DragonConfigHandler.DRAGON_MANA_BLOCKS.containsKey(DragonTypes.CAVE.getTypeName())){
			if(DragonConfigHandler.DRAGON_MANA_BLOCKS.get(DragonTypes.CAVE.getTypeName()).contains(blockBelow.getBlock())){
				if(blockBelow.getBlock() instanceof AbstractFurnaceBlock){
					if(blockBelow.hasProperty(AbstractFurnaceBlock.LIT)){
						return blockBelow.getValue(AbstractFurnaceBlock.LIT);
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
	public List<TagKey<Block>> mineableBlocks(){
		return List.of(BlockTags.MINEABLE_WITH_PICKAXE);
	}

	@Override
	public String getTypeName(){
		return "cave";
	}
}