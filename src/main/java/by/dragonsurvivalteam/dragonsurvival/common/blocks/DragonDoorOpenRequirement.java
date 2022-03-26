package by.dragonsurvivalteam.dragonsurvival.common.blocks;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum DragonDoorOpenRequirement implements StringRepresentable{
	NONE,
	POWER,
	CAVE,
	FOREST,
	SEA,
	LOCKED;

	@Override
	public String getSerializedName(){
		return name().toLowerCase(Locale.ENGLISH);
	}
}