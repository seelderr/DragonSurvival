package by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.objects;

import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;

import java.util.HashMap;

public class DragonEditorObject{
	public HashMap<DragonType, HashMap<DragonLevel, HashMap<EnumSkinLayer, String>>> defaults = new HashMap<>();

	public Dragon sea_dragon;
	public Dragon forest_dragon;
	public Dragon cave_dragon;

	public static class Dragon{
		public HashMap<EnumSkinLayer, Texture[]> layers;
	}

	public static class Texture{
		public String key;
		public String texture;
		public boolean colorable = true;
		public String defaultColor;

		public boolean random = true;
		public boolean randomHue = true;
	}
}