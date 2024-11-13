package by.dragonsurvivalteam.dragonsurvival.common.dragon_types.bodies;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class SouthBodyType extends AbstractDragonBody {
    @Translation(type = Translation.Type.MISC, comments = "South")
    private static final String NAME = Translation.Type.DESCRIPTION.wrap("body.south");

    @Translation(type = Translation.Type.MISC, comments = {
            "§6■ Southern Type§r",
            "■ They are adapted to life on the plains, capable of running swiftly, and leaping high into the air. The special structure of their paws gives them many advantages on the ground, and they are physically strong, but they will struggle at flight and swimming.",
            "§7■ You may change your body type at any time, but you will lose your growth progress. Each type has their own strengths and weaknesses, but the change is mostly cosmetic."
    })
    private static final String INFO = Translation.Type.DESCRIPTION.wrap("body.south_info");

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
    public static Double southFlightStaminaMult = 0.5;

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
        return southJumpBonus;
    }

    @Override
    public Double getStepBonus() {
        return southStepBonus;
    }

    @Override
    public Double getDamageBonus() {
        return southDamageBonus;
    }

    @Override
    public Double getArmorBonus() {
        return southArmorBonus;
    }

    @Override
    public Double getManaBonus() {
        return southManaBonus;
    }

    @Override
    public Double getSwimSpeedBonus() {
        return southSwimSpeedBonus;
    }

    @Override
    public Double getHealthBonus() {
        return southHealthBonus;
    }

    @Override
    public Double getKnockbackBonus() {
        return southKnockbackBonus;
    }

    @Override
    public Double getRunMult() {
        return southRunMult;
    }

    @Override
    public Double getDamageMult() {
        return southDamageMult;
    }

    @Override
    public Double getExpMult() {
        return southExpMult;
    }

    @Override
    public Double getFlightMult() {
        return southFlightMult;
    }

    @Override
    public Double getFlightStaminaMult() {
        return southFlightStaminaMult;
    }

    @Override
    public Double getHealthMult() {
        return southHealthMult;
    }

    @Override
    public Double getGravityMult() {
        return southGravityMult;
    }
}
