package by.jackraidenph.dragonsurvival.magic.abilities.Innate;

import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.magic.common.InnateDragonAbility;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HotBloodAbility extends InnateDragonAbility
{
	public HotBloodAbility(DragonType type, String abilityId, String icon, int minLevel, int maxLevel)
	{
		super(type, abilityId, icon, minLevel, maxLevel);
	}
	
	@Override
	public HotBloodAbility createInstance()
	{
		return new HotBloodAbility(type, id, icon, minLevel, maxLevel);
	}
	
	@Override
	public int getLevel()
	{
		return ConfigHandler.SERVER.penalties.get() && ConfigHandler.SERVER.caveWaterDamage.get() != 0.0 ? 1 : 0;
	}
	
	@OnlyIn( Dist.CLIENT )
	public boolean isDisabled() {
		return super.isDisabled() || !ConfigHandler.SERVER.penalties.get() || ConfigHandler.SERVER.caveWaterDamage.get() == 0.0;
	}
	
	@Override
	public IFormattableTextComponent getDescription()
	{
		return new TranslationTextComponent("ds.skill.description." + getId(), ConfigHandler.SERVER.caveWaterDamage.get(), 0.5);
	}
}