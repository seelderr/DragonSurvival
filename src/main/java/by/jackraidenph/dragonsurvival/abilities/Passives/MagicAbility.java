package by.jackraidenph.dragonsurvival.abilities.Passives;

import by.jackraidenph.dragonsurvival.abilities.common.PassiveDragonAbility;
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
		return new TranslationTextComponent("ds.skill.description." + getId(), getPoints());
	}
}
