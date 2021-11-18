package by.jackraidenph.dragonsurvival.abilities.common;

public class InformationDragonAbility extends DragonAbility {
	public InformationDragonAbility(String abilityId, String icon, int minLevel, int maxLevel)
	{
		super(abilityId, icon, minLevel, maxLevel);
	}
	
	@Override
	public DragonAbility createInstance()
	{
		return new InformationDragonAbility(id, icon, minLevel, maxLevel);
	}
}
