package by.dragonsurvivalteam.dragonsurvival.util;

public class ActionWithCooldown {

    private final long cooldownMs;
    private final Runnable action;

    private long nextAllowedRunMs = 0;

    public ActionWithCooldown(long cooldownMs, Runnable action) {
        this.cooldownMs = cooldownMs;
        this.action = action;
    }

    public long getCooldownMs() {
        return cooldownMs;
    }

    public Runnable getAction() {
        return action;
    }

    public boolean tryRun() {
        if (System.currentTimeMillis() < nextAllowedRunMs) return false;

        forceRun();
        return true;
    }

    public void forceRun() {
        action.run();
        nextAllowedRunMs = System.currentTimeMillis() + cooldownMs;
    }

    public void resetCooldown() {
        nextAllowedRunMs = 0;
    }
}
