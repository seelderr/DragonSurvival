package by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.NBTInterface;

public abstract class SubCap implements NBTInterface{
	public DragonStateHandler handler;

	public SubCap(DragonStateHandler handler){
		this.handler = handler;
	}
}