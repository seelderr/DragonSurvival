/*package by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.passive;


@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = "■ Gives a §c%s%%§r chance to make your attack ignore enemy armor.")
@Translation(type = Translation.Type.ABILITY, comments = "Spectral Impact")
@RegisterDragonAbility
public class SpectralImpactAbility extends PassiveDragonAbility {
    @Translation(key = "spectral_impact", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the spectral ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "passive"}, key = "spectral_impact")
    public static Boolean spectralImpact = true;

    @ConfigRange(min = 0, max = 100)
    @Translation(key = "spectral_impact_chance", type = Translation.Type.CONFIGURATION, comments = "Chance (in %) for this effect to occur (multiplied by the ability level)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "passive"}, key = "spectral_impact_chance")
    public static Integer spectralImpactProcChance = 15;

    @Override
    public Component getDescription() {
        return Component.translatable(Translation.Type.ABILITY_DESCRIPTION.wrap(getName()), getChance());
    }

    @Override
    public String getName() {
        return "spectral_impact";
    }

    @Override
    public int getSortOrder() {
        return 4;
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.SEA;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/spectral_impact_0"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/spectral_impact_1"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/spectral_impact_2"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/spectral_impact_3")};
    }

    public int getChance() {
        return spectralImpactProcChance * getLevel();
    }

    @Override
    public ArrayList<Component> getLevelUpInfo() {
        ArrayList<Component> list = super.getLevelUpInfo();
        list.add(Component.translatable(LangKey.ABILITY_CHANCE, "+" + spectralImpactProcChance));
        return list;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinLevel() {
        return 0;
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !spectralImpact;
    }
}*/