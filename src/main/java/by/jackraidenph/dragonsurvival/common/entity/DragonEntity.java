package by.jackraidenph.dragonsurvival.common.entity;

import by.jackraidenph.dragonsurvival.client.emotes.Emote;
import by.jackraidenph.dragonsurvival.client.handlers.ClientEvents;
import by.jackraidenph.dragonsurvival.client.render.util.AnimationTimer;
import by.jackraidenph.dragonsurvival.client.render.util.CommonTraits;
import by.jackraidenph.dragonsurvival.client.render.util.CustomTickAnimationController;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.handlers.DragonSizeHandler;
import by.jackraidenph.dragonsurvival.common.magic.common.AbilityAnimation;
import by.jackraidenph.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.common.magic.common.ISecondAnimation;
import by.jackraidenph.dragonsurvival.server.handlers.ServerFlightHandler;
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
import software.bernie.geckolib3.core.AnimationState;
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
    
    public DragonEntity(EntityType<? extends LivingEntity> type, World worldIn) {
        super(type, worldIn);
    }
    
    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(tailController);
        animationData.addAnimationController(biteAnimationController);
        animationData.addAnimationController(emoteController);
        animationData.addAnimationController(dragonAnimationController);
        //animationData.addAnimationController(headController);
    }
    
    CustomTickAnimationController tailController = new CustomTickAnimationController(this, "5", 10, this::tailPredicate);
    CustomTickAnimationController headController = new CustomTickAnimationController(this, "4", 10, this::headPredicate);
    CustomTickAnimationController emoteController = new CustomTickAnimationController(this, "2", 2, this::emotePredicate);
    CustomTickAnimationController biteAnimationController = new CustomTickAnimationController(this, "5", 2, this::bitePredicate);
    CustomTickAnimationController dragonAnimationController = new CustomTickAnimationController(this, "3", 2, this::predicate);
    
    private <E extends IAnimatable> PlayState tailPredicate(AnimationEvent<E> animationEvent) {
        if(!tailLocked) {
            animationEvent.getController().setAnimation(new AnimationBuilder().addAnimation("tail_turn", true));
            return PlayState.CONTINUE;
        }else{
            return PlayState.STOP;
        }
    }
    
    private <E extends IAnimatable> PlayState headPredicate(AnimationEvent<E> animationEvent) {
        if(!neckLocked) {
            animationEvent.getController().setAnimation(new AnimationBuilder().addAnimation("bottom_turn", true));
            return PlayState.CONTINUE;
        }else{
            return PlayState.STOP;
        }
    }
    
    private <E extends IAnimatable> PlayState bitePredicate(AnimationEvent<E> animationEvent) {
        final PlayerEntity player = getPlayer();
        DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
        AnimationBuilder builder = new AnimationBuilder();
    
        if(handler != null){
            ActiveDragonAbility curCast = handler.getMagic().getCurrentlyCasting();
            
            if(handler.getEmotes().getCurrentEmote() != null) {
                return PlayState.STOP;
            }
            
            if(curCast instanceof ISecondAnimation || lastCast instanceof ISecondAnimation){
                renderAbility(builder, curCast);
                animationEvent.getController().setAnimation(builder);
                return PlayState.CONTINUE;
            }
            
            if(!ServerFlightHandler.isFlying(player)) {
                if (handler.getMovementData().bite && !handler.getMovementData().dig) {
                    builder.addAnimation("bite", false);
                    animationEvent.getController().setAnimation(builder);
                    return PlayState.CONTINUE;
                }
            }
        }
        
        if(animationEvent.getController().getAnimationState() != AnimationState.Stopped){
            return PlayState.CONTINUE;
        }
        
        return PlayState.STOP;
    }
    
    private <E extends IAnimatable> PlayState emotePredicate(AnimationEvent<E> animationEvent) {
        final PlayerEntity player = getPlayer();
        DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
    
        if(handler != null){
            if(handler.getEmotes().getCurrentEmote() != null){
                Emote emote = handler.getEmotes().getCurrentEmote();
        
                neckLocked = emote.locksHead;
                tailLocked = emote.locksTail;
    
                dragonAnimationController.speed = emote.speed;
        
                if(emote.animation != null && !emote.animation.isEmpty()) {
                    animationEvent.getController().setAnimation(new AnimationBuilder().addAnimation(emote.animation, emote.loops));
                }
        
                lastEmote = emote;
                return PlayState.CONTINUE;
            }
        }
        
        return PlayState.STOP;
    }
    
    @Override
    public AnimationFactory getFactory() {
        return animationFactory;
    }
    public boolean neckLocked = false;
    public boolean tailLocked = false;
    
    ActiveDragonAbility lastCast = null;
    boolean started, ended;
    AnimationTimer animationTimer = new AnimationTimer();
    Emote lastEmote;
    
    public float prevZRot;
    public float prevXRot;
    
    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> animationEvent) {
        final PlayerEntity player = getPlayer();
        final AnimationController animationController = animationEvent.getController();
        DragonStateHandler playerStateHandler = DragonStateProvider.getCap(player).orElse(null);
        
        AnimationBuilder builder = new AnimationBuilder();
    
        dragonAnimationController.speed = 1;
        
        if (player == null || playerStateHandler == null || emoteController.getAnimationState() != AnimationState.Stopped) {
            return PlayState.STOP;
        }
    
        neckLocked = false;
        tailLocked = false;
    
        
        ActiveDragonAbility curCast = playerStateHandler.getMagic().getCurrentlyCasting();
        
        if(!(curCast instanceof ISecondAnimation) && !(lastCast instanceof ISecondAnimation)){
            renderAbility(builder, curCast);
        }
        
        Vector3d motio = new Vector3d(player.getX() - player.xo, player.getY() - player.yo, player.getZ() - player.zo);
        boolean isMovingHorizontal = Math.sqrt(Math.pow(motio.x, 2) + Math.pow(motio.z, 2)) > 0.005;
        
        // Main
        if (player.isSleeping()) {
            builder.addAnimation("sleep", true);
        }else if (player.isPassenger()) {
            builder.addAnimation("sit", true);
        }else if (player.abilities.flying || ServerFlightHandler.isFlying(player)) {
            double preLandDuration = 1;
            double hoverLand = ServerFlightHandler.getLandTime(player, (2.24 + preLandDuration) * 20);
            double fullLand = ServerFlightHandler.getLandTime(player, 2.24 * 20);
            
            if (player.isCrouching() && fullLand != -1 && player.getDeltaMovement().length() < 4) {
                neckLocked = true;
                
                builder.addAnimation("fly_land_end");
            } else if (player.isCrouching() && hoverLand != -1 && player.getDeltaMovement().length() < 4) {
                neckLocked = true;
                builder.addAnimation("fly_land", true);
            }
            
            if (ServerFlightHandler.isGliding(player)) {
                neckLocked = true;
                if(ServerFlightHandler.isSpin(player)) {
                    tailLocked = true;
    
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
                    tailLocked = true;
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
                neckLocked = true;
                tailLocked = true;
                builder.addAnimation("fly_spin_fast", true);
            }else {
                dragonAnimationController.speed = 1 + ((double)MathHelper.sqrt(player.getDeltaMovement().x * player.getDeltaMovement().x + player.getDeltaMovement().z * player.getDeltaMovement().z) / 10);
                builder.addAnimation("swim_fast", true);
            }
        }else if ((player.isInLava() || player.isInWaterOrBubble()) && !player.isOnGround()) {
            if(ServerFlightHandler.isSpin(player)) {
                neckLocked = true;
                tailLocked = true;
                builder.addAnimation("fly_spin_fast", true);
            }else {
                dragonAnimationController.speed = 1 + ((double)MathHelper.sqrt(player.getDeltaMovement().x * player.getDeltaMovement().x + player.getDeltaMovement().z * player.getDeltaMovement().z) / 10);
                builder.addAnimation("swim", true);
            }
        }else if (!player.isOnGround() && motio.y() < 0) {
            if ((player.fallDistance <= 4) && !player.onClimbable()) {
                builder.addAnimation("land", false);
            }

        }else if (ClientEvents.dragonsJumpingTicks.getOrDefault(this.player, 0) > 0) {
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
            
        }else if (player.isSprinting()) {
            dragonAnimationController.speed = 1 + ((double)MathHelper.sqrt(player.getDeltaMovement().x * player.getDeltaMovement().x + player.getDeltaMovement().z * player.getDeltaMovement().z) / 10);
            builder.addAnimation("run", true);
            
        }else if (isMovingHorizontal && player.animationSpeed != 0f) {
            dragonAnimationController.speed = 1 + ((double)MathHelper.sqrt(player.getDeltaMovement().x * player.getDeltaMovement().x + player.getDeltaMovement().z * player.getDeltaMovement().z) / 10);
            builder.addAnimation("walk", true);

        }else if (playerStateHandler.getMovementData().dig) {
            builder.addAnimation("dig", true);
            
        }
        
        if(animationEvent.getController().getCurrentAnimation() == null || builder.getRawAnimationList().size() <= 0){
            builder.addAnimation("idle", true);
        }
        
        animationController.setAnimation(builder);
        return PlayState.CONTINUE;
    }
    
    private void renderAbility(AnimationBuilder builder, ActiveDragonAbility curCast)
    {
        if(curCast != null && lastCast == null){
            if(curCast.getStartingAnimation() != null){
                AbilityAnimation starAni = curCast.getStartingAnimation();
                neckLocked = starAni.locksNeck;
                tailLocked = starAni.locksTail;

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
                tailLocked = loopingAni.locksTail;

                lastCast = curCast;
                builder.addAnimation(loopingAni.animationKey, true);
            }
        }else if(curCast != null && lastCast != null){
            lastCast = curCast;

            if(curCast.getLoopingAnimation() != null) {
                AbilityAnimation loopingAni = curCast.getLoopingAnimation();
                neckLocked = loopingAni.locksNeck;
                tailLocked = loopingAni.locksTail;
    
                builder.addAnimation(loopingAni.animationKey, true);
            }
        }else if(curCast == null && lastCast != null){
            if(lastCast.getStoppingAnimation() != null) {
                AbilityAnimation stopAni = lastCast.getStoppingAnimation();
                neckLocked = stopAni.locksNeck;
                tailLocked = stopAni.locksTail;

                animationTimer.trackAnimation(stopAni.animationKey);

                if(!ended){
                    animationTimer.putAnimation(stopAni.animationKey, stopAni.duration, builder);
                    ended = true;
                }

                builder.addAnimation(stopAni.animationKey);

                if (animationTimer.getDuration(stopAni.animationKey) <= 0) {
                    lastCast = null;
                    ended = false;
                }
            }else{
                lastCast = null;
            }
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
