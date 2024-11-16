package by.dragonsurvivalteam.dragonsurvival.common.dragon_types.bodies;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class WestBodyType extends AbstractDragonBody {
    @Translation(type = Translation.Type.MISC, comments = "West")
    private static final String NAME = Translation.Type.DESCRIPTION.wrap("body.west");

    @Translation(type = Translation.Type.MISC, comments = {
            "§6■ Western Type§r",
            "■ Conquerors of mountain and sky, they are unrivalled in their element, but are rather clumsy on the ground.",
            "§7■ You may change your body type at any time, but you will lose your growth progress."
    })
    private static final String INFO = Translation.Type.DESCRIPTION.wrap("body.west_info");

    @ConfigRange(min = -1.0, max = 100)
    @Translation(key = "west_jump_bonus", type = Translation.Type.CONFIGURATION, comments = "Jump bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "west_jump_bonus")
    public static Double westJumpBonus = 0.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "west_step_height_bonus", type = Translation.Type.CONFIGURATION, comments = "Step height bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "west_step_height_bonus")
    public static Double westStepBonus = 1.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "west_damage_bonus", type = Translation.Type.CONFIGURATION, comments = "Damage bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "west_damage_bonus")
    public static Double westDamageBonus = 0.0;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "west_damage_multiplier", type = Translation.Type.CONFIGURATION, comments = "Damage multiplier (multiply total)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "west_damage_multiplier")
    public static Double westDamageMult = 1.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "west_armor_bonus", type = Translation.Type.CONFIGURATION, comments = "Armor bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "west_armor_bonus")
    public static Double westArmorBonus = 0.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "west_mana_bonus", type = Translation.Type.CONFIGURATION, comments = "Mana bonus")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "west_mana_bonus")
    public static Double westManaBonus = 0.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "west_swim_speed_bonus", type = Translation.Type.CONFIGURATION, comments = "Swim speed bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "west_swim_speed_bonus")
    public static Double westSwimSpeedBonus = -0.3;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "west_health_bonus", type = Translation.Type.CONFIGURATION, comments = "Health bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "west_health_bonus")
    public static Double westHealthBonus = 0.0;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "west_health_multiplier", type = Translation.Type.CONFIGURATION, comments = "Health multiplier (multiply total)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "west_health_multiplier")
    public static Double westHealthMult = 1.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "west_knockback_bonus", type = Translation.Type.CONFIGURATION, comments = "Knockback bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "west_knockback_bonus")
    public static Double westKnockbackBonus = 0.5;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "west_movement_speed_multiplier", type = Translation.Type.CONFIGURATION, comments = "Movement speed multiplier (multiply total)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "west_movement_speed_multiplier")
    public static Double westRunMult = 0.7;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "west_experience_multiplier", type = Translation.Type.CONFIGURATION, comments = "Experience multiplier")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "west_experience_multiplier")
    public static Double westExpMult = 1.0;

    @ConfigRange(min = 0, max = 10)
    @Translation(key = "west_flight_multiplier", type = Translation.Type.CONFIGURATION, comments = "Flight multiplier - values below 1 will cause the dragon to fall instead of flying")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "west_flight_multiplier")
    public static Double westFlightMult = 1.2;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "west_flight_stamina_multiplier", type = Translation.Type.CONFIGURATION, comments = "Flight stamina multiplier (multiply total) - higher values increase the exhaustion rate")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "west_flight_stamina_multiplier")
    public static Double westFlightStaminaMult = 2.0;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "west_gravity_multiplier", type = Translation.Type.CONFIGURATION, comments = "Gravity multiplier (multiply total) - higher values increase fall speed while flying and cause faster drowning")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "west"}, key = "west_gravity_multiplier")
    public static Double westGravityMult = 1.0;

    @Override
    public CompoundTag writeNBT() {
        return new CompoundTag();
    }

    @Override
    public void readNBT(CompoundTag base) {
    }

    @Override
    public String getBodyName() {
        return "west";
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
    public Boolean canHideWings() {
        return false;
    }

    @Override
    public void onPlayerUpdate() {
    }

    @Override
    public void onPlayerDeath() {
    }

    @Override
    public Double getJumpBonus() {
        return westJumpBonus;
    }

    @Override
    public Double getStepBonus() {
        return westStepBonus;
    }

    @Override
    public Double getDamageBonus() {
        return westDamageBonus;
    }

    @Override
    public Double getArmorBonus() {
        return westArmorBonus;
    }

    @Override
    public Double getManaBonus() {
        return westManaBonus;
    }

    @Override
    public Double getSwimSpeedBonus() {
        return westSwimSpeedBonus;
    }

    @Override
    public Double getHealthBonus() {
        return westHealthBonus;
    }

    @Override
    public Double getKnockbackBonus() {
        return westKnockbackBonus;
    }

    @Override
    public Double getRunMult() {
        return westRunMult;
    }

    @Override
    public Double getDamageMult() {
        return westDamageMult;
    }

    @Override
    public Double getExpMult() {
        return westExpMult;
    }

    @Override
    public Double getFlightMult() {
        return westFlightMult;
    }

    @Override
    public Double getFlightStaminaMult() {
        return westFlightStaminaMult;
    }

    @Override
    public Double getHealthMult() {
        return westHealthMult;
    }

    @Override
    public Double getGravityMult() {
        return westGravityMult;
    }
}
