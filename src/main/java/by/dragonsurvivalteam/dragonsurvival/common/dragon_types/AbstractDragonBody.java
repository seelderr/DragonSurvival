package by.dragonsurvivalteam.dragonsurvival.common.dragon_types;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import by.dragonsurvivalteam.dragonsurvival.common.capability.NBTInterface;

public abstract class AbstractDragonBody implements NBTInterface, Comparable<AbstractDragonBody> {
	public abstract String getBodyName();
	public abstract void onPlayerUpdate();
	public abstract void onPlayerDeath();
	
	public int compareTo(@NotNull AbstractDragonBody b) {
		return getBodyName().compareTo(b.getBodyName());
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof AbstractDragonBody b) {
			return Objects.equals(getBodyName(), b.getBodyName());
		}
		return super.equals(obj);
	}

	@Override
	public String toString(){
		return getBodyName();
	}
}
