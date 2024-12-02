/*package by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.active;


@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = {
        "■ AOE Buff: multiplies the amount of §2experience§r gained from monsters up to a limit.\n",
        "■ Effect does not stack. Cannot be used in flight."
})
@Translation(type = Translation.Type.ABILITY, comments = "Soul Revelation")
@RegisterDragonAbility
public class RevealingTheSoulAbility extends AoeBuffAbility {
    @Translation(type = Translation.Type.MISC, comments = "§6■ EXP multiplier:§r %s")
    private static final String XP_MULTIPLIER = Translation.Type.ABILITY_DESCRIPTION.wrap("revealing_the_soul.xp_multiplier");

    @Translation(type = Translation.Type.MISC, comments = "§6■ Max EXP bonus:§r %s")
    private static final String XP_MAXIMUM_GAIN = Translation.Type.ABILITY_DESCRIPTION.wrap("revealing_the_soul.xp_maximum_gain");

    @Translation(key = "revealing_the_soul", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the revealing the soul ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "active", "revealing_the_soul"}, key = "revealing_the_soul")
    public static Boolean revealingTheSoul = true;

    @ConfigRange(min = 1.0, max = 10_000.0)
    @Translation(key = "revealing_the_soul_duration", type = Translation.Type.CONFIGURATION, comments = "The duration (in seconds) of the effect")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "active", "revealing_the_soul"}, key = "revealing_the_soul_duration")
    public static Double revealingTheSoulDuration = 200.0;

    @ConfigRange(min = 0.05, max = 10_000.0)
    @Translation(key = "revealing_the_soul_cooldown", type = Translation.Type.CONFIGURATION, comments = "Cooldown (in seconds) after using the ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "active", "revealing_the_soul"}, key = "revealing_the_soul_cooldown")
    public static Double revealingTheSoulCooldown = 30.0;

    @ConfigRange(min = 0.05, max = 10_000.0)
    @Translation(key = "revealing_the_soul_cast_time", type = Translation.Type.CONFIGURATION, comments = "Cast time (in seconds)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "active", "revealing_the_soul"}, key = "revealing_the_soul_cast_time")
    public static Double revealingTheSoulCasttime = 2.0;

    @ConfigRange(min = 0, max = 100)
    @Translation(key = "revealing_the_soul_mana_cost", type = Translation.Type.CONFIGURATION, comments = "Mana cost")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "active", "revealing_the_soul"}, key = "revealing_the_soul_mana_cost")
    public static Integer revealingTheSoulManaCost = 1;

    @ConfigRange(min = 0, max = 10_000)
    @Translation(key = "revealing_the_soul_max_experience", type = Translation.Type.CONFIGURATION, comments = "The max. amount of extra experience that can be gained from a single mob with the revealing the soul effect")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "active", "revealing_the_soul"}, key = "revealing_the_soul_max_experience")
    public static Integer revealingTheSoulMaxEXP = 20;

    @ConfigRange(min = 0, max = 10_000)
    @Translation(key = "revealing_the_soul_experience_multiplier", type = Translation.Type.CONFIGURATION, comments = "The experience multiplier for the extra experience (as addition with the dropped experience - meaning a multiplier of 1 results in 100% experience increase)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "active", "revealing_the_soul"}, key = "revealing_the_soul_experience_multiplier")
    public static Double revealingTheSoulMultiplier = 1.0;

    @Override
    public String getName() {
        return "revealing_the_soul";
    }

    @Override
    public int getSortOrder() {
        return 3;
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.SEA;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/revealing_the_soul_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/revealing_the_soul_1.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/revealing_the_soul_2.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/revealing_the_soul_3.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/revealing_the_soul_4.png")
        };
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
    public int getManaCost() {
        return revealingTheSoulManaCost;
    }

    @Override
    public Integer[] getRequiredLevels() {
        return new Integer[]{0, 25, 40, 60};
    }

    @Override
    public int getSkillCooldown() {
        return Functions.secondsToTicks(revealingTheSoulCooldown);
    }

    @Override
    public int getRange() {
        return 5;
    }

    @Override
    public ParticleOptions getParticleEffect() {
        return DSParticles.MAGIC_BEACON_PARTICLE.value();
    }

    @Override
    public MobEffectInstance getEffect() {
        return new MobEffectInstance(DSEffects.REVEALING_THE_SOUL, Functions.secondsToTicks(revealingTheSoulDuration), /* TODO :: scale with level? */ /*0, false, false);*/
    /*}

    @Override
    public int getSkillCastingTime() {
        return Functions.secondsToTicks(revealingTheSoulCasttime);
    }

    @Override
    public ArrayList<Component> getInfo() {
        ArrayList<Component> components = super.getInfo();
        components.add(Component.translatable(XP_MULTIPLIER, revealingTheSoulMultiplier + "x"));
        components.add(Component.translatable(XP_MAXIMUM_GAIN, Integer.toString(revealingTheSoulMaxEXP)));
        return components;
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !revealingTheSoul;
    }
}*/