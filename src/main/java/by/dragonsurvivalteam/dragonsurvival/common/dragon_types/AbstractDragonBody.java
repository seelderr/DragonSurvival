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

	public Double getJumpBonus() {
		return 0.0;
	}
	
	public Double getRunBonus() {
		return 0.0;
	}
	
	public Double getStepBonus() {
		return 0.0;
	}
	
	public Double getDamageBonus() {
		return 0.0;
	}
	
	public Double getArmorBonus() {
		return 0.0;
	}
	
	public Double getManaBonus() {
		return 0.0;
	}

	public Double getSwimSpeedBonus() {
		return 0.0;
	}
	
	public Double getKnockbackBonus() {
		return 0.0;
	}
	
	public Double getDamageMult() {
		return 1.0;
	}
	
	public Double getExpMult() {
		return 1.0;
	}

	public Double getFlightMult() {
		return 1.0;
	}

	public Double getFlightStaminaMult() {
		return 1.0;
	}

	public Double getHealthMult() {
		return 1.0;
	}

	public Double getGravityMult() {
		return 1.0;
	}
}
