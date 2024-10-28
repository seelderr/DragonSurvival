package by.dragonsurvivalteam.dragonsurvival.magic.common;

public class AbilityAnimation {
    public String animationKey;
    public double duration;
    public boolean locksNeck;
    public boolean locksTail;

    public AbilityAnimation(String animationKey, double duration, boolean locksNeck, boolean locksTail) {
        this(animationKey, locksNeck, locksTail);
        this.duration = duration;
    }

    public AbilityAnimation(String animationKey, boolean locksNeck, boolean locksTail) {
        this.animationKey = animationKey;
        this.locksNeck = locksNeck;
        this.locksTail = locksTail;
    }
}