package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.animation;

import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;

public record SimpleAbilityAnimation(String animationKey, AnimationLayer layer, int transitionLength, boolean locksNeck, boolean locksTail) implements AbilityAnimation {

    public static final Codec<SimpleAbilityAnimation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("animation_key").forGetter(SimpleAbilityAnimation::animationKey),
            Codec.STRING.xmap(AnimationLayer::valueOf, AnimationLayer::name).fieldOf("layer").forGetter(SimpleAbilityAnimation::layer),
            Codec.INT.optionalFieldOf("transition_length", 0).forGetter(SimpleAbilityAnimation::transitionLength),
            Codec.BOOL.fieldOf("locks_neck").forGetter(SimpleAbilityAnimation::locksNeck),
            Codec.BOOL.fieldOf("locks_tail").forGetter(SimpleAbilityAnimation::locksTail)
    ).apply(instance, SimpleAbilityAnimation::new));

    @Override
    public void play(AnimationState<?> state, DragonEntity entity, AnimationType animationType) {
        entity.tailLocked = locksTail;
        entity.neckLocked = locksNeck;
        state.getController().transitionLength(transitionLength);
        state.setAndContinue(getRawAnimation(animationType));
    }

    @Override
    public AnimationLayer getLayer() {
        return layer();
    }

    private RawAnimation getRawAnimation(AnimationType animationType) {
        RawAnimation rawAnimation = RawAnimation.begin();
        if(animationType == AnimationType.PLAY_AND_HOLD) {
            rawAnimation = rawAnimation.thenPlayAndHold(animationKey);
        } else if(animationType == AnimationType.LOOPING) {
            rawAnimation = rawAnimation.thenLoop(animationKey);
        } else if(animationType == AnimationType.PLAY_ONCE) {
            rawAnimation = rawAnimation.then(animationKey, Animation.LoopType.PLAY_ONCE);
        }
        return rawAnimation;
    }

    @Override
    public String getName() {
        return animationKey;
    }
}
