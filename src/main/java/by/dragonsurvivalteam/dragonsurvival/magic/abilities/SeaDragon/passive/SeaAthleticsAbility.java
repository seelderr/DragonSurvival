/*package by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.passive;


@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = {
        "■ Standing on some blocks will give you §2Speed %s§r\n",
        "■ Duration: §2%s§rs"
})
@Translation(type = Translation.Type.ABILITY, comments = "Sea Athletics")
@RegisterDragonAbility
public class SeaAthleticsAbility extends AthleticsAbility {
    @Translation(key = "sea_athletics", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the sea athletics ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "passive"}, key = "sea_athletics")
    public static Boolean seaAthletics = true;

    @Override
    public String getName() {
        return "sea_athletics";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.SEA;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_athletics_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_athletics_1.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_athletics_2.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_athletics_3.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_athletics_4.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_athletics_5.png")
        };
    }

    @Override
    public int getSortOrder() {
        return 2;
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !seaAthletics;
    }
}*/