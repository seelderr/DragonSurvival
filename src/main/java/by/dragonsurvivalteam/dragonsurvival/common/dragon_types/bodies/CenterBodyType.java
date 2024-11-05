package by.dragonsurvivalteam.dragonsurvival.common.dragon_types.bodies;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class CenterBodyType extends AbstractDragonBody {
    @Translation(type = Translation.Type.MISC, comments = "Center")
    private static final String NAME = Translation.Type.DESCRIPTION.wrap("body.center");

    @Translation(type = Translation.Type.MISC, comments = {
            "§6■ Central Type§r",
            "■ Inhabitants of all biomes, and the most common type of dragon. They are the most balanced type of dragon, having no particular strengths or weaknesses.",
            "§7■ You may change your body type at any time, but you will lose your growth progress."
    })
    private static final String INFO = Translation.Type.DESCRIPTION.wrap("body.center_info");

    @ConfigRange(min = -1.0, max = 100)
    @Translation(key = "center_jump_bonus", type = Translation.Type.CONFIGURATION, comments = "Jump bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "center_jump_bonus")
    public static Double centerJumpBonus = 0.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "center_step_height_bonus", type = Translation.Type.CONFIGURATION, comments = "Step height bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "center_step_height_bonus")
    public static Double centerStepBonus = 0.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "center_damage_bonus", type = Translation.Type.CONFIGURATION, comments = "Damage bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "center_damage_bonus")
    public static Double centerDamageBonus = 0.0;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "center_damage_multiplier", type = Translation.Type.CONFIGURATION, comments = "Damage multiplier (multiply total)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "center_damage_multiplier")
    public static Double centerDamageMult = 1.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "center_armor_bonus", type = Translation.Type.CONFIGURATION, comments = "Armor bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "center_armor_bonus")
    public static Double centerArmorBonus = 0.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "center_mana_bonus", type = Translation.Type.CONFIGURATION, comments = "Mana bonus")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "center_mana_bonus")
    public static Double centerManaBonus = 0.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "center_swim_speed_bonus", type = Translation.Type.CONFIGURATION, comments = "Swim speed bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "center_swim_speed_bonus")
    public static Double centerSwimSpeedBonus = 0.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "center_health_bonus", type = Translation.Type.CONFIGURATION, comments = "Health bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "center_health_bonus")
    public static Double centerHealthBonus = 0.0;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "center_health_multiplier", type = Translation.Type.CONFIGURATION, comments = "Health multiplier (multiply total)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "center_health_multiplier")
    public static Double centerHealthMult = 1.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "center_knockback_bonus", type = Translation.Type.CONFIGURATION, comments = "Knockback bonus (additive)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "center_knockback_bonus")
    public static Double centerKnockbackBonus = 0.0;

    @ConfigRange(min = -10.0, max = 100)
    @Translation(key = "center_movement_speed_multiplier", type = Translation.Type.CONFIGURATION, comments = "Movement speed multiplier (multiply total)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "center_movement_speed_multiplier")
    public static Double centerRunMult = 1.0;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "center_experience_multiplier", type = Translation.Type.CONFIGURATION, comments = "Experience multiplier")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "center_experience_multiplier")
    public static Double centerExpMult = 1.0;

    @ConfigRange(min = 1.0, max = 10)
    @Translation(key = "center_flight_multiplier", type = Translation.Type.CONFIGURATION, comments = "Flight multiplier - values below 1 will cause the dragon to fall instead of flying")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "center_flight_multiplier")
    public static Double centerFlightMult = 1.2;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "center_flight_stamina_multiplier", type = Translation.Type.CONFIGURATION, comments = "Flight stamina multiplier (multiply total) - higher values increase the exhaustion rate")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "center_flight_stamina_multiplier")
    public static Double centerFlightStaminaMult = 1.0;

    @ConfigRange(min = 0.0, max = 100)
    @Translation(key = "center_gravity_multiplier", type = Translation.Type.CONFIGURATION, comments = "Gravity multiplier (multiply total) - higher values increase fall speed while flying and cause faster drowning")
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "body", "center"}, key = "center_gravity_multiplier")
    public static Double centerGravityMult = 1.0;

    @Override
    public CompoundTag writeNBT() {
        return new CompoundTag();
    }

    @Override
    public void readNBT(CompoundTag base) {
    }

    @Override
    public String getBodyName() {
        return "center";
    }

    @Override
    public Component translatableName() {
        return Component.translatable(NAME);
    }

    @Override
    public Component translatableInfo() {
        return Component.translatable(INFO);
    }

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
        return centerJumpBonus;
    }

    @Override
    public Double getStepBonus() {
        return centerStepBonus;
    }

    @Override
    public Double getDamageBonus() {
        return centerDamageBonus;
    }

    @Override
    public Double getArmorBonus() {
        return centerArmorBonus;
    }

    @Override
    public Double getManaBonus() {
        return centerManaBonus;
    }

    @Override
    public Double getSwimSpeedBonus() {
        return centerSwimSpeedBonus;
    }

    @Override
    public Double getHealthBonus() {
        return centerHealthBonus;
    }

    @Override
    public Double getKnockbackBonus() {
        return centerKnockbackBonus;
    }

    @Override
    public Double getRunMult() {
        return centerRunMult;
    }

    @Override
    public Double getDamageMult() {
        return centerDamageMult;
    }

    @Override
    public Double getExpMult() {
        return centerExpMult;
    }

    @Override
    public Double getFlightMult() {
        return centerFlightMult;
    }

    @Override
    public Double getFlightStaminaMult() {
        return centerFlightStaminaMult;
    }

    @Override
    public Double getHealthMult() {
        return centerHealthMult;
    }

    @Override
    public Double getGravityMult() {
        return centerGravityMult;
    }
}
