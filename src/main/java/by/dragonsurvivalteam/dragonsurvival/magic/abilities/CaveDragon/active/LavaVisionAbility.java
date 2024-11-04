package by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.active;

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

@RegisterDragonAbility
public class LavaVisionAbility extends ChargeCastAbility {
    @Translation(key = "lava_vision", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the lava vision ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "lava_vision"}, key = "lava_vision")
    public static Boolean lavaVisionEnabled = true;

    @ConfigRange(min = 1.0, max = 10_000.0)
    @Translation(key = "lava_vision_duration", type = Translation.Type.CONFIGURATION, comments = "The duration (in seconds) of the effect")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "lava_vision"}, key = "lava_vision_duration")
    public static Double lavaVisionDuration = 100.0;

    @ConfigRange(min = 0.05, max = 10_000.0)
    @Translation(key = "lava_vision_cooldown", type = Translation.Type.CONFIGURATION, comments = "The cooldown (in seconds) after using the ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "lava_vision"}, key = "lava_vision_cooldown")
    public static Double lavaVisionCooldown = 30.0;

    @ConfigRange(min = 0.05, max = 10_000.0)
    @Translation(key = "lava_vision_cast_time", type = Translation.Type.CONFIGURATION, comments = "Cast time (in seconds)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "lava_vision"}, key = "lava_vision_cast_time")
    public static Double lavaVisionCasttime = 1.0;

    @ConfigRange(min = 0, max = 100)
    @Translation(key = "lava_vision_mana_cost", type = Translation.Type.CONFIGURATION, comments = "Mana cost")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "lava_vision"}, key = "lava_vision_mana_cost")
    public static Integer lavaVisionManaCost = 1;

    @Override
    public int getSortOrder() {
        return 4;
    }

    @Override
    public ArrayList<Component> getInfo() {
        ArrayList<Component> components = super.getInfo();
        components.add(Component.translatable("ds.skill.duration.seconds", Functions.ticksToSeconds(getDuration())));

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
        return lavaVisionManaCost;
    }

    @Override
    public Integer[] getRequiredLevels() {
        return new Integer[]{0, 25, 45, 60};
    }

    @Override
    public int getSkillCooldown() {
        return Functions.secondsToTicks(lavaVisionCooldown);
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
        return Functions.secondsToTicks(lavaVisionDuration);
    }

    @Override
    public String getName() {
        return "lava_vision";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.CAVE;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/lava_vision_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/lava_vision_1.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/lava_vision_2.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/lava_vision_3.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/lava_vision_4.png")};
    }

    @Override
    public ArrayList<Component> getLevelUpInfo() {
        ArrayList<Component> list = super.getLevelUpInfo();
        list.add(Component.translatable("ds.skill.duration.seconds", "+" + lavaVisionDuration));
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
        return super.isDisabled() || !lavaVisionEnabled;
    }

    @Override
    public int getSkillCastingTime() {
        return Functions.secondsToTicks(lavaVisionCasttime);
    }

    @Override
    public void onCasting(Player player, int currentCastTime) {
    }

    @Override
    public void castingComplete(Player player) {
        player.addEffect(new MobEffectInstance(DSEffects.LAVA_VISION, getDuration(), 0, false, false));
        player.level().playLocalSound(player.position().x, player.position().y + 0.5, player.position().z, SoundEvents.UI_TOAST_IN, SoundSource.PLAYERS, 5F, 0.1F, false);
    }
}