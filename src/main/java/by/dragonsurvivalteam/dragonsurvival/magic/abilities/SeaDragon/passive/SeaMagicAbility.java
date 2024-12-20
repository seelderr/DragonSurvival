/*package by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.passive;


@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = {
        "■ Magic points (Mana) are used for dragon magic. Restores on wet blocks.\n",
        "■ Current amount of §2%s§r mana:",
        " - §2%s§r from «Sea Magic»",
        " - §2%s§r body type",
        " - §2%s§r from experience"
})
@Translation(type = Translation.Type.ABILITY, comments = "Sea Magic")
@RegisterDragonAbility
public class SeaMagicAbility extends MagicAbility {
    @Translation(key = "sea_magic", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the sea magic ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "passive"}, key = "sea_magic")
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
        return new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_0"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_1"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_2"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_3"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_4"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_5"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_6"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_7"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_8"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_9"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_10"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_11"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_12"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_13"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_14"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_15"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_16"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_17"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_18"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_19"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_magic_20")
        };
    }
}*/