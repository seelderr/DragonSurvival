/*package by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive;


@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = {
        "■ Magic points (Mana) are used for dragon magic. Restores under direct sunlight and on grass.\n",
        "■ Current amount of §2%s§r mana:",
        " - §2%s§r from «Forest Magic»",
        " - §2%s§r from body type",
        " - §2%s§r from experience"
})
@Translation(type = Translation.Type.ABILITY, comments = "Forest Magic")
@RegisterDragonAbility
public class ForestMagicAbility extends MagicAbility {
    @Translation(key = "forest_magic", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the forest magic ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "passive"}, key = "forest_magic")
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
        return new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_0"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_1"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_2"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_3"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_4"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_5"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_6"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_7"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_8"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_9"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_10"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_11"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_12"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_13"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_14"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_15"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_16"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_17"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_18"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_19"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/forest_magic_20")
        };
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !forestMagic;
    }
}*/