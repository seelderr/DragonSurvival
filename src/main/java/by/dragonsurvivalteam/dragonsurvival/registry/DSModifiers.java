package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ModifierType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.config.server.dragon.DragonBonusConfig;
import by.dragonsurvivalteam.dragonsurvival.config.server.dragon.SeaDragonConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive.CliffhangerAbility;
import by.dragonsurvivalteam.dragonsurvival.mixins.AttributeMapAccessor;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonBody;
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

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class DSModifiers {

    private record ModifierBuilder(ResourceLocation modifier, Holder<Attribute> attribute, Operation operation,
                                Function<DragonStateHandler, Double> calculator) {
        private AttributeModifier buildModifier(DragonStateHandler handler) {
            return new AttributeModifier(modifier, calculator.apply(handler), operation);
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
                AttributeModifier builtModifier = buildModifier(handler);
                instance.addPermanentModifier(builtModifier);
                if (attribute == Attributes.MAX_HEALTH) {
                    float newHealth = Math.min(player.getMaxHealth(), player.getHealth() * player.getMaxHealth() / oldMax);
                    player.setHealth(newHealth);
                }
            }
        }
    }

    private static final ResourceLocation DRAGON_SWIM_SPEED_MODIFIER = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_swim_speed_modifier");
    private static final ResourceLocation DRAGON_SAFE_FALL_DISTANCE = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_safe_fall_distance");
    private static final ResourceLocation DRAGON_LAVA_SWIM_SPEED_MODIFIER = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_lava_swim_speed_modifier");
    private static final ResourceLocation DRAGON_FOREST_SAFE_FALL_DISTANCE_MODIFIER = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_forest_safe_fall_distance_modifier");

    // Used in EmoteHandler to keep track of the no move state
    public static final ResourceLocation EMOTE_NO_MOVE = ResourceLocation.fromNamespaceAndPath(MODID, "emote_no_move");

    // Modifier from the bolas item
    public static final ResourceLocation SLOW_MOVEMENT = ResourceLocation.fromNamespaceAndPath(MODID, "slow_movement");

    // Modifier for tough skin ability
    public static final ResourceLocation TOUGH_SKIN = ResourceLocation.fromNamespaceAndPath(MODID, "tough_skin");

    private static final List<ModifierBuilder> TYPE_MODIFIER_BUILDERS = List.of(
            new ModifierBuilder(DRAGON_SWIM_SPEED_MODIFIER, NeoForgeMod.SWIM_SPEED, Operation.ADD_VALUE, DSModifiers::buildSwimSpeedMod),
            new ModifierBuilder(DRAGON_LAVA_SWIM_SPEED_MODIFIER, DSAttributes.LAVA_SWIM_SPEED, Operation.ADD_VALUE, DSModifiers::buildLavaSwimSpeedMod),
            new ModifierBuilder(DRAGON_FOREST_SAFE_FALL_DISTANCE_MODIFIER, Attributes.SAFE_FALL_DISTANCE, Operation.ADD_VALUE, DSModifiers::buildForestSafeFallDistanceMod)
    );

    private static double buildForestSafeFallDistanceMod(DragonStateHandler handler) {
        double distance = 0;
        if (DragonUtils.isType(handler, DragonTypes.FOREST)) {
            Optional<CliffhangerAbility> ability = DragonAbilities.getAbility(handler, CliffhangerAbility.class);
            if (ability.isPresent()) {
                distance = ability.get().getHeight();
            }
        }

        return distance;
    }

    public static double buildSwimSpeedMod(DragonStateHandler handler) {
        return DragonUtils.isType(handler, DragonTypes.SEA) && SeaDragonConfig.seaSwimmingBonuses ? 1 : 0;
    }

    private static double buildLavaSwimSpeedMod(DragonStateHandler handler) {
        // No extra config since it's basically already checked through 'ServerConfig#caveLavaSwimming'
        return DragonUtils.isType(handler, DragonTypes.CAVE) ? 1 : 0;
    }

    public static void updateAllModifiers(Player player) {
        if (player.level().isClientSide()) {
            return;
        }

        DragonStateHandler handler = DragonStateProvider.getData(player);

        updateTypeModifiers(player, handler);
        updateSizeModifiers(player, handler);
        updateBodyModifiers(player, handler);
    }

    public static void updateTypeModifiers(Player player, DragonStateHandler handler) {
        if (player.level().isClientSide()) {
            return;
        }

        for (ModifierBuilder builder : TYPE_MODIFIER_BUILDERS) {
            builder.updateModifier(player);
        }
    }

    public static void updateSizeModifiers(@Nullable Player player, DragonStateHandler handler) {
        if (player == null || player.level().isClientSide()) {
            return;
        }

        // FIXME
    }

    public static void updateBodyModifiers(@Nullable Player player, DragonStateHandler handler) {
        if (player == null || player.level().isClientSide()) {
            return;
        }

        Map<Holder<Attribute>, AttributeInstance> attributes = ((AttributeMapAccessor) player.getAttributes()).dragonSurvival$getAttributes();

        attributes.values().forEach(instance -> instance.getModifiers().forEach(modifier -> {
            if (modifier.id().getPath().startsWith(ModifierType.DRAGON_BODY.path())) {
                instance.removeModifier(modifier);
            }
        }));

        if (handler.isDragon()) {
            //noinspection DataFlowIssue -> body is present
            handler.getBody().value().modifiers().forEach(modifier -> {
                AttributeInstance instance = player.getAttribute(modifier.attribute());

                if (instance != null && (modifier.dragonType().isEmpty() || modifier.dragonType().get().equals(handler.getTypeNameLowerCase()))) {
                    instance.addPermanentModifier(modifier.modifier());
                } else {
                    DragonSurvival.LOGGER.error("Player does not have the attribute [{}] - bonus from dragon body [{}] cannot be applied", modifier.attribute(), handler.getBody());
                }
            });
        }
    }
}