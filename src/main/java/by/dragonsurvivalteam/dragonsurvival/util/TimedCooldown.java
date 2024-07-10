package by.dragonsurvivalteam.dragonsurvival.util;

/**
 * Tracks a cooldown period.
 *
 * @see ActionWithTimedCooldown
 */
public class TimedCooldown {
    private final long cooldownMs;

    /**
     * The time when the cooldown expires
     */
    private long expirationMs = 0;

    public TimedCooldown(long cooldownMs) {
        this.cooldownMs = cooldownMs;
    }

    public long getCooldownMs() {
        return cooldownMs;
    }

    /**
     * Tries to set the cooldown. If the cooldown is already active, this method does nothing.
     *
     * @return True if the cooldown was set. False if it was already active.
     */
    public boolean trySet() {
        if (isOnCooldown()) return false;

        forceSet();
        return true;
    }

    /**
     * Sets the cooldown immediately.
     */
    public void forceSet() {
        expirationMs = System.currentTimeMillis() + cooldownMs;
    }

    public boolean isOnCooldown() {
        return System.currentTimeMillis() < expirationMs;
    }

    /**
     * Resets the cooldown immediately.
     */
    public void reset() {
        expirationMs = 0;
    }
}
