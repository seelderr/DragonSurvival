package by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive;


import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.PassiveDragonAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

@RegisterDragonAbility
public class ContrastShowerAbility extends PassiveDragonAbility {
    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "cave_dragon", "passives"}, key = "contrastShower", comment = "Whether the contrast shower ability should be enabled")
    public static Boolean contrastShower = true;

    @Override
    public int getSortOrder() {
        return 3;
    }

    @Override
    public Component getDescription() {
        return Component.translatable("ds.skill.description." + getName(), getDuration());
    }

    @Override
    public String getName() {
        return "contrast_shower";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.CAVE;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/contrast_shower_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/contrast_shower_1.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/contrast_shower_2.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/contrast_shower_3.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/contrast_shower_4.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/contrast_shower_5.png")};
    }


    public int getDuration() {
        return 30 * getLevel();
    }

    @Override
    public ArrayList<Component> getLevelUpInfo() {
        ArrayList<Component> list = super.getLevelUpInfo();
        list.add(Component.translatable("ds.skill.duration.seconds", "+30"));
        return list;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getMinLevel() {
        return 0;
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !contrastShower;
    }
}