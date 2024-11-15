package by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.innate;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.innate.DragonWingAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import net.minecraft.resources.ResourceLocation;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = {
        "■ Dragons can use §2levitation§r to fly, but are rarely born with that ability. Only one dragon in this world can share this power with you.\n",
        "■ Enable flight: [%s]",
})
@Translation(type = Translation.Type.ABILITY, comments = "Forest Wings")
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
        return new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_wings_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_wings_1.png")
        };
    }


    @Override
    public int getSortOrder() {
        return 2;
    }
}