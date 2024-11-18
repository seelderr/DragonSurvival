package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.config.server.dragon.DragonBonusConfig;
import by.dragonsurvivalteam.dragonsurvival.config.server.dragon.SeaDragonConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive.CliffhangerAbility;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class DSModifiers {

    private record ModifierBuilder(ResourceLocation modifier, Holder<Attribute> attribute, Operation operation,
                                Function<Player, Double> calculator) {
        private AttributeModifier buildModifier(Player player) {
            return new AttributeModifier(modifier, calculator.apply(player), operation);
        }

        public void updateModifier(Player player) {
            // Special case for health modifier
            float oldMax = player.getMaxHealth();

            AttributeInstance instance = player.getAttribute(attribute);
            if (instance == null) {
                return;
            }
            AttributeModifier oldMod = instance.getModifier(modifier);
            if (oldMod != null) {
                instance.removeModifier(oldMod);
            }

            DragonStateHandler handler = DragonStateProvider.getData(player);
            if (handler.isDragon()) {
                AttributeModifier builtModifier = buildModifier(player);
                instance.addPermanentModifier(builtModifier);
                if (attribute == Attributes.MAX_HEALTH) {
                    float newHealth = Math.min(player.getMaxHealth(), player.getHealth() * player.getMaxHealth() / oldMax);
                    player.setHealth(newHealth);
                }
            }
        }
    }

    private static final ResourceLocation DRAGON_REACH_MODIFIER = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_reach_modifier");
    private static final ResourceLocation DRAGON_HEALTH_MODIFIER = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_health_modifier");
    private static final ResourceLocation DRAGON_DAMAGE_MODIFIER = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_damage_modifier");
    private static final ResourceLocation DRAGON_SWIM_SPEED_MODIFIER = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_swim_speed_modifier");
    private static final ResourceLocation DRAGON_STEP_HEIGHT_MODIFIER = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_step_height_modifier");
    private static final ResourceLocation DRAGON_MOVEMENT_SPEED_MODIFIER = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_movement_speed_modifier");
    private static final ResourceLocation DRAGON_JUMP_BONUS = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_jump_bonus");
    private static final ResourceLocation DRAGON_SAFE_FALL_DISTANCE = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_safe_fall_distance");
    private static final ResourceLocation DRAGON_SUBMERGED_MINING_SPEED = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_submerged_mining_speed");
    private static final ResourceLocation DRAGON_LAVA_SWIM_SPEED_MODIFIER = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_lava_swim_speed_modifier");
    private static final ResourceLocation DRAGON_FOREST_SAFE_FALL_DISTANCE_MODIFIER = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_forest_safe_fall_distance_modifier");

    private static final ResourceLocation DRAGON_BODY_MOVEMENT_SPEED = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_movement_speed");
    private static final ResourceLocation DRAGON_BODY_HEALTH_BONUS = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_health_bonus");
    private static final ResourceLocation DRAGON_BODY_ARMOR = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_armor");
    private static final ResourceLocation DRAGON_BODY_STRENGTH = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_strength");
    private static final ResourceLocation DRAGON_BODY_STRENGTH_MULT = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_strength_mult");
    private static final ResourceLocation DRAGON_BODY_KNOCKBACK_BONUS = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_knockback_bonus");
    private static final ResourceLocation DRAGON_BODY_SWIM_SPEED_BONUS = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_swim_speed_bonus");
    private static final ResourceLocation DRAGON_BODY_STEP_HEIGHT_BONUS = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_step_height_bonus");
    private static final ResourceLocation DRAGON_BODY_GRAVITY_MULT = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_gravity_mult");
    private static final ResourceLocation DRAGON_BODY_HEALTH_MULT = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_health_mult");
    private static final ResourceLocation DRAGON_BODY_JUMP_BONUS = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_jump_bonus");
    private static final ResourceLocation DRAGON_BODY_SAFE_FALL_DISTANCE = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_safe_fall_distance");
    private static final ResourceLocation DRAGON_BODY_FLIGHT_STAMINA = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_flight_stamina");

    // Used in MixinPlayerEntity to add the slow falling effect to dragons
    public static final ResourceLocation SLOW_FALLING = ResourceLocation.fromNamespaceAndPath(MODID, "slow_falling");

    // Used in EmoteHandler to keep track of the no move state
    public static final ResourceLocation EMOTE_NO_MOVE = ResourceLocation.fromNamespaceAndPath(MODID, "emote_no_move");

    // Modifier from the bolas item
    public static final ResourceLocation SLOW_MOVEMENT = ResourceLocation.fromNamespaceAndPath(MODID, "slow_movement");

    // Modifier for tough skin ability
    public static final ResourceLocation TOUGH_SKIN = ResourceLocation.fromNamespaceAndPath(MODID, "tough_skin");

    private static final List<ModifierBuilder> TYPE_MODIFIER_BUILDERS = List.of(
            new ModifierBuilder(DRAGON_SWIM_SPEED_MODIFIER, NeoForgeMod.SWIM_SPEED, Operation.ADD_VALUE, DSModifiers::buildSwimSpeedMod),
            new ModifierBuilder(DRAGON_SUBMERGED_MINING_SPEED, Attributes.SUBMERGED_MINING_SPEED, Operation.ADD_MULTIPLIED_TOTAL, DSModifiers::buildSubmergedMiningSpeedMod),
            new ModifierBuilder(DRAGON_LAVA_SWIM_SPEED_MODIFIER, DSAttributes.LAVA_SWIM_SPEED, Operation.ADD_VALUE, DSModifiers::buildLavaSwimSpeedMod),
            new ModifierBuilder(DRAGON_FOREST_SAFE_FALL_DISTANCE_MODIFIER, Attributes.SAFE_FALL_DISTANCE, Operation.ADD_VALUE, DSModifiers::buildForestSafeFallDistanceMod)
    );

    private static final List<ModifierBuilder> SIZE_MODIFIER_BUILDERS = List.of(
            new ModifierBuilder(DRAGON_HEALTH_MODIFIER, Attributes.MAX_HEALTH, Operation.ADD_VALUE, DSModifiers::buildHealthMod),
            new ModifierBuilder(DRAGON_DAMAGE_MODIFIER, Attributes.ATTACK_DAMAGE, Operation.ADD_VALUE, DSModifiers::buildDamageMod),
            new ModifierBuilder(DRAGON_REACH_MODIFIER, Attributes.BLOCK_INTERACTION_RANGE, Operation.ADD_MULTIPLIED_BASE, DSModifiers::buildReachMod),
            new ModifierBuilder(DRAGON_STEP_HEIGHT_MODIFIER, Attributes.STEP_HEIGHT, Operation.ADD_VALUE, DSModifiers::buildStepHeightMod),
            new ModifierBuilder(DRAGON_MOVEMENT_SPEED_MODIFIER, Attributes.MOVEMENT_SPEED, Operation.ADD_MULTIPLIED_TOTAL, DSModifiers::buildMovementSpeedMod),
            new ModifierBuilder(DRAGON_JUMP_BONUS, Attributes.JUMP_STRENGTH, Operation.ADD_VALUE, DSModifiers::buildJumpMod),
            new ModifierBuilder(DRAGON_SAFE_FALL_DISTANCE, Attributes.SAFE_FALL_DISTANCE, Operation.ADD_VALUE, DSModifiers::buildJumpMod)
    );

    private static final List<ModifierBuilder> BODY_MODIFIER_BUILDERS = List.of(
            new ModifierBuilder(DRAGON_BODY_MOVEMENT_SPEED, Attributes.MOVEMENT_SPEED, Operation.ADD_MULTIPLIED_TOTAL, player ->  DragonStateProvider.getData(player).getBody().getRunMult() - 1),
            new ModifierBuilder(DRAGON_BODY_ARMOR, Attributes.ARMOR, Operation.ADD_VALUE, player ->  DragonStateProvider.getData(player).getBody().getArmorBonus()),
            new ModifierBuilder(DRAGON_BODY_STRENGTH, Attributes.ATTACK_DAMAGE, Operation.ADD_VALUE, player ->  DragonStateProvider.getData(player).getBody().getDamageBonus()),
            new ModifierBuilder(DRAGON_BODY_STRENGTH_MULT, Attributes.ATTACK_DAMAGE, Operation.ADD_MULTIPLIED_TOTAL, player ->  DragonStateProvider.getData(player).getBody().getDamageMult() - 1),
            new ModifierBuilder(DRAGON_BODY_KNOCKBACK_BONUS, Attributes.ATTACK_KNOCKBACK, Operation.ADD_VALUE, player ->  DragonStateProvider.getData(player).getBody().getKnockbackBonus()),
            new ModifierBuilder(DRAGON_BODY_SWIM_SPEED_BONUS, NeoForgeMod.SWIM_SPEED, Operation.ADD_VALUE, player ->  DragonStateProvider.getData(player).getBody().getSwimSpeedBonus()),
            new ModifierBuilder(DRAGON_BODY_STEP_HEIGHT_BONUS, Attributes.STEP_HEIGHT, Operation.ADD_VALUE, player ->  DragonStateProvider.getData(player).getBody().getStepBonus()),
            new ModifierBuilder(DRAGON_BODY_GRAVITY_MULT, Attributes.GRAVITY, Operation.ADD_MULTIPLIED_TOTAL, player ->  DragonStateProvider.getData(player).getBody().getGravityMult() - 1),
            new ModifierBuilder(DRAGON_BODY_HEALTH_BONUS, Attributes.MAX_HEALTH, Operation.ADD_VALUE, player ->  DragonStateProvider.getData(player).getBody().getHealthBonus()),
            new ModifierBuilder(DRAGON_BODY_HEALTH_MULT, Attributes.MAX_HEALTH, Operation.ADD_MULTIPLIED_TOTAL, player ->  DragonStateProvider.getData(player).getBody().getHealthMult() - 1),
            new ModifierBuilder(DRAGON_BODY_JUMP_BONUS, Attributes.JUMP_STRENGTH, Operation.ADD_VALUE, player ->  DragonStateProvider.getData(player).getBody().getJumpBonus()),
            new ModifierBuilder(DRAGON_BODY_SAFE_FALL_DISTANCE, Attributes.SAFE_FALL_DISTANCE, Operation.ADD_VALUE, player -> DragonStateProvider.getData(player).getBody().getSafeFallBonus()),
            new ModifierBuilder(DRAGON_BODY_FLIGHT_STAMINA, DSAttributes.FLIGHT_STAMINA_COST, Operation.ADD_MULTIPLIED_TOTAL, player ->  DragonStateProvider.getData(player).getBody().getFlightStaminaMult())
    );
  
    private static double buildForestSafeFallDistanceMod(Player player) {
        double distance = 0;
        if (DragonUtils.isDragonType(player, DragonTypes.FOREST)) {
            Optional<CliffhangerAbility> ability = DragonAbilities.getAbility(player, CliffhangerAbility.class);
            if (ability.isPresent()) {
                distance = ability.get().getHeight();
            }
        }

        return distance;
    }
  
    private static double buildHealthMod(Player player) {
        if (!DragonBonusConfig.healthAdjustments) return 0;
        DragonStateHandler handler = DragonStateProvider.getData(player);
        double healthModifier;
        double size = handler.getSize();
        // TODO :: change to sth. simpler? e.g. health point per size point (0.5 by default?)
        //  still need to subtract default player health of 20 so smaller dragon sizes give a negative value (reduce health)
        if (ServerConfig.allowLargeScaling && size > ServerConfig.maxHealthSize) {
            healthModifier = ServerConfig.maxHealth + ServerConfig.largeMaxHealthScalar * ((size - ServerConfig.maxHealthSize) / ServerConfig.DEFAULT_MAX_GROWTH_SIZE) - 20;
        } else {
            double healthModifierPercentage = Math.min(1.0, (size - DragonLevel.NEWBORN.size) / (ServerConfig.maxHealthSize - DragonLevel.NEWBORN.size));
            healthModifier = Mth.lerp(healthModifierPercentage, ServerConfig.minHealth, ServerConfig.maxHealth) - 20;
        }
        return healthModifier;
    }

    private static double buildReachMod(Player player) {
        DragonStateHandler handler = DragonStateProvider.getData(player);
        double reachModifier;
        double size = handler.getSize();
        if (ServerConfig.allowLargeScaling && size > ServerConfig.DEFAULT_MAX_GROWTH_SIZE) {
            reachModifier = ServerConfig.reachBonus + ServerConfig.largeReachScalar * (size / ServerConfig.DEFAULT_MAX_GROWTH_SIZE);
        } else {
            reachModifier = Math.max(ServerConfig.reachBonus, (size - DragonLevel.NEWBORN.size) / (ServerConfig.DEFAULT_MAX_GROWTH_SIZE - DragonLevel.NEWBORN.size) * ServerConfig.reachBonus);
        }
        return reachModifier;
    }

    private static double buildDamageMod(Player player) {
        if (!DragonBonusConfig.isDamageBonusEnabled) return 0;
        DragonStateHandler handler = DragonStateProvider.getData(player);
        double ageBonus = handler.getLevel() == DragonLevel.ADULT ? DragonBonusConfig.adultBonusDamage : handler.getLevel() == DragonLevel.YOUNG ? DragonBonusConfig.youngBonusDamage : DragonBonusConfig.newbornBonusDamage;
        if (ServerConfig.allowLargeScaling && handler.getSize() > ServerConfig.DEFAULT_MAX_GROWTH_SIZE) {
            ageBonus += ServerConfig.largeDamageBonus * ((handler.getSize() - ServerConfig.DEFAULT_MAX_GROWTH_SIZE) / ServerConfig.DEFAULT_MAX_GROWTH_SIZE);
        }
        return ageBonus;
    }

    public static double buildSwimSpeedMod(Player player) {
        DragonStateHandler handler = DragonStateProvider.getData(player);
        return DragonUtils.isDragonType(handler, DragonTypes.SEA) && SeaDragonConfig.seaSwimmingBonuses ? 1 : 0;
    }

    private static double buildLavaSwimSpeedMod(Player player) {
        DragonStateHandler handler = DragonStateProvider.getData(player);
        // No extra config since it's basically already checked through 'ServerConfig#caveLavaSwimming'
        return DragonUtils.isDragonType(handler, DragonTypes.CAVE) ? 1 : 0;
    }

    private static double buildStepHeightMod(Player player) {
        DragonStateHandler handler = DragonStateProvider.getData(player);
        double size = handler.getSize();
        double stepHeightBonus = handler.getLevel() == DragonLevel.ADULT ? DragonBonusConfig.adultStepHeight : handler.getLevel() == DragonLevel.YOUNG ? DragonBonusConfig.youngStepHeight : DragonBonusConfig.newbornStepHeight;
        if (size > ServerConfig.DEFAULT_MAX_GROWTH_SIZE && ServerConfig.allowLargeScaling) {
            stepHeightBonus += ServerConfig.largeStepHeightScalar * (size - ServerConfig.DEFAULT_MAX_GROWTH_SIZE) / ServerConfig.DEFAULT_MAX_GROWTH_SIZE;
        }
        return stepHeightBonus;
    }

    private static double buildMovementSpeedMod(Player player) {
        DragonStateHandler handler = DragonStateProvider.getData(player);
        double moveSpeedMultiplier = 1;
        double size = handler.getSize();
        if (handler.getLevel() == DragonLevel.NEWBORN) {
            double youngPercent = Math.min(1.0, (size - DragonLevel.NEWBORN.size) / (DragonLevel.YOUNG.size - DragonLevel.NEWBORN.size));
            moveSpeedMultiplier = Mth.lerp(youngPercent, ServerConfig.moveSpeedNewborn, ServerConfig.moveSpeedYoung);
        } else if (handler.getLevel() == DragonLevel.YOUNG) {
            double adultPercent = Math.min(1.0, (size - DragonLevel.YOUNG.size) / (DragonLevel.ADULT.size - DragonLevel.YOUNG.size));
            moveSpeedMultiplier = Mth.lerp(adultPercent, ServerConfig.moveSpeedYoung, ServerConfig.moveSpeedAdult);
        } else if (handler.getLevel() == DragonLevel.ADULT) {
            if (ServerConfig.allowLargeScaling && size > ServerConfig.DEFAULT_MAX_GROWTH_SIZE) {
                moveSpeedMultiplier = ServerConfig.moveSpeedAdult + ServerConfig.largeMovementSpeedScalar * (size - ServerConfig.DEFAULT_MAX_GROWTH_SIZE) / ServerConfig.DEFAULT_MAX_GROWTH_SIZE;
            } else {
                moveSpeedMultiplier = ServerConfig.moveSpeedAdult;
            }
        }
        return moveSpeedMultiplier - 1;
    }

    // Needs to be public for fall damage math in DragonBonusHandler
    public static double buildJumpMod(Player player) {
        DragonStateHandler handler = DragonStateProvider.getData(player);
        double jumpBonus = 0;
        if (handler.getBody() != null) {
            jumpBonus = handler.getBody().getJumpBonus();
            if (ServerConfig.allowLargeScaling && handler.getSize() > ServerConfig.DEFAULT_MAX_GROWTH_SIZE) {
                jumpBonus += ServerConfig.largeJumpHeightScalar * (handler.getSize() - ServerConfig.DEFAULT_MAX_GROWTH_SIZE) / ServerConfig.DEFAULT_MAX_GROWTH_SIZE;
            }
        }
        switch (handler.getLevel()) {
            case NEWBORN -> jumpBonus += DragonBonusConfig.newbornJump; //1+ block
            case YOUNG -> jumpBonus += DragonBonusConfig.youngJump; //1.5+ block
            case ADULT -> jumpBonus += DragonBonusConfig.adultJump; //2+ blocks
        }
        return jumpBonus;
    }

    private static double buildSubmergedMiningSpeedMod(Player player) {
        DragonStateHandler handler = DragonStateProvider.getData(player);
        return DragonUtils.isDragonType(handler, DragonTypes.SEA) ? (2 * (handler.getLevel().ordinal() + 1)) : 0;
    }

    public static void updateAllModifiers(Player player) {
        updateTypeModifiers(player);
        updateSizeModifiers(player);
        updateBodyModifiers(player);
    }

    public static void updateTypeModifiers(Player player) {
        for (ModifierBuilder builder : TYPE_MODIFIER_BUILDERS) {
            builder.updateModifier(player);
        }
    }

    public static void updateSizeModifiers(Player player) {
        for (ModifierBuilder builder : SIZE_MODIFIER_BUILDERS) {
            builder.updateModifier(player);
        }
    }

    public static void updateBodyModifiers(Player player) {
        for (ModifierBuilder builder : BODY_MODIFIER_BUILDERS) {
            builder.updateModifier(player);
        }
    }

    public static void updateSafeFallDistanceModifiers(Player player) {
        for (ModifierBuilder builder : SIZE_MODIFIER_BUILDERS) {
            if (builder.modifier.equals(DRAGON_SAFE_FALL_DISTANCE)) {
                builder.updateModifier(player);
            }
        }
        for (ModifierBuilder builder : BODY_MODIFIER_BUILDERS) {
            if (builder.modifier.equals(DRAGON_BODY_SAFE_FALL_DISTANCE)) {
                builder.updateModifier(player);
            }
        }
    }
}