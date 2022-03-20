package by.dragonsurvivalteam.dragonsurvival.common.blocks;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum DragonDoorOpenRequirement implements IStringSerializable{
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