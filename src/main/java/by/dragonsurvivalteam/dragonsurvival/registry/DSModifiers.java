package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ModifierType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.server.dragon.SeaDragonConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive.CliffhangerAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.AttributeModifierSupplier;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class DSModifiers {
    private record ModifierBuilder(ResourceLocation modifier, Holder<Attribute> attribute, Operation operation, Function<DragonStateHandler, Double> calculator) {
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
        return DragonAbilities.getAbility(handler, CliffhangerAbility.class).map(CliffhangerAbility::getHeight).orElse(0);
    }

    public static double buildSwimSpeedMod(DragonStateHandler handler) {
        return DragonUtils.isType(handler, DragonTypes.SEA) && SeaDragonConfig.seaSwimmingBonuses ? 1 : 0;
    }

    private static double buildLavaSwimSpeedMod(DragonStateHandler handler) {
        // No extra config since it's basically already checked through 'ServerConfig#caveLavaSwimming'
        return DragonUtils.isType(handler, DragonTypes.CAVE) ? 1 : 0;
    }

    public static void updateAllModifiers(Player player) {
        if (player == null || player.level().isClientSide()) {
            return;
        }

        DragonStateHandler handler = DragonStateProvider.getData(player);
        updateTypeModifiers(player, handler);
        updateSizeModifiers(player, handler);
        updateBodyModifiers(player, handler);

        // TODO :: determine where it's best to handle max health
    }

    public static void updateTypeModifiers(Player player, DragonStateHandler handler) {
        if (player == null || player.level().isClientSide()) {
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

        AttributeModifierSupplier.removeModifiers(ModifierType.DRAGON_STAGE, player);

        if (handler.isDragon()) {
            handler.getStage().value().applyModifiers(player, handler.getTypeNameLowerCase(), handler.getSize());
        }
    }

    public static void updateBodyModifiers(@Nullable Player player, DragonStateHandler handler) {
        if (player == null || player.level().isClientSide()) {
            return;
        }

        AttributeModifierSupplier.removeModifiers(ModifierType.DRAGON_BODY, player);

        if (handler.isDragon()) {
            handler.getBody().value().applyModifiers(player, handler.getTypeNameLowerCase(), /* Body has nothing to scale */ 1);
        }
    }
}