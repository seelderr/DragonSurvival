package by.dragonsurvivalteam.dragonsurvival.common.dragon_types.bodies;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import net.minecraft.nbt.CompoundTag;

public class WestBodyType extends AbstractDragonBody {

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "westJumpBonus", comment = "The jump bonus given to West-type dragons")
	public static Double westJumpBonus = 0.0;

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "westRunBonus", comment = "The run bonus given to West-type dragons")
	public static Double westRunBonus = 0.0;

	//@ConfigRange(min = -10.0, max = 100)
	//@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "westFlightBonus", comment = "The flight bonus given to West-type dragons")
	public static Double westFlightBonus = 0.0;

	//@ConfigRange(min = -10.0, max = 100)
	//@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "westStepBonus", comment = "The step bonus given to West-type dragons")
	public static Double westStepBonus = 0.0;

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "westDamageBonus", comment = "The damage bonus given to West-type dragons")
	public static Double westDamageBonus = 0.0;

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "westArmorBonus", comment = "The armor bonus given to West-type dragons")
	public static Double westArmorBonus = 0.0;

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "westManaBonus", comment = "The mana bonus given to West-type dragons")
	public static Double westManaBonus = 0.0;

	//@ConfigRange(min = -10.0, max = 100)
	//@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "westSwimSpeedBonus", comment = "The swimSpeed bonus given to West-type dragons")
	public static Double westSwimSpeedBonus = 0.0;

	@ConfigRange(min = 0.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "westDamageMult", comment = "The damage multiplier given to West-type dragons")
	public static Double westDamageMult = 1.0;

	@ConfigRange(min = 0.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "westExpMult", comment = "The exp multiplier given to West-type dragons")
	public static Double westExpMult = 1.0;

	@Override
	public CompoundTag writeNBT() {
		CompoundTag tag = new CompoundTag();
		return tag;
	}

	@Override
	public void readNBT(CompoundTag base) {}

	@Override
	public String getBodyName() {
		return "west";
	}

	@Override
	public void onPlayerUpdate() {}

	@Override
	public void onPlayerDeath() {}

	public Double getJumpBonus() {
		return westJumpBonus;
	}
	public Double getRunBonus() {
		return westRunBonus;
	}
	public Double getFlightBonus() {
		return westFlightBonus;
	}
	public Double getStepBonus() {
		return westStepBonus;
	}
	public Double getDamageBonus() {
		return westDamageBonus;
	}
	public Double getArmorBonus() {
		return westArmorBonus;
	}
	public Double getManaBonus() {
		return westManaBonus;
	}
	public Double getSwimSpeedBonus() {
		return westSwimSpeedBonus;
	}
	public Double getDamageMult() {
		return westDamageMult;
	}
	public Double getExpMult() {
		return westExpMult;
	}
}
