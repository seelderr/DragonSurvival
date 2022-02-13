package by.jackraidenph.dragonsurvival.client.SkinCustomization;

import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import by.jackraidenph.dragonsurvival.misc.DragonType;

import java.util.HashMap;

public class SavedCustomizations
{
	public HashMap<DragonType, HashMap<Integer, HashMap<DragonLevel, HashMap<CustomizationLayer, String>>>> saved = new HashMap<>();
	public HashMap<DragonType, HashMap<DragonLevel, Integer>> current = new HashMap<>();
}
