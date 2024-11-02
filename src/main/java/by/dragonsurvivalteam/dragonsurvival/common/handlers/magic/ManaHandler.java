package by.dragonsurvivalteam.dragonsurvival.common.handlers.magic;

import by.dragonsurvivalteam.dragonsurvival.common.blocks.TreasureBlock;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.CaveDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.ForestDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.SeaDragonType;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.config.server.dragon.CaveDragonConfig;
import by.dragonsurvivalteam.dragonsurvival.config.server.dragon.ForestDragonConfig;
import by.dragonsurvivalteam.dragonsurvival.config.server.dragon.SeaDragonConfig;
import by.dragonsurvivalteam.dragonsurvival.config.types.BlockStateConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.MagicAbility;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncMagicStats;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.tags.DSBlockTags;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EventBusSubscriber
public class ManaHandler {
    private static final int EXPERIENCE_TO_MANA = 10;

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        DragonStateProvider.getOptional(player).ifPresent(cap -> {
            if (cap.getMagicData().getCurrentlyCasting() != null) {
                return;
            }

            boolean goodConditions = ManaHandler.isPlayerInGoodConditions(player);

            int timeToRecover = goodConditions ? ServerConfig.favorableManaTicks : ServerConfig.normalManaTicks;

            if (player.hasEffect(DSEffects.SOURCE_OF_MAGIC)) {
                timeToRecover = 1;
            }

            if (player.tickCount % Functions.secondsToTicks(timeToRecover) == 0) {
                if (cap.getMagicData().getCurrentMana() < getMaxMana(player)) {
                    replenishMana(player, 1);
                }
            }
        });
    }

    public static boolean isPlayerInGoodConditions(@NotNull Player player) {
        DragonStateHandler data = DragonStateProvider.getData(player);

        if (!data.isDragon()) {
            return false;
        }

        if (player.hasEffect(DSEffects.SOURCE_OF_MAGIC)) {
            return true;
        }

        BlockState state = player.getBlockStateOn();
        List<BlockStateConfig> conditionalBlocks;
        TagKey<Block> manaBlocks;

        switch (data.getType()) {
            case CaveDragonType ignored -> {
                conditionalBlocks = CaveDragonConfig.caveConditionalManaBlocks;
                manaBlocks = DSBlockTags.REGENERATES_CAVE_DRAGON_MANA;
            }
            case SeaDragonType ignored -> {
                conditionalBlocks = SeaDragonConfig.seaConditionalManaBlocks;
                manaBlocks = DSBlockTags.REGENERATES_SEA_DRAGON_MANA;
            }
            case ForestDragonType ignored -> {
                conditionalBlocks = ForestDragonConfig.forestConditionalManaBlocks;
                manaBlocks = DSBlockTags.REGENERATES_FOREST_DRAGON_MANA;
            }
            default -> throw new IllegalStateException("Invalid dragon type: [" + data.getType().getClass().getName() + "]");
        }

        if (state.getBlock() instanceof TreasureBlock || state.is(manaBlocks)) {
            return true;
        }

        for (BlockStateConfig config : conditionalBlocks) {
            if (config.test(state)) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasEnoughMana(final Player player, int manaCost) {
        return player.hasEffect(DSEffects.SOURCE_OF_MAGIC) || getCurrentMana(player) - manaCost >= 0;
    }

    public static int getMaxMana(final Player player) {
        int mana;

        if (ServerConfig.consumeExperienceAsMana) {
            mana = 1 + getManaExperienceFromLevels(player);
        } else {
            mana = 10;
        }

        DragonStateHandler data = DragonStateProvider.getData(player);
        mana += DragonAbilities.getAbility(player, MagicAbility.class, data.getType()).map(MagicAbility::getMana).orElse(0);

        if (DragonUtils.getDragonBody(player) != null) {
            mana += data.getBody().getManaBonus();
        }

        return Math.max(mana, 0);
    }

    public static void replenishMana(Player player, int mana) {
        if (player.level().isClientSide()) {
            return;
        }

        DragonStateHandler data = DragonStateProvider.getData(player);

        if (getCurrentMana(player) == getMaxMana(player)) {
            return;
        }

        data.getMagicData().setCurrentMana(data.getMagicData().getCurrentMana() + mana);
        PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncMagicStats.Data(player.getId(), data.getMagicData().getSelectedAbilitySlot(), data.getMagicData().getCurrentMana(), data.getMagicData().shouldRenderAbilities()));
    }

    public static void consumeMana(Player player, int manaCost) {
        if (player == null || player.isCreative() || player.hasEffect(DSEffects.SOURCE_OF_MAGIC) || !hasEnoughMana(player, manaCost)) {
            return;
        }

        DragonStateHandler data = DragonStateProvider.getData(player);
        int pureMana = data.getMagicData().getCurrentMana();

        if (ServerConfig.consumeExperienceAsMana && player.level().isClientSide()) {
            // Check if experience would be consumed as part of the mana cost
            if (pureMana < manaCost && getCurrentMana(player) >= manaCost) {
                player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.01F, 0.01F);
            }
        }

        if (player.level().isClientSide()) {
            return;
        }

        if (ServerConfig.consumeExperienceAsMana) {
            if (pureMana < manaCost) {
                int missingMana = pureMana - manaCost;
                int experienceCost = missingMana * EXPERIENCE_TO_MANA;
                player.giveExperiencePoints(experienceCost);
                data.getMagicData().setCurrentMana(0);
            } else {
                data.getMagicData().setCurrentMana(pureMana - manaCost);
            }
        } else {
            data.getMagicData().setCurrentMana(pureMana - manaCost);
        }

        PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncMagicStats.Data(player.getId(), data.getMagicData().getSelectedAbilitySlot(), data.getMagicData().getCurrentMana(), data.getMagicData().shouldRenderAbilities()));
    }

    public static int getCurrentMana(Player player) {
        DragonStateHandler data = DragonStateProvider.getData(player);
        int currentMana = data.getMagicData().getCurrentMana();

        if (ServerConfig.consumeExperienceAsMana) {
            currentMana += getManaExperienceFromLevels(player);
        }

        return Math.min(currentMana, getMaxMana(player));
    }

    private static int getManaExperienceFromLevels(final Player player) {
        return getManaExperience(getTotalExperience(player));
    }

    /** 10 experience points count as 1 mana */
    private static int getManaExperience(int experience) {
        return experience / EXPERIENCE_TO_MANA;
    }

    /**
     * <a href="https://github.com/Shadows-of-Fire/Placebo/blob/1.21/src/main/java/dev/shadowsoffire/placebo/util/EnchantmentUtils.java#L60">Taken from here</a>
     * <br> <br>
     * Calculates the amount of experience the passed level is worth <br>
     * <a href="https://minecraft.wiki/w/Experience#Leveling_up">Reference</a>
     *
     * @param level The target level
     * @return The amount of experience required to reach the given level when starting from the previous level
     */
    public static int getExperienceForLevel(int level) {
        if (level == 0) return 0;
        if (level > 30) return 112 + (level - 31) * 9;
        if (level > 15) return 37 + (level - 16) * 5;
        return 7 + (level - 1) * 2;
    }

    /** Calculate teh total experience the player has based on their experience levels */
    public static int getTotalExperience(final Player player) {
        int experience = 0;

        for (int level = 1; level <= player.experienceLevel; level++) {
            experience += getExperienceForLevel(level);
        }

        return experience;
    }
}