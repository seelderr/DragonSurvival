package by.jackraidenph.dragonsurvival.common.magic.abilities.Innate;

import by.jackraidenph.dragonsurvival.client.handlers.KeyInputHandler;
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.magic.common.InnateDragonAbility;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Locale;

public class DragonWingAbility extends InnateDragonAbility
{
	public DragonWingAbility(DragonType type, String abilityId, String icon, int minLevel, int maxLevel)
	{
		super(type, abilityId, icon, minLevel, maxLevel);
	}
	
	@Override
	public DragonWingAbility createInstance()
	{
		return new DragonWingAbility(type,id, icon, minLevel, maxLevel);
	}
	
	@Override
	public int getLevel()
	{
		return DragonStateProvider.getCap(getPlayer()).map(cap -> cap.hasWings()).orElse(false) ? 1 : 0;
	}
	
	@Override
	public Component getDescription()
	{
		String key = KeyInputHandler.TOGGLE_WINGS.getKey().getDisplayName().getContents().toUpperCase(Locale.ROOT);
		
		if(key.isEmpty()){
			key = KeyInputHandler.TOGGLE_WINGS.getKey().getDisplayName().getString();
		}
		
		DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
		
		return new TranslatableComponent("ds.skill.description." + getId(), key).append("\n").append(new TranslatableComponent("ds.skill.description." + getId() + (handler != null && handler.getMovementData().spinLearned ? ".has_spin" : ".no_spin")));
	}
}
