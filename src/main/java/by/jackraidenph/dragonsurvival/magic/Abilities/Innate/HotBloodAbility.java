package by.jackraidenph.dragonsurvival.magic.Abilities.Innate;

import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.magic.common.InnateDragonAbility;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class HotBloodAbility extends InnateDragonAbility
{
	public HotBloodAbility(String abilityId, String icon, int minLevel, int maxLevel)
	{
		super(abilityId, icon, minLevel, maxLevel);
	}
	
	@Override
	public HotBloodAbility createInstance()
	{
		return new HotBloodAbility(id, icon, minLevel, maxLevel);
	}
	
	@Override
	public IFormattableTextComponent getDescription()
	{
		return new TranslationTextComponent("ds.skill.description." + getId(), ConfigHandler.SERVER.caveWaterDamage.get(), 0.5);
	}
}