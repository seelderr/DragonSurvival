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
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southRunBonus", comment = "The run bonus given to South-type dragons")
	public static Double southRunBonus = 0.0;

	//@ConfigRange(min = -10.0, max = 100)
	//@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southFlightBonus", comment = "The flight bonus given to South-type dragons")
	public static Double southFlightBonus = 0.0;

	//@ConfigRange(min = -10.0, max = 100)
	//@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southStepBonus", comment = "The step bonus given to South-type dragons")
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

	//@ConfigRange(min = -10.0, max = 100)
	//@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southSwimSpeedBonus", comment = "The swimSpeed bonus given to South-type dragons")
	public static Double southSwimSpeedBonus = 0.0;

	@ConfigRange(min = 0.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southDamageMult", comment = "The damage multiplier given to South-type dragons")
	public static Double southDamageMult = 1.0;

	@ConfigRange(min = 0.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "southExpMult", comment = "The exp multiplier given to South-type dragons")
	public static Double southExpMult = 1.0;

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
	public Double getRunBonus() {
		return southRunBonus;
	}
	public Double getFlightBonus() {
		return southFlightBonus;
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
	public Double getDamageMult() {
		return southDamageMult;
	}
	public Double getExpMult() {
		return southExpMult;
	}
}
