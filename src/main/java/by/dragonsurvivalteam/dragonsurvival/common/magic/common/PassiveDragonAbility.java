package by.dragonsurvivalteam.dragonsurvival.common.magic.common;

import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;

public class PassiveDragonAbility extends DragonAbility{

	public PassiveDragonAbility(DragonType type, String abilityId, String icon, int minLevel, int maxLevel){
		super(type, abilityId, icon, minLevel, maxLevel);
	}

	public int getLevelCost(){
		return ServerConfig.initialPassiveCost + (int)(ServerConfig.passiveScalingCost * getLevel());
	}

	@Override
	public PassiveDragonAbility createInstance(){
		return new PassiveDragonAbility(type, id, icon, minLevel, maxLevel);
	}
}