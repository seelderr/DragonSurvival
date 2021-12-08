package by.jackraidenph.dragonsurvival.handlers.Magic;

import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.registration.BlockInit;
import by.jackraidenph.dragonsurvival.registration.DragonEffects;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
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
		
		PlayerEntity player = event.player;
		
		DragonStateProvider.getCap(player).ifPresent(cap -> {
			if(cap.getMagic().getCurrentlyCasting() != null) return;
			
			boolean goodConditions = ManaHandler.isPlayerInGoodConditions(player);
			
			int timeToRecover = goodConditions ? ConfigHandler.SERVER.favorableManaTicks.get() : ConfigHandler.SERVER.normalManaTicks.get();
			
			if (player.tickCount % Functions.secondsToTicks(timeToRecover) == 0) {
				if (cap.getMagic().getCurrentMana() < DragonStateProvider.getMaxMana(player)) {
					DragonStateProvider.replenishMana(player, 1);
				}
			}
		});
	}
	
	public static boolean isPlayerInGoodConditions(PlayerEntity player){
		BlockState blockBelow = player.level.getBlockState(player.blockPosition().below());
		BlockState feetBlock = player.getFeetBlockState();
		
		return DragonStateProvider.getCap(player).map(cap -> {
			switch (cap.getType()) {
				case SEA:
					if (player.isInWaterRainOrBubble() || blockBelow.getMaterial() == Material.SNOW || blockBelow.getMaterial() == Material.WATER || blockBelow.getBlock() == Blocks.WET_SPONGE
					    || blockBelow.getMaterial() == Material.ICE || player.hasEffect(DragonEffects.CHARGED) || player.hasEffect(DragonEffects.PEACE)) {
						return true;
					}
					
					if(blockBelow.getBlock() == BlockInit.smallSeaNest || blockBelow.getBlock() == BlockInit.mediumSeaNest || blockBelow.getBlock() == BlockInit.bigSeaNest){
						return true;
					}
					
					if(blockBelow.getBlock() == Blocks.CAULDRON){
						if(blockBelow.hasProperty(CauldronBlock.LEVEL)) {
							int level = blockBelow.getValue(CauldronBlock.LEVEL);
							
							if(level > 0){
								return true;
							}
						}
					}
					
					if(feetBlock.getBlock() == Blocks.CAULDRON){
						if(feetBlock.hasProperty(CauldronBlock.LEVEL)) {
							int level = feetBlock.getValue(CauldronBlock.LEVEL);
							
							if(level > 0){
								return true;
							}
						}
					}
					
					break;
				
				case FOREST:
					if(!player.level.canSeeSky(player.blockPosition()) || !player.level.isDay()){
						return false;
					}
					
					if (player.hasEffect(DragonEffects.DRAIN) || player.hasEffect(DragonEffects.MAGIC)
					    || blockBelow.getMaterial() == Material.PLANT || blockBelow.getMaterial() == Material.REPLACEABLE_PLANT
					    || feetBlock.getMaterial() == Material.PLANT || feetBlock.getMaterial() == Material.REPLACEABLE_PLANT)  {
						return true;
					}
					
					if(blockBelow.getBlock() == BlockInit.smallForestNest || blockBelow.getBlock() == BlockInit.mediumForestNest || blockBelow.getBlock() == BlockInit.bigForestNest){
						return true;
					}
					
					break;
				
				case CAVE:
					if (player.isInLava() || blockBelow.getMaterial() == Material.LAVA || blockBelow.getMaterial() == Material.FIRE || player.isOnFire()
					    || blockBelow.getBlock() == Blocks.CAMPFIRE || blockBelow.getBlock() == Blocks.SOUL_CAMPFIRE || blockBelow.getBlock() == Blocks.BLAST_FURNACE
						|| blockBelow.getBlock() == Blocks.SMOKER || blockBelow.getBlock() == Blocks.FURNACE || blockBelow.getBlock() == Blocks.MAGMA_BLOCK
						|| player.hasEffect(DragonEffects.BURN) || player.hasEffect(DragonEffects.FIRE)) {
						return true;
					}
					
					if(blockBelow.getBlock() == BlockInit.smallCaveNest || blockBelow.getBlock() == BlockInit.mediumCaveNest || blockBelow.getBlock() == BlockInit.bigCaveNest){
						return true;
					}
					
					//If cave dragon is ontop of a burning furnace
					if(blockBelow.getBlock() instanceof AbstractFurnaceBlock){
						if(blockBelow.hasProperty(AbstractFurnaceBlock.LIT)) {
							if (blockBelow.getValue(AbstractFurnaceBlock.LIT)) {
								return true;
							}
						}
					}
					
					break;
			}
			
			return false;
		}).orElse(false);
	}
}
