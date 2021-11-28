package by.jackraidenph.dragonsurvival.magic.abilities.Passives;

import by.jackraidenph.dragonsurvival.magic.common.PassiveDragonAbility;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;

public class AthleticsAbility extends PassiveDragonAbility
{
	public AthleticsAbility(String abilityId, String icon, int minLevel, int maxLevel)
	{
		super(abilityId, icon, minLevel, maxLevel);
	}
	
	public int getDuration(){
		return getLevel();
	}
	
	@Override
	public AthleticsAbility createInstance()
	{
		return new AthleticsAbility(id, icon, minLevel, maxLevel);
	}
	
	@Override
	public IFormattableTextComponent getDescription()
	{
		return new TranslationTextComponent("ds.skill.description." + getId(), getDuration(), getLevel() == getMaxLevel() ? "III" : "II");
	}
	
	@Override
	public ArrayList<ITextComponent> getInfo()
	{
		ArrayList<ITextComponent> list = super.getInfo();
		
		return list;
	}
}
