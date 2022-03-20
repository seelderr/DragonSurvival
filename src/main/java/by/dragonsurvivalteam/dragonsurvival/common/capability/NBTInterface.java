package by.dragonsurvivalteam.dragonsurvival.common.capability;

import net.minecraft.nbt.CompoundNBT;

public interface NBTInterface{
	CompoundNBT writeNBT();
	void readNBT(CompoundNBT base);
}