package by.jackraidenph.dragonsurvival.magic.abilities.Innate;

import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.magic.common.InnateDragonAbility;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FearOfDarkAbility extends InnateDragonAbility
{
	public FearOfDarkAbility(String abilityId, String icon, int minLevel, int maxLevel)
	{
		super(abilityId, icon, minLevel, maxLevel);
	}
	
	@Override
	public FearOfDarkAbility createInstance()
	{
		return new FearOfDarkAbility(id, icon, minLevel, maxLevel);
	}
	
	@Override
	public int getLevel()
	{
		return ConfigHandler.SERVER.penalties.get() && ConfigHandler.SERVER.forestStressTicks.get() != 0.0 ? 1 : 0;
	}
	
	@OnlyIn( Dist.CLIENT )
	public boolean isDisabled() {
		return !ConfigHandler.SERVER.penalties.get() || ConfigHandler.SERVER.forestStressTicks.get() == 0.0;
	}
	
	@Override
	public IFormattableTextComponent getDescription()
	{
		return new TranslationTextComponent("ds.skill.description." + getId(), 3, ConfigHandler.SERVER.caveWaterDamage.get(), 0.5);
	}
}