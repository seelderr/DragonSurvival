package by.dragonsurvivalteam.dragonsurvival.common.handlers.magic;

import by.dragonsurvivalteam.dragonsurvival.common.blocks.TreasureBlock;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.CaveDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.ForestDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.SeaDragonType;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
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
                conditionalBlocks = ServerConfig.caveConditionalManaBlocks;
                manaBlocks = DSBlockTags.REGENERATES_CAVE_DRAGON_MANA;
            }
            case SeaDragonType ignored -> {
                conditionalBlocks = ServerConfig.seaConditionalManaBlocks;
                manaBlocks = DSBlockTags.REGENERATES_SEA_DRAGON_MANA;
            }
            case ForestDragonType ignored -> {
                conditionalBlocks = ServerConfig.forestConditionalManaBlocks;
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

    public static int getMaxMana(Player entity) {
        int mana = 1 + (ServerConfig.noEXPRequirements ? 9 : Math.max(0, (Math.min(50, entity.experienceLevel) - 5) / 5) + (DragonAbilities.getSelfAbility(entity, MagicAbility.class) != null ? DragonAbilities.getSelfAbility(entity, MagicAbility.class).getMana() : 0));
        if (DragonUtils.getDragonBody(entity) != null)
            mana += DragonUtils.getDragonBody(entity).getManaBonus();
        return Math.max(mana, 0);
    }

    public static boolean canConsumeMana(Player player, int manaCost) {
        manaCost -= ManaHandler.getCurrentMana(player);
        if (ServerConfig.consumeEXPAsMana)
            manaCost -= player.totalExperience / 10;
        return manaCost <= 0;
    }

    public static void replenishMana(Player entity, int mana) {
        if (entity.level().isClientSide()) {
            return;
        }

        DragonStateProvider.getOptional(entity).ifPresent(cap -> {
            cap.getMagicData().setCurrentMana(Math.min(getMaxMana(entity), cap.getMagicData().getCurrentMana() + mana));
            PacketDistributor.sendToPlayer((ServerPlayer) entity, new SyncMagicStats.Data(entity.getId(), cap.getMagicData().getSelectedAbilitySlot(), cap.getMagicData().getCurrentMana(), cap.getMagicData().shouldRenderAbilities()));
        });
    }

    public static void consumeMana(Player entity, int mana) {
        if (entity == null || entity.isCreative() || entity.hasEffect(DSEffects.SOURCE_OF_MAGIC))
            return;

        if (ServerConfig.consumeEXPAsMana) {
            if (entity.level().isClientSide()) {
                if (getCurrentMana(entity) < mana && (getCurrentMana(entity) + entity.totalExperience / 10 >= mana || entity.experienceLevel > 0)) {
                    entity.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.01F, 0.01F);
                }
            }
        }

        if (entity.level().isClientSide()) {
            return;
        }

        DragonStateProvider.getOptional(entity).ifPresent(cap -> {
            if (ServerConfig.consumeEXPAsMana) {
                if (getCurrentMana(entity) < mana && (getCurrentMana(entity) + entity.totalExperience / 10 >= mana || entity.experienceLevel > 0)) {
                    int missingMana = mana - getCurrentMana(entity);
                    int missingExp = missingMana * 10;
                    entity.giveExperiencePoints(-missingExp);
                    cap.getMagicData().setCurrentMana(0);
                } else {
                    cap.getMagicData().setCurrentMana(Math.max(0, cap.getMagicData().getCurrentMana() - mana));
                }
            } else {
                cap.getMagicData().setCurrentMana(Math.max(0, cap.getMagicData().getCurrentMana() - mana));
            }

            PacketDistributor.sendToPlayer((ServerPlayer) entity, new SyncMagicStats.Data(entity.getId(), cap.getMagicData().getSelectedAbilitySlot(), cap.getMagicData().getCurrentMana(), cap.getMagicData().shouldRenderAbilities()));
        });
    }

    public static int getCurrentMana(Player entity) {
        return DragonStateProvider.getOptional(entity).map(cap -> Math.min(cap.getMagicData().getCurrentMana(), getMaxMana(entity))).orElse(0);
    }
}