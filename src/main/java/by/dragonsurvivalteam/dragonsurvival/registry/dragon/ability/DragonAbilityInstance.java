package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public class DragonAbilityInstance implements INBTSerializable<CompoundTag> {
    private final Holder<DragonAbility> ability;
    private int level = 1;

    // TODO :: store cooldown here?

    public DragonAbilityInstance(final Holder<DragonAbility> ability) {
        this.ability = ability;
    }

    public DragonAbility getAbility() {
        return ability.value();
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
