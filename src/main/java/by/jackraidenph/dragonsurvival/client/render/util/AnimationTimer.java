package by.jackraidenph.dragonsurvival.client.render.util;

import net.minecraft.client.Minecraft;
import software.bernie.geckolib3.core.builder.AnimationBuilder;

import java.util.HashMap;

public class AnimationTimer {
    protected HashMap<String, Double> animationTimes;

    public AnimationTimer() {
        animationTimes = new HashMap<>();
    }
    
    protected void putDuration(String animation, Double ticks) {
        animationTimes.put(animation, ticks);
    }
    
    public void trackAnimation(String animation) {
        animationTimes.computeIfPresent(animation, (s, d) -> d -= Minecraft.getInstance().getDeltaFrameTime());
    }
    
    public double getDuration(String animation) {
        return animationTimes.getOrDefault(animation, 0.0);
    }

    public void putAnimation(String animation, Double ticks, AnimationBuilder builder) {
        builder.addAnimation(animation);
        putDuration(animation, ticks);
    }
}
