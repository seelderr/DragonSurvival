package by.dragonsurvivalteam.dragonsurvival.common.handlers.magic;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.config.types.BlockStateConfig;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncMana;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAttributes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MagicData;
import by.dragonsurvivalteam.dragonsurvival.util.ExperienceUtils;
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
    /**
     * Ratio for converting experience to mana and vice versa <br> <br>
     * Level  5:   55 experience points <br>
     * Level 10:  160 experience points <br>
     * Level 20:  550 experience points <br>
     * Level 30: 1395 experience points <br>
     */
    private static final int EXPERIENCE_TO_MANA = 10;
    private static final int LEVELS_TO_MANA = 4;

    public static final int MAX_MANA_FROM_ABILITY = 10;
    private static final int MAX_MANA_FROM_LEVELS = 9;

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        MagicData data = MagicData.getData(player);
        if (data.getCurrentlyCasting() != null) {
            return;
        }

        boolean goodConditions = ManaHandler.isPlayerInGoodConditions(player);

        int timeToRecover = goodConditions ? ServerConfig.favorableManaTicks : ServerConfig.normalManaTicks;

        if (player.hasEffect(DSEffects.SOURCE_OF_MAGIC)) {
            timeToRecover = 1;
        }

        if (player.tickCount % Functions.secondsToTicks(timeToRecover) == 0) {
            if (data.getCurrentMana() < getMaxMana(player)) {
                replenishMana(player, 1);
            }
        }
    }

    public static boolean isPlayerInGoodConditions(@NotNull Player player) {
        DragonStateHandler data = DragonStateProvider.getData(player);

        if (!data.isDragon()) {
            return false;
        }

        // FIXME
        if (player.hasEffect(DSEffects.SOURCE_OF_MAGIC) /*|| data.isInManaCondition(player)*/) {
            return true;
        }

        BlockState state = player.getBlockStateOn();
        List<BlockStateConfig> conditionalBlocks;
        TagKey<Block> manaBlocks;

        // FIXME
        /*switch (data.getType()) {
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
        }*/

        return false;
    }

    public static boolean hasEnoughMana(final Player player, int manaCost) {
        if (player.hasEffect(DSEffects.SOURCE_OF_MAGIC) || player.isCreative()) {
            return true;
        }

        int currentMana = getCurrentMana(player);

        if (ServerConfig.consumeExperienceAsMana) {
            currentMana += getManaFromExperience(player);
        }

        return currentMana - manaCost >= 0;
    }

    public static int getMaxMana(final Player player) {
        int mana = (int) player.getAttributeValue(DSAttributes.MANA);
        mana += getBonusManaFromExperience(player);

        return Math.max(0, mana);
    }

    public static void replenishMana(Player player, int mana) {
        if (player.level().isClientSide()) {
            return;
        }

        MagicData data = MagicData.getData(player);

        if (getCurrentMana(player) == getMaxMana(player)) {
            return;
        }

        data.setCurrentMana(data.getCurrentMana() + mana);
        PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncMana(player.getId(), data.getCurrentMana()));
    }

    public static void consumeMana(Player player, int manaCost) {
        if (player == null || player.isCreative() || player.hasEffect(DSEffects.SOURCE_OF_MAGIC) || !hasEnoughMana(player, manaCost)) {
            return;
        }

        int pureMana = getCurrentMana(player);

        if (ServerConfig.consumeExperienceAsMana && player.level().isClientSide()) {
            // Check if experience would be consumed as part of the mana cost
            if (pureMana < manaCost && getCurrentMana(player) + getManaFromExperience(player) >= manaCost) {
                player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.01F, 0.01F);
            }
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        MagicData magicData = MagicData.getData(serverPlayer);
        if (ServerConfig.consumeExperienceAsMana) {
            if (pureMana < manaCost) {
                int missingMana = pureMana - manaCost;
                player.giveExperiencePoints(convertMana(missingMana));
                magicData.setCurrentMana(0);
            } else {
                magicData.setCurrentMana(pureMana - manaCost);
            }
        } else {
            magicData.setCurrentMana(pureMana - manaCost);
        }

        PacketDistributor.sendToPlayer(serverPlayer, new SyncMana(player.getId(), magicData.getCurrentMana()));
    }

    public static int getCurrentMana(Player player) {
        return Math.min(MagicData.getData(player).getCurrentMana(), getMaxMana(player));
    }

    public static int getBonusManaFromExperience(final Player player) {
        return Math.min(MAX_MANA_FROM_LEVELS, convertLevels(player.experienceLevel));
    }

    public static int getManaFromExperience(final Player player) {
        return convertExperience(ExperienceUtils.getTotalExperience(player));
    }

    /** Convert experience points to mana based on the {@link ManaHandler#LEVELS_TO_MANA} ratio */
    private static int convertLevels(int levels) {
        return levels / LEVELS_TO_MANA;
    }

    /** Convert experience points to mana based on the {@link ManaHandler#EXPERIENCE_TO_MANA} ratio */
    private static int convertExperience(int experience) {
        return experience / EXPERIENCE_TO_MANA;
    }

    /** Convert mana to experience points based on the {@link ManaHandler#EXPERIENCE_TO_MANA} ratio */
    private static int convertMana(int mana) {
        return mana * EXPERIENCE_TO_MANA;
    }
}