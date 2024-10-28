package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.TickablePassiveAbility;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class DragonTraitHandler {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post playerTickEvent) {
        Player player = playerTickEvent.getEntity();
        DragonStateProvider.getOptional(player).ifPresent(dragonStateHandler -> {
            if (dragonStateHandler.isDragon()) {
                for (DragonAbility passiveAbility : dragonStateHandler.getMagicData().abilities.values()) {
                    if (passiveAbility instanceof TickablePassiveAbility tickablePassiveAbility) {
                        if (tickablePassiveAbility.getLevel() > 0) {
                            tickablePassiveAbility.onTick(player);
                        }
                    }
                }

                dragonStateHandler.getType().onPlayerUpdate(player, dragonStateHandler);
            }
        });
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            DragonStateHandler handler = DragonStateProvider.getData(player);
            if (handler.isDragon()) {
                handler.getType().onPlayerDeath();
            }
        }
    }

    public static boolean isInCauldron(BlockState feetBlock, BlockState blockUnder) {
        //Because it is used for both cave and sea dragon it is added here
        boolean isInCauldron = false;
        if (blockUnder.getBlock() instanceof LayeredCauldronBlock) {
            if (blockUnder.hasProperty(LayeredCauldronBlock.LEVEL)) {
                int level = blockUnder.getValue(LayeredCauldronBlock.LEVEL);

                if (level > 0) {
                    isInCauldron = true;
                }
            }
        } else if (feetBlock.getBlock() instanceof LayeredCauldronBlock) {
            if (feetBlock.hasProperty(LayeredCauldronBlock.LEVEL)) {
                int level = feetBlock.getValue(LayeredCauldronBlock.LEVEL);

                if (level > 0) {
                    isInCauldron = true;
                }
            }
        }
        return isInCauldron;
    }
}