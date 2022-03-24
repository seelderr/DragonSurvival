package by.jackraidenph.dragonsurvival.client.SkinCustomization;

import java.util.HashMap;

public class CustomizationObject
{
	public Dragon sea_dragon;
	public Dragon forest_dragon;
	public Dragon cave_dragon;
	
	public static class Dragon{
		public HashMap<CustomizationLayer, Texture[]> layers;
	}
	
	public static class Texture{
		public String key;
		public String texture;
		public boolean glowing;
	}
}
