package by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.MagicAbility;
import net.minecraft.resources.ResourceLocation;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

@RegisterDragonAbility
public class ForestMagicAbility extends MagicAbility {
    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "forest_dragon", "passives"}, key = "forestMagic", comment = "Whether the forest magic ability should be enabled")
    public static Boolean forestMagic = true;

    @Override
    public String getName() {
        return "forest_magic";
    }

    @Override
    public int getSortOrder() {
        return 1;
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.FOREST;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_1.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_2.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_3.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_4.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_5.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_6.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_7.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_8.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_9.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_10.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_11.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_12.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_13.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_14.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_15.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_16.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_17.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_18.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_19.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_magic_20.png")
        };
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !forestMagic;
    }
}