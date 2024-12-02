package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public class DragonAbilityInstance implements INBTSerializable<CompoundTag> {
    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 255;

    private final Holder<DragonAbility> ability;
    private int level = 1; // TODO :: remove value
    private int cooldown;
    private boolean isEnabled = true;

    // TODO :: values which will not be saved
    private int currentTick;

    public DragonAbilityInstance(final Holder<DragonAbility> ability) {
        this.ability = ability;
    }

    public void apply(final ServerPlayer dragon) {
        if (!isActive()) {
            return;
        }

        currentTick++;
        ability.value().effects().forEach(effect -> effect.tick(dragon, this, currentTick));
    }

    public boolean isActive() {
        return isEnabled && cooldown <= 0;
    }

    // TODO :: called when the pressed key is released (+ can also call this when the ability is disabled (relevant for passive only))
    public void release() {
        currentTick = 0;
        cooldown = ability.value().getCooldown(level);
    }

    public DragonAbility getAbility() {
        return ability.value();
    }

    public int getLevel() {
        return level;
    }

    public void enable() {
        isEnabled = true;
    }

    public void disable() {
        isEnabled = false;
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
