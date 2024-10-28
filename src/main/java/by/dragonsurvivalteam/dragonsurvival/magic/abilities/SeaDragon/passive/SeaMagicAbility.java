package by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.passive;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.MagicAbility;
import net.minecraft.resources.ResourceLocation;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

@RegisterDragonAbility
public class SeaMagicAbility extends MagicAbility {
    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "sea_dragon", "passives"}, key = "seaMagic", comment = "Whether the sea magic ability should be enabled")
    public static Boolean seaMagic = true;

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !seaMagic;
    }

    @Override
    public String getName() {
        return "sea_magic";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.SEA;
    }

    @Override
    public int getSortOrder() {
        return 1;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_1.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_2.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_3.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_4.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_5.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_6.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_7.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_8.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_9.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_10.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_11.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_12.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_13.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_14.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_15.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_16.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_17.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_18.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_19.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_magic_20.png")
        };
    }
}