package by.jackraidenph.dragonsurvival.magic.Abilities.Innate;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.magic.common.InnateDragonAbility;
import by.jackraidenph.dragonsurvival.util.DragonLevel;

public class DragonClawsAbility extends InnateDragonAbility
{
	public DragonClawsAbility(String abilityId, String icon, int minLevel, int maxLevel)
	{
		super(abilityId, icon, minLevel, maxLevel);
	}
	
	@Override
	public DragonAbility createInstance()
	{
		return new DragonClawsAbility(id, icon, minLevel, maxLevel);
	}
	
	@Override
	public int getLevel()
	{
		return DragonStateProvider.getCap(getPlayer()).map(cap -> cap.getLevel()).orElse(DragonLevel.BABY).ordinal() + 1;
	}
}
