package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.HarvestBonus;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;

public class HarvestBonuses extends Storage<HarvestBonus.Instance> {
    public int get(final BlockState state) {
        if (storage == null) {
            return 0;
        }

        Holder<Block> block = state.getBlockHolder();
        int bonus = 0;

        for (HarvestBonus.Instance instance : storage.values()) {
            bonus += instance.getBonus(block);
        }

        return bonus;
    }

    @SubscribeEvent
    public static void tickBonuses(final EntityTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        if (event.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.getExistingData(DSDataAttachments.HARVEST_BONUSES).ifPresent(storage -> {
                storage.tick();

                if (storage.isEmpty()) {
                    livingEntity.removeData(DSDataAttachments.HARVEST_BONUSES);
                }
            });
        }
    }

    @Override
    protected Tag save(@NotNull final HolderLookup.Provider provider, final HarvestBonus.Instance entry) {
        return entry.save(provider);
    }

    @Override
    protected HarvestBonus.Instance load(@NotNull final HolderLookup.Provider provider, final CompoundTag tag) {
        return HarvestBonus.Instance.load(provider, tag);
    }
}
