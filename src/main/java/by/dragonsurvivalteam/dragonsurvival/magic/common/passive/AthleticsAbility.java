package by.dragonsurvivalteam.dragonsurvival.magic.common.passive;


import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

public abstract class AthleticsAbility extends PassiveDragonAbility {
	@Override
	public Component getDescription(){
		return new TranslatableComponent("ds.skill.description." + getName(), getDuration(), getLevel() == getMaxLevel() ? "III" : "II");
	}

	public int getDuration(){
		return getLevel();
	}

	@Override
	public int getMaxLevel(){
		return 5;
	}

	@Override
	public int getMinLevel(){
		return 0;
	}

	@OnlyIn( Dist.CLIENT )
	public ArrayList<Component> getLevelUpInfo(){
		ArrayList<Component> list = super.getLevelUpInfo();
		list.add(new TranslatableComponent("ds.skill.duration.seconds", "+1"));
		return list;
	}
}