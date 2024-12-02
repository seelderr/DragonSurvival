/*package by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.active;


@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = "Grants additional armor points to all entities in an area around the dragon.")
@Translation(type = Translation.Type.ABILITY, comments = "Sturdy Skin") // TODO :: strong leather, tough skin or sturdy skin?
@RegisterDragonAbility
public class ToughSkinAbility extends AoeBuffAbility {
    @Translation(type = Translation.Type.MISC, comments = "§6■ Defense:§r %s")
    private static final String DEFENSE = Translation.Type.ABILITY_DESCRIPTION.wrap("tough_skin.defense");

    @Translation(key = "tough_skin", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the tough skin ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "tough_skin"}, key = "tough_skin")
    public static Boolean toughSkinEnabled = true;

    @ConfigRange(min = 1.0, max = 10_000.0)
    @Translation(key = "tough_skin_duration", type = Translation.Type.CONFIGURATION, comments = "The duration (in seconds) of the effect")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "tough_skin"}, key = "tough_skin_duration")
    public static Double toughSkinDuration = 200.0;

    @ConfigRange(min = 1.0, max = 10_000.0)
    @Translation(key = "tough_skin_cooldown", type = Translation.Type.CONFIGURATION, comments = "Cooldown (in seconds) after using the ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "tough_skin"}, key = "tough_skin_cooldown")
    public static Double toughSkinCooldown = 30.0;

    @ConfigRange(min = 1, max = 10_000)
    @Translation(key = "tough_skin_cast_time", type = Translation.Type.CONFIGURATION, comments = "Cast time (in seconds)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "tough_skin"}, key = "tough_skin_cast_time")
    public static Double toughSkinCasttime = 2.0;

    @ConfigRange(min = 0, max = 100)
    @Translation(key = "tough_skin_mana_cost", type = Translation.Type.CONFIGURATION, comments = "Mana cost")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "tough_skin"}, key = "tough_skin_mana_cost")
    public static Integer toughSkinManaCost = 1;

    @ConfigRange(min = 0, max = 10_000)
    @Translation(key = "tough_skin_armor_scaling", type = Translation.Type.CONFIGURATION, comments = "Amount of extra armor per level of tough skin effect")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "tough_skin"}, key = "tough_skin_armor_scaling")
    public static Double toughSkinArmorValue = 3.0;

    @Override
    public int getSortOrder() {
        return 3;
    }

    @Override
    public int getSkillCastingTime() {
        return Functions.secondsToTicks(toughSkinCasttime);
    }

    @Override
    public ArrayList<Component> getInfo() {
        ArrayList<Component> components = super.getInfo();

        if (!Keybind.ABILITY3.get().isUnbound()) {
            components = new ArrayList<>(components.subList(0, components.size() - 1));
        }

        components.add(Component.translatable(LangKey.ABILITY_DURATION, toughSkinDuration));

        if (!Keybind.ABILITY3.get().isUnbound()) {
            String key = Keybind.ABILITY3.getKey().getDisplayName().getString().toUpperCase(Locale.ROOT);

            if (key.isEmpty()) {
                key = Keybind.ABILITY3.getKey().getDisplayName().getString();
            }

            components.add(Component.translatable(LangKey.ABILITY_KEYBIND, key));
        }

        return components;
    }

    @Override
    public int getRange() {
        return 5;
    }

    @Override
    public ParticleOptions getParticleEffect() {
        return DSParticles.PEACE_BEACON_PARTICLE.value();
    }

    @Override
    public int getManaCost() {
        return toughSkinManaCost;
    }

    @Override
    public Integer[] getRequiredLevels() {
        return new Integer[]{0, 15, 35};
    }

    @Override
    public int getSkillCooldown() {
        return Functions.secondsToTicks(toughSkinCooldown);
    }

    @Override
    public MobEffectInstance getEffect() {
        return new MobEffectInstance(DSEffects.STRONG_LEATHER, Functions.secondsToTicks(toughSkinDuration), getLevel() - 1, false, false);
    }

    @Override
    public Component getDescription() {
        return Component.translatable(Translation.Type.ABILITY_DESCRIPTION.wrap(getName()), toughSkinDuration, getDefence(getLevel()));
    }

    @Override
    public String getName() {
        return "strong_leather";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.CAVE;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/strong_leather_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/strong_leather_1.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/strong_leather_2.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/strong_leather_3.png")
        };
    }


    public static double getDefence(int level) {
        return level * toughSkinArmorValue;
    }

    @Override
    public ArrayList<Component> getLevelUpInfo() {
        ArrayList<Component> list = super.getLevelUpInfo();
        list.add(Component.translatable(DEFENSE, "+" + toughSkinArmorValue));
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
        return super.isDisabled() || !toughSkinEnabled;
    }
}*/