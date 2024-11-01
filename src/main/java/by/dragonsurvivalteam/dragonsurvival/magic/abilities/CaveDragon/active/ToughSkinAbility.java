package by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.active;


import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.input.Keybind;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.AoeBuffAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.DSParticles;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.Locale;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

@RegisterDragonAbility
public class ToughSkinAbility extends AoeBuffAbility {
    @Translation(key = "tough_skin", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the tough skin ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "tough_skin"}, key = "toughSkinEnabled")
    public static Boolean toughSkinEnabled = true;

    @ConfigRange(min = 1.0, max = 10_000.0)
    @Translation(key = "tough_skin_duration", type = Translation.Type.CONFIGURATION, comments = "The duration (in seconds) of the effect")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "tough_skin"}, key = "tough_skin_duration")
    public static Double toughSkinDuration = 200.0;

    @ConfigRange(min = 1.0, max = 10_000.0)
    @Translation(key = "tough_skin_cooldown", type = Translation.Type.CONFIGURATION, comments = "Cooldown (in seconds) after using the ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "tough_skin"}, key = "tough_skin_cooldown")
    public static Double toughSkinCooldown = 30.0;

    @ConfigRange(min = 1, max = 10_000)
    @Translation(key = "tough_skin_cast_time", type = Translation.Type.CONFIGURATION, comments = "Cast time (in seconds)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "tough_skin"}, key = "tough_skin_cast_time")
    public static Double toughSkinCasttime = 1.0;

    @ConfigRange(min = 0, max = 100)
    @Translation(key = "tough_skin_mana_cost", type = Translation.Type.CONFIGURATION, comments = "Mana cost")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "tough_skin"}, key = "tough_skin_mana_cost")
    public static Integer toughSkinManaCost = 1;

    @ConfigRange(min = 0, max = 10_000)
    @Translation(key = "tough_skin_armor_scaling", type = Translation.Type.CONFIGURATION, comments = "Amount of extra armor per level of tough skin effect")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "tough_skin"}, key = "tough_skin_armor_scaling")
    public static Double toughSkinArmorValue = 3.0;

    @Override
    public int getSortOrder() {
        return 3;
    }

    @Override
    public int getSkillCastingTime() {
        return Functions.secondsToTicks(toughSkinCasttime);
    }

    @Override
    public ArrayList<Component> getInfo() {
        ArrayList<Component> components = super.getInfo();

        if (!Keybind.ABILITY3.get().isUnbound()) {
            components = new ArrayList<>(components.subList(0, components.size() - 1));
        }

        components.add(Component.translatable("ds.skill.duration.seconds", toughSkinDuration));

        if (!Keybind.ABILITY3.get().isUnbound()) {

            String key = Keybind.ABILITY3.getKey().getDisplayName().getString().toUpperCase(Locale.ROOT);

            if (key.isEmpty()) {
                key = Keybind.ABILITY3.getKey().getDisplayName().getString();
            }
            components.add(Component.translatable("ds.skill.keybind", key));
        }

        return components;
    }

    @Override
    public int getRange() {
        return 5;
    }

    @Override
    public ParticleOptions getParticleEffect() {
        return DSParticles.PEACE_BEACON_PARTICLE.value();
    }

    @Override
    public int getManaCost() {
        return toughSkinManaCost;
    }

    @Override
    public Integer[] getRequiredLevels() {
        return new Integer[]{0, 15, 35};
    }

    @Override
    public int getSkillCooldown() {
        return Functions.secondsToTicks(toughSkinCooldown);
    }

    @Override
    public MobEffectInstance getEffect() {
        return new MobEffectInstance(DSEffects.STRONG_LEATHER, Functions.secondsToTicks(toughSkinDuration), getLevel() - 1);
    }

    @Override
    public Component getDescription() {
        return Component.translatable("ds.skill.description." + getName(), toughSkinDuration, getDefence(getLevel()));
    }

    @Override
    public String getName() {
        return "strong_leather";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.CAVE;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/strong_leather_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/strong_leather_1.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/strong_leather_2.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/strong_leather_3.png")};
    }


    public static double getDefence(int level) {
        return level * toughSkinArmorValue;
    }

    @Override
    public ArrayList<Component> getLevelUpInfo() {
        ArrayList<Component> list = super.getLevelUpInfo();
        list.add(Component.translatable("ds.skill.defence", "+" + toughSkinArmorValue));
        return list;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinLevel() {
        return 0;
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !toughSkinEnabled;
    }
}