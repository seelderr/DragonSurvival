package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.HarvestBonus;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import javax.annotation.Nullable;

// TODO :: have some generic class since the structure is similar for the different things like modifier, harvest bonus, damage modification
//  like Bonus<type> and some sort of interface or sth.?
public class HarvestBonuses implements INBTSerializable<CompoundTag> {
    public static final String HARVEST_BONUSES = "harvest_bonuses";

    @Nullable private Map<ResourceLocation, HarvestBonus.Instance> harvestBonuses;

    public void tick(final LivingEntity entity) {
        if (harvestBonuses != null) {
            Set<ResourceLocation> finished = new HashSet<>();

            harvestBonuses.values().forEach(bonus -> {
                if (bonus.tick()) {
                    finished.add(bonus.baseData().id());
                }
            });

            finished.forEach(id -> harvestBonuses.remove(id));

            if (harvestBonuses.isEmpty()) {
                entity.removeData(DSDataAttachments.HARVEST_BONUSES);
            }
        }
    }

    public int get(final BlockState state) {
        if (harvestBonuses == null) {
            return 0;
        }

        Holder<Block> block = state.getBlockHolder();
        int bonus = 0;

        for (HarvestBonus.Instance instance : harvestBonuses.values()) {
            bonus += instance.getBonus(block);
        }

        return bonus;
    }

    public void add(final HarvestBonus.Instance bonus) {
        if (harvestBonuses == null) {
            harvestBonuses = new HashMap<>();
        }

        harvestBonuses.put(bonus.baseData().id(), bonus);
    }

    public void remove(final HarvestBonus bonus) {
        if (harvestBonuses == null) {
            return;
        }

        harvestBonuses.remove(bonus.id());
    }

    public @Nullable HarvestBonus.Instance get(final HarvestBonus bonus) {
        if (harvestBonuses == null) {
            return null;
        }

        return harvestBonuses.get(bonus.id());
    }

    public int size() {
        if (harvestBonuses == null) {
            return 0;
        }

        return harvestBonuses.size();
    }

    public Collection<HarvestBonus.Instance> all() {
        if (harvestBonuses == null) {
            return Collections.emptyList();
        }

        return harvestBonuses.values();
    }

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag entries = new ListTag();

        if (harvestBonuses != null) {
            harvestBonuses.values().forEach(HarvestBonus.Instance::save);
            tag.put(HARVEST_BONUSES, entries);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        Map<ResourceLocation, HarvestBonus.Instance> bonuses = new HashMap<>();
        ListTag entries = tag.getList(HARVEST_BONUSES, ListTag.TAG_COMPOUND);

        for (int i = 0; i < entries.size(); i++) {
            HarvestBonus.Instance bonus = HarvestBonus.Instance.load(entries.getCompound(i));

            if (bonus != null) {
                bonuses.put(bonus.baseData().id(), bonus);
            }
        }

        if (!bonuses.isEmpty()) {
            harvestBonuses = bonuses;
        } else {
            harvestBonuses = null;
        }
    }

    @SubscribeEvent
    public static void tickModifiers(final EntityTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        if (event.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.getExistingData(DSDataAttachments.HARVEST_BONUSES).ifPresent(data -> data.tick(livingEntity));
        }
    }
}
