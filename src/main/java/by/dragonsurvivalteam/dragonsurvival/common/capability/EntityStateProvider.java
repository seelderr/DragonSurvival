package by.dragonsurvivalteam.dragonsurvival.common.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public class EntityStateProvider implements ICapabilitySerializable<CompoundTag> {
    private final EntityStateHandler handlerObject = new EntityStateHandler();
    private final LazyOptional<EntityStateHandler> instance = LazyOptional.of(() -> handlerObject);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull final Capability<T> cap, Direction side){
        return cap == Capabilities.ENTITY_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT(){
        return instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")).writeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt){
        instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")).readNBT(nbt);
    }

    public void invalidate(){
        instance.invalidate();
    }
}
