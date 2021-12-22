package by.jackraidenph.dragonsurvival.gecko.entity.dragon;

import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.emotes.Emote;
import by.jackraidenph.dragonsurvival.gecko.AnimationTimer;
import by.jackraidenph.dragonsurvival.gecko.CommonTraits;
import by.jackraidenph.dragonsurvival.handlers.ClientSide.ClientEvents;
import by.jackraidenph.dragonsurvival.handlers.DragonSizeHandler;
import by.jackraidenph.dragonsurvival.handlers.ServerSide.ServerFlightHandler;
import by.jackraidenph.dragonsurvival.magic.abilities.Actives.BreathAbilities.BreathAbility;
import by.jackraidenph.dragonsurvival.magic.common.AbilityAnimation;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class DragonEntity extends LivingEntity implements IAnimatable, CommonTraits
{
    AnimationFactory animationFactory = new AnimationFactory(this);
    
    /**
     * This reference must be updated whenever player is remade, for example, when changing dimensions
     */
    public volatile int player;
    
    @Override
    public boolean isMultipartEntity()
    {
        return true;
    }
    
    public DragonEntity(EntityType<? extends LivingEntity> type, World worldIn) {
        super(type, worldIn);
    }
    
    
    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(biteAnimationController);
        animationData.addAnimationController(dragonAnimationController);
    }
    
    CustomTickAnimationController biteAnimationController = new CustomTickAnimationController(this, "bite_controller", this::bitePredicate);
    CustomTickAnimationController dragonAnimationController = new CustomTickAnimationController(this, "controller", this::predicate);
    
    double landDuration = 0;
    final double landAnimationDuration = 2.24 * 20;
    
    
    private <E extends IAnimatable> PlayState bitePredicate(AnimationEvent<E> animationEvent) {
        final PlayerEntity player = getPlayer();
        DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
        AnimationBuilder builder = new AnimationBuilder();
        
        if(handler != null){
            ActiveDragonAbility curCast = handler.getMagic().getCurrentlyCasting();
            
            if(handler.getEmotes().getCurrentEmote() == null) {
                if(curCast instanceof BreathAbility || lastCast instanceof BreathAbility){
                    renderAbility(builder, curCast);
                    animationEvent.getController().setAnimation(builder);
                    return PlayState.CONTINUE;
                }
                if(!ServerFlightHandler.isFlying(player)) {
                    if (handler.getMovementData().bite && !handler.getMovementData().dig) {
                        builder.addAnimation("bite");
                        animationEvent.getController().setAnimation(builder);
                        return PlayState.CONTINUE;
                    }
                }
            }
        }
        
        return PlayState.STOP;
    }
    
    @Override
    public AnimationFactory getFactory() {
        return animationFactory;
    }
    
    ActiveDragonAbility lastCast = null;
    boolean started, ended;
    public boolean neckLocked = false;
    AnimationTimer animationTimer = new AnimationTimer();
    Emote lastEmote;
    
    public float prevZRot;
    public float prevXRot;
    
    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> animationEvent) {
        final PlayerEntity player = getPlayer();
        final AnimationController animationController = animationEvent.getController();
        DragonStateHandler playerStateHandler = DragonStateProvider.getCap(player).orElse(null);
        
        neckLocked = false;
        dragonAnimationController.speed = 1;
        
        AnimationBuilder builder = new AnimationBuilder();
        
        if (player != null && playerStateHandler != null) {
            ActiveDragonAbility curCast = playerStateHandler.getMagic().getCurrentlyCasting();
            
            if(playerStateHandler.getEmotes().getCurrentEmote() != null){
                Emote emote = playerStateHandler.getEmotes().getCurrentEmote();
                
                neckLocked = emote.locksHead;
                dragonAnimationController.speed = emote.speed;
                
               if(emote.animation != null && !emote.animation.isEmpty()) {
                   builder.addAnimation(emote.animation, emote.loops);
               }
               
                lastEmote = emote;
            }
            
            if(!(curCast instanceof BreathAbility) && !(lastCast instanceof BreathAbility)){
                renderAbility(builder, curCast);
            }
            
            Vector3d motio = new Vector3d(player.getX() - player.xo, player.getY() - player.yo, player.getZ() - player.zo);
            boolean isMovingHorizontal = Math.sqrt(Math.pow(motio.x, 2) + Math.pow(motio.z, 2)) > 0.005;
            
            // Main
            if (player.isSleeping()) {
                builder.addAnimation("sleep", true);

            }else if (player.isPassenger()) {
                builder.addAnimation("sit", true);
                
            } else if (player.abilities.flying || ServerFlightHandler.isFlying(player) || landDuration > 0) {
                double preLandDuration = 1;
                double hoverLand = ServerFlightHandler.getLandTime(player, (2.24 + preLandDuration) * 20);
                double fullLand = ServerFlightHandler.getLandTime(player, 2.24 * 20);

                if(ServerFlightHandler.isGliding(player)){
                    landDuration = 0;
                }
                
                if (player.isCrouching() && fullLand != -1 && player.getDeltaMovement().length() < 4 || landDuration > 0) {
                    neckLocked = true;
                    
//                        if (landDuration == 0 && ServerFlightHandler.isFlying(player)) {
//                            landDuration = landAnimationDuration;
//                        }

                    builder.addAnimation("fly_land_end");
                    
                    if(landDuration > 0) {
                        landDuration -= Minecraft.getInstance().getDeltaFrameTime() * dragonAnimationController.speed;
                    }
                    
                } else if (player.isCrouching() && hoverLand != -1 && player.getDeltaMovement().length() < 4) {
                    neckLocked = true;
                    builder.addAnimation("fly_land", true);
                }else{
                    landDuration = 0;
                }
                
                if (ServerFlightHandler.isGliding(player)) {
                    neckLocked = true;
                    if(ServerFlightHandler.isSpin(player)) {
                        builder.addAnimation("fly_spin_fast", true);
                    }else if (player.getDeltaMovement().y < -1) {
                        builder.addAnimation("fly_dive_alt", true);
                    }else if (player.getDeltaMovement().y < -0.25) {
                        builder.addAnimation("fly_dive", true);
                    } else if(player.getDeltaMovement().y > 0.25){
                        dragonAnimationController.speed = 1 + ((player.getDeltaMovement().y / 2) / 5);
                        builder.addAnimation("fly_fast", true);
                    }else{
                        builder.addAnimation("fly_soaring", true);
                    }
                } else {
                    if(ServerFlightHandler.isSpin(player)) {
                        neckLocked = true;
                        builder.addAnimation("fly_spin", true);
                    } else if(player.getDeltaMovement().y > 0.25){
                        dragonAnimationController.speed = 1 + ((player.getDeltaMovement().y / 2) / 5);
                        builder.addAnimation("fly_fast", true);
                    }else{
                        builder.addAnimation("fly", true);
                    }
                }

            }else if (player.getPose() == Pose.SWIMMING) {
                if(ServerFlightHandler.isSpin(player)) {
                    builder.addAnimation("fly_spin_fast", true);
                }else {
                    dragonAnimationController.speed = 1 + ((double)MathHelper.sqrt(player.getDeltaMovement().x * player.getDeltaMovement().x + player.getDeltaMovement().z * player.getDeltaMovement().z) / 4);
                    builder.addAnimation("swim_fast", true);
                }

            }else if ((player.isInLava() || player.isInWaterOrBubble()) && !player.isOnGround()) {
                if(ServerFlightHandler.isSpin(player)) {
                    builder.addAnimation("fly_spin_fast", true);
                }else {
                    dragonAnimationController.speed = 1 + ((double)MathHelper.sqrt(player.getDeltaMovement().x * player.getDeltaMovement().x + player.getDeltaMovement().z * player.getDeltaMovement().z) / 4);
                    builder.addAnimation("swim", true);
                }
                
            }else if (!player.isOnGround() && motio.y() < 0) {
                if ((player.fallDistance <= 4) && !player.onClimbable()) {
                    builder.addAnimation("land", false);
                }

            } else if (ClientEvents.dragonsJumpingTicks.getOrDefault(this.player, 0) > 0) {
                builder.addAnimation("jump", false);
                
            }else if (player.isShiftKeyDown() ||
                      (!DragonSizeHandler.canPoseFit(player, Pose.STANDING)
                       && DragonSizeHandler.canPoseFit(player, Pose.CROUCHING))) {
                // Player is Sneaking
                if (isMovingHorizontal && player.animationSpeed != 0f) {
                    builder.addAnimation("sneak_walk", true);
                    
                } else if (playerStateHandler.getMovementData().dig) {
                    builder.addAnimation("dig_sneak", true);
                    
                } else {
                    builder.addAnimation("sneak", true);
                }
                
            } else if (player.isSprinting()) {
                dragonAnimationController.speed = 1 + ((double)MathHelper.sqrt(player.getDeltaMovement().x * player.getDeltaMovement().x + player.getDeltaMovement().z * player.getDeltaMovement().z) / 4);
                builder.addAnimation("run", true);
                
            }else if (isMovingHorizontal && player.animationSpeed != 0f) {
                dragonAnimationController.speed = 1 + ((double)MathHelper.sqrt(player.getDeltaMovement().x * player.getDeltaMovement().x + player.getDeltaMovement().z * player.getDeltaMovement().z) / 4);

                builder.addAnimation("walk", true);

            }else if (playerStateHandler.getMovementData().dig) {
                builder.addAnimation("dig", true);
            }
        }
    
        builder.addAnimation("idle", true);
        animationController.setAnimation(builder);
        return PlayState.CONTINUE;
    }
    
    private void renderAbility(AnimationBuilder builder, ActiveDragonAbility curCast)
    {
        if(curCast != null && lastCast == null){
            if(curCast.getStartingAnimation() != null){
                AbilityAnimation starAni = curCast.getStartingAnimation();
                neckLocked = starAni.locksNeck;

                animationTimer.trackAnimation(starAni.animationKey);

                if(!started){
                    animationTimer.putAnimation(starAni.animationKey, starAni.duration, builder);
                    started = true;
                }

                builder.addAnimation(starAni.animationKey);

                if (animationTimer.getDuration(starAni.animationKey) <= 0) {
                    lastCast = curCast;
                    started = false;
                }

            }else if(curCast.getLoopingAnimation() != null){
                AbilityAnimation loopingAni = curCast.getLoopingAnimation();
                neckLocked = loopingAni.locksNeck;

                lastCast = curCast;
                builder.addAnimation(loopingAni.animationKey, true);
            }
        }else if(curCast != null && lastCast != null){
            lastCast = curCast;

            if(curCast.getLoopingAnimation() != null) {
                AbilityAnimation loopingAni = curCast.getLoopingAnimation();
                neckLocked = loopingAni.locksNeck;
                builder.addAnimation(loopingAni.animationKey, true);
            }
        }else if(curCast == null && lastCast != null){
            if(lastCast.getStoppingAnimation() != null) {
                AbilityAnimation stopAni = lastCast.getStoppingAnimation();
                neckLocked = stopAni.locksNeck;

                animationTimer.trackAnimation(stopAni.animationKey);

                if(!ended){
                    animationTimer.putAnimation(stopAni.animationKey, stopAni.duration, builder);
                    ended = true;
                }

                builder.addAnimation(stopAni.animationKey);

                if (animationTimer.getDuration(stopAni.animationKey) <= 0) {
                    lastCast = null;
                    ended = false;
                    neckLocked = false;
                }
            }else{
                lastCast = null;
                neckLocked = false;
            }
        }else{
            lastCast = null;
            neckLocked = false;
        }
    }
    
    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return getPlayer().getArmorSlots();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlotType slotIn) {
        return getPlayer().getItemBySlot(slotIn);
    }

    @Override
    public void setItemSlot(EquipmentSlotType slotIn, ItemStack stack) {
        getPlayer().setItemSlot(slotIn, stack);
    }

    @Override
    public HandSide getMainArm() {
        return getPlayer().getMainArm();
    }

    public PlayerEntity getPlayer() {
        return (PlayerEntity) level.getEntity(player);
    }
}
