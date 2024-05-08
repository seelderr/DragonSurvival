package by.dragonsurvivalteam.dragonsurvival.common.dragon_types.bodies;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import net.minecraft.nbt.CompoundTag;

public class CenterBodyType extends AbstractDragonBody {

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "centerJumpBonus", comment = "The jump bonus given to Center-type dragons")
	public static Double centerJumpBonus = 0.0;

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "centerRunBonus", comment = "The run bonus given to Center-type dragons")
	public static Double centerRunBonus = 0.0;

	//@ConfigRange(min = -10.0, max = 100)
	//@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "centerFlightBonus", comment = "The flight bonus given to Center-type dragons")
	public static Double centerFlightBonus = 0.0;

	//@ConfigRange(min = -10.0, max = 100)
	//@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "centerStepBonus", comment = "The step bonus given to Center-type dragons")
	public static Double centerStepBonus = 0.0;

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "centerDamageBonus", comment = "The damage bonus given to Center-type dragons")
	public static Double centerDamageBonus = 0.0;

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "centerArmorBonus", comment = "The armor bonus given to Center-type dragons")
	public static Double centerArmorBonus = 0.0;

	@ConfigRange(min = -10.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "centerManaBonus", comment = "The mana bonus given to Center-type dragons")
	public static Double centerManaBonus = 0.0;

	//@ConfigRange(min = -10.0, max = 100)
	//@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "centerSwimSpeedBonus", comment = "The swimSpeed bonus given to Center-type dragons")
	public static Double centerSwimSpeedBonus = 0.0;

	@ConfigRange(min = 0.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "centerDamageMult", comment = "The damage multiplier given to Center-type dragons")
	public static Double centerDamageMult = 1.0;

	@ConfigRange(min = 0.0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "centerExpMult", comment = "The exp multiplier given to Center-type dragons")
	public static Double centerExpMult = 1.0;

	@Override
	public CompoundTag writeNBT() {
		CompoundTag tag = new CompoundTag();
		return tag;
	}

	@Override
	public void readNBT(CompoundTag base) {}

	@Override
	public String getBodyName() {
		return "center";
	}

	@Override
	public void onPlayerUpdate() {}

	@Override
	public void onPlayerDeath() {}

	public Double getJumpBonus() {
		return centerJumpBonus;
	}
	public Double getRunBonus() {
		return centerRunBonus;
	}
	public Double getFlightBonus() {
		return centerFlightBonus;
	}
	public Double getStepBonus() {
		return centerStepBonus;
	}
	public Double getDamageBonus() {
		return centerDamageBonus;
	}
	public Double getArmorBonus() {
		return centerArmorBonus;
	}
	public Double getManaBonus() {
		return centerManaBonus;
	}
	public Double getSwimSpeedBonus() {
		return centerSwimSpeedBonus;
	}
	public Double getDamageMult() {
		return centerDamageMult;
	}
	public Double getExpMult() {
		return centerExpMult;
	}
}
