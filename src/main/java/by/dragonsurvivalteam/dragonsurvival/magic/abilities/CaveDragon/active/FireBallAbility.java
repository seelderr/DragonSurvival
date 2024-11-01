package by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.active;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.FireBallEntity;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.input.Keybind;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ChargeCastAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Locale;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

@RegisterDragonAbility
public class FireBallAbility extends ChargeCastAbility {
    @Translation(key = "fireball", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the fireball ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "fireball"}, key = "fireball")
    public static Boolean fireballEnabled = true;

    @ConfigRange(min = 0.05, max = 10_000.0)
    @Translation(key = "fireball_cooldown", type = Translation.Type.CONFIGURATION, comments = "The cooldown (in seconds) after using the ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "fireball"}, key = "fireball_cooldown")
    public static Double fireballCooldown = 7.0;

    @ConfigRange(min = 0.05, max = 10_000.0)
    @Translation(key = "fireball_cast_time", type = Translation.Type.CONFIGURATION, comments = "Cast time (in seconds)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "fireball"}, key = "fireball_cast_time")
    public static Double fireballCasttime = 2.0;

    @ConfigRange(min = 0, max = 100.0)
    @Translation(key = "fireball_damage", type = Translation.Type.CONFIGURATION, comments = "Amount of damage (scales with ability level)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "fireball"}, key = "fireball_damage")
    public static Double fireballDamage = 5.0;

    @ConfigRange(min = 0, max = 100)
    @Translation(key = "fireball_mana_cost", type = Translation.Type.CONFIGURATION, comments = "Mana cost")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "fireball"}, key = "fireball_mana_cost")
    public static Integer fireballManaCost = 1;

    @Override
    public int getManaCost() {
        return fireballManaCost;
    }

    @Override
    public int getSortOrder() {
        return 2;
    }

    @Override
    public Integer[] getRequiredLevels() {
        return new Integer[]{0, 20, 40, 45};
    }

    @Override
    public int getSkillCooldown() {
        return Functions.secondsToTicks(fireballCooldown);
    }

    @Override
    public ArrayList<Component> getInfo() {
        ArrayList<Component> components = super.getInfo();
        components.add(Component.translatable("ds.skill.damage", getDamage()));

        if (!Keybind.ABILITY2.get().isUnbound()) {
            String key = Keybind.ABILITY2.getKey().getDisplayName().getString().toUpperCase(Locale.ROOT);

            if (key.isEmpty())
                key = Keybind.ABILITY2.getKey().getDisplayName().getString();
            components.add(Component.translatable("ds.skill.keybind", key));
        }

        return components;
    }

    public float getDamage() {
        return getDamage(getLevel());
    }

    public static float getDamage(int level) {
        return fireballDamage.floatValue() * level;
    }

    @Override
    public String getName() {
        return "fireball";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.CAVE;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/fireball_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/fireball_1.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/fireball_2.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/fireball_3.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/fireball_4.png")
        };
    }

    @Override
    public ArrayList<Component> getLevelUpInfo() {
        ArrayList<Component> list = super.getLevelUpInfo();
        list.add(Component.translatable("ds.skill.damage", "+" + fireballDamage.floatValue()));
        list.add(Component.translatable("ds.skill.aoe", "+1"));
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
        return super.isDisabled() || !fireballEnabled;
    }

    @Override
    public int getSkillCastingTime() {
        return Functions.secondsToTicks(fireballCasttime);
    }

    @Override
    public boolean requiresStationaryCasting() {
        return false;
    }

    @Override
    public void onCasting(Player player, int currentCastTime) {
    }

    @Override
    public void castingComplete(Player player) {
        float speed = 1;

        Vec3 eyePos = player.getEyePosition();
        Vec3 lookAngle = player.getLookAngle();

        Vec3 projPos;
        if (player.getAbilities().flying) {
            projPos = lookAngle.scale(2.0F).add(eyePos);
        } else {
            projPos = lookAngle.scale(1.0F).add(eyePos);
        }

        FireBallEntity entity = new FireBallEntity(projPos.x, projPos.y, projPos.z, Vec3.ZERO, player.level());
        entity.accelerationPower = 0;
        entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 1.0F, speed, 0);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE, entity.getSoundSource(), 1.0F, 2.0F);
        player.level().addFreshEntity(entity);
    }
}