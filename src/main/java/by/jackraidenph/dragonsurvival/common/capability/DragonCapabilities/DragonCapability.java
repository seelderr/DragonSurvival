package by.jackraidenph.dragonsurvival.common.capability.DragonCapabilities;

import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;

public interface DragonCapability
{
	Tag writeNBT(Capability<DragonStateHandler> capability, Direction side);
	void readNBT(Capability<DragonStateHandler> capability, Direction side, Tag base);
	void clone(DragonStateHandler oldCap);
}
