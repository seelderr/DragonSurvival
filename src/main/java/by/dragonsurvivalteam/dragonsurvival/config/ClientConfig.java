package by.dragonsurvivalteam.dragonsurvival.config;

import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig{
	ClientConfig(ForgeConfigSpec.Builder builder){
		ConfigHandler.addConfigs(builder, ConfigSide.CLIENT);
	}

	@ConfigOption( side = ConfigSide.CLIENT, category = "misc", key = "clientDebugMessages", comment = "Enable client-side debug messages" )
	public static Boolean clientDebugMessages = false;

	@ConfigOption( side = ConfigSide.CLIENT, category = "rendering", key = "enableTailPhysics", comment = "Enable movement based physics on the tail, this is still a working progress and can be buggy." )
	public static Boolean enableTailPhysics = true;
}