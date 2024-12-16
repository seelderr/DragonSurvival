package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public interface StorageEntry {
    default void apply(final LivingEntity entity) { /* Nothing to do */ }
    default void remove(final LivingEntity entity) { /* Nothing to do */ }

    boolean tick();
    ResourceLocation getId();
}
