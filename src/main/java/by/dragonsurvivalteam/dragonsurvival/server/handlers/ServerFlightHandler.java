package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientFlightHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.network.flight.SyncFlyingStatus;
import by.dragonsurvivalteam.dragonsurvival.network.flight.SyncSpinStatus;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAttributes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

/** Used in pair with {@link ClientFlightHandler} */
@EventBusSubscriber
public class ServerFlightHandler {
    public static final int SPIN_DURATION = Functions.secondsToTicks(0.76);

    @ConfigRange(min = 0.1, max = 1)
    @Translation(key = "flight_speed_multiplier", type = Translation.Type.CONFIGURATION, comments = "Flight speed multiplier")
    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "flight_speed_multiplier")
    public static Double maxFlightSpeed = 0.3;

    @Translation(key = "start_with_flight", type = Translation.Type.CONFIGURATION, comments = {
            "If enabled dragons can fly from the start",
            "If disabled players will have to use the item that grants wings or interact with the ender dragon"
    })
    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "start_with_flight")
    public static Boolean startWithFlight = true;

    @Translation(key = "ender_dragon_interaction", type = Translation.Type.CONFIGURATION, comments = {
            "The ender dragon will be able to grant flight and the spin attack if enabled",
            "If disabled special items will have to be used"
    })
    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "enderDragonGrantsSpin")
    public static Boolean enderDragonInteraction = true;

    @ConfigRange(min = 0, max = 20)
    @Translation(key = "flight_hunger_threshold", type = Translation.Type.CONFIGURATION, comments = "Determines the required food values to be able to fly")
    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "flight_hunger_threshold")
    public static Integer flightHungerThreshold = 6;

    @ConfigRange(min = 0, max = 20)
    @Translation(key = "fold_wings_threshold", type = Translation.Type.CONFIGURATION, comments = "Determines the food values at which the dragon will stop being able to fly mid-flight")
    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "fold_wings_threshold")
    public static Integer foldWingsThreshold = 0;

    @Translation(key = "flight_fall_damage", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable fall damage from falling while in flight")
    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "flight_fall_damage")
    public static Boolean enableFlightFallDamage = true;

    @Translation(key = "lethal_fall_damage", type = Translation.Type.CONFIGURATION, comments = "Determines whether fall damage during flight can be lethal or not - if disabled it will leave the player at half a heart")
    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "lethal_fall_damage")
    public static Boolean lethalFlight = false;

    @Translation(key = "fold_wings_on_land", type = Translation.Type.CONFIGURATION, comments = "If enabled dragons will automatically stop fold their wings (i.e. stop flying) when landing")
    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "fold_wings_on_land")
    public static Boolean foldWingsOnLand = false;

    @ConfigRange(min = 0, max = /* 1 hour */ 3600)
    @Translation(key = "flight_spin_cooldown", type = Translation.Type.CONFIGURATION, comments = "Cooldown (in seconds) of the spin attack during flifght")
    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "flight_spin_cooldown")
    public static Integer flightSpinCooldown = 5;

    @ConfigRange(min = 1, max = /* 1 hour */ 72_000)
    @Translation(key = "flight_hunger_ticks", type = Translation.Type.CONFIGURATION, comments = "Determines the amount of ticks (20 ticks = 1 second) it takes for one hunger point to be drained while flying")
    @ConfigOption(side = ConfigSide.SERVER, key = "flightHungerTicks", category = "flight_hunger_ticks")
    public static int flightHungerTicks = 50;

    @Translation(key = "stable_hover", type = Translation.Type.CONFIGURATION, comments = "If enabled hovering will behave the same as creative flight (i.e. stable flight)")
    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "stable_hover")
    public static boolean stableHover = false;

    /**
     * Sets the fall damage based on flight speed and dragon's size
     */
    @SubscribeEvent
    public static void changeFallDistance(LivingFallEvent event) {
        LivingEntity livingEntity = event.getEntity();
        double flightSpeed = event.getDistance();

        DragonStateProvider.getOptional(livingEntity).ifPresent(dragonStateHandler -> {
            if (dragonStateHandler.isDragon() && dragonStateHandler.hasFlight()) {
                try {
                    if (!enableFlightFallDamage) {
                        event.setCanceled(true);
                        return;
                    }

                    if (flightSpeed <= 2 || dragonStateHandler.isWingsSpread() && !livingEntity.isSprinting() && flightSpeed <= 4) {
                        event.setCanceled(true);
                        return;
                    }

                    if (livingEntity.isPassenger() && DragonStateProvider.isDragon(livingEntity.getVehicle())) {
                        event.setCanceled(true);
                        return;
                    }


                    MobEffectInstance effectinstance = livingEntity.getEffect(MobEffects.JUMP);
                    float f = effectinstance == null ? 0.0F : (float) (effectinstance.getAmplifier() + 1);

                    double damage = livingEntity.getDeltaMovement().lengthSqr() * (dragonStateHandler.getSize() / 20);
                    damage = Mth.clamp(damage, 0, livingEntity.getHealth() - (lethalFlight ? 0 : 1));

                    if (!livingEntity.level().isClientSide() && dragonStateHandler.isWingsSpread()) {
                        event.setDistance((float) Math.floor((damage + 3.0F + f) * event.getDamageMultiplier()));
                    }
                } finally {
                    if (!livingEntity.level().isClientSide()) {
                        if (foldWingsOnLand) {
                            if (dragonStateHandler.isWingsSpread()) {
                                dragonStateHandler.setWingsSpread(false);
                                PacketDistributor.sendToPlayersTrackingEntityAndSelf(livingEntity, new SyncFlyingStatus.Data(livingEntity.getId(), false));
                            }
                        }
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public static void foldWings(PlayerTickEvent.Post tickEvent) {
        Player player = tickEvent.getEntity();
        if (player.level().isClientSide() || !DragonStateProvider.isDragon(player)) {
            return;
        }

        if (!foldWingsOnLand || player.getFoodData().getFoodLevel() <= flightHungerThreshold && !player.isCreative()) {
            return;
        }

        DragonStateHandler dragonStateHandler = DragonStateProvider.getData(player);

        if (dragonStateHandler.isWingsSpread()) {
            player.fallDistance = Math.max(0, player.fallDistance * 0.5f);
        }

        if (dragonStateHandler.hasFlown && player.onGround()) {
            if (dragonStateHandler.isWingsSpread() && player.isCreative()) {
                dragonStateHandler.hasFlown = false;
                dragonStateHandler.setWingsSpread(false);
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncFlyingStatus.Data(player.getId(), false));
            }
        } else {
            if (!dragonStateHandler.hasFlown && isFlying(player)) {
                dragonStateHandler.hasFlown = true;
            }
        }
    }

    public static boolean isFlying(Player player) {
        DragonStateHandler dragonStateHandler = DragonStateProvider.getData(player);
        return dragonStateHandler.hasFlight() && dragonStateHandler.isWingsSpread() && !player.onGround() && !player.isInWater() && !player.isInLava();
    }

    @SubscribeEvent
    public static void playerFlightIcon(PlayerTickEvent.Post playerTickEvent) {
        Player player = playerTickEvent.getEntity();
        DragonStateProvider.getOptional(player).ifPresent(handler -> {
            if (handler.isDragon()) {
                if (player.tickCount % 10 == 0) {
                    if (handler.isWingsSpread()) {
                        if (DragonUtils.isDragonType(player, DragonTypes.SEA)) {
                            player.addEffect(new MobEffectInstance(DSEffects.sea_wings, 500, 0, true, false, true));

                        }
                        if (DragonUtils.isDragonType(player, DragonTypes.CAVE)) {
                            player.addEffect(new MobEffectInstance(DSEffects.cave_wings, 500, 0, true, false, true));

                        }
                        if (DragonUtils.isDragonType(player, DragonTypes.FOREST)) {
                            player.addEffect(new MobEffectInstance(DSEffects.forest_wings, 500, 0, true, false, true));
                        }
                    }
                }
            }

            if (!handler.isDragon() || !handler.isWingsSpread()) {
                if (player.hasEffect(DSEffects.sea_wings)) {
                    player.resetFallDistance();
                    player.removeEffect(DSEffects.sea_wings);
                }
                if (player.hasEffect(DSEffects.cave_wings)) {
                    player.resetFallDistance();
                    player.removeEffect(DSEffects.cave_wings);
                }
                if (player.hasEffect(DSEffects.forest_wings)) {
                    player.resetFallDistance();
                    player.removeEffect(DSEffects.forest_wings);
                }
            }
        });
    }

    @SubscribeEvent
    public static void playerFlightAttacks(PlayerTickEvent.Post playerTickEvent) {

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