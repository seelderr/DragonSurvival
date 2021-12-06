package by.jackraidenph.dragonsurvival.magic.abilities.Innate;

import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.magic.common.InnateDragonAbility;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AmphibianAbility extends InnateDragonAbility
{
	public AmphibianAbility(DragonType type, String abilityId, String icon, int minLevel, int maxLevel)
	{
		super(type, abilityId, icon, minLevel, maxLevel);
	}
	
	@Override
	public AmphibianAbility createInstance()
	{
		return new AmphibianAbility(type, id, icon, minLevel, maxLevel);
	}
	
	@Override
	public int getLevel()
	{
		return ConfigHandler.SERVER.penalties.get() && ConfigHandler.SERVER.seaTicksWithoutWater.get() != 0.0 ? 1 : 0;
	}
	
	@OnlyIn( Dist.CLIENT )
	public boolean isDisabled() {
		return super.isDisabled() || !ConfigHandler.SERVER.penalties.get() || ConfigHandler.SERVER.seaTicksWithoutWater.get() == 0.0;
	}
	
	
	@Override
	public IFormattableTextComponent getDescription()
	{
		return new TranslationTextComponent("ds.skill.description." + getId(), ConfigHandler.SERVER.seaDehydrationDamage.get(), 2);
	}
}