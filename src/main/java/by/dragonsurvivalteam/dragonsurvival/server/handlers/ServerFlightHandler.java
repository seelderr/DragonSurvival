package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientFlightHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.network.flight.SyncFlyingStatus;
import by.dragonsurvivalteam.dragonsurvival.network.flight.SyncSpinStatus;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAttributes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

/**
 * Used in pair with {@link ClientFlightHandler}
 */
@EventBusSubscriber()
@SuppressWarnings("unused")
public class ServerFlightHandler {

    public static final int spinDuration = (int) Math.round(0.76 * 20);

    @ConfigRange(min = 0.1, max = 1)
    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "maxFlightSpeed", comment = "Maximum acceleration fly speed up and down. Take into account the chunk load speed. A speed of 0.3 is optimal.")
    public static Double maxFlightSpeed = 0.3;

    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "startWithLevitation", comment = "Whether dragons can use levitation magic from birth.")
    public static Boolean startWithLevitation = true;

    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "enderDragonGrantsSpin", comment = "Whether you should be able to obtain the spin ability from the ender dragon or take special item.")
    public static Boolean enderDragonGrantsSpin = true;

    @ConfigRange(min = 0, max = 20)
    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "flightHungerThreshold", comment = "If the player's hunger is below this parameter, he can't open his wings.")
    public static Integer flightHungerThreshold = 6;

    @ConfigRange(min = 0, max = 20)
    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "foldWingsThreshold", comment = "If the player's hunger is less then or equal to this parameter, the wings will be folded even during flight.")
    public static Integer foldWingsThreshold = 0;

    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "foldWingsOnLand", comment = "Whether your wings will fold automatically when landing. Has protection against accidental triggering, so the wings do not always close. If False you must close the wings manually.")
    public static Boolean foldWingsOnLand = true;

    @ConfigRange(min = 0, max = 100000)
    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "flightSpinCooldown", comment = "The cooldown in seconds in between uses of the spin attack in flight")
    public static Integer flightSpinCooldown = 5;

    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "stableHover", comment = "Should hovering be completely stable similar to creative flight?")
    public static boolean stableHover = false;

    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "collisionDamageSpeedFactor", comment = "How much does the change in horizontal speed impact the damage taken from a collision whilst flying?")
    public static float collisionDamageSpeedFactor = 10.0f;

    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "collisionDamageThreshold", comment = "The amount of damage subtracted from the base damage when a collision occurs whilst flying.")
    public static float collisionDamageThreshold = 3.0f;

    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "enableCollisionDamage", comment = "Dragons will take damage from colliding whilst glide-flying (similar to elytra).")
    public static boolean enableCollisionDamage = true;

    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "enableFlightFallDamage", comment = "Dragons will take fall damage from colliding whilst glide-flying (similar to elytra).")
    public static boolean enableFlightFallDamage = true;

    // Even if the event is ultimately cancelled, we still want to trigger this, so make it highest priority.
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void foldWingsOnLand(LivingFallEvent event) {
        LivingEntity livingEntity = event.getEntity();
        double flightSpeed = event.getDistance();

        DragonStateProvider.getOptional(livingEntity).ifPresent(dragonStateHandler -> {
            if (dragonStateHandler.isDragon() && dragonStateHandler.hasFlight()) {
                if (!livingEntity.level().isClientSide()) {
                    if (foldWingsOnLand) {
                        if (dragonStateHandler.isWingsSpread()) {
                            dragonStateHandler.setWingsSpread(false);
                            PacketDistributor.sendToPlayersTrackingEntityAndSelf(livingEntity, new SyncFlyingStatus.Data(livingEntity.getId(), false));
                        }
                    }
                }
            }
        });
    }

    /**
     * Sets the fall damage based on flight speed and dragon's size
     */
    @SubscribeEvent
    public static void changeFallDistanceToUseFlightSpeed(LivingFallEvent event) {
        LivingEntity livingEntity = event.getEntity();
        double verticalFlightSpeed = livingEntity.getDeltaMovement().y * livingEntity.getDeltaMovement().y;

        if(livingEntity instanceof Player player) {
            DragonStateProvider.getOptional(livingEntity).ifPresent(handler -> {
                // Don't use the helper functions here, as isFlying() will return false if the player is grounded
                if (handler.isDragon() && handler.hasFlight() && handler.isWingsSpread() && player.isSprinting()) {
                    if (!enableFlightFallDamage
                            || verticalFlightSpeed <= 1
                            || (livingEntity.isPassenger() && DragonStateProvider.isDragon(livingEntity.getVehicle())))
                    {
                        event.setCanceled(true);
                        return;
                    }

                    double damage = verticalFlightSpeed * (handler.getSize() / 20);
                    damage = Mth.clamp(damage, 0, livingEntity.getHealth() -  1);

                    // See Attributes.SAFE_FALL_DISTANCE
                    final float SAFE_FALL_DISTANCE_DEFAULT_VALUE = 3.0f;
                    event.setDistance((float) damage + SAFE_FALL_DISTANCE_DEFAULT_VALUE);
                }
            });
        }
    }

    public static boolean isFlying(Player player) {
        DragonStateHandler dragonStateHandler = DragonStateProvider.getData(player);
        return dragonStateHandler.hasFlight() && dragonStateHandler.isWingsSpread() && !player.onGround() && !player.isInWater() && !player.isInLava();
    }

    private static Holder<MobEffect> getFlightEffectForType(AbstractDragonType type) {
        if (type.equals(DragonTypes.SEA)) {
            return DSEffects.sea_wings;
        } else if (type.equals(DragonTypes.CAVE)) {
            return DSEffects.cave_wings;
        } else if (type.equals(DragonTypes.FOREST)) {
            return DSEffects.forest_wings;
        }

        return null;
    }

    private static boolean hasCorrectFlightEffect(Player player) {
        DragonStateHandler dragonStateHandler = DragonStateProvider.getData(player);
        Holder<MobEffect> flightEffect = getFlightEffectForType(dragonStateHandler.getType());
        if(flightEffect == null) {
            return false;
        }

        return player.hasEffect(flightEffect);
    }

    private static void clearAllFlightEffects(Player player) {
        player.removeEffect(DSEffects.sea_wings);
        player.removeEffect(DSEffects.cave_wings);
        player.removeEffect(DSEffects.forest_wings);
    }

    @SubscribeEvent
    public static void showFlightIcon(PlayerTickEvent.Pre playerTickEvent) {
        Player player = playerTickEvent.getEntity();
        DragonStateHandler handler = DragonStateProvider.getData(player);
        if (handler.isDragon() && handler.isWingsSpread()) {
            if (!hasCorrectFlightEffect(player)) {
                clearAllFlightEffects(player);
                Holder<MobEffect> flightEffect = getFlightEffectForType(handler.getType());
                if(flightEffect != null) {
                    player.addEffect(new MobEffectInstance(flightEffect, -1, 0, true, false, true));
                }
            }
        } else {
            clearAllFlightEffects(player);
        }
    }

    @SubscribeEvent
    public static void getPreCollisionData(PlayerTickEvent.Pre playerTickEvent) {
        if(playerTickEvent.getEntity().level().isClientSide()) return;

        Player player = playerTickEvent.getEntity();
        DragonStateProvider.getOptional(player).ifPresent(handler -> {
            if (isGliding(player)) {
                handler.preCollisionDeltaMovement = player.getDeltaMovement();
            }
        });
    }

    @SubscribeEvent
    public static void handleWallCollisionsWhenFlying(PlayerTickEvent.Post playerTickEvent) {
        if(playerTickEvent.getEntity().level().isClientSide()) return;

        Player player = playerTickEvent.getEntity();
        DragonStateProvider.getOptional(player).ifPresent(handler -> {
            // This collision code is from Elytra flying. See isFallFlying() section in LivingEntity#travel()
            if (player.horizontalCollision && isGliding(player) && enableCollisionDamage) {
                double lostSpeed = handler.preCollisionDeltaMovement.horizontalDistance() - player.getDeltaMovement().horizontalDistance();
                float damage = (float)(lostSpeed * collisionDamageSpeedFactor - collisionDamageThreshold);
                if (damage > 0.0F) {
                    player.playSound(player.getFallDamageSound((int)damage), 1.0F, 1.0F);
                    player.hurt(player.damageSources().flyIntoWall(), damage);
                    handler.setWingsSpread(false);
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncFlyingStatus.Data(player.getId(), false));
                }
            }
        });
    }

    @SubscribeEvent
    public static void resetFallDistanceWhenFlying(PlayerTickEvent.Pre playerTickEvent) {
        if(playerTickEvent.getEntity().level().isClientSide()) return;

        Player player = playerTickEvent.getEntity();
        DragonStateProvider.getOptional(player).ifPresent(handler -> {
            if (handler.isDragon() && isFlying(player) && !isGliding(player)) {
                player.resetFallDistance();
            }
        });
    }

    @SubscribeEvent
    public static void playerFlightAttacks(PlayerTickEvent.Pre playerTickEvent) {

        Player player = playerTickEvent.getEntity();
        DragonStateProvider.getOptional(player).ifPresent(handler -> {
            if (handler.isDragon()) {
                if (handler.getMovementData().spinAttack > 0) {
                    if (!isFlying(player) && !canSwimSpin(player)) {
                        if (!player.level().isClientSide()) {
                            handler.getMovementData().spinAttack = 0;
                            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncSpinStatus.Data(player.getId(), handler.getMovementData().spinAttack, handler.getMovementData().spinCooldown, handler.getMovementData().spinLearned));
                        }
                    }
                }

                if (isSpin(player)) {
                    int range = 5;
                    List<Entity> entities = player.level().getEntities(null, new AABB(player.position().x - range, player.position().y - range, player.position().z - range, player.position().x + range, player.position().y + range, player.position().z + range));
                    entities.removeIf(e -> e.distanceTo(player) > range);
                    entities.remove(player);
                    entities.removeIf(e -> e instanceof Player && !player.canHarmPlayer((Player) e));
                    for (Entity ent : entities) {
                        if (player.hasPassenger(ent)) {
                            continue;
                        }
                        if (ent instanceof LivingEntity entity) {
                            //Don't hit the same mob multiple times
                            if (entity.getLastHurtByMob() == player && entity.getLastHurtByMobTimestamp() <= entity.tickCount + 5 * 20) {
                                continue;
                            }
                        }
                        player.attack(ent);
                    }

                    handler.getMovementData().spinAttack--;

                    if (!player.level().isClientSide()) {
                        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncSpinStatus.Data(player.getId(), handler.getMovementData().spinAttack, handler.getMovementData().spinCooldown, handler.getMovementData().spinLearned));
                    }
                } else if (handler.getMovementData().spinCooldown > 0) {
                    if (!player.level().isClientSide()) {
                        handler.getMovementData().spinCooldown--;
                        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncSpinStatus.Data(player.getId(), handler.getMovementData().spinAttack, handler.getMovementData().spinCooldown, handler.getMovementData().spinLearned));
                    }
                }
            }
        });
    }

    public static boolean isSpin(Player entity) {
        DragonStateHandler handler = DragonStateProvider.getData(entity);

        if (isFlying(entity) || canSwimSpin(entity)) {
            return handler.getMovementData().spinAttack > 0;
        }

        return false;
    }

    public static boolean canSwimSpin(Player player){
        DragonStateHandler dragonStateHandler = DragonStateProvider.getData(player);
        boolean validSwim = (DragonUtils.isDragonType(dragonStateHandler, DragonTypes.SEA) || DragonUtils.isDragonType(dragonStateHandler, DragonTypes.FOREST)) && player.isInWater() || player.isInLava() && DragonUtils.isDragonType(dragonStateHandler, DragonTypes.CAVE);
        return validSwim && dragonStateHandler.hasFlight() && !player.onGround();
    }

    @ConfigRange(min = 1, max = 60 * 60 * 20)
    @ConfigOption(side = ConfigSide.SERVER, key = "flightHungerTicks", category = "wings", comment = "How many ticks it takes for one hunger point to be drained while flying, this is based on hover flight.")
    public static int flightHungerTicks = 50;

    @SubscribeEvent
    public static void playerFoodExhaustion(PlayerTickEvent.Post playerTickEvent) {
        Player player = playerTickEvent.getEntity();

        DragonStateProvider.getOptional(player).ifPresent(dragonStateHandler -> {
            if (dragonStateHandler.isDragon()) {
                boolean wingsSpread = dragonStateHandler.isWingsSpread();

                if (wingsSpread) {
                    if (isFlying(player)) {
                        if (!player.level().isClientSide()) {
                            if (player.getFoodData().getFoodLevel() <= foldWingsThreshold && !player.isCreative()) {
                                player.sendSystemMessage(Component.translatable("ds.wings.nohunger"));
                                dragonStateHandler.setWingsSpread(false);
                                PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncFlyingStatus.Data(player.getId(), false));
                                return;
                            }
                        }
                        Vec3 delta = player.getDeltaMovement();
                        float moveSpeed = (float) delta.horizontalDistance();
                        float l = 4f / flightHungerTicks;
                        float moveSpeedReq = 1.0F;
                        float minFoodReq = l / 10f;
                        float drain = Math.max(minFoodReq, (float) (Math.min(1.0, Math.max(0, Math.max(moveSpeedReq - moveSpeed, 0) / moveSpeedReq)) * l));
                        AttributeInstance flightStamina;
                        if ((flightStamina = player.getAttribute(DSAttributes.FLIGHT_STAMINA_COST)) != null && flightStamina.getValue() != 0) {
                            drain /= (float) flightStamina.getValue();
                        }

                        player.causeFoodExhaustion(drain);
                    }
                }
            }
        });
    }

    public static boolean isGliding(Player player) {
        DragonStateHandler dragonStateHandler = DragonStateProvider.getData(player);
        boolean hasFood = player.getFoodData().getFoodLevel() > flightHungerThreshold || player.isCreative();
        return hasFood && player.isSprinting() && isFlying(player);
    }

    public static double distanceFromGround(Player player) {
        BlockPos blockHeight = player.level().getHeightmapPos(Types.MOTION_BLOCKING, player.blockPosition());
        int height = blockHeight.getY();
        return Math.max(0, player.position().y - height);
    }
}