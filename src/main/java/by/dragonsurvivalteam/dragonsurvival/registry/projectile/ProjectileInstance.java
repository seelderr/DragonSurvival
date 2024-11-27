package by.dragonsurvivalteam.dragonsurvival.registry.projectile;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public class ProjectileInstance implements INBTSerializable<CompoundTag> {
    private int level;

    // TODO :: store cooldown here?
    // TODO: How to pass down holder of ProjectileEffect?
    public ProjectileInstance(final int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    // TODO :: could also handle this through a CODEC defined for the instance (like MobEffectInstance)
    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(LEVEL, level);

        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        this.level = tag.getInt(LEVEL);
    }

    private final String LEVEL = "level";
}
