package by.dragonsurvivalteam.dragonsurvival.common.dragon_types.bodies;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import net.minecraft.nbt.CompoundTag;

public class SouthBodyType extends AbstractDragonBody {
	
	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southJumpBonus", comment = "The jump bonus given to South-type dragons")
	public static Double southJumpBonus = 0.0;

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southStepBonus", comment = "The step bonus given to South-type dragons")
	public static Double southStepBonus = 0.0;

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southDamageBonus", comment = "The damage bonus given to South-type dragons")
	public static Double southDamageBonus = 0.0;

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southArmorBonus", comment = "The armor bonus given to South-type dragons")
	public static Double southArmorBonus = 0.0;

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southManaBonus", comment = "The mana bonus given to South-type dragons")
	public static Double southManaBonus = 0.0;

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southSwimSpeedBonus", comment = "The swimSpeed bonus given to South-type dragons")
	public static Double southSwimSpeedBonus = 0.0;

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southHealthBonus", comment = "The health bonus given to South-type dragons")
	public static Double southHealthBonus = 0.0;

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southKnockbackBonus", comment = "The knockback bonus given to South-type dragons")
	public static Double southKnockbackBonus = 0.0;

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southRunMult", comment = "The run speed multiplier given to South-type dragons")
	public static Double southRunMult = 1.0;

	@ConfigRange(min = 0.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southDamageMult", comment = "The damage multiplier given to South-type dragons")
	public static Double southDamageMult = 1.0;

	@ConfigRange(min = 0.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southExpMult", comment = "The exp multiplier given to South-type dragons")
	public static Double southExpMult = 1.0;

	@ConfigRange(min = 0.0, max = 10)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southFlightMult", comment = "The flight multiplier given to South-type dragons")
	public static Double southFlightMult = 1.0;

	@ConfigRange(min = 0.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southFlightStaminaMult", comment = "The flightStamina multiplier given to South-type dragons")
	public static Double southFlightStaminaMult = 1.0;

	@ConfigRange(min = 0.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southHealthMult", comment = "The health multiplier given to South-type dragons")
	public static Double southHealthMult = 1.0;

	@ConfigRange(min = 0.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southGravityMult", comment = "The gravity multiplier given to South-type dragons")
	public static Double southGravityMult = 1.0;

	@Override
	public CompoundTag writeNBT() {
		CompoundTag tag = new CompoundTag();
		return tag;
	}

	@Override
	public void readNBT(CompoundTag base) {}

	@Override
	public String getBodyName() {
		return "south";
	}

	@Override
	public void onPlayerUpdate() {}

	@Override
	public void onPlayerDeath() {}

	public Double getJumpBonus() {
		return southJumpBonus;
	}
	public Double getStepBonus() {
		return southStepBonus;
	}
	public Double getDamageBonus() {
		return southDamageBonus;
	}
	public Double getArmorBonus() {
		return southArmorBonus;
	}
	public Double getManaBonus() {
		return southManaBonus;
	}
	public Double getSwimSpeedBonus() {
		return southSwimSpeedBonus;
	}
	public Double getHealthBonus() {
		return southHealthBonus;
	}
	public Double getKnockbackBonus() {
		return southKnockbackBonus;
	}
	public Double getRunMult() {
		return southRunMult;
	}
	public Double getDamageMult() {
		return southDamageMult;
	}
	public Double getExpMult() {
		return southExpMult;
	}
	public Double getFlightMult() {
		return southFlightMult;
	}
	public Double getFlightStaminaMult() {
		return southFlightStaminaMult;
	}
	public Double getHealthMult() {
		return southHealthMult;
	}
	public Double getGravityMult() {
		return southGravityMult;
	}
}
