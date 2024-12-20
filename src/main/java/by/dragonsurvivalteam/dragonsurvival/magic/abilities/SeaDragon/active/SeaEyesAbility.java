/*package by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.active;


@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = {
        "■ Personal Buff: provides §2Sea Vision§r for a short time.\n",
        "■ Effect does not stack. Cannot be used in flight."
})
@Translation(type = Translation.Type.ABILITY, comments = "Sea Vision")
@RegisterDragonAbility
public class SeaEyesAbility extends ChargeCastAbility {
    @Translation(key = "sea_vision", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the sea vision ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "active", "sea_vision"}, key = "sea_vision")
    public static Boolean seaEyes = true;

    @ConfigRange(min = 1.0, max = 10_000.0)
    @Translation(key = "sea_vision_duration", type = Translation.Type.CONFIGURATION, comments = "The duration (in seconds) of the effect")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "active", "sea_vision"}, key = "sea_vision_duration")
    public static Double seaEyesDuration = 100.0;

    @ConfigRange(min = 0.05, max = 10_000)
    @Translation(key = "sea_vision_cooldown", type = Translation.Type.CONFIGURATION, comments = "Cooldown (in seconds) after using the ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "active", "sea_vision"}, key = "sea_vision_cooldown")
    public static Double seaEyesCooldown = 60.0;

    @ConfigRange(min = 0.05, max = 10_000)
    @Translation(key = "sea_vision_cast_time", type = Translation.Type.CONFIGURATION, comments = "Cast time (in seconds)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "active", "sea_vision"}, key = "sea_vision_cast_time")
    public static Double seaEyesCasttime = 1.0;

    @ConfigRange(min = 0, max = 100)
    @Translation(key = "sea_vision_mana_cost", type = Translation.Type.CONFIGURATION, comments = "Mana cost")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "active", "sea_vision"}, key = "sea_vision_mana_cost")
    public static Integer seaEyesManaCost = 1;

    @Override
    public int getSkillCastingTime() {
        return Functions.secondsToTicks(seaEyesCasttime);
    }

    @Override
    public int getSortOrder() {
        return 4;
    }

    @Override
    public void onCasting(Player player, int currentCastTime) { }

    @Override
    public void castingComplete(Player player) {
        player.addEffect(new MobEffectInstance(DSEffects.WATER_VISION, getDuration(), 0, false, false));
        player.level().playLocalSound(player.position().x, player.position().y + 0.5, player.position().z, SoundEvents.UI_TOAST_IN, SoundSource.PLAYERS, 5F, 0.1F, true);
    }

    @Override
    public ArrayList<Component> getInfo() {
        ArrayList<Component> components = super.getInfo();
        components.add(Component.translatable(LangKey.ABILITY_DURATION, Functions.ticksToSeconds(getDuration())));

        if (!Keybind.ABILITY4.get().isUnbound()) {
            String key = Keybind.ABILITY4.getKey().getDisplayName().getString().toUpperCase(Locale.ROOT);

            if (key.isEmpty()) {
                key = Keybind.ABILITY4.getKey().getDisplayName().getString();
            }
            components.add(Component.translatable(LangKey.ABILITY_KEYBIND, key));
        }

        return components;
    }

    @Override
    public int getManaCost() {
        return seaEyesManaCost;
    }

    @Override
    public Integer[] getRequiredLevels() {
        return new Integer[]{0, 15, 45, 60};
    }

    @Override
    public int getSkillCooldown() {
        return Functions.secondsToTicks(seaEyesCooldown);
    }

    @Override
    public boolean requiresStationaryCasting() {
        return false;
    }

    @Override
    public AbilityAnimation getLoopingAnimation() {
        return new AbilityAnimation("cast_self_buff", true, false);
    }

    @Override
    public AbilityAnimation getStoppingAnimation() {
        return new AbilityAnimation("self_buff", 0.52 * 20, true, false);
    }

    public int getDuration() {
        return Functions.secondsToTicks(seaEyesDuration * getLevel());
    }

    @Override
    public Component getDescription() {
        return Component.translatable(Translation.Type.ABILITY_DESCRIPTION.wrap(getName()), Functions.ticksToSeconds(getDuration()));
    }

    @Override
    public String getName() {
        return "sea_eyes";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.SEA;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_eyes_0"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_eyes_1"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_eyes_2"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_eyes_3"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/sea/sea_eyes_4")
        };
    }

    @Override
    public ArrayList<Component> getLevelUpInfo() {
        ArrayList<Component> list = super.getLevelUpInfo();
        list.add(Component.translatable(LangKey.ABILITY_DURATION, "+" + seaEyesDuration));
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
        return super.isDisabled() || !seaEyes;
    }
}*/