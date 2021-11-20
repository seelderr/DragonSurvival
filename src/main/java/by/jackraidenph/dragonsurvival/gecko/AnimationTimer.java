package by.jackraidenph.dragonsurvival.gecko;

import software.bernie.geckolib3.core.builder.AnimationBuilder;

import java.util.HashMap;

public class AnimationTimer {
    protected HashMap<String, Integer> animationTimes;

    public AnimationTimer() {
        animationTimes = new HashMap<>();
    }

    protected void putDuration(String animation, int ticks) {
        animationTimes.put(animation, ticks);
    }

    public void trackAnimation(String animation) {
        animationTimes.computeIfPresent(animation, (s, integer) -> --integer);
    }

    public int getDuration(String animation) {
        return animationTimes.getOrDefault(animation, 0);
    }

    public void putAnimation(String animation, int ticks, AnimationBuilder builder) {
        builder.addAnimation(animation);
        putDuration(animation, ticks);
    }
}
