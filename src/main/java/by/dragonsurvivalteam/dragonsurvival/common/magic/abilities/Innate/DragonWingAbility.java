package by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Innate;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.KeyInputHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.InnateDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Locale;

public class DragonWingAbility extends InnateDragonAbility{
	public DragonWingAbility(DragonType type, String abilityId, String icon, int minLevel, int maxLevel){
		super(type, abilityId, icon, minLevel, maxLevel);
	}

	@Override
	public DragonWingAbility createInstance(){
		return new DragonWingAbility(type, id, icon, minLevel, maxLevel);
	}

	@Override
	public IFormattableTextComponent getDescription(){
		String key = KeyInputHandler.TOGGLE_WINGS.getKey().getDisplayName().getContents().toUpperCase(Locale.ROOT);

		if(key.isEmpty()){
			key = KeyInputHandler.TOGGLE_WINGS.getKey().getDisplayName().getString();
		}

		DragonStateHandler handler = DragonUtils.getHandler(player);

		return new TranslationTextComponent("ds.skill.description." + getId(), key).append("\n").append(new TranslationTextComponent("ds.skill.description." + getId() + (handler != null && handler.getMovementData().spinLearned ? ".has_spin" : ".no_spin")));
	}

	@Override
	public int getLevel(){
		return DragonStateProvider.getCap(getPlayer()).map(cap -> cap.hasWings()).orElse(false) ? 1 : 0;
	}
}