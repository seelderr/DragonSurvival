package by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.active;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.input.Keybind;
import by.dragonsurvivalteam.dragonsurvival.magic.common.AbilityAnimation;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ChargeCastAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Locale;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = {
        "■ Personal buff: Activates the §2Hunter§r effect, which allows you to become invisible in tall grass and increases your movement speed. Your first melee strike will remove this effect and cause a critical hit with a §c%s§r damage bonus.\n",
        "■ Effect does not stack. Cannot be used in flight. Will be removed early if you take damage, or attack a target.",
})
@Translation(type = Translation.Type.ABILITY, comments = "Hunter")
@RegisterDragonAbility
public class HunterAbility extends ChargeCastAbility {
    @Translation(key = "hunter", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the hunter ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "active", "hunter"}, key = "hunter")
    public static Boolean hunterEnabled = true;

    @ConfigRange(min = 1.0, max = 10_000.0)
    @Translation(key = "hunter_duration", type = Translation.Type.CONFIGURATION, comments = "Duration (in seconds) of the effect")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "active", "hunter"}, key = "hunter_duration")
    public static Double hunterDuration = 30.0;

    @ConfigRange(min = 0.05, max = 10_000.0)
    @Translation(key = "hunter_cooldown", type = Translation.Type.CONFIGURATION, comments = "Cooldown (in seconds) after using the ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "active", "hunter"}, key = "hunter_cooldown")
    public static Double hunterCooldown = 30.0;

    @ConfigRange(min = 0.05, max = 10_000.0)
    @Translation(key = "hunter_cast_time", type = Translation.Type.CONFIGURATION, comments = "Cast time (in seconds)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "active", "hunter"}, key = "hunter_cast_time")
    public static Double hunterCasttime = 3.0;

    @ConfigRange(min = 0.0, max = 100.0)
    @Translation(key = "hunter_damage_multiplier", type = Translation.Type.CONFIGURATION, comments = {"Determines the damage multiplier when attacking while the effect is active (multiplied by the ability level) - disabled if set to 0", "Note that the effect will be removed after the first attack"})
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "active", "hunter"}, key = "hunter_damage_multiplier")
    public static float hunterDamageBonus = 1.0f;

    @ConfigRange(min = 0, max = 100)
    @Translation(key = "hunter_mana_cost", type = Translation.Type.CONFIGURATION, comments = "Mana cost")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "active", "hunter"}, key = "hunter_mana_cost")
    public static Integer hunterManaCost = 1;

    @Translation(key = "hunter_fully_invisible", type = Translation.Type.CONFIGURATION, comments = "If enabled other players will be fully invisible at maximum hunter stacks")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "active", "hunter"}, key = "full_invisibility")
    public static Boolean fullyInvisible = false;

    @Translation(key = "hunter_translucent_items_first_person", type = Translation.Type.CONFIGURATION, comments = "If enabled items held in first person will also appear translucent")
    @ConfigOption(side = ConfigSide.CLIENT, category = {"forest_dragon", "magic", "abilities", "active", "hunter"}, key = "hunter_translucent_items_first_person")
    public static Boolean translucentItemsFirstPerson = true;

    @Translation(key = "hunter_fix_translucency", type = Translation.Type.CONFIGURATION, comments = "This enables the shader features of fabulous mode which are needed for translucency to work correctly")
    @ConfigOption(side = ConfigSide.CLIENT, category = {"forest_dragon", "magic", "abilities", "active", "hunter"}, key = "hunter_fix_translucency", /* Otherwise might crash */ requiresRestart = true)
    public static Boolean fixTranslucency = true;

    private static final Integer[] REQUIRED_LEVELS = new Integer[]{0, 25, 35, 55};

    // currently not done for all abilities (not worth with future rework in mind)
    public static int maxLevel() {
        return REQUIRED_LEVELS.length;
    }

    @Override
    public int getSortOrder(){
        return 4;
    }

    @Override
    public int getSkillCastingTime() {
        return Functions.secondsToTicks(hunterCasttime);
    }

    @Override
    public void onCasting(Player player, int currentCastTime) {
    }

    @Override
    public void castingComplete(Player player) {
        player.addEffect(new MobEffectInstance(DSEffects.HUNTER, getDuration(), getLevel() - 1, false, false));
        player.level().playLocalSound(player.position().x, player.position().y + 0.5, player.position().z, SoundEvents.UI_TOAST_IN, SoundSource.PLAYERS, 5F, 0.1F, true);
    }

    @Override
    public ArrayList<Component> getInfo() {
        ArrayList<Component> components = super.getInfo();

        if (!Keybind.ABILITY4.get().isUnbound()) {
            String key = Keybind.ABILITY4.getKey().getDisplayName().getString().toUpperCase(Locale.ROOT);

            if (key.isEmpty()) {
                key = Keybind.ABILITY4.getKey().getDisplayName().getString();
            }
            components.add(Component.translatable("ds.skill.keybind", key));
        }

        return components;
    }


    @Override
    public int getManaCost() {
        return hunterManaCost;
    }

    @Override
    public Integer[] getRequiredLevels(){
        return REQUIRED_LEVELS; // .clone() to be safe for modifications? currently there are none
    }

    @Override
    public int getSkillCooldown() {
        return Functions.secondsToTicks(hunterCooldown);
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
        return Functions.secondsToTicks(hunterDuration * getLevel());
    }

    public float getDamage() {
        return hunterDamageBonus * getLevel();
    }

    @Override
    public Component getDescription() {
        return Component.translatable(Translation.Type.ABILITY_DESCRIPTION.wrap(getName()), "+" + hunterDamageBonus * getLevel() + "x", getDuration());
    }

    @Override
    public String getName() {
        return "hunter";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.FOREST;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/hunter_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/hunter_1.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/hunter_2.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/hunter_3.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/hunter_4.png")
        };
    }


    @Override
    public ArrayList<Component> getLevelUpInfo() {
        ArrayList<Component> list = super.getLevelUpInfo();
        list.add(Component.translatable("ds.skill.duration.seconds", "+" + hunterDuration));
        list.add(Component.translatable("ds.skill.damage", "+" + hunterDamageBonus + "X"));
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
        return super.isDisabled() || !hunterEnabled;
    }
}