package by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public abstract class SubCap implements INBTSerializable<CompoundTag> {
	public DragonStateHandler handler;

	public SubCap(DragonStateHandler handler) {
		this.handler = handler;
	}
}