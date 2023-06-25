package by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive.LightInDarknessAbility;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncDragonTypeData;
import by.dragonsurvivalteam.dragonsurvival.registry.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class ForestDragonType extends AbstractDragonType{
	public int timeInDarkness;

	@Override
	public CompoundTag writeNBT(){
		CompoundTag tag = new CompoundTag();
		tag.putInt("timeInDarkness", timeInDarkness);
		return tag;
	}

	@Override
	public void readNBT(CompoundTag base){
		timeInDarkness = base.getInt("timeInDarkness");
	}

	@Override
	public void onPlayerUpdate(Player player, DragonStateHandler dragonStateHandler){
		Level world = player.level;
		BlockState feetBlock = player.getFeetBlockState();
		BlockState blockUnder = world.getBlockState(player.blockPosition().below());
		Block block = blockUnder.getBlock();
		Biome biome = world.getBiome(player.blockPosition()).value();

		int maxStressTicks = ServerConfig.forestStressTicks;
		LightInDarknessAbility lightInDarkness = DragonAbilities.getSelfAbility(player, LightInDarknessAbility.class);
	
		if(lightInDarkness != null){
			maxStressTicks += Functions.secondsToTicks(lightInDarkness.getDuration());
		}
		
		double oldDarknessTime = timeInDarkness;
		
		if(ServerConfig.penalties && !player.hasEffect(DragonEffects.MAGIC)
		   && ServerConfig.forestStressTicks > 0
		   && !player.isCreative() &&
		   !player.isSpectator()) {
			if (!world.isClientSide) {
				LevelLightEngine lightManager = world.getChunkSource().getLightEngine();
				if (lightManager.getLayerListener(LightLayer.BLOCK).getLightValue(player.blockPosition()) < 3 && lightManager.getLayerListener(LightLayer.SKY).getLightValue(player.blockPosition()) < 3 && lightManager.getLayerListener(LightLayer.SKY).getLightValue(
						player.blockPosition().above()) < 3) {
					if (timeInDarkness < maxStressTicks) {
						timeInDarkness++;
					}
					
				} else {
					timeInDarkness = Math.max(timeInDarkness - (int)Math.ceil(maxStressTicks * 0.02F), 0);
				}
				
				timeInDarkness = Math.min(timeInDarkness, maxStressTicks);
				
				if (timeInDarkness >= maxStressTicks && player.tickCount % 21 == 0) {
					player.addEffect(new MobEffectInstance(DragonEffects.STRESS, Functions.secondsToTicks(ServerConfig.forestStressEffectDuration)));
				}
				
				
				if (timeInDarkness != oldDarknessTime) {
						NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncDragonTypeData(player.getId(), dragonStateHandler.getType()));
				}
			}
			
			if (world.isClientSide && !player.isCreative() && !player.isSpectator()) {
				if (!player.hasEffect(DragonEffects.MAGIC) && timeInDarkness == ServerConfig.forestStressTicks) {
					world.addParticle(ParticleTypes.SMOKE, player.getX() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1), player.getY() + 0.5F, player.getZ() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1), 0, 0, 0);
				}
			}
		}
	}

	@Override
	public boolean isInManaCondition(Player player, DragonStateHandler cap){
		BlockState blockBelow = player.level.getBlockState(player.blockPosition().below());
		BlockState feetBlock = player.getFeetBlockState();

		if(player.level.canSeeSky(player.blockPosition())){
			int light = player.level.getBrightness(LightLayer.SKY, player.blockPosition()) - player.level.getSkyDarken();
			float f = player.level.getSunAngle(1.0F);

			float f1 = f < (float)Math.PI ? 0.0F : (float)Math.PI * 2F;
			f = f + (f1 - f) * 0.2F;
			light = Math.round((float)light * Mth.cos(f));
			light = Mth.clamp(light, 0, 15);

			if(light >= 10){
				return true;
			}
		}
		
		return player.hasEffect(DragonEffects.DRAIN) || player.hasEffect(DragonEffects.MAGIC);
	}

	@Override
	public void onPlayerDeath(){
		timeInDarkness = 0;
	}

	@Override
	public List<Pair<ItemStack, FoodData>> validFoods(Player player, DragonStateHandler handler){
		return null;
	}

	@Override
	public String getTypeName(){
		return "forest";
	}

	@Override
	public List<TagKey<Block>> mineableBlocks(Player player){
		return List.of(BlockTags.MINEABLE_WITH_AXE);
	}
}