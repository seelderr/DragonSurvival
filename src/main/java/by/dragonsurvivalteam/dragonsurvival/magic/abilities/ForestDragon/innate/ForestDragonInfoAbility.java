package by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.innate;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.innate.InnateDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import net.minecraft.resources.ResourceLocation;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = {
        "■ Forest dragons have a diamond skeleton, and are composed mostly of predatory plants. Their diet includes raw meat and sweet berries, and most animals fear them.\n",
        "■ They have innate §2immunity to thorn bushes and cacti§r§7. They feel best on the surface of the Overworld.",
})
@Translation(type = Translation.Type.ABILITY, comments = "Forest Dragon")
@RegisterDragonAbility
public class ForestDragonInfoAbility extends InnateDragonAbility {
    @Override
    public String getName() {
        return "forest_dragon";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.FOREST;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_dragon_1.png")};
    }

    @Override
    public int getSortOrder() {
        return 3;
    }
}