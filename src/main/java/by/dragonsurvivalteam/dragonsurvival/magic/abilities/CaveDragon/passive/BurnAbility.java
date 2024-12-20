/*package by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive;


@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = {
        "■ Your target has a %s%% chance to receive the §c«Burned»§r effect from your attacks.\n",
        "The effect deals damage when the target moves.\n",
        "The faster the movement, the more damage is done.\n",
        "■ Creatures with fire resistance are immune to this effect."
})
@Translation(type = Translation.Type.ABILITY, comments = "Burn")
@RegisterDragonAbility
public class BurnAbility extends PassiveDragonAbility {
    @Translation(key = "burn", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the burn ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "passive"}, key = "burn")
    public static Boolean burn = true;

    @ConfigRange(min = 0, max = 100)
    @Translation(key = "burn_chance", type = Translation.Type.CONFIGURATION, comments = "The chance (in %) of the burn effect to apply (multiplied by the ability level)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "passive"}, key = "burn_chance")
    public static Integer burnProcChance = 15;

    @Override
    public Component getDescription() {
        return Component.translatable(Translation.Type.ABILITY_DESCRIPTION.wrap(getName()), getChance());
    }

    @Override
    public String getName() {
        return "burn";
    }

    @Override
    public int getSortOrder() {
        return 4;
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.CAVE;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/burn_0"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/burn_1"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/burn_2"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/burn_3"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/burn_4")
        };
    }

    public int getChance() {
        return burnProcChance * getLevel();
    }

    @Override
    public ArrayList<Component> getLevelUpInfo() {
        ArrayList<Component> list = super.getLevelUpInfo();
        list.add(Component.translatable(LangKey.ABILITY_CHANCE, "+" + burnProcChance));
        return list;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getMinLevel() {
        return 0;
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !burn;
    }
}*/