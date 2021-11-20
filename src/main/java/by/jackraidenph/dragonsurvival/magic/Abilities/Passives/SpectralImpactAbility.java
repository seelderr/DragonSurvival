package by.jackraidenph.dragonsurvival.magic.Abilities.Passives;

import by.jackraidenph.dragonsurvival.magic.common.PassiveDragonAbility;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SpectralImpactAbility extends PassiveDragonAbility
{
	public SpectralImpactAbility(String abilityId, String icon, int minLevel, int maxLevel)
	{
		super(abilityId, icon, minLevel, maxLevel);
	}
	
	public int getChance(){
		return 15 * getLevel();
	}
	
	@Override
	public SpectralImpactAbility createInstance()
	{
		return new SpectralImpactAbility(id, icon, minLevel, maxLevel);
	}
	
	@Override
	public IFormattableTextComponent getDescription()
	{
		return new TranslationTextComponent("ds.skill.description." + getId(), getChance());
	}
}
