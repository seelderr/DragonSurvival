package by.jackraidenph.dragonsurvival.common.capability.provider;

import by.jackraidenph.dragonsurvival.common.capability.Capabilities;
import by.jackraidenph.dragonsurvival.common.capability.caps.GenericCapability;
import by.jackraidenph.dragonsurvival.common.capability.storage.GenericCapabilityStorage;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public class GenericCapabilityProvider implements ICapabilitySerializable<CompoundTag>
{
	private final LazyOptional<GenericCapability> instance;
	public final GenericCapabilityStorage storage;
	
	public GenericCapabilityProvider()
	{
		instance = LazyOptional.of(GenericCapability::new);
		storage  = new GenericCapabilityStorage();
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return cap == Capabilities.GENERIC_CAPABILITY ? instance.cast() : LazyOptional.empty();
	}
	
	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap)
	{
		return cap == Capabilities.GENERIC_CAPABILITY ? instance.cast() : LazyOptional.empty();
	}
	
	@Override
	public CompoundTag serializeNBT() {
		return (CompoundTag)storage.writeNBT(Capabilities.GENERIC_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		storage.readNBT(Capabilities.GENERIC_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);
	}
}
