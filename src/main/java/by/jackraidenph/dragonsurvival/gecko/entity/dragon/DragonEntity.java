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
        animationData.addAnimationController(new AnimationController<>(this, "bite_controller", 2, this::bitePredicate));
        animationData.addAnimationController(new AnimationController<>(this, "controller", 2, this::predicate));
        animationData.addAnimationController(landingController);
    }
    
    AnimationBuilder landingBuilder = new AnimationBuilder();
    LandingAnimationController landingController = new LandingAnimationController(this, this::landPredicate);
    double landDuration = 0;
    final double landAnimationDuration = 2.24 * 20;
    
    private <E extends IAnimatable> PlayState landPredicate(AnimationEvent<E> animationEvent) {
        final PlayerEntity player = getPlayer();
        DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
        landingBuilder = new AnimationBuilder();
        if(handler != null){
            double preLandDuration = 1;
            double hoverLand = ServerFlightHandler.getLandTime(player, (2.24 + preLandDuration) * 20);
            double fullLand = ServerFlightHandler.getLandTime(player, 2.24 * 20);
            landingController.speed = 1;
            
            if(landingBuilder.getRawAnimationList().size() == 0 || true) {
                neckLocked = true;
                
                if(ServerFlightHandler.isGliding(player)){
                    landDuration = 0;
                    return PlayState.STOP;
                }
                
                if(landDuration == 0 && fullLand != -1 && fullLand < (landAnimationDuration / 2)){
                    return PlayState.STOP;
                }
                
                if (player.isCrouching() && fullLand != -1 && player.getDeltaMovement().length() < 4 || landDuration > 0) {
                    if (landDuration == 0) {
                        landDuration = landAnimationDuration;
                    }
        
                    if(fullLand == -1 && landDuration < (landAnimationDuration / 4)){
                        landDuration = 0;
                    }
                    
                    if (fullLand > 0 && fullLand < (landDuration)) {
                        double dif = landDuration / fullLand;
                        landingController.speed = Math.max(0, dif);
                    }
        
                    landingBuilder.addAnimation("fly_land_end");
                    landDuration -= Math.max(0.1, Minecraft.getInstance().getDeltaFrameTime() * landingController.speed);
                } else if (player.isCrouching() && hoverLand != -1 && player.getDeltaMovement().length() < 4) {
                    neckLocked = true;
    
                    landingBuilder.addAnimation("fly_land", true);
                }else{
                    landDuration = 0;
                    return PlayState.STOP;
                }
            }
            
            animationEvent.getController().setAnimation(landingBuilder);
            return PlayState.CONTINUE;
        }
        
        return PlayState.STOP;
    }
    
    private <E extends IAnimatable> PlayState bitePredicate(AnimationEvent<E> animationEvent) {
        if(landingBuilder.getRawAnimationList().size() > 0){
            return PlayState.STOP;
        }
        
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
    
    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> animationEvent) {
        if(landingBuilder.getRawAnimationList().size() > 0){
            return PlayState.STOP;
        }
        
        final PlayerEntity player = getPlayer();
        final AnimationController animationController = animationEvent.getController();
        
        neckLocked = false;
        
        AnimationBuilder builder = new AnimationBuilder();
        
        if (player != null) {
            DragonStateProvider.getCap(player).ifPresent(playerStateHandler -> {
                ActiveDragonAbility curCast = playerStateHandler.getMagic().getCurrentlyCasting();
                
                if(playerStateHandler.getEmotes().getCurrentEmote() != null){
                    neckLocked = playerStateHandler.getEmotes().getCurrentEmote().locksHead;
                    
                   if(playerStateHandler.getEmotes().getCurrentEmote().animation != null && !playerStateHandler.getEmotes().getCurrentEmote().animation.isEmpty()) {
                       builder.addAnimation(playerStateHandler.getEmotes().getCurrentEmote().animation, playerStateHandler.getEmotes().getCurrentEmote().loops);
                   }
                   
                    lastEmote = playerStateHandler.getEmotes().getCurrentEmote();
                   
                   if(playerStateHandler.getEmotes().getCurrentEmote().animation != null && !playerStateHandler.getEmotes().getCurrentEmote().animation.isEmpty()) {
                       return;
                   }
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
                    
                } else if (player.abilities.flying || ServerFlightHandler.isFlying(player)) {
                    
                    if (ServerFlightHandler.isGliding(player)) {
                        neckLocked = true;
                        if(ServerFlightHandler.isSpin(player)) {
                            builder.addAnimation("fly_spin_fast", true);
                        }else if (player.getDeltaMovement().y < -1) {
                            builder.addAnimation("fly_dive_alt", true);
                        }else if (player.getDeltaMovement().y < -0.25) {
                            builder.addAnimation("fly_dive", true);
                        } else if(player.getDeltaMovement().y > 0.25){
                            builder.addAnimation("fly_fast", true);
                        }else{
                            builder.addAnimation("fly_soaring", true);
                        }
                    } else {
                        neckLocked = true;
                        
                        if(ServerFlightHandler.isSpin(player)) {
                            builder.addAnimation("fly_spin", true);
                        } else if(player.getDeltaMovement().y > 0.25){
                                builder.addAnimation("fly_fast", true);
                        }else{
                            builder.addAnimation("fly", true);
                            neckLocked = false;
                        }
                    }
    
                }else if (player.getPose() == Pose.SWIMMING) {
                    if(ServerFlightHandler.isSpin(player)) {
                        builder.addAnimation("fly_spin_fast", true);
                    }else {
                        builder.addAnimation("swim_fast", true);
                    }
    
                }else if ((player.isInLava() || player.isInWaterOrBubble()) && !player.isOnGround()) {
                    if(ServerFlightHandler.isSpin(player)) {
                        builder.addAnimation("fly_spin_fast", true);
                    }else {
                        builder.addAnimation("swim", true);
                    }
                    
                }else if (!player.isOnGround() && motio.y() < 0) {
                    if(player.fallDistance > 4 || player.onClimbable()){
                        builder.addAnimation("idle", true);
                    }else {
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
                    builder.addAnimation("run", true);
                    
                }else if (isMovingHorizontal && player.animationSpeed != 0f) {
                    builder.addAnimation("walk", true);
    
                }else if (playerStateHandler.getMovementData().dig) {
                    builder.addAnimation("dig", true);
                }else {
                    builder.addAnimation("idle", true);
                }
            });
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
