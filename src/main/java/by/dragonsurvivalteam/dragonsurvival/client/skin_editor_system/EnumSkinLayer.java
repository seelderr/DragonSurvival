package by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system;

public enum EnumSkinLayer{
	BASE("Base", true),
	BOTTOM("Bottom", true),
	EYES("Eyes", true),
	HORNS("Horns", true),
	SPIKES("Spikes", true),
	CLAWS("Claws", true),
	TEETH("Teeth", true),
	EXTRA("Extra", false),
	EXTRA1("Extra", false),
	EXTRA2("Extra", false),
	EXTRA3("Extra", false),
	EXTRA4("Extra", false),
	EXTRA5("Extra", false),
	EXTRA6("Extra", false);

	public String name;
	public boolean base;

	EnumSkinLayer(String name, boolean base){
		this.name = name;
		this.base = base;
	}
}