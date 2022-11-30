package by.dragonsurvivalteam.dragonsurvival.common.capability.provider;

import by.dragonsurvivalteam.dragonsurvival.common.capability.Capabilities;
import by.dragonsurvivalteam.dragonsurvival.common.capability.GenericCapability;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class GenericCapabilityProvider implements ICapabilitySerializable<CompoundTag>{
	private final GenericCapability handlerObject = new GenericCapability();
	private final LazyOptional<GenericCapability> instance = LazyOptional.of(() -> handlerObject);

	public static LazyOptional<GenericCapability> getCap(Entity entity){
		return entity.getCapability(Capabilities.GENERIC_CAPABILITY, null);
	}

	public static GenericCapability getGenericCapability(Entity entity){
		if(entity == null) return new GenericCapability();
		return getCap(entity).orElse(new GenericCapability());
	}

	public void invalidate(){
		//		instance.invalidate();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		return cap == Capabilities.GENERIC_CAPABILITY ? instance.cast() : LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT(){
		return this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")).writeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt){
		this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")).readNBT(nbt);
	}
}