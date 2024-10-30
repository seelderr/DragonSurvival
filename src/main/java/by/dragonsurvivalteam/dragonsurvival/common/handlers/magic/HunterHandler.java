package by.dragonsurvivalteam.dragonsurvival.common.handlers.magic;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.active.HunterAbility;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncHunterStacksRemoval;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.tags.DSBlockTags;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.util.Color;

/**
 * Handles things related to the hunter ability (or the effect to be more precise)
 * There is no check whether the player is a dragon to make this effect re-usable for other scenarios
 */
@EventBusSubscriber
public class HunterHandler { // FIXME :: disable shadows in EntityRenderDispatcher#render | entities / water are can only be seen through translucency with fabulous graphics enabled -> how to handle?
    public static final int MAX_HUNTER_STACKS = Functions.secondsToTicks(2) * HunterAbility.maxLevel();
    // Lower values starts to just be invisible (vanilla uses ~0.15)
    public static final float MIN_ALPHA = 0.2f;

    // When first or third person held items are actually being rendered there is not enough context to determine this value
    public static float itemTranslucency = -1;

    @SubscribeEvent
    public static void modifyHunterStacks(final PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        DragonStateHandler data = DragonStateProvider.getData(player);
        MobEffectInstance hunterEffect = player.getEffect(DSEffects.HUNTER);

        if (hunterEffect != null) {
            int modification;

            if (/* Below feet*/ isHunterRelevant(player.getBlockStateOn()) || /* Within block */ isHunterRelevant(player.getInBlockState())) {
                // Gain more stacks per tick per amplifier level (min. of 1 and max. of max. ability level)
                modification = Math.min(HunterAbility.maxLevel(), 1 + hunterEffect.getAmplifier());
            } else {
                // Per amplifier level lose fewer stacks per tick (min. of 1 and max. of max. ability level)
                modification = Math.min(HunterAbility.maxLevel() - 1, hunterEffect.getAmplifier()) - HunterAbility.maxLevel();
            }

            data.modifyHunterStacks(modification);
        }
    }

    @SubscribeEvent
    public static void clearCurrentTarget(final EntityTickEvent.Post event) {
        if (event.getEntity() instanceof Mob mob && mob.getTarget() instanceof Player player) {
            if (DragonStateProvider.getData(player).hasMaxHunterStacks()) {
                mob.setTarget(null);
            }
        }
    }

    @SubscribeEvent
    public static void removeHunterEffect(final LivingDamageEvent.Post event) {
        if (event.getEntity() instanceof Player player) {
            MobEffectInstance hunterEffect = player.getEffect(DSEffects.HUNTER);

            if (hunterEffect != null && event.getNewDamage() > hunterEffect.getAmplifier()) {
                player.removeEffect(DSEffects.HUNTER);
            }
        }
    }

    @SubscribeEvent
    public static void modifyVisibility(final LivingEvent.LivingVisibilityEvent event) {
        if (event.getEntity() instanceof Player player) {
            DragonStateHandler data = DragonStateProvider.getData(player);

            if (data.hasHunterStacks()) {
                // Even if this is set to 0, the min. radius will be set to 4 (2x2) in TargetingConditions#test
                event.modifyVisibility(1 - (double) data.getHunterStacks() / MAX_HUNTER_STACKS);
            }
        }
    }

    @SubscribeEvent
    public static void clearHunterStacks(final MobEffectEvent.Remove event) {
        if (event.getEffect().is(DSEffects.HUNTER) && event.getEntity() instanceof ServerPlayer serverPlayer) {
            clearHunterStacks(serverPlayer);
        }
    }

