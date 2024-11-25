package by.dragonsurvivalteam.dragonsurvival.util;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

public class ResourceHelper {
    public static ResourceLocation getKey(Block object) {
        return BuiltInRegistries.BLOCK.getKey(object);
    }

    public static ResourceLocation getKey(Item object) {
        return BuiltInRegistries.ITEM.getKey(object);
    }

    public static ResourceLocation getKey(EntityType<?> object) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(object);
    }

    public static ResourceLocation getKey(Entity object) {
        return getKey(object.getType());
    }

    public static ResourceLocation getKey(SoundEvent event) {
        return BuiltInRegistries.SOUND_EVENT.getKey(event);
    }

    public static Potion getPotionFromItem(Item item) {
        return BuiltInRegistries.POTION.getHolder(PotionItem.getId(item)).get().value();
    }

    public static <T> Optional<Holder.Reference<T>> get(@Nullable final HolderLookup.Provider provider, final ResourceKey<T> key, ResourceKey<Registry<T>> registryKey) {
        HolderLookup.RegistryLookup<T> registry;

        if (provider == null) {
            registry = CommonHooks.resolveLookup(registryKey);
        } else {
            registry = provider.lookupOrThrow(registryKey);
        }

        return Objects.requireNonNull(registry).get(key);
    }

    public static <T> List<ResourceKey<T>> keys(@Nullable final HolderLookup.Provider provider, ResourceKey<Registry<T>> registryKey) {
        HolderLookup.RegistryLookup<T> registry;

        if (provider == null) {
            registry = CommonHooks.resolveLookup(registryKey);
        } else {
            registry = provider.lookupOrThrow(registryKey);
        }

        //noinspection DataFlowIssue -> registry is expected to be present
        return registry.listElementIds().toList();
    }
}
