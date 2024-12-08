package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ModifierWithDuration;
import by.dragonsurvivalteam.dragonsurvival.network.modifiers.SyncModifierWithDuration;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import javax.annotation.Nullable;

@EventBusSubscriber
public class ModifiersWithDuration implements INBTSerializable<CompoundTag> {
    public static final String MODIFIERS_WITH_DURATION = "modifiers_with_duration";

    @Nullable private Map<ResourceLocation, ModifierWithDuration.Instance> modifiersWithDuration;

    public void tick(final LivingEntity entity) {
        if (modifiersWithDuration != null) {
            Set<ResourceLocation> finished = new HashSet<>();

            modifiersWithDuration.values().forEach(modifier -> {
                if (modifier.tick()) {
                    finished.add(modifier.baseData().id());
                }
            });

            finished.forEach(id -> modifiersWithDuration.remove(id));

            if (modifiersWithDuration.isEmpty()) {
                entity.removeData(DSDataAttachments.MODIFIERS_WITH_DURATION);
            }
        }
    }

    public void add(final LivingEntity target, final ModifierWithDuration.Instance modifier) {
        if (modifiersWithDuration == null) {
            modifiersWithDuration = new HashMap<>();
        }

        modifiersWithDuration.put(modifier.baseData().id(), modifier);

        Holder<DragonType> dragonType = DragonStateProvider.getOptional(target).map(DragonStateHandler::getDragonType).orElse(null);
        modifier.applyModifiers(target, dragonType, modifier.appliedAbilityLevel());
    }

    public void remove(final LivingEntity entity, final ModifierWithDuration.Instance modifier) {
        if (modifiersWithDuration == null) {
            return;
        }

        modifiersWithDuration.remove(modifier.baseData().id());
        modifier.removeModifiers(entity);
    }

    public void removeAll(final LivingEntity entity) {
        if (modifiersWithDuration == null) {
            return;
        }

        modifiersWithDuration.values().forEach(modifier -> remove(entity, modifier));
    }

    public @Nullable ModifierWithDuration.Instance get(final ModifierWithDuration modification) {
        if (modifiersWithDuration == null) {
            return null;
        }

        return modifiersWithDuration.get(modification.id());
    }

    public int size() {
        if (modifiersWithDuration == null) {
            return 0;
        }

        return modifiersWithDuration.size();
    }

    public Collection<ModifierWithDuration.Instance> all() {
        if (modifiersWithDuration == null) {
            return Collections.emptyList();
        }

        return modifiersWithDuration.values();
    }

    public void syncModifiersToPlayer(final ServerPlayer player) {
        for(ModifierWithDuration.Instance modifier : all()) {
            PacketDistributor.sendToPlayer(player, new SyncModifierWithDuration(player.getId(), modifier));
        }
    }

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag entries = new ListTag();

        if (modifiersWithDuration != null) {
            modifiersWithDuration.values().forEach(modifier -> entries.add(modifier.save(provider)));
            tag.put(MODIFIERS_WITH_DURATION, entries);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        Map<ResourceLocation, ModifierWithDuration.Instance> modifiers = new HashMap<>();
        ListTag entries = tag.getList(MODIFIERS_WITH_DURATION, ListTag.TAG_COMPOUND);

        for (int i = 0; i < entries.size(); i++) {
            ModifierWithDuration.Instance modifier = ModifierWithDuration.Instance.load(provider, entries.getCompound(i));

            if (modifier != null) {
                modifiers.put(modifier.baseData().id(), modifier);
            }
        }

        if (!modifiers.isEmpty()) {
            modifiersWithDuration = modifiers;
        } else {
            modifiersWithDuration = null;
        }
    }

    @SubscribeEvent
    public static void tickModifiers(final EntityTickEvent.Post event) {
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
