package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public class DragonModifiers {
    public static final UUID REACH_MODIFIER_UUID = UUID.fromString("7455d5c7-4e1f-4cca-ab46-d79353764020");
    public static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("03574e62-f9e4-4f1b-85ad-fde00915e446");
    public static final UUID DAMAGE_MODIFIER_UUID = UUID.fromString("5bd3cebc-132e-4f9d-88ef-b686c7ad1e2c");
    public static final UUID SWIM_SPEED_MODIFIER_UUID = UUID.fromString("2a9341f3-d19e-446c-924b-7cf2e5259e10");
    public static final UUID STEP_HEIGHT_MODIFIER_UUID = UUID.fromString("f3b0b3e3-3b7d-4b1b-8f3d-3b7d4b1b8f3d");
	  public static final UUID ATTACK_RANGE_MODIFIER_UUID = UUID.fromString("a2e9a028-4bef-48d4-a25b-9cfdcac99480");

    public static final Function<Player, AttributeModifier> HEALTH_MODIFIER = player -> buildHealthMod(DragonStateProvider.getUnsafeHandler(player).getSize());
    public static final Function<Player, AttributeModifier> REACH_MODIFIER = player -> buildReachMod(DragonStateProvider.getUnsafeHandler(player).getSize());
    public static final Function<Player, AttributeModifier> ATTACK_RANGE_MODIFIER = player -> buildAttackRangeMod(DragonStateProvider.getUnsafeHandler(player).getSize());
    public static final Function<Player, AttributeModifier> DAMAGE_MODIFIER = player -> buildDamageMod(DragonStateProvider.getUnsafeHandler(player));
    public static final Function<Player, AttributeModifier> SWIM_SPEED_MODIFIER = player -> buildSwimSpeedMod(DragonStateProvider.getUnsafeHandler(player).getType());
    public static final Function<Player, AttributeModifier> STEP_HEIGHT_MODIFIER = player -> buildStepHeightMod(DragonStateProvider.getUnsafeHandler(player).getSize());

    public static AttributeModifier buildHealthMod(double size) {
        double healthModifier;
        if(ServerConfig.allowLargeScaling && size > ServerConfig.maxHealthSize) {
            double healthModifierPercentage = Math.max(1.0, (size - ServerConfig.maxHealthSize) / (ServerConfig.maxGrowthSize - DragonLevel.ADULT.size));
            healthModifier = Mth.lerp(healthModifierPercentage, ServerConfig.maxHealth, ServerConfig.largeMaxHealth) - 20;
        }
        else {
            double healthModifierPercentage = Math.max(1.0, (size - DragonLevel.NEWBORN.size) / (ServerConfig.maxHealthSize - DragonLevel.NEWBORN.size));
            healthModifier = Mth.lerp(healthModifierPercentage, ServerConfig.minHealth, ServerConfig.maxHealth) - 20;
        }
        return new AttributeModifier(HEALTH_MODIFIER_UUID, "Dragon Health Adjustment", healthModifier, AttributeModifier.Operation.ADDITION);
    }

    public static AttributeModifier buildReachMod(double size) {
        double reachModifier;
        if(ServerConfig.allowLargeScaling && size > ServerConfig.DEFAULT_MAX_GROWTH_SIZE) {
            reachModifier = ServerConfig.reachBonus + ServerConfig.largeReachScalar * (size / ServerConfig.DEFAULT_MAX_GROWTH_SIZE);
        }
        else {
            reachModifier = Math.max(ServerConfig.reachBonus, (size - DragonLevel.NEWBORN.size) / (ServerConfig.DEFAULT_MAX_GROWTH_SIZE - DragonLevel.NEWBORN.size) * ServerConfig.reachBonus);
        }
        return new AttributeModifier(REACH_MODIFIER_UUID, "Dragon Reach Adjustment", reachModifier, Operation.MULTIPLY_BASE);
    }

    public static AttributeModifier buildDamageMod(final DragonStateHandler handler) {
        double ageBonus = handler.isDragon() ? handler.getLevel() == DragonLevel.ADULT ? ServerConfig.adultBonusDamage : handler.getLevel() == DragonLevel.YOUNG ? ServerConfig.youngBonusDamage : ServerConfig.babyBonusDamage : 0;
        if(ServerConfig.allowLargeScaling && handler.getSize() > ServerConfig.DEFAULT_MAX_GROWTH_SIZE) {
            double damageModPercentage = Math.max(1.0, (handler.getSize() - ServerConfig.DEFAULT_MAX_GROWTH_SIZE) / (ServerConfig.maxGrowthSize - ServerConfig.DEFAULT_MAX_GROWTH_SIZE));
            ageBonus = Mth.lerp(damageModPercentage, ageBonus, ServerConfig.largeDamageBonus);
        }
        return new AttributeModifier(DAMAGE_MODIFIER_UUID, "Dragon Damage Adjustment", ageBonus, Operation.ADDITION);
    }
	
	public static AttributeModifier buildAttackRangeMod(double size) {
		double rangeMod = (size - DragonLevel.NEWBORN.size) / (60.0 - DragonLevel.NEWBORN.size) * ServerConfig.attackRangeBonus;
		return new AttributeModifier(ATTACK_RANGE_MODIFIER_UUID, "Dragon Attack Range Adjustment", rangeMod, Operation.MULTIPLY_BASE);
	}

    public static AttributeModifier buildSwimSpeedMod(AbstractDragonType dragonType) {
        return new AttributeModifier(SWIM_SPEED_MODIFIER_UUID, "Dragon Swim Speed Adjustment", Objects.equals(dragonType, DragonTypes.SEA) && ServerConfig.seaSwimmingBonuses ? 1 : 0, Operation.ADDITION);
    }

    public static AttributeModifier buildStepHeightMod(double size) {
        double stepHeightBonus = 0;
        if(size > ServerConfig.DEFAULT_MAX_GROWTH_SIZE && ServerConfig.allowLargeScaling)  {
            stepHeightBonus = ServerConfig.largeStepHeightScalar * (size - ServerConfig.DEFAULT_MAX_GROWTH_SIZE) / ServerConfig.DEFAULT_MAX_GROWTH_SIZE;
        }
        return new AttributeModifier(STEP_HEIGHT_MODIFIER_UUID, "Dragon Step Height Adjustment", stepHeightBonus, Operation.ADDITION);
    }

    public static void updateModifiers(final Player oldPlayer, final Player newPlayer) {
        if (!DragonUtils.isDragon(newPlayer)) {
            return;
        }

        updateHealthModifier(newPlayer, getHealthModifier(oldPlayer));
        updateDamageModifier(newPlayer, getDamageModifier(oldPlayer));
        updateSwimSpeedModifier(newPlayer, getSwimSpeedModifier(oldPlayer));
        updateBlockReachModifier(newPlayer, getBlockReachModifier(oldPlayer));
        updateEntityReachModifier(newPlayer, getEntityReachModifier(oldPlayer));
        updateStepHeightModifier(newPlayer, getStepHeightModifier(oldPlayer));
    }

    public static @Nullable AttributeModifier getBlockReachModifier(final Player player) {
        return Objects.requireNonNull(player.getAttribute(ForgeMod.BLOCK_REACH.get())).getModifier(REACH_MODIFIER_UUID);
    }

    public static @Nullable AttributeModifier getEntityReachModifier(final Player player) {
        return Objects.requireNonNull(player.getAttribute(ForgeMod.ENTITY_REACH.get())).getModifier(REACH_MODIFIER_UUID);
    }

    public static @Nullable AttributeModifier getHealthModifier(final Player player) {
        return Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH)).getModifier(HEALTH_MODIFIER_UUID);
    }

    public static @Nullable AttributeModifier getDamageModifier(final Player player) {
        return Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_DAMAGE)).getModifier(DAMAGE_MODIFIER_UUID);
    }

    public static @Nullable AttributeModifier getSwimSpeedModifier(final Player player) {
        return Objects.requireNonNull(player.getAttribute(ForgeMod.SWIM_SPEED.get())).getModifier(SWIM_SPEED_MODIFIER_UUID);
    }

    public static @Nullable AttributeModifier getStepHeightModifier(final Player player) {
        return Objects.requireNonNull(player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get())).getModifier(STEP_HEIGHT_MODIFIER_UUID);
    }

    public static void updateBlockReachModifier(final Player player, @Nullable final AttributeModifier modifier) {
        if (!ServerConfig.bonuses) {
            return;
        }

        swap(player, ForgeMod.BLOCK_REACH.get(), modifier != null ? modifier : REACH_MODIFIER.apply(player));
    }

    public static void updateEntityReachModifier(final Player player, @Nullable final AttributeModifier modifier) {
        if (!ServerConfig.bonuses) {
            return;
        }

        swap(player, ForgeMod.ENTITY_REACH.get(), modifier != null ? modifier : REACH_MODIFIER.apply(player));
    }

    public static void updateHealthModifier(final Player player, @Nullable final AttributeModifier modifier) {
        if (!ServerConfig.healthAdjustments) {
            return;
        }

        float oldMax = player.getMaxHealth();
        swap(player, Attributes.MAX_HEALTH, modifier != null ? modifier : HEALTH_MODIFIER.apply(player));
        float newHealth = player.getHealth() * player.getMaxHealth() / oldMax;
        player.setHealth(newHealth);
    }

    public static void updateDamageModifier(final Player player, @Nullable final AttributeModifier modifier) {
        if (!ServerConfig.bonuses || !ServerConfig.attackDamage) {
            return;
        }

        swap(player, Attributes.ATTACK_DAMAGE, modifier != null ? modifier : DAMAGE_MODIFIER.apply(player));
    }

    public static void updateSwimSpeedModifier(final Player player, @Nullable final AttributeModifier modifier) {
        if (!ServerConfig.bonuses || !ServerConfig.seaSwimmingBonuses) {
            return;
        }

        swap(player, ForgeMod.SWIM_SPEED.get(), modifier != null ? modifier : SWIM_SPEED_MODIFIER.apply(player));
    }

    public static void updateStepHeightModifier(final Player player, @Nullable final AttributeModifier modifier) {
        if (!ServerConfig.bonuses || !ServerConfig.allowLargeScaling) {
            return;
        }

        swap(player, ForgeMod.STEP_HEIGHT_ADDITION.get(), modifier != null ? modifier : STEP_HEIGHT_MODIFIER.apply(player));
    }

    public static void swap(final Player player, final Attribute attribute, @NotNull final AttributeModifier modifier) {
        AttributeInstance instance = player.getAttribute(attribute);

        if (instance != null) {
            instance.removeModifier(modifier);
            instance.addPermanentModifier(modifier);
        }
    }
}