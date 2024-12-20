/*package by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive;


@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = {
        "■ Magic points (Mana) are used for dragon magic. Restores while standing on hot blocks.\n",
        "■ Current amount of §2%s§r mana:",
        " - §2%s§r from «Cave Magic»",
        " - §2%s§r from body type",
        " - §2%s§r from experience"
})
@Translation(type = Translation.Type.ABILITY, comments = "Cave Magic")
@RegisterDragonAbility
public class CaveMagicAbility extends MagicAbility {
    @Translation(key = "cave_magic", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the cave magic ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "passive"}, key = "cave_magic")
    public static Boolean caveMagic = true;

    @Override
    public int getSortOrder() {
        return 1;
    }

    @Override
    public String getName() {
        return "cave_magic";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.CAVE;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_0"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_1"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_2"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_3"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_4"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_5"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_6"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_7"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_8"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_9"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_10"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_11"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_12"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_13"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_14"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_15"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_16"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_17"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_18"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_19"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/cave_magic_20")
        };
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !caveMagic;
    }
}*/