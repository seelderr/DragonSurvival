package by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Passives;


import by.dragonsurvivalteam.dragonsurvival.common.magic.common.PassiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

public class ContrastShowerAbility extends PassiveDragonAbility{
	public ContrastShowerAbility(DragonType type, String abilityId, String icon, int minLevel, int maxLevel){
		super(type, abilityId, icon, minLevel, maxLevel);
	}

	@Override
	public ContrastShowerAbility createInstance(){
		return new ContrastShowerAbility(type, id, icon, minLevel, maxLevel);
	}

	@Override

	public Component getDescription(){
		return new TranslatableComponent("ds.skill.description." + getId(), getDuration());
	}

	public int getDuration(){
		return 30 * getLevel();
	}

	@OnlyIn( Dist.CLIENT )
	public ArrayList<Component> getLevelUpInfo(){
		ArrayList<Component> list = super.getLevelUpInfo();
		list.add(new TranslatableComponent("ds.skill.duration.seconds", "+30"));
		return list;
	}

	@Override
	public boolean isDisabled(){
		return super.isDisabled() || !ServerConfig.contrastShower;
	}
}