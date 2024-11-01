package by.dragonsurvivalteam.dragonsurvival.config.client.ui;

import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;

public class UiConfig {
    @ConfigRange(min = -1000, max = 1000)
    @Translation(key = "spin_cooldown_x_offset", type = Translation.Type.CONFIGURATION, comments = "Offset to the x position of the spin cooldown indicator")
    @ConfigOption(side = ConfigSide.CLIENT, category = {"ui", "spin"}, key = "spin_cooldown_x_offset")
    public static Integer spinCooldownXOffset = 0;

    @ConfigRange(min = -1000, max = 1000)
    @Translation(key = "spin_cooldown_y_offset", type = Translation.Type.CONFIGURATION, comments = "Offset to the y position of the spin cooldown indicator")
    @ConfigOption(side = ConfigSide.CLIENT, category = {"ui", "spin"}, key = "spin_cooldown_y_offset")
    public static Integer spinCooldownYOffset = 0;
}
