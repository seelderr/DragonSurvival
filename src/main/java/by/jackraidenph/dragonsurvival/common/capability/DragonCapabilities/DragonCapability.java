package by.jackraidenph.dragonsurvival.common.capability.DragonCapabilities;

import net.minecraft.nbt.Tag;

public interface DragonCapability
{
	Tag writeNBT();
	void readNBT(Tag base);
}
