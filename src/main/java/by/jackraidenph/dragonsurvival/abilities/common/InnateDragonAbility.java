package by.jackraidenph.dragonsurvival.abilities.common;

public class InnateDragonAbility extends DragonAbility {
	public InnateDragonAbility(String abilityId, String icon, int minLevel, int maxLevel)
	{
		super(abilityId, icon, minLevel, maxLevel);
	}
	
	@Override
	public DragonAbility createInstance()
	{
		return new InnateDragonAbility(id, icon, minLevel, maxLevel);
	}
}
