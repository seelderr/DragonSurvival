package by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.innate;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.innate.InnateDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import net.minecraft.resources.ResourceLocation;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = {
        "■ Sea dragons are made mostly of water, with a golden skeleton. Their diet includes a variety of seafood, and peaceful animals fear dragons.\n",
        "■ They have innate immunity to lightning and §2unlimited oxygen§r§7. They feel best in the sea.§r"
})
@Translation(type = Translation.Type.ABILITY, comments = "Sea Dragon")
@RegisterDragonAbility
public class SeaDragonInfoAbility extends InnateDragonAbility {
    @Override
    public String getName() {
        return "sea_dragon";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.SEA;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_dragon_1.png")};
    }

    @Override
    public int getSortOrder() {
        return 3;
    }
}