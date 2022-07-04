package by.dragonsurvivalteam.dragonsurvival.magic.common.passive;


import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public abstract class MagicAbility extends PassiveDragonAbility {
	@Override
	public Component getDescription(){
		int level = Math.max(0, (Math.min(50, getPlayer().experienceLevel) - 5) / 5);
		String points = getPoints() > 0 ? "+" + getPoints() : "0";
		String levels = level > 0 ? "+" + level : "0";

		return new TranslatableComponent("ds.skill.description." + getName(), ManaHandler.getMaxMana(player), points, levels);
	}

	public int getPoints(){
		return getLevel();
	}
}