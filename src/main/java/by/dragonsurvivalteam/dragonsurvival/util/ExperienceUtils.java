package by.dragonsurvivalteam.dragonsurvival.util;

import net.minecraft.world.entity.player.Player;

public class ExperienceUtils {
    /**
     * <a href="https://github.com/Shadows-of-Fire/Placebo/blob/1.21/src/main/java/dev/shadowsoffire/placebo/util/EnchantmentUtils.java#L60">Taken from here</a>
     * <br> <br>
     * Calculates the amount of experience the passed level is worth <br>
     * <a href="https://minecraft.wiki/w/Experience#Leveling_up">Reference</a>
     *
     * @param level The target level
     * @return The amount of experience required to reach the given level when starting from the previous level
     */
    public static int getExperienceForLevel(int level) {
        if (level == 0) return 0;
        if (level >= 30) return 112 + (level - 30) * 9;
        if (level >= 15) return 37 + (level - 15) * 5;
        return 7 + level * 2;
    }

    public static int getLevelForExperience(int experience) {
        int level = 0;
        int xp = 0;
        while (xp <= experience) {
            xp += getExperienceForLevel(level);
            level++;
        }
        return level - 1;
    }

    /** Calculate the total experience a level is worth given experience levels */
    public static int getTotalExperienceForLevel(int level) {
        int total = 0;
        for (int i = 0; i <= level; i++) {
            total += getExperienceForLevel(i);
        }
        return total;
    }

    /** Calculate the total experience the player has. We use this instead of player.totalExperience as that doesn't update properly when using commands to give levels. */
    public static int getTotalExperience(final Player player) {
        return getTotalExperienceForLevel(player.experienceLevel) + Math.round(player.experienceProgress * getExperienceForLevel(player.experienceLevel + 1));
    }
}
