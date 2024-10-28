package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonSizeHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.util.BlockPosHelper;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.EnchantmentUtils;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Objects;

import static by.dragonsurvivalteam.dragonsurvival.registry.DSModifiers.SLOW_FALLING;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    /**
     * Add -0.07 to 0.08, so we get the vanilla default of 0.01
     */
    @Unique private final static AttributeModifier dragonSurvival$SLOW_FALL_MOD = new AttributeModifier(SLOW_FALLING, -0.07, AttributeModifier.Operation.ADD_VALUE);

    protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    public void isInvulnerableTo(DamageSource source, CallbackInfoReturnable<Boolean> callback) {
        if (ServerConfig.disableSuffocation && source == damageSources().inWall() && DragonStateProvider.isDragon(this)) {
            callback.setReturnValue(true);
        }
    }

    @Inject(method = "isImmobile", at = @At("HEAD"), cancellable = true)
    private void castMovement(CallbackInfoReturnable<Boolean> callback) {
        DragonStateHandler handler = DragonStateProvider.getData((Player) (Object) this);

        if (!isDeadOrDying() && !isSleeping()) {
            if (!ServerConfig.canMoveWhileCasting) {
                ActiveDragonAbility casting = handler.getMagicData().getCurrentlyCasting();

                if (casting != null && casting.requiresStationaryCasting()) {
                    callback.setReturnValue(true);
                }
            }

            if (!ServerConfig.canMoveInEmote && Arrays.stream(handler.getEmoteData().currentEmotes).noneMatch(Objects::nonNull)) {
                callback.setReturnValue(true);
            }
        }
    }

    /**
     * Allow treasure blocks to trigger sleep logic
     */
    @Inject(method = "isSleepingLongEnough", at = @At("HEAD"), cancellable = true)
    public void isSleepingLongEnough(CallbackInfoReturnable<Boolean> callback) {
        DragonStateProvider.getOptional(this).ifPresent(handler -> {
            if (handler.isDragon() && handler.treasureResting && handler.treasureSleepTimer >= 100) {
                callback.setReturnValue(true);
            }
        });
    }

    /**
     * Make sure dragon hitboxes are considered here
     */
    @ModifyReturnValue(method = "canPlayerFitWithinBlocksAndEntitiesWhen", at = @At("RETURN"))
    private boolean considerDragonPosesInCanPlayerFit(boolean returnValue, @Local(argsOnly = true) Pose pose) {
        if (DragonStateProvider.isDragon(this)) {
            return DragonSizeHandler.canPoseFit(this, pose);
        } else {
            return returnValue;
        }
    }

    /**
     * Enable cave dragons to properly swim in lava
     */
    @SuppressWarnings("ConstantValue") // it's not always false
    @Inject(method = "travel", at = @At("HEAD"))
    public void travel(Vec3 travelVector, CallbackInfo callback) {
        DragonStateProvider.getOptional(this).ifPresent(handler -> {
            if (!handler.isDragon()) {
                return;
            }

            boolean handleLavaSwimming = ServerConfig.bonusesEnabled && ServerConfig.caveLavaSwimming && DragonUtils.isDragonType(handler, DragonTypes.CAVE);

            if (handleLavaSwimming && /* Only triggers when sprinting when in lava */ DragonSizeHandler.getOverridePose((Player) (Object) this) == Pose.SWIMMING || isSwimming() && !isPassenger()) {
                // Mostly a copy from vanilla Player#travel
                double lookY = getLookAngle().y;
                double yModifier = lookY < -0.2 ? 0.185 : 0.06;

                if (lookY <= 0 || jumping || !level().getBlockState(BlockPosHelper.get(getX(), getY() + 1 - 0.1, getZ())).getFluidState().isEmpty()) {
                    Vec3 deltaMovement = getDeltaMovement();
                    setDeltaMovement(deltaMovement.add(0, (lookY - deltaMovement.y) * yModifier, 0));
                }

                // Mostly a copy from vanilla LivingEntity#travel (but adjusted for lava)
                if (isEffectiveAi() || isControlledByLocalInstance()) {
                    // TODO :: Unsure what this slow falling / gravity code actually does - doesn't seem to affect much if anything
                    AttributeInstance gravity = getAttribute(Attributes.GRAVITY);
                    boolean isFalling = getDeltaMovement().y <= 0;

                    if (isFalling && hasEffect(MobEffects.SLOW_FALLING)) {
                        if (gravity != null && !gravity.hasModifier(SLOW_FALLING)) {
                            gravity.addTransientModifier(dragonSurvival$SLOW_FALL_MOD);
                        }

                        resetFallDistance();
                    } else if (gravity != null && gravity.hasModifier(SLOW_FALLING)) {
                        gravity.removeModifier(SLOW_FALLING);
                    }

                    double gravityValue = gravity != null ? gravity.getValue() : 0.08;

                    if (handleLavaSwimming && isInLava() && isAffectedByFluids() && !canStandOnFluid(level().getFluidState(blockPosition()))) {
                        double oldY = getY();
                        float speedModifier = isSprinting() ? 0.9f : getWaterSlowDown();
                        float swimSpeed = 0.05f;
                        // FIXME :: Use Attributes#WATER_MOVEMENT_EFFICIENCY instead
                        float swimSpeedModifier = Math.min(3, EnchantmentUtils.getLevel(this, Enchantments.DEPTH_STRIDER));

                        if (!onGround()) {
                            swimSpeedModifier *= 0.5f;
                        }

                        if (swimSpeedModifier > 0) {
                            speedModifier += (0.54600006f - speedModifier) * swimSpeedModifier / 2.5f;
                            swimSpeed += (getSpeed() - swimSpeed) * swimSpeedModifier / 2.5f;
                        }

                        if (hasEffect(MobEffects.DOLPHINS_GRACE)) {
                            speedModifier = 0.96f;
                        }

                        swimSpeed *= (float) getAttributeValue(NeoForgeMod.SWIM_SPEED);
                        moveRelative(swimSpeed, travelVector);
                        move(MoverType.SELF, getDeltaMovement());
                        Vec3 deltaMovement = getDeltaMovement();

                        if (horizontalCollision && onClimbable()) {
                            deltaMovement = new Vec3(deltaMovement.x, 0.2, deltaMovement.z);
                        }

                        setDeltaMovement(deltaMovement.multiply(speedModifier, 0.8, speedModifier));
                        Vec3 adjustedMovement = getFluidFallingAdjustedMovement(gravityValue, isFalling, getDeltaMovement());
                        setDeltaMovement(adjustedMovement);

                        if (horizontalCollision && isFree(adjustedMovement.x, adjustedMovement.y + 0.6 - getY() + oldY, adjustedMovement.z)) {
                            setDeltaMovement(adjustedMovement.x, 0.3, adjustedMovement.z);
                        }
                    }
                }

                calculateEntityAnimation(this instanceof FlyingAnimal);
            }
        });
    }
}