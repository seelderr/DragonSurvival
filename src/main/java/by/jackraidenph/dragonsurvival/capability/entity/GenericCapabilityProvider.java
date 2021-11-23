package by.jackraidenph.dragonsurvival.capability.entity;

import by.jackraidenph.dragonsurvival.capability.Capabilities;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class GenericCapabilityProvider implements ICapabilitySerializable<CompoundNBT> {
    private final LazyOptional<GenericCapability> instance = LazyOptional.of(() -> (GenericCapability) Objects.<Object>requireNonNull(Capabilities.GENERIC_CAPABILITY.getDefaultInstance()));

    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return (cap == Capabilities.GENERIC_CAPABILITY) ? this.instance.cast() : LazyOptional.empty();
    }

    public CompoundNBT serializeNBT() {
        return (CompoundNBT) Capabilities.GENERIC_CAPABILITY.getStorage().writeNBT(Capabilities.GENERIC_CAPABILITY, this.instance.orElse(null), null);
    }

    public void deserializeNBT(CompoundNBT nbt) {
        Capabilities.GENERIC_CAPABILITY.getStorage().readNBT(Capabilities.GENERIC_CAPABILITY, this.instance.orElse(null), null, (INBT) nbt);
    }
}
