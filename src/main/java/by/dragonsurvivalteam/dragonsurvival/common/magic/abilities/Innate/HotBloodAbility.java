package by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Innate;

import by.dragonsurvivalteam.dragonsurvival.common.magic.common.InnateDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HotBloodAbility extends InnateDragonAbility{
	public HotBloodAbility(DragonType type, String abilityId, String icon, int minLevel, int maxLevel){
		super(type, abilityId, icon, minLevel, maxLevel);
	}

	@Override
	public HotBloodAbility createInstance(){
		return new HotBloodAbility(type, id, icon, minLevel, maxLevel);
	}

	@Override
	public IFormattableTextComponent getDescription(){
		return new TranslationTextComponent("ds.skill.description." + getId(), ConfigHandler.SERVER.caveWaterDamage.get(), 0.5);
	}

	@Override
	public int getLevel(){
		return ConfigHandler.SERVER.penalties.get() && ConfigHandler.SERVER.caveWaterDamage.get() != 0.0 ? 1 : 0;
	}

	@OnlyIn( Dist.CLIENT )
	public boolean isDisabled(){
		return super.isDisabled() || !ConfigHandler.SERVER.penalties.get() || ConfigHandler.SERVER.caveWaterDamage.get() == 0.0;
	}
}