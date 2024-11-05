package by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.active;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.BallLightningEntity;
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

@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = {
        "■ Ranged attack: shoots out a condensed ball of electrical energy. Deals damage and §celectrifies§r nearby enemies as it travels.\n",
        "■ During a thunderstorm, lightning may strike the ball."
})
@Translation(type = Translation.Type.ABILITY, comments = "Ball Lightning")
@RegisterDragonAbility
public class BallLightningAbility extends ChargeCastAbility {
    @Translation(key = "ball_lightning", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the ball lightning ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "active", "ball_lightning"}, key = "ball_lightning")
    public static Boolean ballLightning = true;

    @ConfigRange(min = 0.05, max = 10_000.0)
    @Translation(key = "ball_lightning_cooldown", type = Translation.Type.CONFIGURATION, comments = "Cooldown (in seconds) after using the ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "active", "ball_lightning"}, key = "ball_lightning_cooldown")
    public static Double ballLightningCooldown = 20.0;

    @ConfigRange(min = 0.05, max = 10_000.0)
    @Translation(key = "ball_lightning_cast_time", type = Translation.Type.CONFIGURATION, comments = "Cast time (in seconds)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "active", "ball_lightning"}, key = "ball_lightning_cast_time")
    public static Double ballLightningCasttime = 2.0;

    @ConfigRange(min = 0.0, max = 100.0)
    @Translation(key = "ball_lightning_damage", type = Translation.Type.CONFIGURATION, comments = "Amount of damage (multiplied by the ability level)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "active", "ball_lightning"}, key = "ball_lightning_damage")
    public static Double ballLightningDamage = 4.0;

    @ConfigRange(min = 0, max = 100)
    @Translation(key = "ball_lightning_mana_cost", type = Translation.Type.CONFIGURATION, comments = "Mana cost")
    @ConfigOption(side = ConfigSide.SERVER, category = {"sea_dragon", "magic", "abilities", "active", "ball_lightning"}, key = "ball_lightning_mana_cost")
    public static Integer ballLightningManaCost = 1;

    @Override
    public int getManaCost() {
        return ballLightningManaCost;
    }

    @Override
    public int getSortOrder() {
        return 2;
    }

    @Override
    public Integer[] getRequiredLevels() {
        return new Integer[]{0, 20, 45, 50};
    }

    @Override
    public int getSkillCooldown() {
        return Functions.secondsToTicks(ballLightningCooldown);
    }

    @Override
    public int getSkillCastingTime() {
        return Functions.secondsToTicks(ballLightningCasttime);
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

        BallLightningEntity entity = new BallLightningEntity(projPos.x, projPos.y, projPos.z, Vec3.ZERO, player.level());
        entity.accelerationPower = 0;
        entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, speed, 0);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE, entity.getSoundSource(), 1.0F, 2.0F);
        player.level().addFreshEntity(entity);
    }

    @Override
    public ArrayList<Component> getInfo() {
        ArrayList<Component> components = super.getInfo();
        components.add(Component.translatable("ds.skill.aoe", getRange() + "x" + getRange() + "x" + getRange()));
        components.add(Component.translatable("ds.skill.damage", getDamage()));

        if (!Keybind.ABILITY2.get().isUnbound()) {
            String key = Keybind.ABILITY2.getKey().getDisplayName().getString().toUpperCase(Locale.ROOT);

            if (key.isEmpty()) {
                key = Keybind.ABILITY2.getKey().getDisplayName().getString();
            }
            components.add(Component.translatable("ds.skill.keybind", key));
        }

        return components;
    }

    public int getRange() {
        return 4;
    }

    public float getDamage() {
        return getDamage(getLevel());
    }

    public static float getDamage(int level) {
        return (float) (ballLightningDamage * level);
    }

    @Override

    public Component getDescription() {
        return Component.translatable(Translation.Type.ABILITY_DESCRIPTION.wrap(getName()), getDamage());
    }

    @Override
    public String getName() {
        return "ball_lightning";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.SEA;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/ball_lightning_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/ball_lightning_1.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/ball_lightning_2.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/ball_lightning_3.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/ball_lightning_4.png")
        };
    }

    @Override
    public ArrayList<Component> getLevelUpInfo() {
        ArrayList<Component> list = super.getLevelUpInfo();
        list.add(Component.translatable("ds.skill.damage", "+" + ballLightningDamage));
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
        return super.isDisabled() || !ballLightning;
    }
}