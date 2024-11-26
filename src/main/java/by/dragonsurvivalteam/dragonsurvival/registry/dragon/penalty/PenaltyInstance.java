package by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

// TODO :: consider if this is needed
public class PenaltyInstance implements INBTSerializable<CompoundTag>  {
    Holder<DragonPenalty> penalty;

    public PenaltyInstance(Holder<DragonPenalty> penalty) {
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
    }
}
