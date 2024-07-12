package by.dragonsurvivalteam.dragonsurvival.client.render.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import software.bernie.geckolib.animation.RawAnimation;

public class RandomAnimationPicker {
    public record WeightedAnimation(RawAnimation animation, float weight) {}

    private final List<WeightedAnimation> weightedAnimations = new ArrayList<>();
    private final float totalWeight;

    public RandomAnimationPicker(WeightedAnimation weightedAnimation, WeightedAnimation... moreWeightedAnimations) {
        weightedAnimations.add(weightedAnimation);
        Collections.addAll(weightedAnimations, moreWeightedAnimations);
        totalWeight = weightedAnimations.stream().map(WeightedAnimation::weight).reduce(0f, Float::sum);
    }

    public RawAnimation pickRandomAnimation() {
        float randomWeight = (float) Math.random() * totalWeight;
        for (WeightedAnimation weightedAnimation : weightedAnimations) {
            randomWeight -= weightedAnimation.weight();
            if (randomWeight <= 0) {
                return weightedAnimation.animation();
            }
        }

        throw new IllegalStateException("No animation was picked for a RandomAnimationPicker instance!");
    }
}
