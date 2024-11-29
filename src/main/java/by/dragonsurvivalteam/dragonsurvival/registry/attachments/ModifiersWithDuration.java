package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.ModifierWithDuration;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class ModifiersWithDuration implements INBTSerializable<CompoundTag> {
    public static final String MODIFIERS_WITH_DURATION = "modifiers_with_duration";

    @Nullable public List<ModifierWithDuration> modifiersWithDuration;

    public void tick(final LivingEntity entity) {
        if (modifiersWithDuration != null) {
            modifiersWithDuration.forEach(modifier -> modifier.tick(entity));
        }
    }

    public void add(final ModifierWithDuration modifier) {
        if (modifiersWithDuration == null) {
            modifiersWithDuration = new ArrayList<>();
        }

        modifiersWithDuration.add(modifier);
    }

    public void remove(final LivingEntity entity, final ModifierWithDuration modifier) {
        if (modifiersWithDuration == null) {
            return;
        }

        modifiersWithDuration.remove(modifier);
        modifier.removeModifiers(entity);
    }

    public boolean contains(final ModifierWithDuration modifier) {
        if (modifiersWithDuration == null) {
            return false;
        }

        return modifiersWithDuration.contains(modifier);
    }

    public void removeAll(final LivingEntity entity) {
        if (modifiersWithDuration == null) {
            return;
        }

        modifiersWithDuration.forEach(modifier -> modifier.removeModifiers(entity));
    }

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag entries = new ListTag();

        if (modifiersWithDuration != null) {
            modifiersWithDuration.forEach(ModifierWithDuration::save);
            tag.put(MODIFIERS_WITH_DURATION, entries);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        ArrayList<ModifierWithDuration> modifiersWithDuration = new ArrayList<>();
        ListTag entries = tag.getList(MODIFIERS_WITH_DURATION, ListTag.TAG_COMPOUND);

        for (int i = 0; i < entries.size(); i++) {
            ModifierWithDuration modifier = ModifierWithDuration.load(entries.getCompound(i));

            if (modifier != null) {
                modifiersWithDuration.add(modifier);
            }
        }

        if (!modifiersWithDuration.isEmpty()) {
            this.modifiersWithDuration = modifiersWithDuration;
        }
    }

    @SubscribeEvent
    public static void tickModifiers(final EntityTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        if (event.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.getExistingData(DSDataAttachments.MODIFIERS_WITH_DURATION).ifPresent(data -> data.tick(livingEntity));
        }
    }

    @SubscribeEvent
    public static void removeModifiers(final PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getExistingData(DSDataAttachments.MODIFIERS_WITH_DURATION).ifPresent(data -> data.removeAll(event.getEntity()));
        }
    }
}
