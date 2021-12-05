package by.jackraidenph.dragonsurvival.capability.DragonCapabilities;

import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public interface DragonCapability
{
	INBT writeNBT(Capability<DragonStateHandler> capability, Direction side);
	void readNBT(Capability<DragonStateHandler> capability, Direction side, INBT base);
	void clone(DragonStateHandler oldCap);
}
