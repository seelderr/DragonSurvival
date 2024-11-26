package by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class PenaltyInstance implements INBTSerializable<CompoundTag>  {
    Holder<DragonPenalty> penalty;
    int penaltySupply;

    public PenaltyInstance(Holder<DragonPenalty> penalty) {
        this.penalty = penalty;
        this.penaltySupply = penalty.value().trigger().durationToTrigger() == 0 ? penalty.value().trigger().triggerRate() : penalty.value().trigger().durationToTrigger();
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(PENALTY_SUPPLY, penaltySupply);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        penaltySupply = nbt.getInt(PENALTY_SUPPLY);
    }

    public String PENALTY_SUPPLY = "penalty_supply";
}
