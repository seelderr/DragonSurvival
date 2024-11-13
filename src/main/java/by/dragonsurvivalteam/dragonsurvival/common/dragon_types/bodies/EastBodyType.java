package by.dragonsurvivalteam.dragonsurvival.common.dragon_types.bodies;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class EastBodyType extends AbstractDragonBody {
    @Translation(type = Translation.Type.MISC, comments = "East")
    private static final String NAME = Translation.Type.DESCRIPTION.wrap("body.east");

    @Translation(type = Translation.Type.MISC, comments = {
            "§6■ Eastern Type§r",
            "■ Adapted to life in caves, they lack large wings, reducing the effectiveness of their levitation magic, but they are still excellent swimmers. They have a larger supply of mana, and natural armor.",
            "§7■ You may change your body type at any time, but you will lose your growth progress."
    })
    private static final String INFO = Translation.Type.DESCRIPTION.wrap("body.east_info");

    @ConfigRange(min = -1.0, max = 100)
    @Translation(key = "east_jump_bonus", type = Translation.Type.CONFIGURATION, comments = "Jump bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "east"}, key = "east_jump_bonus")
    public static Double eastJumpBonus = 0.1;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "east_step_height_bonus", type = Translation.Type.CONFIGURATION, comments = "Step height bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "east"}, key = "east_step_height_bonus")
    public static Double eastStepBonus = 0.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "east_damage_bonus", type = Translation.Type.CONFIGURATION, comments = "Damage bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "east"}, key = "east_damage_bonus")
    public static Double eastDamageBonus = -1.0;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "east_damage_multiplier", type = Translation.Type.CONFIGURATION, comments = "Damage multiplier (multiply total)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "east"}, key = "east_damage_multiplier")
    public static Double eastDamageMult = 1.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "east_armor_bonus", type = Translation.Type.CONFIGURATION, comments = "Armor bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "east"}, key = "east_armor_bonus")
    public static Double eastArmorBonus = 2.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "east_mana_bonus", type = Translation.Type.CONFIGURATION, comments = "Mana bonus")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "east"}, key = "east_mana_bonus")
    public static Double eastManaBonus = 2.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "east_swim_speed_bonus", type = Translation.Type.CONFIGURATION, comments = "Swim speed bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "east"}, key = "east_swim_speed_bonus")
    public static Double eastSwimSpeedBonus = 1;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "east_health_bonus", type = Translation.Type.CONFIGURATION, comments = "Health bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "east"}, key = "east_health_bonus")
    public static Double eastHealthBonus = 0.0;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "east_health_multiplier", type = Translation.Type.CONFIGURATION, comments = "Health multiplier (multiply total)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "east"}, key = "east_health_multiplier")
    public static Double eastHealthMult = 1.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "east_knockback_bonus", type = Translation.Type.CONFIGURATION, comments = "Knockback bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "east"}, key = "east_knockback_bonus")
    public static Double eastKnockbackBonus = -1.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "east_movement_speed_multiplier", type = Translation.Type.CONFIGURATION, comments = "Movement speed multiplier (multiply total)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "east"}, key = "east_movement_speed_multiplier")
    public static Double eastRunMult = 1.1;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "east_experience_multiplier", type = Translation.Type.CONFIGURATION, comments = "Experience multiplier")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "east"}, key = "east_experience_multiplier")
    public static Double eastExpMult = 1.0;

    @ConfigRange(min = 1.0, max = 10)
    @Translation(key = "east_flight_multiplier", type = Translation.Type.CONFIGURATION, comments = "Flight multiplier - values below 1 will cause the dragon to fall instead of flying")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "east"}, key = "east_flight_multiplier")
    public static Double eastFlightMult = 1.0;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "east_flight_stamina_multiplier", type = Translation.Type.CONFIGURATION, comments = "Flight stamina multiplier (multiply total) - higher values increase the exhaustion rate")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "east"}, key = "east_flight_stamina_multiplier")
    public static Double eastFlightStaminaMult = 0.8;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "east_gravity_multiplier", type = Translation.Type.CONFIGURATION, comments = "Gravity multiplier (multiply total) - higher values increase fall speed while flying and cause faster drowning")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "east"}, key = "east_gravity_multiplier")
    public static Double eastGravityMult = 1.1;

    @Override
    public CompoundTag writeNBT() {
        return new CompoundTag();
    }

    @Override
    public void readNBT(CompoundTag base) {
    }

    @Override
    public String getBodyName() {
        return "east";
    }

    @Override
    public Component translatableName() {
        return Component.translatable(NAME);
    }

    @Override
    public Component translatableInfo() {
        return Component.translatable(INFO);
    }

    @Override
    public void onPlayerUpdate() {
    }

    @Override
    public void onPlayerDeath() {
    }

    @Override
    public Double getJumpBonus() {
        return eastJumpBonus;
    }

    @Override
    public Double getFlightMult() {
        return eastFlightMult;
    }

    @Override
    public Double getStepBonus() {
        return eastStepBonus;
    }

    @Override
    public Double getDamageBonus() {
        return eastDamageBonus;
    }

    @Override
    public Double getArmorBonus() {
        return eastArmorBonus;
    }

    @Override
    public Double getManaBonus() {
        return eastManaBonus;
    }

    @Override
    public Double getSwimSpeedBonus() {
        return eastSwimSpeedBonus;
    }

    @Override
    public Double getHealthBonus() {
        return eastHealthBonus;
    }

    @Override
    public Double getKnockbackBonus() {
        return eastKnockbackBonus;
    }

    @Override
    public Double getRunMult() {
        return eastRunMult;
    }

    @Override
    public Double getDamageMult() {
        return eastDamageMult;
    }

    @Override
    public Double getExpMult() {
        return eastExpMult;
    }

    @Override
    public Double getFlightStaminaMult() {
        return eastFlightStaminaMult;
    }

    @Override
    public Double getHealthMult() {
        return eastHealthMult;
    }

    @Override
    public Double getGravityMult() {
        return eastGravityMult;
    }
}
