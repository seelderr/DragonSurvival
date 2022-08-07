package by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects;

import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.DragonType;

import java.util.HashMap;

public class SavedSkinPresets{
	public HashMap<DragonType, HashMap<Integer, SkinPreset>> skinPresets = new HashMap<>();
	public HashMap<DragonType, HashMap<DragonLevel, Integer>> current = new HashMap<>();
}