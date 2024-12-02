/*package by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive;


@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = {
        "■ Standing on wooden or grassy blocks will give you §2Speed %s§r\n",
        "■ Duration: §2%s§rs"
})
@Translation(type = Translation.Type.ABILITY, comments = "Forest Athletics")
@RegisterDragonAbility
public class ForestAthleticsAbility extends AthleticsAbility {
    @Translation(key = "forest_athletics", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the forest athletics ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "passive"}, key = "forest_athletics")
    public static Boolean forestAthletics = true;

    @Override
    public String getName() {
        return "forest_athletics";
    }

    @Override
    public int getSortOrder() {
        return 2;
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.FOREST;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_athletics_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_athletics_1.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_athletics_2.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_athletics_3.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_athletics_4.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_athletics_5.png")
        };
    }

    @Override
    @SuppressWarnings("RedundantMethodOverride")
    public int getMaxLevel() {
        return 5;
    }

    @Override
    @SuppressWarnings("RedundantMethodOverride")
    public int getMinLevel() {
        return 0;
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !ForestAthleticsAbility.forestAthletics;
    }
}*/