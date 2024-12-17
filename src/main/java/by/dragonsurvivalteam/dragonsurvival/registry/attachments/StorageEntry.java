package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public interface StorageEntry {
    default void apply(final Entity entity) { /* Nothing to do */ }
    default void remove(final Entity entity) { /* Nothing to do */ }

    boolean tick();
    ResourceLocation getId();
}
