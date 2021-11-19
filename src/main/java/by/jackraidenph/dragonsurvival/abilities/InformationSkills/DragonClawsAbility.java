package by.jackraidenph.dragonsurvival.abilities.InformationSkills;

import by.jackraidenph.dragonsurvival.abilities.common.DragonAbility;
import by.jackraidenph.dragonsurvival.abilities.common.InnateDragonAbility;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.util.DragonLevel;
import net.minecraft.client.Minecraft;

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
		return DragonStateProvider.getCap(Minecraft.getInstance().player).map(cap -> cap.getLevel()).orElse(DragonLevel.BABY).ordinal() + 1;
	}
	
}
