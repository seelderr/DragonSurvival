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
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Locale;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

@RegisterDragonAbility
public class HunterAbility extends ChargeCastAbility {
    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "forest_dragon", "actives", "hunter"}, key = "hunterEnabled", comment = "Whether the hunter ability should be enabled")
    public static Boolean hunterEnabled = true;

    @ConfigRange(min = 1.0, max = 10000.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "forest_dragon", "actives", "hunter"}, key = "hunterDuration", comment = "The duration in seconds of the hunter effect given when the ability is used")
    public static Double hunterDuration = 30.0;

    @ConfigRange(min = 0.05, max = 10000.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "forest_dragon", "actives", "hunter"}, key = "hunterCooldown", comment = "The cooldown in seconds of the hunter ability")
    public static Double hunterCooldown = 30.0;

    @ConfigRange(min = 0.05, max = 10000.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "forest_dragon", "actives", "hunter"}, key = "hunterCasttime", comment = "The cast time in seconds of the hunter ability")
    public static Double hunterCasttime = 3.0;

    @ConfigRange(min = 0.0, max = 100.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "forest_dragon", "actives", "hunter"}, key = "hunterDamageBonus", comment = "The bonus damage multiplier the hunter effect gives when invisible. This value is multiplied by the skill level.")
    public static Double hunterDamageBonus = 1.0;

    @ConfigRange(min = 0, max = 100)
    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "forest_dragon", "actives", "hunter"}, key = "hunterManaCost", comment = "The mana cost for using the hunter ability")
    public static Integer hunterManaCost = 1;

    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "forest_dragon", "actives", "hunter"}, key = "full_invisibility", comment = "Whether other players should appear fully invisible at maximum hunter stacks")
    public static Boolean fullyInvisible = false;

    @ConfigOption(side = ConfigSide.CLIENT, category = {"magic", "abilities", "forest_dragon", "actives", "hunter"}, key = "translucent_items", comment = "Whether your held items should also appear translucent")
    public static Boolean translucentItems = true;

    @ConfigOption(side = ConfigSide.CLIENT, category = {"magic", "abilities", "forest_dragon", "actives", "hunter"}, key = "fix_translucency", comment = "This enables the shader features of fabulous mode which are needed for translucency to work correctly")
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
        player.addEffect(new MobEffectInstance(DSEffects.HUNTER, getDuration(), getLevel() - 1));
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

    public double getDamage() {
        return hunterDamageBonus * getLevel();
    }

    @Override
    public Component getDescription() {
        return Component.translatable("ds.skill.description." + getName(), "+" + hunterDamageBonus * getLevel() + "x", getDuration());
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
        return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/hunter_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/hunter_1.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/hunter_2.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/hunter_3.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/hunter_4.png")};
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