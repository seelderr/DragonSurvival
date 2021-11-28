package by.jackraidenph.dragonsurvival.magic.abilities.Passives;

import by.jackraidenph.dragonsurvival.magic.common.PassiveDragonAbility;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ContrastShowerAbility extends PassiveDragonAbility
{
	public ContrastShowerAbility(String abilityId, String icon, int minLevel, int maxLevel)
	{
		super(abilityId, icon, minLevel, maxLevel);
	}
	
	public int getDuration(){
		return 30 * getLevel();
	}
	
	@Override
	public ContrastShowerAbility createInstance()
	{
		return new ContrastShowerAbility(id, icon, minLevel, maxLevel);
	}
	
	@Override
	public IFormattableTextComponent getDescription()
	{
		return new TranslationTextComponent("ds.skill.description." + getId(), getDuration());
	}
}
