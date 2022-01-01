package by.jackraidenph.dragonsurvival.common.magic.common;

import by.jackraidenph.dragonsurvival.misc.DragonType;

public class PassiveDragonAbility extends DragonAbility {
	
	public PassiveDragonAbility(DragonType type, String abilityId, String icon, int minLevel, int maxLevel)
	{
		super(type, abilityId, icon, minLevel, maxLevel);
	}
	
	public int getLevelCost(){
		return 1 + (int)(0.75 * getLevel());
	}
	
	@Override
	public PassiveDragonAbility createInstance()
	{
		return new PassiveDragonAbility(type, id, icon, minLevel, maxLevel);
	}
}
