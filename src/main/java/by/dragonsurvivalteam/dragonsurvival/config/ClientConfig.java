package by.dragonsurvivalteam.dragonsurvival.config;

import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
	@ConfigOption(side = ConfigSide.CLIENT, category = "misc", key = "alternateCastMode", comment = "Should the cast mode where you click the keybind to cast be used?")
	public static Boolean alternateCastMode = false;

	ClientConfig(ModConfigSpec.Builder builder) {
		ConfigHandler.addConfigs(builder, ConfigSide.CLIENT);
	}

	@ConfigOption(side = ConfigSide.CLIENT, category = "misc", key = "renderBreathRange", comment = "Whether the range of the breath should be rendered (while hitboxes are shown)")
	public static Boolean renderBreathRange = true;

	@ConfigOption(side = ConfigSide.CLIENT, category = "misc", key = "stableNightVision", comment = "When enabled it stops the blinking effect of night vision when low duration, disable if it causes rendering issues with other mods.")
	public static Boolean stableNightVision = true;

	@ConfigRange(min = 0.0, max = 1.0)
	@ConfigOption(side = ConfigSide.CLIENT, category = "animation", key = "smallSizeAnimationSpeedFactor", comment = "The factor by which the additional animation speed from being smaller is multiplied. 1.0 represents the speed accurately reflecting the size of the dragon.")
	public static Double smallSizeAnimationSpeedFactor = 0.3;

	@ConfigRange(min = 0.0, max = 1.0)
	@ConfigOption(side = ConfigSide.CLIENT, category = "animation", key = "largeSizeAnimationSpeedFactor", comment = "The factor by which the reduced additional animation speed from being bigger is multiplied. 1.0 represents the speed accurately reflecting the size of the dragon.")
	public static Double largeSizeAnimationSpeedFactor = 1.0;

	@ConfigRange(min = 0.0, max = 1.0)
	@ConfigOption(side = ConfigSide.CLIENT, category = "animation", key = "movementAnimationSpeedFactor", comment = "The amount by which the movement animation speed factor is multiplied. 1.0 represents the animation speed accurately reflecting the speed of your movement.")
	public static Double movementAnimationSpeedFactor = 1.0;

	@ConfigRange(min = 0.0, max = 10.0)
	@ConfigOption(side = ConfigSide.CLIENT, category = "animation", key = "movementAnimationSpeedFactor", comment = "The maximum value that the speed factor can add to the base animation speed.")
	public static Double maxAnimationSpeedFactor = 5.0;

	@ConfigRange(min = 1.0, max = 5.0)
	@ConfigOption(side = ConfigSide.CLIENT, category = "animation", key = "maxAnimationSpeed", comment = "The maximum animation speed allowed for dragons.")
	public static Double maxAnimationSpeed = 1.5;

	@ConfigRange(min = 0.05, max = 1.0)
	@ConfigOption(side = ConfigSide.CLIENT, category = "animation", key = "minAnimationSpeed", comment = "The minimum animation speed allowed for dragons.")
	public static Double minAnimationSpeed = 0.2;

	@ConfigOption(side = ConfigSide.CLIENT, category = "misc", key = "forceCPUSkinGeneration", comment = "When enabled it forces skin generation to happen on the CPU instead of the GPU.")
	public static Boolean forceCPUSkinGeneration = false;

}