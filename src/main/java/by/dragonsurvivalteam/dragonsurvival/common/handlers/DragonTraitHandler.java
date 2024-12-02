package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
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
        // FIXME not needed anymore?
        /*DragonStateProvider.getOptional(player).ifPresent(dragonStateHandler -> {
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
        });*/
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            DragonStateHandler handler = DragonStateProvider.getData(player);
            if (handler.isDragon()) {
                // FIXME
               // handler.getType().onPlayerDeath();
            }
        }
    }

    public static boolean isInCauldron(final Player player, final Block typeToCheck) {
        if (isInCauldron(player.getBlockStateOn(), typeToCheck)) {
            return true;
        }

        return isInCauldron(player.getInBlockState(), typeToCheck);
    }

    private static boolean isInCauldron(final BlockState state, final Block typeToCheck) {
        if (state.getBlock() == typeToCheck && state.hasProperty(LayeredCauldronBlock.LEVEL)) {
            return state.getValue(LayeredCauldronBlock.LEVEL) > 0;
        }

        return false;
    }
}