package by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Innate;


import by.dragonsurvivalteam.dragonsurvival.common.magic.common.InnateDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AmphibianAbility extends InnateDragonAbility{
	public AmphibianAbility(DragonType type, String abilityId, String icon, int minLevel, int maxLevel){
		super(type, abilityId, icon, minLevel, maxLevel);
	}

	@Override
	public AmphibianAbility createInstance(){
		return new AmphibianAbility(type, id, icon, minLevel, maxLevel);
	}

	@Override
	public Component getDescription(){
		return new TranslatableComponent("ds.skill.description." + getId(), ServerConfig.seaDehydrationDamage, 2);
	}

	@Override
	public int getLevel(){
		return ServerConfig.penalties && ServerConfig.seaTicksWithoutWater != 0.0 ? 1 : 0;
	}

	@OnlyIn( Dist.CLIENT )
	public boolean isDisabled(){
		return super.isDisabled() || !ServerConfig.penalties || ServerConfig.seaTicksWithoutWater == 0.0;
	}
}