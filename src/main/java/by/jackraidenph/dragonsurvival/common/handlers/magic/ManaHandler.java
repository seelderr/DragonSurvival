package by.jackraidenph.dragonsurvival.common.handlers.magic;

import by.jackraidenph.dragonsurvival.common.DragonEffects;
import by.jackraidenph.dragonsurvival.common.blocks.TreasureBlock;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.handlers.DragonConfigHandler;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.util.Functions;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ManaHandler
{
	@SubscribeEvent
	public static void playerTick(PlayerTickEvent event){
		if(event.phase == Phase.START) return;
		
		Player player = event.player;
		
		DragonStateProvider.getCap(player).ifPresent(cap -> {
			if(cap.getMagic().getCurrentlyCasting() != null) return;
			
			boolean goodConditions = ManaHandler.isPlayerInGoodConditions(player);
			
			int timeToRecover = goodConditions ? ConfigHandler.SERVER.favorableManaTicks.get() : ConfigHandler.SERVER.normalManaTicks.get();
			
			if(player.hasEffect(DragonEffects.SOURCE_OF_MAGIC)){
				timeToRecover = 1;
			}
			
			if (player.tickCount % Functions.secondsToTicks(timeToRecover) == 0) {
				if (cap.getMagic().getCurrentMana() < DragonStateProvider.getMaxMana(player)) {
					DragonStateProvider.replenishMana(player, 1);
				}
			}
		});
	}
	
	public static boolean isPlayerInGoodConditions(Player player){
		if(!DragonStateProvider.isDragon(player)){
			return false;
		}
		
		BlockState blockBelow = player.level.getBlockState(player.blockPosition().below());
		BlockState feetBlock = player.getFeetBlockState();
		
		if(feetBlock.getBlock() instanceof TreasureBlock || blockBelow.getBlock() instanceof TreasureBlock){
			return true;
		}
		
		if(player.hasEffect(DragonEffects.SOURCE_OF_MAGIC)){
			return true;
		}
		
		return DragonStateProvider.getCap(player).map(cap -> {
			
			if(DragonConfigHandler.DRAGON_MANA_BLOCKS != null && DragonConfigHandler.DRAGON_MANA_BLOCKS.containsKey(cap.getType())) {
				if (DragonConfigHandler.DRAGON_MANA_BLOCKS.get(cap.getType()).contains(blockBelow.getBlock()) || DragonConfigHandler.DRAGON_MANA_BLOCKS.get(cap.getType()).contains(feetBlock.getBlock())) {
					if (!(blockBelow.getBlock() instanceof AbstractFurnaceBlock) && !(feetBlock.getBlock() instanceof AbstractFurnaceBlock) && !(blockBelow.getBlock() instanceof CauldronBlock) && !(feetBlock.getBlock() instanceof CauldronBlock)) {
						return true;
					}
				}
			}
			
				switch (cap.getType()) {
				case SEA:
					if (player.isInWaterRainOrBubble() || player.hasEffect(DragonEffects.CHARGED) || player.hasEffect(DragonEffects.PEACE)) {
						return true;
					}
					if(DragonConfigHandler.DRAGON_MANA_BLOCKS != null && DragonConfigHandler.DRAGON_MANA_BLOCKS.containsKey(DragonType.SEA)) {
						if (DragonConfigHandler.DRAGON_MANA_BLOCKS.get(DragonType.SEA).contains(blockBelow.getBlock())) {
							if (blockBelow.getBlock() instanceof LayeredCauldronBlock) {
								if (blockBelow.hasProperty(LayeredCauldronBlock.LEVEL)) {
									int level = blockBelow.getValue(LayeredCauldronBlock.LEVEL);
									
									if (level > 0) {
										return true;
									}
								}
							}
							
							if (feetBlock.getBlock() instanceof LayeredCauldronBlock) {
								if (feetBlock.hasProperty(LayeredCauldronBlock.LEVEL)) {
									int level = feetBlock.getValue(LayeredCauldronBlock.LEVEL);
									
									if (level > 0) {
										return true;
									}
								}
							}
						}
					}
					
					break;
				
				case FOREST:
					LevelLightEngine lightManager = player.level.getChunkSource().getLightEngine();
					if(player.level.canSeeSky(player.blockPosition())){
						int light = player.level.getBrightness(LightLayer.SKY, player.blockPosition()) - player.level.getSkyDarken();
						float f = player.level.getSunAngle(1.0F);
						
						float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
						f = f + (f1 - f) * 0.2F;
						light = Math.round((float)light * Mth.cos(f));
						light = Mth.clamp(light, 0, 15);
						
						if(light >= 14){
							return true;
						}
					}
					
					if (player.hasEffect(DragonEffects.DRAIN) || player.hasEffect(DragonEffects.MAGIC))  {
						return true;
					}
					
					break;
				
				case CAVE:
					if (player.isInLava() || player.isOnFire() || player.hasEffect(DragonEffects.BURN) || player.hasEffect(DragonEffects.FIRE)) {
						return true;
					}
					
					//If cave dragon is ontop of a burning furnace
					if(DragonConfigHandler.DRAGON_MANA_BLOCKS != null && DragonConfigHandler.DRAGON_MANA_BLOCKS.containsKey(DragonType.CAVE)) {
						if (DragonConfigHandler.DRAGON_MANA_BLOCKS.get(DragonType.CAVE).contains(blockBelow.getBlock())) {
							if (blockBelow.getBlock() instanceof AbstractFurnaceBlock) {
								if (blockBelow.hasProperty(AbstractFurnaceBlock.LIT)) {
									if (blockBelow.getValue(AbstractFurnaceBlock.LIT)) {
										return true;
									}
								}
							}
						}
					}
					
					break;
			}
			
			return false;
		}).orElse(false);
	}
}
