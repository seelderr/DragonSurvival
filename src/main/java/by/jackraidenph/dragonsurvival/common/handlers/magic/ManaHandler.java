package by.jackraidenph.dragonsurvival.common.handlers.magic;

import by.jackraidenph.dragonsurvival.util.Functions;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.common.blocks.DSBlocks;
import by.jackraidenph.dragonsurvival.common.DragonEffects;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.lighting.WorldLightManager;
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
					
					if(blockBelow.getBlock() == DSBlocks.smallSeaNest || blockBelow.getBlock() == DSBlocks.mediumSeaNest || blockBelow.getBlock() == DSBlocks.bigSeaNest){
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
					WorldLightManager lightManager = player.level.getChunkSource().getLightEngine();
					if(player.level.canSeeSky(player.blockPosition())){
						int light = player.level.getBrightness(LightType.SKY, player.blockPosition()) - player.level.getSkyDarken();
						float f = player.level.getSunAngle(1.0F);
						
						float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
						f = f + (f1 - f) * 0.2F;
						light = Math.round((float)light * MathHelper.cos(f));
						light = MathHelper.clamp(light, 0, 15);
						
						if(light >= 14){
							return true;
						}
					}
					
					if (player.hasEffect(DragonEffects.DRAIN) || player.hasEffect(DragonEffects.MAGIC)
					    || blockBelow.getMaterial() == Material.PLANT || blockBelow.getMaterial() == Material.REPLACEABLE_PLANT
					    || feetBlock.getMaterial() == Material.PLANT || feetBlock.getMaterial() == Material.REPLACEABLE_PLANT)  {
						return true;
					}
					
					if(blockBelow.getBlock() == DSBlocks.smallForestNest || blockBelow.getBlock() == DSBlocks.mediumForestNest || blockBelow.getBlock() == DSBlocks.bigForestNest){
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
					
					if(blockBelow.getBlock() == DSBlocks.smallCaveNest || blockBelow.getBlock() == DSBlocks.mediumCaveNest || blockBelow.getBlock() == DSBlocks.bigCaveNest){
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
