package by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Innate;


import by.dragonsurvivalteam.dragonsurvival.common.magic.common.InnateDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
	public Component getDescription(){
		return new TranslatableComponent("ds.skill.description." + getId(), ServerConfig.caveWaterDamage, 0.5);
	}

	@Override
	public int getLevel(){
		return ServerConfig.penalties && ServerConfig.caveWaterDamage != 0.0 ? 1 : 0;
	}

	@OnlyIn( Dist.CLIENT )
	public boolean isDisabled(){
		return super.isDisabled() || !ServerConfig.penalties || ServerConfig.caveWaterDamage == 0.0;
	}
}