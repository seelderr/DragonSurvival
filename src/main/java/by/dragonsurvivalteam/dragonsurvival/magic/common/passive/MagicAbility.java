package by.dragonsurvivalteam.dragonsurvival.magic.common.passive;


import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class MagicAbility extends PassiveDragonAbility {
	@Override
	public Component getDescription(){
		int level = Math.max(0, (Math.min(50, getPlayer().experienceLevel) - 5) / 5);
		String points = getLevel() > 0 ? "+" + getLevel() : "0";
		String levels = level > 0 ? "+" + level : "0";

		return new TranslatableComponent("ds.skill.description." + getName(), ManaHandler.getMaxMana(player), points, levels);
	}

	public ResourceLocation getIcon(){
		return getPlayer() == null ? super.getIcon() : getSkillTextures()[Mth.clamp(getLevel() + Math.max(0, (Math.min(50, getPlayer().experienceLevel) - 5) / 5), 0, getSkillTextures().length - 1)];
	}
}