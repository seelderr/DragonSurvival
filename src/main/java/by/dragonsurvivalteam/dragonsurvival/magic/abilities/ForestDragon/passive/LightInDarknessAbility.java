/*package by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive;


@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = "■ Allows you to stay longer in dark areas. Delay before you gain §c«Stress»§r while in low light level: §2%ss§r.")
@Translation(type = Translation.Type.ABILITY, comments = "Light the Dark")
@RegisterDragonAbility
public class LightInDarknessAbility extends PassiveDragonAbility {
    @Translation(key = "light_in_darkness", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the light in darkness ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "passive"}, key = "light_in_darkness")
    public static Boolean lightInDarkness = true;

    @Override
    public Component getDescription() {
        return Component.translatable(Translation.Type.ABILITY_DESCRIPTION.wrap(getName()), getDuration() + Functions.ticksToSeconds(ForestDragonConfig.stressTicks));
    }

    @Override
    public int getSortOrder() {
        return 3;
    }

    @Override
    public String getName() {
        return "light_in_darkness";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.FOREST;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/light_in_darkness_0"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/light_in_darkness_1"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/light_in_darkness_2"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/light_in_darkness_3"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/light_in_darkness_4"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/light_in_darkness_5"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/light_in_darkness_6"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/light_in_darkness_7"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/light_in_darkness_8")
        };
    }

    public int getDuration() {
        return 10 * getLevel();
    }

    @Override
    public ArrayList<Component> getLevelUpInfo() {
        ArrayList<Component> list = super.getLevelUpInfo();
        list.add(Component.translatable(LangKey.ABILITY_DURATION, "+10"));
        return list;
    }

    @Override
    public int getMaxLevel() {
        return 8;
    }

    @Override
    public int getMinLevel() {
        return 0;
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !lightInDarkness;
    }
}*/