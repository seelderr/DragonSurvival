package by.jackraidenph.dragonsurvival.handlers.ServerSide;

import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.handlers.ClientSide.ClientFlightHandler;
import by.jackraidenph.dragonsurvival.network.status.SyncFlyingStatus;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

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
                if(!ConfigHandler.SERVER.enableFlightFallDamage.get()){
                    event.setCanceled(true);
                }
                
                if (dragonStateHandler.isFlying() && !livingEntity.isSprinting()
                    && flightSpeed < 1){
                    event.setCanceled(true);
                }
    
                if(!livingEntity.level.isClientSide) {
                    if(dragonStateHandler.isFlying()) {
                        dragonStateHandler.setFlying(false);
                        NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity), new SyncFlyingStatus(livingEntity.getId(), false));
                    }
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
                        if (flightSpeed > 0.08) {
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
                        if (!playerTickEvent.player.isOnGround() && !playerTickEvent.player.isInWater() && !playerTickEvent.player.isInLava()) {
                            Vector3d delta = playerTickEvent.player.getDeltaMovement();
                            double l = delta.length();
                            if(delta.x == 0 && delta.z == 0){
                                l = 15;
                            }
                            float exhaustion = ConfigHandler.SERVER.creativeFlight.get() ? (playerTickEvent.player.abilities.flying ?  playerTickEvent.player.flyingSpeed : 0F) : (float)(0.005F * l);
                            playerTickEvent.player.causeFoodExhaustion(exhaustion);
                        }
                    }
                }
            }
        });
    }
	
	public static double getLandTime(PlayerEntity playerEntity, double goalTime)
	{
	    DragonStateHandler dragonStateHandler = DragonStateProvider.getCap(playerEntity).orElse(null);
	    if (dragonStateHandler != null && dragonStateHandler.isDragon()) {
	        if (dragonStateHandler.isFlying()) {
	            Vector3d motion = playerEntity.getDeltaMovement();
	            BlockPos blockHeight = playerEntity.level.getHeightmapPos(Type.MOTION_BLOCKING, playerEntity.blockPosition());
	            int height = blockHeight.getY();
	            double aboveGround = Math.max(0, playerEntity.position().y - height);
	            double timeToGround = (aboveGround / Math.abs(motion.y));
	            if(playerEntity.fallDistance > 5 && motion.y < 0) {
	                if (aboveGround < 20 && timeToGround <= goalTime) {
	                    return timeToGround;
	                }
	            }
	        }
	    }
	    return -1;
	}
	
	public static boolean canGlide(PlayerEntity player){
	    DragonStateHandler dragonStateHandler = DragonStateProvider.getCap(player).orElse(null);
	    boolean hasFood = player.getFoodData().getFoodLevel() > ConfigHandler.SERVER.flightHungerThreshold.get() || player.isCreative() || ConfigHandler.SERVER.allowFlyingWithoutHunger.get();
	    boolean flight = dragonStateHandler != null && dragonStateHandler.isFlying() && !player.isOnGround() && !player.isInWater() && !player.isInLava();
	    return hasFood && player.isSprinting() && flight;
	}
}
