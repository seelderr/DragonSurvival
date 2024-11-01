package by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive;


import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.config.server.dragon.ForestDragonConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.PassiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

@RegisterDragonAbility
public class LightInDarknessAbility extends PassiveDragonAbility {
    @Translation(key = "light_in_darkness", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the light in darkness ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "passive"}, key = "lightInDarkness")
    public static Boolean lightInDarkness = true;

    @Override
    public Component getDescription() {
        return Component.translatable("ds.skill.description." + getName(), getDuration() + Functions.ticksToSeconds(ForestDragonConfig.stressTicks));
    }

    @Override
    public int getSortOrder() {
        return 3;
    }

    @Override
    public String getName() {
        return "light_in_darkness";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.FOREST;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/light_in_darkness_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/light_in_darkness_1.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/light_in_darkness_2.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/light_in_darkness_3.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/light_in_darkness_4.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/light_in_darkness_5.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/light_in_darkness_6.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/light_in_darkness_7.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/light_in_darkness_8.png")
        };
    }

    public int getDuration() {
        return 10 * getLevel();
    }

    @Override
    public ArrayList<Component> getLevelUpInfo() {
        ArrayList<Component> list = super.getLevelUpInfo();
        list.add(Component.translatable("ds.skill.duration.seconds", "+10"));
        return list;
    }

    @Override
    public int getMaxLevel() {
        return 8;
    }

    @Override
    public int getMinLevel() {
        return 0;
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !lightInDarkness;
    }
}