    @SubscribeEvent // When an effect expires it does not trigger the 'MobEffectEvent.Remove' event
    public static void clearHunterStacks(final MobEffectEvent.Expired event) {
        MobEffectInstance instance = event.getEffectInstance();

        if (instance != null && instance.getEffect().is(DSEffects.HUNTER) && event.getEntity() instanceof ServerPlayer serverPlayer) {
            clearHunterStacks(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void avoidTarget(final LivingChangeTargetEvent event) {
        if (event.getNewAboutToBeSetTarget() instanceof Player player) {
            if (DragonStateProvider.getData(player).hasMaxHunterStacks()) {
                event.setNewAboutToBeSetTarget(null);
            }
        }
    }

    @SubscribeEvent
    public static void handleCriticalBonus(final CriticalHitEvent event) {
        MobEffectInstance hunterEffect = event.getEntity().getEffect(DSEffects.HUNTER);

        if (hunterEffect == null) {
            return;
        }

        // An addition of 1 just means 100% of the damage (i.e. no multiplier)
        float bonus = hunterEffect.getAmplifier() + 2;

        if (DragonStateProvider.getData(event.getEntity()).hasMaxHunterStacks()) {
            bonus += 2f;
        }

        event.setCriticalHit(true);
        event.setDamageMultiplier(event.getDamageMultiplier() + bonus);
        event.getEntity().removeEffect(DSEffects.HUNTER);
    }

    /** Replaces (and returns) the alpha value of the packed color with the supplied alpha */
    public static int applyAlpha(float alpha, int packedColor) {
        return FastColor.ARGB32.color((int) (alpha * 255), FastColor.ARGB32.red(packedColor), FastColor.ARGB32.green(packedColor), FastColor.ARGB32.blue(packedColor));
    }

    /** Replaces (and returns) the alpha value of the packed color with the supplied alpha */
    public static int applyAlpha(int alpha, int packedColor) {
        return FastColor.ARGB32.color(alpha, FastColor.ARGB32.red(packedColor), FastColor.ARGB32.green(packedColor), FastColor.ARGB32.blue(packedColor));
    }

    /** Returns the packed color in the {@link FastColor.ARGB32#color(int, int, int, int)} format */
    public static int modifyAlpha(@Nullable final Player player, int packedColor) {
        if (player == null) {
            return packedColor;
        }

        DragonStateHandler data = DragonStateProvider.getData(player);

        if (!data.hasHunterStacks()) {
            return packedColor;
        }

        float alpha = calculateAlpha(data, player == DragonSurvival.PROXY.getLocalPlayer());
        return applyAlpha(alpha, packedColor);
    }

    /** Returns the packed color in the {@link Color#ofARGB(int, int, int, int)} format */
    public static Color modifyAlpha(@Nullable final Player player, Color color) {
        if (player == null) {
            return color;
        }

        DragonStateHandler data = DragonStateProvider.getData(player);

        if (!data.hasHunterStacks()) {
            return color;
        }

        int packedColor = color.getColor();
        float alpha = calculateAlpha(data, player == DragonSurvival.PROXY.getLocalPlayer());
        return Color.ofARGB((int) (alpha * 255), FastColor.ARGB32.red(packedColor), FastColor.ARGB32.green(packedColor), FastColor.ARGB32.blue(packedColor));
    }

    public static int calculateAlpha(@NotNull final Player player) {
        return (int) (calculateAlphaAsFloat(player) * 255);
    }

    public static float calculateAlphaAsFloat(@NotNull final Player player) {
        return calculateAlpha(DragonStateProvider.getData(player), player == DragonSurvival.PROXY.getLocalPlayer());
    }

    private static float calculateAlpha(@NotNull final DragonStateHandler data, boolean isLocalPlayer) {
        if (!data.hasHunterStacks() || data.isBeingRenderedInInventory) {
            return 1;
        }

        float min = isLocalPlayer || !HunterAbility.fullyInvisible ? MIN_ALPHA : 0;
        return Math.max(min, 1f - (float) data.getHunterStacks() / HunterHandler.MAX_HUNTER_STACKS);
    }

    private static boolean isHunterRelevant(final BlockState blockState) {
        return blockState.is(DSBlockTags.ENABLES_HUNTER_EFFECT);
    }

    private static void clearHunterStacks(final ServerPlayer player) {
        DragonStateProvider.getData(player).clearHunterStacks();
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncHunterStacksRemoval(player.getId()));
    }
}
