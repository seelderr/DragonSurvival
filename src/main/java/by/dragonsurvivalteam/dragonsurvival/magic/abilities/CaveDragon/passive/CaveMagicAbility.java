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
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_1.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_2.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_3.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_4.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_5.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_6.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_7.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_8.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_9.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_10.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_11.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_12.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_13.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_14.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_15.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_16.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_17.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_18.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_19.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_20.png")
        };
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !caveMagic;
    }
}*/