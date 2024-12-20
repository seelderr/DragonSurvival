/*package by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive;


@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = "Your landing becomes much softer. Safe fall distance: §2%sb§r.")
@Translation(type = Translation.Type.ABILITY, comments = "Cliffhanger")
@RegisterDragonAbility
public class CliffhangerAbility extends PassiveDragonAbility {
    @Translation(key = "cliff_hanger", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the cliff hanger ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "passive"}, key = "cliff_hanger")
    public static Boolean cliffHanger = true;

    @Translation(key = "cliff_hanger_base_fall_reduction", type = Translation.Type.CONFIGURATION, comments = "How many blocks of fall damage is mitigated for cliffhanger level 0.")
    @ConfigRange(min = 0.0, max = 100.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "forest_dragon", "passives"}, key = "cliff_hanger_base_fall_reduction")
    public static Double cliffHangerBaseFallReduction = 5.0;

    @Override
    public Component getDescription() {
        return Component.translatable(Translation.Type.ABILITY_DESCRIPTION.wrap(getName()), 3 + getHeight() + ForestDragonConfig.fallReduction);
    }

    @Override
    public int getSortOrder() {
        return 4;
    }

    @Override
    public String getName() {
        return "cliffhanger";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.FOREST;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/cliffhanger_0"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/cliffhanger_1"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/cliffhanger_2"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/cliffhanger_3"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/cliffhanger_4"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/cliffhanger_5"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/cliffhanger_6")};
    }

    public int getHeight() {
        return getLevel() + cliffHangerBaseFallReduction.intValue();
    }

    @Override
    public ArrayList<Component> getLevelUpInfo() {
        ArrayList<Component> list = super.getLevelUpInfo();
        list.add(Component.translatable(LangKey.ABILITY_RANGE, "+1"));
        return list;
    }

    @Override
    public int getMaxLevel() {
        return 6;
    }

    @Override
    public int getMinLevel() {
        return 0;
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !cliffHanger;
    }
}*/