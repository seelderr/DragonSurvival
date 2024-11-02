package by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.active;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.AoeBuffAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DSParticles;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

@RegisterDragonAbility
public class InspirationAbility extends AoeBuffAbility {
    @Translation(key = "inspiration", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the inspiration ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "active", "inspiration"}, key = "inspiration")
    public static Boolean inspirationEnabled = true;

    @ConfigRange(min = 0.05, max = 10_000.0)
    @Translation(key = "inspiration_cooldown", type = Translation.Type.CONFIGURATION, comments = "Cooldown (in seconds) after using the ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "active", "inspiration"}, key = "inspiration_cooldown")
    public static Double inspirationCooldown = 60.0;

    @ConfigRange(min = 0.05, max = 10_000)
    @Translation(key = "inspiration_cast_time", type = Translation.Type.CONFIGURATION, comments = "Cast time (in seconds)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "active", "inspiration"}, key = "inspiration_cast_time")
    public static Double inspirationCasttime = 1.0;

    @ConfigRange(min = 1.0, max = 10_000.0)
    @Translation(key = "inspiration_duration", type = Translation.Type.CONFIGURATION, comments = "The duration (in seconds) of the effect")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "active", "inspiration"}, key = "inspiration_duration")
    public static Double inspirationDuration = 200.0;

    @ConfigRange(min = 0, max = 100)
    @Translation(key = "inspiration_mana_cost", type = Translation.Type.CONFIGURATION, comments = "Mana cost")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "active", "inspiration"}, key = "inspiration_mana_cost")
    public static Integer inspirationManaCost = 1;

    @Override
    public String getName() {
        return "inspiration";
    }

    @Override
    public int getSortOrder() {
        return 3;
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.FOREST;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/inspiration_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/inspiration_1.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/inspiration_2.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/inspiration_3.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/inspiration_4.png")};
    }


    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getMinLevel() {
        return 0;
    }

    @Override
    public int getManaCost() {
        return inspirationManaCost;
    }

    @Override
    public Integer[] getRequiredLevels() {
        return new Integer[]{0, 15, 35, 50};
    }

    @Override
    public int getSkillCooldown() {
        return Functions.secondsToTicks(inspirationCooldown);
    }

    @Override
    public int getRange() {
        return 5;
    }

    @Override
    public ParticleOptions getParticleEffect() {
        return DSParticles.FIRE_BEACON_PARTICLE.value();
    }

    @Override
    public MobEffectInstance getEffect() {
        return new MobEffectInstance(MobEffects.DIG_SPEED, Functions.secondsToTicks(inspirationDuration), 2);
    }

    @Override
    public int getSkillCastingTime() {
        return Functions.secondsToTicks(inspirationCasttime);
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !inspirationEnabled;
    }
}