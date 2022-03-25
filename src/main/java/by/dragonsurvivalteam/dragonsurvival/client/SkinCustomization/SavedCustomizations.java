package by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization;

import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;

import java.util.HashMap;

public class SavedCustomizations{
	public HashMap<DragonType, HashMap<Integer, HashMap<DragonLevel, HashMap<by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationLayer, String>>>> saved = new HashMap<>();
	public HashMap<DragonType, HashMap<DragonLevel, Integer>> current = new HashMap<>();
}