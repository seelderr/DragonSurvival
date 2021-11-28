package by.jackraidenph.dragonsurvival.magic.common;

public class PassiveDragonAbility extends DragonAbility {
	
	public PassiveDragonAbility(String abilityId, String icon, int minLevel, int maxLevel)
	{
		super(abilityId, icon, minLevel, maxLevel);
	}
	
	public int getLevelCost(){
		return 1 + (int)(0.75 * getLevel());
	}
	@Override
	public PassiveDragonAbility createInstance()
	{
		return new PassiveDragonAbility(id, icon, minLevel, maxLevel);
	}
}
