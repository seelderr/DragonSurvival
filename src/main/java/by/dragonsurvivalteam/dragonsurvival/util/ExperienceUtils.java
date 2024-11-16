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
        if (level > 30) return 112 + (level - 31) * 9;
        if (level > 15) return 37 + (level - 16) * 5;
        return 7 + (level - 1) * 2;
    }

    /** Calculate teh total experience the player has based on their experience levels */
    public static int getTotalExperience(final Player player) {
        return getTotalExperience(player.experienceLevel);
    }

    /** Calculate the total experience the player has based on their experience levels */
    public static int getTotalExperience(int currentLevel) {
        int experience = 0;

        for (int level = 1; level <= currentLevel; level++) {
            experience += getExperienceForLevel(level);
        }

        return experience;
    }
}
