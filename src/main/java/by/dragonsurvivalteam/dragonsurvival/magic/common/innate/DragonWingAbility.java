package by.dragonsurvivalteam.dragonsurvival.magic.common.innate;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.KeyInputHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Locale;

public abstract class DragonWingAbility extends InnateDragonAbility{
	@Override
	public Component getDescription(){
		String key = KeyInputHandler.TOGGLE_WINGS.getKey().getDisplayName().getContents().toUpperCase(Locale.ROOT);

		if(key.isEmpty())
			key = KeyInputHandler.TOGGLE_WINGS.getKey().getDisplayName().getString();

		DragonStateHandler handler = DragonUtils.getHandler(player);
		return new TranslatableComponent("ds.skill.description." + getName(), key).append("\n").append(new TranslatableComponent("ds.skill.description." + getName() + (handler.getMovementData().spinLearned ? ".has_spin" : ".no_spin")));
	}

	@Override
	public int getLevel(){
		return DragonStateProvider.getCap(getPlayer()).map(DragonStateHandler::hasWings).orElse(false) ? 1 : 0;
	}
}