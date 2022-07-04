package by.dragonsurvivalteam.dragonsurvival.magic.common.passive;

import by.dragonsurvivalteam.dragonsurvival.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;

public abstract class PassiveDragonAbility extends DragonAbility {
	public int getLevelCost(){
		return ServerConfig.initialPassiveCost + (int)(ServerConfig.passiveScalingCost * getLevel());
	}
}