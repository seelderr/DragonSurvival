/*package by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive;


@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = {
        "■ You are resistant to Rain, snow and snowfall for: §2%s§rs\n",
        "■ Water, potions and snowballs are still dangerous"
})
@Translation(type = Translation.Type.ABILITY, comments = "Contrast Shower")
@RegisterDragonAbility
public class ContrastShowerAbility extends PassiveDragonAbility {
    @Translation(key = "contrast_shower", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the contrast shower ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "passive"}, key = "contrast_shower")
    public static Boolean contrastShower = true;

    @Override
    public int getSortOrder() {
        return 3;
    }

    @Override
    public Component getDescription() {
        return Component.translatable(Translation.Type.ABILITY_DESCRIPTION.wrap(getName()), getDuration());
    }

    @Override
    public String getName() {
        return "contrast_shower";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.CAVE;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/contrast_shower_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/contrast_shower_1.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/contrast_shower_2.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/contrast_shower_3.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/contrast_shower_4.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/contrast_shower_5.png")
        };
    }


    public int getDuration() {
        return 30 * getLevel();
    }

    @Override
    public ArrayList<Component> getLevelUpInfo() {
        ArrayList<Component> list = super.getLevelUpInfo();
        list.add(Component.translatable(LangKey.ABILITY_DURATION, "+30"));
        return list;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getMinLevel() {
        return 0;
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !contrastShower;
    }
}*/