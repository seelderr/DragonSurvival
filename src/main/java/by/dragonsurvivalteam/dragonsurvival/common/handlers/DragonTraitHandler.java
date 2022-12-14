package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.PassiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.TickablePassiveAbility;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
				for(PassiveDragonAbility passiveAbility : dragonStateHandler.getMagicData().getPassiveAbilities()){
					if(passiveAbility instanceof TickablePassiveAbility tickablePassiveAbility){
						if(tickablePassiveAbility.getLevel() > 0){
							tickablePassiveAbility.onTick(player);
						}
					}
				}

				dragonStateHandler.getType().onPlayerUpdate(player, dragonStateHandler);
			}
		});
	}

	@SubscribeEvent
	public static void onDeath(LivingDeathEvent event){
		if(event.getEntity() instanceof Player){
			DragonStateHandler handler = DragonUtils.getHandler(event.getEntity());
			if(handler.isDragon()){
				handler.getType().onPlayerDeath();
			}
		}
	}

	public static boolean isInCauldron(BlockState feetBlock, BlockState blockUnder){
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
		return isInCauldron;
	}
}