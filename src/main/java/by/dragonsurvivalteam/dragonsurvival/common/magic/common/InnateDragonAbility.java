package by.dragonsurvivalteam.dragonsurvival.common.magic.common;

import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;

public class InnateDragonAbility extends DragonAbility{
	public InnateDragonAbility(DragonType type, String abilityId, String icon, int minLevel, int maxLevel){
		super(type, abilityId, icon, minLevel, maxLevel);
	}

	@Override
	public DragonAbility createInstance(){
		return new InnateDragonAbility(type, id, icon, minLevel, maxLevel);
	}
}