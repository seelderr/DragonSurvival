package by.jackraidenph.dragonsurvival.magic.Abilities.Passives;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.magic.common.PassiveDragonAbility;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class MagicAbility extends PassiveDragonAbility
{
	public MagicAbility(String abilityId, String icon, int minLevel, int maxLevel)
	{
		super(abilityId, icon, minLevel, maxLevel);
	}
	
	public int getPoints(){
		return getLevel();
	}
	
	@Override
	public MagicAbility createInstance()
	{
		return new MagicAbility(id, icon, minLevel, maxLevel);
	}
	
	@Override
	public IFormattableTextComponent getDescription()
	{
		int level = Math.max(0, (Math.min(50, getPlayer().experienceLevel) - 5) / 5);
		String points = getPoints() > 0 ? "+" + getPoints() : "0";
		String levels = level > 0 ? "+" + level : "0";
		
		return new TranslationTextComponent("ds.skill.description." + getId(), DragonStateProvider.getMaxMana(player), points, levels);
	}
}
