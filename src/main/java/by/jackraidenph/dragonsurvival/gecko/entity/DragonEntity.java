package by.jackraidenph.dragonsurvival.gecko.entity;

import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.gecko.AnimationTimer;
import by.jackraidenph.dragonsurvival.gecko.CommonTraits;
import by.jackraidenph.dragonsurvival.handlers.ClientSide.ClientDragonRender;
import by.jackraidenph.dragonsurvival.handlers.ClientSide.ClientEvents;
import by.jackraidenph.dragonsurvival.handlers.DragonSizeHandler;
import by.jackraidenph.dragonsurvival.magic.common.AbilityAnimation;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
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

    public DragonEntity(EntityType<? extends LivingEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller", 2, this::predicate));
        animationData.addAnimationController(new AnimationController<>(this, "bite_controller", 0, this::bitePredicate));
    }
    

    static int biteEndingTicks = 150;
    private int biteEnd = biteEndingTicks;
    private <E extends IAnimatable> PlayState bitePredicate(AnimationEvent<E> animationEvent) {
        final PlayerEntity player = getPlayer();
        DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
        AnimationBuilder builder = new AnimationBuilder();
    
        if(handler != null){
            if(handler.getMovementData().bite && biteEnd <= biteEndingTicks || biteEnd > 0 && biteEnd <= biteEndingTicks){
                builder.addAnimation("bite");
    
                biteEnd++;
                
                animationEvent.getController().setAnimation(builder);
                return PlayState.CONTINUE;
            }
        }
        
        biteEnd = 0;
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
    
    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> animationEvent) {
        final PlayerEntity player = getPlayer();
        final AnimationController animationController = animationEvent.getController();
        
        AnimationBuilder builder = new AnimationBuilder();
        
        if (player != null) {
            DragonStateProvider.getCap(player).ifPresent(playerStateHandler -> {
                ActiveDragonAbility curCast = playerStateHandler.getCurrentlyCasting();
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
                
                Vector3d motio = new Vector3d(player.getX() - player.xo, player.getY() - player.yo, player.getZ() - player.zo);
                boolean isMovingHorizontal = Math.sqrt(Math.pow(motio.x, 2) + Math.pow(motio.z, 2)) > 0.005;
                
                // Main
                if (player.isSleeping()) {
                    builder.addAnimation("sleep", true);
    
                }else if (player.isPassenger()) {
                    builder.addAnimation("sit", true);
                    
                }else if (player.isPassenger()){
                    builder.addAnimation("idle", true); // TODO: Passenger animation for riding entities
                
                }else if (player.getPose() == Pose.SWIMMING) {
                    builder.addAnimation("swim_fast", true);
                    
                }else if ((player.isInLava() || player.isInWaterOrBubble()) && !player.isOnGround()) {
                    builder.addAnimation("swim", true);
                    
                } else if ((player.abilities.flying || ClientDragonRender.dragonsFlying.getOrDefault(player.getId(), false))
                           && !player.isOnGround() && !player.isInWater()
                           && !player.isInLava() && player.getCapability(DragonStateProvider.DRAGON_CAPABILITY).orElse(null).hasWings()) {
                    builder.addAnimation("fly", true);
                    
                }else if (!player.isOnGround() && motio.y() < 0) {
                    if(player.fallDistance > 4){
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
                        
                    } else if (ClientEvents.dragonsDigging.getOrDefault(this.player, false)) {
                        builder.addAnimation("dig_sneak", true);
                        
                    } else {
                        builder.addAnimation("sneak", true);
                    }
                    
                } else if (player.isSprinting()) {
                    builder.addAnimation("run", true);
                    
                }else if (isMovingHorizontal && player.animationSpeed != 0f) {
                    builder.addAnimation("walk", true);
    
                }else if (ClientEvents.dragonsDigging.getOrDefault(this.player, false)) {
                    builder.addAnimation("dig", true);
                }else {
                    builder.addAnimation("idle", true);
                }
            });
        } else {
            builder.addAnimation("idle", true);
        }
        animationController.setAnimation(builder);
        return PlayState.CONTINUE;
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
