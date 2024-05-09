package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.player.SynchronizeDragonCap;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber
public class DragonExplosionHandler {

    @SubscribeEvent
    public static void cancelSelfExplosionDamageFromDragon(LivingDamageEvent event) {
        if(!event.getSource().isExplosion()) {
            return;
        }

        if(event.getSource().getEntity() instanceof ServerPlayer source){
            if(event.getEntity() instanceof ServerPlayer target){
                DragonStateProvider.getCap(source).ifPresent(targetCap -> {
                    if(targetCap.isDragon()){
                        if(source == target) {
                            event.setCanceled(true);
                        }
                    }
                });
            }
        }
    }
}
