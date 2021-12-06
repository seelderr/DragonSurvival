package by.jackraidenph.dragonsurvival.magic.abilities.Innate;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.handlers.ClientSide.KeyInputHandler;
import by.jackraidenph.dragonsurvival.magic.common.InnateDragonAbility;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

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
	public IFormattableTextComponent getDescription()
	{
		String key = KeyInputHandler.TOGGLE_WINGS.getKey().getDisplayName().getContents().toUpperCase(Locale.ROOT);
		
		if(key.isEmpty()){
			key = KeyInputHandler.TOGGLE_WINGS.getKey().getDisplayName().getString();
		}
		
		return new TranslationTextComponent("ds.skill.description." + getId(), key);
	}
}
