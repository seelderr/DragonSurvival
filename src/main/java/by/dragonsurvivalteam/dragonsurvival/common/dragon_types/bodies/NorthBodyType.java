package by.dragonsurvivalteam.dragonsurvival.common.dragon_types.bodies;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import net.minecraft.nbt.CompoundTag;

public class NorthBodyType extends AbstractDragonBody {

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "northJumpBonus", comment = "The jump bonus given to North-type dragons")
	public static Double northJumpBonus = 0.0;

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "northRunBonus", comment = "The run bonus given to North-type dragons")
	public static Double northRunBonus = 0.0;

	//@ConfigRange(min = -10.0, max = 100)
	//@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "northFlightBonus", comment = "The flight bonus given to North-type dragons")
	public static Double northFlightBonus = 0.0;

	//@ConfigRange(min = -10.0, max = 100)
	//@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "northStepBonus", comment = "The step bonus given to North-type dragons")
	public static Double northStepBonus = 0.0;

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "northDamageBonus", comment = "The damage bonus given to North-type dragons")
	public static Double northDamageBonus = 0.0;

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "northArmorBonus", comment = "The armor bonus given to North-type dragons")
	public static Double northArmorBonus = 0.0;

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "northManaBonus", comment = "The mana bonus given to North-type dragons")
	public static Double northManaBonus = 0.0;

	//@ConfigRange(min = -10.0, max = 100)
	//@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "northSwimSpeedBonus", comment = "The swimSpeed bonus given to North-type dragons")
	public static Double northSwimSpeedBonus = 0.0;

	@ConfigRange(min = 0.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "northDamageMult", comment = "The damage multiplier given to North-type dragons")
	public static Double northDamageMult = 1.0;

	@ConfigRange(min = 0.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "northExpMult", comment = "The exp multiplier given to North-type dragons")
	public static Double northExpMult = 1.0;

	@Override
	public CompoundTag writeNBT() {
		CompoundTag tag = new CompoundTag();
		return tag;
	}

	@Override
	public void readNBT(CompoundTag base) {}

	@Override
	public String getBodyName() {
		return "north";
	}

	@Override
	public void onPlayerUpdate() {}

	@Override
	public void onPlayerDeath() {}

	public Double getJumpBonus() {
		return northJumpBonus;
	}
	public Double getRunBonus() {
		return northRunBonus;
	}
	public Double getFlightBonus() {
		return northFlightBonus;
	}
	public Double getStepBonus() {
		return northStepBonus;
	}
	public Double getDamageBonus() {
		return northDamageBonus;
	}
	public Double getArmorBonus() {
		return northArmorBonus;
	}
	public Double getManaBonus() {
		return northManaBonus;
	}
	public Double getSwimSpeedBonus() {
		return northSwimSpeedBonus;
	}
	public Double getDamageMult() {
		return northDamageMult;
	}
	public Double getExpMult() {
		return northExpMult;
	}
}
