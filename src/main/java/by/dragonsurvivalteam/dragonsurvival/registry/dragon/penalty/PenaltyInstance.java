package by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class PenaltyInstance implements INBTSerializable<CompoundTag>  {
    Holder<DragonPenalty> penalty;
    float penaltySupply;
    float supplyRate;

    public PenaltyInstance(Holder<DragonPenalty> penalty) {
        this.penalty = penalty;
        this.penaltySupply = 0;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat(PENALTY_SUPPLY, penaltySupply);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        penaltySupply = nbt.getInt(PENALTY_SUPPLY);
    }

    public String PENALTY_SUPPLY = "penalty_supply";
}
