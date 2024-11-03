package by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.active;


import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.DragonSpikeEntity;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.input.Keybind;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.InstantCastAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Locale;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

@RegisterDragonAbility
public class SpikeAbility extends InstantCastAbility {
    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "forest_dragon", "actives", "spike"}, key = "spikeEnabled", comment = "Whether the spike ability should be enabled")
    public static Boolean spikeEnabled = true;

    @ConfigRange(min = 0.0, max = 100.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "forest_dragon", "actives", "spike"}, key = "spikeSpread", comment = "The amount each additional spike fired will add to its inaccuracy")
    public static Float spikeSpread = 1.0F;

    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "forest_dragon", "actives", "spike"}, key = "spikeMultishot", comment = "Whether the spike ability will fire an additional shot per level")
    public static Boolean spikeMultishot = true;

    @ConfigRange(min = 0.05, max = 10000.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "forest_dragon", "actives", "spike"}, key = "spikeCooldown", comment = "The cooldown in seconds of the spike ability")
    public static Double spikeCooldown = 3.0;

    @ConfigRange(min = 0, max = 100.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "forest_dragon", "actives", "spike"}, key = "spikeDamage", comment = "The amount of damage the spike ability deals. This value is multiplied by the skill level.")
    public static Double spikeDamage = 2.0;

    @ConfigRange(min = 0, max = 100)
    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "forest_dragon", "actives", "spike"}, key = "spikeManaCost", comment = "The mana cost for using the spike ability")
    public static Integer spikeManaCost = 1;

    @Override
    public Component getDescription() {
        return Component.translatable("ds.skill.description." + getName(), getDamage());
    }

    @Override
    public int getSortOrder() {
        return 2;
    }

    @Override
    public String getName() {
        return "spike";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.FOREST;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/spike_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/spike_1.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/spike_2.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/spike_3.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/spike_4.png")};
    }


    public float getDamage() {
        return (float) (spikeDamage * getLevel());
    }

    @Override
    public ArrayList<Component> getLevelUpInfo() {
        ArrayList<Component> list = super.getLevelUpInfo();
        list.add(Component.translatable("ds.skill.damage", "+" + spikeDamage));
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
        return super.isDisabled() || !spikeEnabled;
    }

    @Override
    public int getManaCost() {
        return spikeManaCost;
    }

    @Override
    public Integer[] getRequiredLevels() {
        return new Integer[]{0, 20, 30, 40};
    }

    @Override
    public int getSkillCooldown() {
        return Functions.secondsToTicks(spikeCooldown);
    }

    @Override
    public ArrayList<Component> getInfo() {
        ArrayList<Component> components = super.getInfo();
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

    @Override
    public boolean requiresStationaryCasting() {
        return false;
    }

    @Override
    public void onCast(Player player) {
        float speed = 1;
        // Copied from AbstractArrow.java constructor
        Vec3 launchPos = new Vec3(player.getX(), player.getEyeY() - 0.1F, player.getZ());
        for (int i = 0; i < getLevel(); i++) {
            DragonSpikeEntity entity = new DragonSpikeEntity(DSEntities.DRAGON_SPIKE.get(), player.level());
            entity.setPos(launchPos);
            entity.setOwner(player);
            entity.setArrow_level(getLevel());
            entity.setBaseDamage(getDamage());
            entity.pickup = AbstractArrow.Pickup.DISALLOWED;
            entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, speed, i * spikeSpread);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, entity.getSoundSource(), 1.0F, 2.0F);
            player.level().addFreshEntity(entity);
            if (!spikeMultishot)
                break;
        }
    }
}