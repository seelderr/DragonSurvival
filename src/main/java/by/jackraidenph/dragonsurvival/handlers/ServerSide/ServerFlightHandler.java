package by.jackraidenph.dragonsurvival.handlers.ServerSide;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.handlers.ClientSide.ClientFlightHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Used in pair with {@link ClientFlightHandler}
 */
@Mod.EventBusSubscriber()
@SuppressWarnings("unused")
public class ServerFlightHandler {

    /**
     * Sets the fall damage based on flight speed and dragon's size
     */
    @SubscribeEvent
    public static void changeFallDistance(LivingFallEvent event) {
        LivingEntity livingEntity = event.getEntityLiving();
        final double flightSpeed = livingEntity.getDeltaMovement().length();
        DragonStateProvider.getCap(livingEntity).ifPresent(dragonStateHandler -> {
            if(dragonStateHandler.isDragon()) {
                if (dragonStateHandler.isFlying() && !livingEntity.isSprinting()
                    || !ConfigHandler.SERVER.enableFlightFallDamage.get()
                    || flightSpeed < 0.08){
                    event.setCanceled(true);
                }
            }
        });
    }
    
    
    @SubscribeEvent
    public static void changeFlightFallDamage(LivingHurtEvent event) {
        LivingEntity livingEntity = event.getEntityLiving();
        DamageSource damageSource = event.getSource();
        if (damageSource == DamageSource.FALL) {
            final double flightSpeed = livingEntity.getDeltaMovement().length();
            if (livingEntity.isPassenger() && DragonStateProvider.isDragon(livingEntity.getVehicle())) {
                event.setCanceled(true);
            } else if (ConfigHandler.SERVER.enableFlightFallDamage.get()) {
                DragonStateProvider.getCap(livingEntity).ifPresent(dragonStateHandler -> {
                    if (dragonStateHandler.isDragon() && dragonStateHandler.isFlying()) {
                        if (flightSpeed > 0.08 && livingEntity.isSprinting()) {
                            DragonSurvivalMod.LOGGER.info(flightSpeed);
                            double damage = flightSpeed * 35 * dragonStateHandler.getSize() / 20;
                            damage = MathHelper.clamp(damage, 0, livingEntity.getHealth() - 1);
                            event.setAmount((float) (damage));
                        } else {
                            event.setCanceled(true);
                        }
                    }
                });
            }
        }
    }
    
    @SubscribeEvent
    public static void playerFoodExhaustion(TickEvent.PlayerTickEvent playerTickEvent) {
        if(playerTickEvent.phase == Phase.START) return;
    
        DragonStateProvider.getCap(playerTickEvent.player).ifPresent(dragonStateHandler -> {
            if(dragonStateHandler.isDragon()) {
                boolean wingsSpread = dragonStateHandler.isFlying();
                if(ConfigHandler.SERVER.creativeFlight.get() && !playerTickEvent.player.level.isClientSide){
                    if(playerTickEvent.player.abilities.flying != wingsSpread){
                        playerTickEvent.player.abilities.flying = wingsSpread;
                        playerTickEvent.player.onUpdateAbilities();
                    }
                }
                
                if (wingsSpread) {
                    if (ConfigHandler.SERVER.flyingUsesHunger.get()) {
                        if (!playerTickEvent.player.isOnGround()) {
                            Vector3d delta = playerTickEvent.player.getDeltaMovement();
                            float l = Math.round(MathHelper.sqrt(delta.x * delta.x + delta.y * delta.y + delta.z * delta.z));
                            float exhaustion = ConfigHandler.SERVER.creativeFlight.get() ? (playerTickEvent.player.abilities.flying ?  playerTickEvent.player.flyingSpeed : 0F) : (0.005F * l);
                            playerTickEvent.player.causeFoodExhaustion(exhaustion);
                        }
                    }
                }
            }
        });
    }
}
