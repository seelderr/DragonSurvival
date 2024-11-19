package by.dragonsurvivalteam.dragonsurvival.config.server.dragon;

import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;

public class DragonBonusConfig {
    // FIXME :: remove this config - doubt it's even correctly checked in all places
    @Translation(key = "bonuses_enabled", type = Translation.Type.CONFIGURATION, comments = "If enabled all dragon bonuses are disabled")
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "bonuses_enabled")
    public static Boolean bonusesEnabled = true;

    @Translation(key = "health_adjustments", type = Translation.Type.CONFIGURATION, comments = "If enabled health from dragons will scale with their size")
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "health_adjustments")
    public static Boolean healthAdjustments = true;

    @Translation(key = "damage_bonus", type = Translation.Type.CONFIGURATION, comments = "If enabled dragons will gain a damage modifier")
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "damage_bonus")
    public static Boolean isDamageBonusEnabled = true;

    // FIXME :: i don't think this gets checked at all the points it would be relevant
    // FIXME :: was the original intention for this only affect block breaking if no tools are in the claw slot or mainhand (i.e. mining with paw)?
    @Translation(key = "claws_as_tools", type = Translation.Type.CONFIGURATION, comments = "N/A")
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "claws_as_tools")
    public static Boolean clawsAreTools = true;

    @ConfigRange(min = 0, max = 100)
    @Translation(key = "speed_up_effect_level", type = Translation.Type.CONFIGURATION, comments = "Determines the level of the speed effect gained when a dragon is on its speed up block type - disabled if set to 0")
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "speed_up_effect_level")
    public static Integer speedupEffectLevel = 2;

    // --- Break speed bonus --- //

    @ConfigRange(min = 1, max = 10)
    @Translation(key = "break_speed_reduction", type = Translation.Type.CONFIGURATION, comments = {"The base break speed / bonus break speed will be divided by this value if an effective claw tool is present for the block"})
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "break_speed_reduction")
    public static Float bonusBreakSpeedReduction = 2f;
}
