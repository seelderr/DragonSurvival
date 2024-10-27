package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.objects.DragonMovementData;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.HunterHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.DSEntityTypeTags;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements ICapabilityProvider<Entity, Void, DragonStateHandler> {
    /** Correctly position the passenger when riding a player dragon */
    @Inject(method = "positionRider(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$MoveFunction;)V", at = @At(value = "HEAD"), cancellable = true)
    private void dragonSurvival$positionRider(Entity entity, Entity.MoveFunction move, CallbackInfo callback) {
        if (!((Entity) (Object) this instanceof Player player) || !(entity instanceof Player passenger) || !hasPassenger(passenger)) {
            return;
        }

        DragonStateProvider.getOptional(player).ifPresent(handler -> {
            if (!handler.isDragon()) {
                return;
            }

            DragonMovementData movementData = handler.getMovementData();
            Vec3 originalPassPos = player.getPassengerRidingPosition(player);
            double size = DragonStateProvider.getData(passenger).getSize();
            double heightOffset = size > ServerConfig.DEFAULT_MAX_GROWTH_SIZE ? -0.55 : -0.15 - size / DragonLevel.ADULT.size * 0.2;

            Vec3 offsetFromBb = new Vec3(0, heightOffset, -1.4 * player.getBbWidth());
            Vec3 offsetFromCenter = originalPassPos.subtract(player.position());
            offsetFromCenter = offsetFromCenter.xRot((float) Math.toRadians(movementData.prevXRot * 1.5)).zRot(-(float) Math.toRadians(movementData.prevZRot * 90));
            Vec3 totalOffset = offsetFromCenter.add(offsetFromBb).yRot(-(float) Math.toRadians(movementData.bodyYawLastFrame));
            Vec3 passPos = player.position().add(totalOffset);

            move.accept(passenger, passPos.x(), passPos.y(), passPos.z());
            player.onPassengerTurned(passenger);

            callback.cancel();
        });
    }

    /** Correctly rotate the passenger when riding a dragon */
    @SuppressWarnings("ConstantValue") // the if statement checks are valid
    @Inject(method = "onPassengerTurned(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
    private void dragonSurvival$onPassengerTurned(Entity entity, CallbackInfo callback) {
        if (!(entity instanceof Player passenger) || !((Entity) (Object) this instanceof Player vehicle) || !passenger.level().isClientSide()) {
            return;
        }

        DragonStateProvider.getOptional(vehicle).ifPresent(vehicleHandler -> {
            if (!vehicleHandler.isDragon()) {
                return;
            }

            DragonStateProvider.getOptional(passenger).ifPresent(passengerHandler -> {
                DragonMovementData vehicleMovement = vehicleHandler.getMovementData();

                if (passengerHandler.isDragon()) {
                    DragonMovementData passengerMovement = passengerHandler.getMovementData();
                    float facing = (float) Mth.wrapDegrees(passenger.getYRot() - vehicleMovement.bodyYawLastFrame);
                    float facingClamped = Mth.clamp(facing, -150.0F, 150.0F);
                    passenger.yRotO += facingClamped - facing + vehicle.yRotO;
                    passengerMovement.bodyYaw = vehicleMovement.bodyYawLastFrame;
                    passengerMovement.headYaw = -facing;
                    passenger.setYRot((float) (passenger.getYRot() + facingClamped - facing + (vehicleMovement.bodyYawLastFrame - vehicleMovement.bodyYaw)));
                } else {
                    float facing = (float) Mth.wrapDegrees(passenger.getYRot() - vehicleMovement.bodyYawLastFrame);
                    float facingClamped = Mth.clamp(facing, -120.0F, 120.0F);
                    passenger.yRotO += facingClamped - facing + vehicle.yRotO;
                    passenger.setYBodyRot((float) (passenger.getYRot() + facingClamped - facing + (vehicleMovement.bodyYawLastFrame - vehicleMovement.bodyYaw)));
                    passenger.setYRot((float) (passenger.getYRot() + facingClamped - facing + (vehicleMovement.bodyYawLastFrame - vehicleMovement.bodyYaw)));
                    passenger.setYHeadRot(passenger.getYRot());
                }
            });
        });
    }

    /** Don't show fire animation (when burning) when being a cave dragon when rendered in the inventory */
    @Inject(method = "displayFireAnimation()Z", at = @At(value = "HEAD"), cancellable = true)
    private void dragonSurvival$hideCaveDragonFireAnimation(CallbackInfoReturnable<Boolean> callback) {
        DragonStateProvider.getOptional((Entity) (Object) this).ifPresent(handler -> {
            if (handler.isDragon() && DragonUtils.isDragonType(handler, DragonTypes.CAVE)) {
                callback.setReturnValue(false);
            }
        });
    }

    @Inject(method = "isVisuallyCrawling()Z", at = @At(value = "HEAD"), cancellable = true)
    public void dragonSurvival$isDragonVisuallyCrawling(CallbackInfoReturnable<Boolean> callback) {
        if (DragonStateProvider.isDragon((Entity) (Object) this)) {
            callback.setReturnValue(false);
        }
    }

    /** Prevent dragons from riding certain vehicles */
    @SuppressWarnings("ConstantValue") // the if statement checks are valid
    @ModifyReturnValue(method = "canRide", at = @At(value = "RETURN"))
    public boolean dragonSurvival$canRide(boolean original, @Local(argsOnly = true, ordinal = 0) Entity entity) {
        if (ServerConfig.ridingBlacklist && DragonStateProvider.isDragon((Entity) (Object) this) && /* Still allow riding dragons */ !DragonStateProvider.isDragon(entity)) {
            return entity.getType().is(DSEntityTypeTags.VEHICLE_WHITELIST);
        }

        return original;
    }

    /** To just skip rendering entirely instead of rendering with a 0 alpha value */
    @SuppressWarnings("ConstantValue") // the if statement checks are valid
    @ModifyReturnValue(method = "isInvisible", at = @At("RETURN"))
    private boolean dragonSurvival$enableHunterStacksInvisibility(boolean isInvisible) {
        if (isInvisible) {
            return true;
        }

        if ((Object) this instanceof Player player && DragonStateProvider.getData(player).hasMaxHunterStacks()) {
            // With max. stacks the visibility value is set to 0 anyway so this shouldn't affect actual gameplay features
            return HunterHandler.calculateAlpha(player) == 0;
        }

        return false;
    }

    @Shadow
    public boolean hasPassenger(Entity entity) {
        throw new IllegalStateException("Mixin failed to shadow hasPassenger()");
    }

    @Shadow
    public double getX() {
        throw new IllegalStateException("Mixin failed to shadow getX()");
    }

    @Shadow
    public double getY() {
        throw new IllegalStateException("Mixin failed to shadow getY()");
    }

    @Shadow
    public double getZ() {
        throw new IllegalStateException("Mixin failed to shadow getZ()");
    }
}