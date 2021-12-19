package by.jackraidenph.dragonsurvival.gecko;

import software.bernie.geckolib3.core.builder.AnimationBuilder;

import java.util.HashMap;

public class AnimationTimer {
    protected HashMap<String, Double> animationTimes;
    protected HashMap<String, Integer> oldAnimationTimes;

    public AnimationTimer() {
        animationTimes = new HashMap<>();
        oldAnimationTimes = new HashMap<>();
    
    }
    
    protected void putDuration(String animation, Integer ticks) {
        oldAnimationTimes.put(animation, ticks);
    }

    protected void putDuration(String animation, Double ticks) {
        animationTimes.put(animation, ticks);
    }
    
    public void trackAnimation(String animation) {
        oldAnimationTimes.computeIfPresent(animation, (s, d) -> --d);
    }
    
    public void trackAnimation(String animation, double partial) {
        animationTimes.computeIfPresent(animation, (s, d) -> d -= partial);
    }

    public double getDoubleDuration(String animation) {
        return animationTimes.getOrDefault(animation, 0.0);
    }
    
    public int getDuration(String animation) {
        return oldAnimationTimes.getOrDefault(animation, 0);
    }

    public void putAnimation(String animation, Double ticks, AnimationBuilder builder) {
        builder.addAnimation(animation);
        putDuration(animation, ticks);
    }
    
    public void putAnimation(String animation, Integer ticks, AnimationBuilder builder) {
        builder.addAnimation(animation);
        putDuration(animation, ticks);
    }
}
