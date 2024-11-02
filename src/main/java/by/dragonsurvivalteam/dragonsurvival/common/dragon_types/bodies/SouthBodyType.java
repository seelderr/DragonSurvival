package by.dragonsurvivalteam.dragonsurvival.common.dragon_types.bodies;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import net.minecraft.nbt.CompoundTag;

public class SouthBodyType extends AbstractDragonBody {
    @ConfigRange(min = -1.0, max = 100)
    @Translation(key = "south_jump_bonus", type = Translation.Type.CONFIGURATION, comments = "Jump bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "south_jump_bonus")
    public static Double southJumpBonus = 0.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "south_step_height_bonus", type = Translation.Type.CONFIGURATION, comments = "Step height bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "south_step_height_bonus")
    public static Double southStepBonus = 0.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "south_damage_bonus", type = Translation.Type.CONFIGURATION, comments = "Damage bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "south_damage_bonus")
    public static Double southDamageBonus = 0.0;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "south_damage_multiplier", type = Translation.Type.CONFIGURATION, comments = "Damage multiplier (multiply total)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "south_damage_multiplier")
    public static Double southDamageMult = 1.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "south_armor_bonus", type = Translation.Type.CONFIGURATION, comments = "Armor bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "south_armor_bonus")
    public static Double southArmorBonus = 0.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "south_mana_bonus", type = Translation.Type.CONFIGURATION, comments = "Mana bonus")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "south_mana_bonus")
    public static Double southManaBonus = 0.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "south_swim_speed_bonus", type = Translation.Type.CONFIGURATION, comments = "Swim speed bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "south_swim_speed_bonus")
    public static Double southSwimSpeedBonus = 0.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "south_health_bonus", type = Translation.Type.CONFIGURATION, comments = "Health bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "south_health_bonus")
    public static Double southHealthBonus = 0.0;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "south_health_multiplier", type = Translation.Type.CONFIGURATION, comments = "Health multiplier (multiply total)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "south_health_multiplier")
    public static Double southHealthMult = 1.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "south_knockback_bonus", type = Translation.Type.CONFIGURATION, comments = "Knockback bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "south_knockback_bonus")
    public static Double southKnockbackBonus = 0.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "south_movement_speed_multiplier", type = Translation.Type.CONFIGURATION, comments = "Movement speed multiplier (multiply total)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "south_movement_speed_multiplier")
    public static Double southRunMult = 1.0;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "south_experience_multiplier", type = Translation.Type.CONFIGURATION, comments = "Experience multiplier")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "south_experience_multiplier")
    public static Double southExpMult = 1.0;

    @ConfigRange(min = 1.0, max = 10)
    @Translation(key = "south_flight_multiplier", type = Translation.Type.CONFIGURATION, comments = "Flight multiplier - values below 1 will cause the dragon to fall instead of flying")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "south_flight_multiplier")
    public static Double southFlightMult = 1.2;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "south_flight_stamina_multiplier", type = Translation.Type.CONFIGURATION, comments = "Flight stamina multiplier (multiply total) - higher values increase the exhaustion rate")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "south_flight_stamina_multiplier")
    public static Double southFlightStaminaMult = 1.0;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "south_gravity_multiplier", type = Translation.Type.CONFIGURATION, comments = "Gravity multiplier (multiply total) - higher values increase fall speed while flying and cause faster drowning")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "south"}, key = "south_gravity_multiplier")
    public static Double southGravityMult = 1.0;

    @Override
    public CompoundTag writeNBT() {
        return new CompoundTag();
    }

    @Override
    public void readNBT(CompoundTag base) {
    }

    @Override
    public String getBodyName() {
        return "south";
    }

    @Override
    public void onPlayerUpdate() {
    }

    @Override
    public void onPlayerDeath() {
    }

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
