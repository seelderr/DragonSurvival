package by.jackraidenph.dragonsurvival.common.capability.subcapabilities;

import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.NBTInterface;

public abstract class SubCap implements NBTInterface
{
	public DragonStateHandler handler;
	
	public SubCap(DragonStateHandler handler)
	{
		this.handler = handler;
	}
}
