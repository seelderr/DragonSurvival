package by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.innate;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.innate.DragonWingAbility;
import net.minecraft.resources.ResourceLocation;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

@RegisterDragonAbility
public class ForestWingsAbility extends DragonWingAbility {
    @Override
    public String getName() {
        return "forest_wings";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.FOREST;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_wings_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_wings_1.png")};
    }


    @Override
    public int getSortOrder() {
        return 2;
    }
}