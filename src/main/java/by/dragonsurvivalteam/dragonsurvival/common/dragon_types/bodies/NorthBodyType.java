package by.dragonsurvivalteam.dragonsurvival.common.dragon_types.bodies;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import net.minecraft.nbt.CompoundTag;

public class NorthBodyType extends AbstractDragonBody {
    @ConfigRange(min = -1.0, max = 100)
    @Translation(key = "north_jump_bonus", type = Translation.Type.CONFIGURATION, comments = "Jump bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "north_jump_bonus")
    public static Double northJumpBonus = 0.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "north_step_height_bonus", type = Translation.Type.CONFIGURATION, comments = "Step height bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "north_step_height_bonus")
    public static Double northStepBonus = 0.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "north_damage_bonus", type = Translation.Type.CONFIGURATION, comments = "Damage bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "north_damage_bonus")
    public static Double northDamageBonus = 0.0;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "north_damage_multiplier", type = Translation.Type.CONFIGURATION, comments = "Damage multiplier (multiply total)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "north_damage_multiplier")
    public static Double northDamageMult = 0.8;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "north_armor_bonus", type = Translation.Type.CONFIGURATION, comments = "Armor bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "north_armor_bonus")
    public static Double northArmorBonus = 0.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "north_mana_bonus", type = Translation.Type.CONFIGURATION, comments = "Mana bonus")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "north_mana_bonus")
    public static Double northManaBonus = 2.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "north_swim_speed_bonus", type = Translation.Type.CONFIGURATION, comments = "Swim speed bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "north_swim_speed_bonus")
    public static Double northSwimSpeedBonus = 1.5;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "north_health_bonus", type = Translation.Type.CONFIGURATION, comments = "Health bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "north_health_bonus")
    public static Double northHealthBonus = 0.0;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "north_health_multiplier", type = Translation.Type.CONFIGURATION, comments = "Health multiplier (multiply total)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "north_health_multiplier")
    public static Double northHealthMult = 1.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "north_knockback_bonus", type = Translation.Type.CONFIGURATION, comments = "Knockback bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "north_knockback_bonus")
    public static Double northKnockbackBonus = -0.5;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "north_movement_speed_multiplier", type = Translation.Type.CONFIGURATION, comments = "Movement speed multiplier (multiply total)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "north_movement_speed_multiplier")
    public static Double northRunMult = 0.7;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "north_experience_multiplier", type = Translation.Type.CONFIGURATION, comments = "Experience multiplier")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "north_experience_multiplier")
    public static Double northExpMult = 1.0;

    @ConfigRange(min = 1.0, max = 10)
    @Translation(key = "north_flight_multiplier", type = Translation.Type.CONFIGURATION, comments = "Flight multiplier - values below 1 will cause the dragon to fall instead of flying")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "north_flight_multiplier")
    public static Double northFlightMult = 1.0;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "north_flight_stamina_multiplier", type = Translation.Type.CONFIGURATION, comments = "Flight stamina multiplier (multiply total) - higher values increase the exhaustion rate")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "north_flight_stamina_multiplier")
    public static Double northFlightStaminaMult = 1.2;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "north_gravity_multiplier", type = Translation.Type.CONFIGURATION, comments = "Gravity multiplier (multiply total) - higher values increase fall speed while flying and cause faster drowning")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "north"}, key = "north_gravity_multiplier")
    public static Double northGravityMult = 1.0;

    @Override
    public CompoundTag writeNBT() {
        return new CompoundTag();
    }

    @Override
    public void readNBT(CompoundTag base) {
    }

    @Override
    public String getBodyName() {
        return "north";
    }

    public Double getHeightMult() {
        return 0.55;
    }

    public Double getEyeHeightMult() {
        return 0.55;
    }

    public Boolean isSquish() {
        return AbstractDragonBody.bodyAffectsHitbox;
    }

    @Override
    public void onPlayerUpdate() {
    }

    @Override
    public void onPlayerDeath() {
    }

    public Double getJumpBonus() {
        return northJumpBonus;
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

    public Double getHealthBonus() {
        return northHealthBonus;
    }

    public Double getKnockbackBonus() {
        return northKnockbackBonus;
    }

    public Double getRunMult() {
        return northRunMult;
    }

    public Double getDamageMult() {
        return northDamageMult;
    }

    public Double getExpMult() {
        return northExpMult;
    }

    public Double getFlightMult() {
        return northFlightMult;
    }

    public Double getFlightStaminaMult() {
        return northFlightStaminaMult;
    }

    public Double getHealthMult() {
        return northHealthMult;
    }

    public Double getGravityMult() {
        return northGravityMult;
    }
}
